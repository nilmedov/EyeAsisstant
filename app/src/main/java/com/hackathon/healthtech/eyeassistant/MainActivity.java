package com.hackathon.healthtech.eyeassistant;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity implements CvCameraViewListener2, View.OnClickListener {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
	public static final int JAVA_DETECTOR = 0;
	private static final int TM_SQDIFF = 0;
	private static final int TM_SQDIFF_NORMED = 1;
	private static final int TM_CCOEFF = 2;
	private static final int TM_CCOEFF_NORMED = 3;
	private static final int TM_CCORR = 4;
	private static final int TM_CCORR_NORMED = 5;

	private static final int EYE_AREA_WIDTH_CONST = 8;
	private static final float EYE_AREA_HEIGHT_CONST = 4.5f;

	private static final int EYE_TOP_THRESHOLD = 10;
	private static final int EYE_BOTTOM_THRESHOLD = 7;
	private static final int EYE_LEFT_THRESHOLD = 10;
	private static final int EYE_RIGHT_THRESHOLD = 10;


	private int learn_frames = 0;
	private Mat teplateR;
	private Mat teplateL;
	int method = 0;

	private MenuItem mItemFace50;
	private MenuItem mItemFace40;
	private MenuItem mItemFace30;
	private MenuItem mItemFace20;
	private MenuItem mItemType;

	private Mat mRgba;
	private Mat mGray;

	private CascadeClassifier mJavaDetector;
	private CascadeClassifier mJavaDetectorEye;


	private int mDetectorType = JAVA_DETECTOR;
	private String[] mDetectorName;

	private float mRelativeFaceSize = 0.2f;
	private int mAbsoluteFaceSize = 0;

	private CameraBridgeViewBase mOpenCvCameraView;

	private ViewFlipper mViewFlipper;
	private TextView mValue, mTxtDirection;

	private Rect leftEyeCalibrated, rightEyeCalibrated;

	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			learn_frames = 0;

			long timePerUpdate = 5000;
			mHandler.postDelayed(mRunnable, timePerUpdate);
		}
	};


	double xCenter = -1;
	double yCenter = -1;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
				case LoaderCallbackInterface.SUCCESS: {
					Log.i(TAG, "OpenCV loaded successfully");


					try {
						// load cascade file from application resources
						InputStream is = getResources().openRawResource(
								R.raw.lbpcascade_frontalface);
						File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
						File mCascadeFile = new File(cascadeDir,
								"lbpcascade_frontalface.xml");
						FileOutputStream os = new FileOutputStream(mCascadeFile);

						byte[] buffer = new byte[4096];
						int bytesRead;
						while ((bytesRead = is.read(buffer)) != -1) {
							os.write(buffer, 0, bytesRead);
						}
						is.close();
						os.close();

						// --------------------------------- load left eye
						// classificator -----------------------------------
						InputStream iser = getResources().openRawResource(
								R.raw.haarcascade_lefteye_2splits);
						File cascadeDirER = getDir("cascadeER",
								Context.MODE_PRIVATE);
						File cascadeFileER = new File(cascadeDirER,
								"haarcascade_eye_right.xml");
						FileOutputStream oser = new FileOutputStream(cascadeFileER);

						byte[] bufferER = new byte[4096];
						int bytesReadER;
						while ((bytesReadER = iser.read(bufferER)) != -1) {
							oser.write(bufferER, 0, bytesReadER);
						}
						iser.close();
						oser.close();

						mJavaDetector = new CascadeClassifier(
								mCascadeFile.getAbsolutePath());
						if (mJavaDetector.empty()) {
							Log.e(TAG, "Failed to load cascade classifier");
							mJavaDetector = null;
						} else
							Log.i(TAG, "Loaded cascade classifier from "
									+ mCascadeFile.getAbsolutePath());

						mJavaDetectorEye = new CascadeClassifier(
								cascadeFileER.getAbsolutePath());
						if (mJavaDetectorEye.empty()) {
							Log.e(TAG, "Failed to load cascade classifier");
							mJavaDetectorEye = null;
						} else {
							Log.i(TAG, "Loaded cascade classifier from "
									+ mCascadeFile.getAbsolutePath());
						}
						cascadeDir.delete();

					} catch (IOException e) {
						e.printStackTrace();
						Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
					}
					mOpenCvCameraView.setCameraIndex(1);
					mOpenCvCameraView.enableFpsMeter();
					mOpenCvCameraView.enableView();
				}
				break;
				default: {
					super.onManagerConnected(status);
				}
				break;
			}
		}
	};

	public MainActivity() {
		mDetectorName = new String[2];
		mDetectorName[JAVA_DETECTOR] = "Java";

		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_main);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);

		mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

		findViewById(R.id.btn_recreate).setOnClickListener(this);
		findViewById(R.id.btn_flip).setOnClickListener(this);

		SeekBar mMethodSeekbar = (SeekBar) findViewById(R.id.methodSeekBar);
		mValue = (TextView) findViewById(R.id.method);
		mTxtDirection = (TextView) findViewById(R.id.txt_direction);

		mMethodSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				method = progress;
				switch (method) {
					case 0:
						mValue.setText("TM_SQDIFF");
						break;
					case 1:
						mValue.setText("TM_SQDIFF_NORMED");
						break;
					case 2:
						mValue.setText("TM_CCOEFF");
						break;
					case 3:
						mValue.setText("TM_CCOEFF_NORMED");
						break;
					case 4:
						mValue.setText("TM_CCORR");
						break;
					case 5:
						mValue.setText("TM_CCORR_NORMED");
						break;
				}
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this,
				mLoaderCallback);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mOpenCvCameraView.disableView();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		mGray = new Mat();
		mRgba = new Mat();
	}

	public void onCameraViewStopped() {
		mGray.release();
		mRgba.release();
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();

		//detect faces
		if (mAbsoluteFaceSize == 0) {
			int height = mGray.rows();
			if (Math.round(height * mRelativeFaceSize) > 0) {
				mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
			}
		}

		MatOfRect faces = new MatOfRect();

		Core.rectangle(mRgba, new Point(mOpenCvCameraView.getFrameWidth() / 2 - 250, mOpenCvCameraView.getFrameHeight() / 2 + 300), new Point(mOpenCvCameraView.getFrameWidth() / 2 + 250, mOpenCvCameraView.getFrameHeight() / 2 - 300), FACE_RECT_COLOR, 3);

		if (mJavaDetector != null)
			mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2,
					new Size(mAbsoluteFaceSize, mAbsoluteFaceSize),
					new Size());

		Rect[] facesArray = faces.toArray();
		for (int i = 0; i < facesArray.length; i++) {
			xCenter = (facesArray[i].x + facesArray[i].width + facesArray[i].x) / 2;
			yCenter = (facesArray[i].y + facesArray[i].y + facesArray[i].height) / 2;
			Rect r = facesArray[i];

			//detect eye areas
			final Rect eyearea_right = new Rect(r.x + r.width / EYE_AREA_WIDTH_CONST,
					(int) (r.y + (r.height / EYE_AREA_HEIGHT_CONST)),
					(r.width - 2 * r.width / EYE_AREA_WIDTH_CONST) / 2, (int) (r.height / 3.0));
			final Rect eyearea_left = new Rect(r.x + r.width / EYE_AREA_WIDTH_CONST
					+ (r.width - 2 * r.width / EYE_AREA_WIDTH_CONST) / 2,
					(int) (r.y + (r.height / EYE_AREA_HEIGHT_CONST)),
					(r.width - 2 * r.width / EYE_AREA_WIDTH_CONST) / 2, (int) (r.height / 3.0));
			Core.rectangle(mRgba, eyearea_left.tl(), eyearea_left.br(),
					new Scalar(255, 0, 0, 255), 2);
			Core.rectangle(mRgba, eyearea_right.tl(), eyearea_right.br(),
					new Scalar(255, 0, 0, 255), 2);

			//detect eye gaze
			final Rect leftEye;
			final Rect rightEye;
			if (learn_frames < 5) {
				teplateR = get_template(mJavaDetectorEye, eyearea_right, 24);
				teplateL = get_template(mJavaDetectorEye, eyearea_left, 24);
				learn_frames++;
				if (learn_frames == 5) {
					leftEyeCalibrated = match_eye(eyearea_left, teplateR, method);
					rightEyeCalibrated = match_eye(eyearea_right, teplateR, method);
				}
			} else {
				Core.rectangle(mRgba, leftEyeCalibrated.tl(), leftEyeCalibrated.br(),
						new Scalar(255, 200, 0, 255), 2);
				Core.rectangle(mRgba, rightEyeCalibrated.tl(), rightEyeCalibrated.br(),
						new Scalar(255, 200, 0, 255), 2);
				rightEye = match_eye(eyearea_right, teplateR, method);
				leftEye = match_eye(eyearea_left, teplateL, method);

				if (leftEyeCalibrated != null && rightEyeCalibrated != null && leftEye != null && rightEye != null) {
					checkAnswerSection(leftEye, rightEye);
				}
			}
		}
		return mRgba;
	}

	private void checkAnswerSection(Rect currentLeftEye, Rect currentRightEye) {
		int direction = 0;
		if (leftEyeCalibrated.x - currentLeftEye.x >= EYE_RIGHT_THRESHOLD && rightEyeCalibrated.x - currentRightEye.x >= EYE_RIGHT_THRESHOLD) {
			// translation to the right
			direction = 1;
		}
		if (currentLeftEye.x - leftEyeCalibrated.x >= EYE_LEFT_THRESHOLD && currentRightEye.x - rightEyeCalibrated.x >= EYE_LEFT_THRESHOLD) {
			// translation to the left
			direction = 2;
		}
		if (leftEyeCalibrated.y - currentLeftEye.y >= EYE_BOTTOM_THRESHOLD && rightEyeCalibrated.y - currentRightEye.y >= EYE_BOTTOM_THRESHOLD) {
			// translation to the bottom
			direction = 3;
		}
		if (currentLeftEye.y - leftEyeCalibrated.y>= EYE_TOP_THRESHOLD && currentRightEye.y - rightEyeCalibrated.y >= EYE_TOP_THRESHOLD) {
			// translation to the top
			direction = 4;
		}
		printDirection(direction);
	}

	private void printDirection(final int direction) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String message = null;
				switch (direction) {
					case 0:
						message = "NONE";
						break;
					case 1:
						message = "RIGHT";
						break;
					case 2:
						message = "LEFT";
						break;
					case 3:
						message = "TOP";
						break;
					case 4:
						message = "BOTTOM";
						break;
				}
				mTxtDirection.setText(message != null ? message : "");
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "called onCreateOptionsMenu");
		mItemFace50 = menu.add("Face size 50%");
		mItemFace40 = menu.add("Face size 40%");
		mItemFace30 = menu.add("Face size 30%");
		mItemFace20 = menu.add("Face size 20%");
		mItemType = menu.add(mDetectorName[mDetectorType]);
		return true;
	}

	private Rect match_eye(Rect area, Mat mTemplate, int type) {
		Point matchLoc;
		Mat mROI = mGray.submat(area);
		int result_cols = mROI.cols() - mTemplate.cols() + 1;
		int result_rows = mROI.rows() - mTemplate.rows() + 1;
		if (mTemplate.cols() == 0 || mTemplate.rows() == 0) {
			return null;
		}
		Mat mResult = new Mat(result_cols, result_rows, CvType.CV_8U);

		switch (type) {
			case TM_SQDIFF:
				Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_SQDIFF);
				break;
			case TM_SQDIFF_NORMED:
				Imgproc.matchTemplate(mROI, mTemplate, mResult,
						Imgproc.TM_SQDIFF_NORMED);
				break;
			case TM_CCOEFF:
				Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_CCOEFF);
				break;
			case TM_CCOEFF_NORMED:
				Imgproc.matchTemplate(mROI, mTemplate, mResult,
						Imgproc.TM_CCOEFF_NORMED);
				break;
			case TM_CCORR:
				Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_CCORR);
				break;
			case TM_CCORR_NORMED:
				Imgproc.matchTemplate(mROI, mTemplate, mResult,
						Imgproc.TM_CCORR_NORMED);
				break;
		}

		Core.MinMaxLocResult mmres = Core.minMaxLoc(mResult);
		// there is difference in matching methods - best match is max/min value
		if (type == TM_SQDIFF || type == TM_SQDIFF_NORMED) {
			matchLoc = mmres.minLoc;
		} else {
			matchLoc = mmres.maxLoc;
		}

		Point matchLoc_tx = new Point(matchLoc.x + area.x, matchLoc.y + area.y);
		Point matchLoc_ty = new Point(matchLoc.x + mTemplate.cols() + area.x,
				matchLoc.y + mTemplate.rows() + area.y);

		Core.rectangle(mRgba, matchLoc_tx, matchLoc_ty, new Scalar(255, 255, 0,
				255));
		return new Rect(matchLoc_tx,matchLoc_ty);
	}

	private Mat get_template(CascadeClassifier clasificator, Rect area, int size) {
		Mat template = new Mat();
		Mat mROI = mGray.submat(area);
		MatOfRect eyes = new MatOfRect();
		Point iris = new Point();
		Rect eye_template = new Rect();
		clasificator.detectMultiScale(mROI, eyes, 1.15, 2,
				Objdetect.CASCADE_FIND_BIGGEST_OBJECT
						| Objdetect.CASCADE_SCALE_IMAGE, new Size(30, 30),
				new Size());

		Rect[] eyesArray = eyes.toArray();
		for (int i = 0; i < eyesArray.length;) {
			Rect e = eyesArray[i];
			e.x = area.x + e.x;
			e.y = area.y + e.y;
			Rect eye_only_rectangle = new Rect((int) e.tl().x,
					(int) (e.tl().y + e.height * 0.4), (int) e.width,
					(int) (e.height * 0.6));
			mROI = mGray.submat(eye_only_rectangle);
			Mat vyrez = mRgba.submat(eye_only_rectangle);


			Core.MinMaxLocResult mmG = Core.minMaxLoc(mROI);

			Core.circle(vyrez, mmG.minLoc, 2, new Scalar(255, 255, 255, 255), 2);
			iris.x = mmG.minLoc.x + eye_only_rectangle.x;
			iris.y = mmG.minLoc.y + eye_only_rectangle.y;
			eye_template = new Rect((int) iris.x - size / 2, (int) iris.y
					- size / 2, size, size);
			Core.rectangle(mRgba, eye_template.tl(), eye_template.br(),
					new Scalar(255, 0, 0, 255), 2);
			template = (mGray.submat(eye_template)).clone();
			return template;
		}
		return template;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_recreate:
				learn_frames = 0;
				break;
			case R.id.btn_flip:
				mViewFlipper.showNext();
				break;
		}
	}
}
