package com.hackathon.healthtech.eyeassistant.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.hackathon.healthtech.eyeassistant.R;
import com.hackathon.healthtech.eyeassistant.dialogs.AnswerDialog;
import com.hackathon.healthtech.eyeassistant.dialogs.MyListDialog;
import com.hackathon.healthtech.eyeassistant.entities.Question;
import com.hackathon.healthtech.eyeassistant.fragments.AskFragment;
import com.hackathon.healthtech.eyeassistant.fragments.FillInAnswersFragment;
import com.hackathon.healthtech.eyeassistant.fragments.FillInQuestionFragment;
import com.hackathon.healthtech.eyeassistant.fragments.QuestionFragment;
import com.hackathon.healthtech.eyeassistant.utils.ParcelableUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class ConnectionActivity extends BaseActivity implements
		FillInQuestionFragment.OnFragmentInteractionListener,
		FillInAnswersFragment.OnFragmentInteractionListener,
		AskFragment.OnFragmentInteractionListener,
		QuestionFragment.OnFragmentInteractionListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, Connections.ConnectionRequestListener, Connections.EndpointDiscoveryListener, Connections.MessageListener {
	private final static String TAG = ConnectionActivity.class.getSimpleName();
	private static final int ASK_QUESTION_CODE = 10001;
	private String mOtherEndpointId;
	private Question mQuestion;
	private FloatingActionButton fab;

	@Retention(RetentionPolicy.CLASS)
	@IntDef({STATE_IDLE, STATE_READY, STATE_ADVERTISING, STATE_DISCOVERING, STATE_CONNECTED})
	public @interface NearbyConnectionState {
	}

	private static final int STATE_IDLE = 1023;
	private static final int STATE_READY = 1024;
	private static final int STATE_ADVERTISING = 1025;
	private static final int STATE_DISCOVERING = 1026;
	private static final int STATE_CONNECTED = 1027;

	private static final long TIMEOUT_ADVERTISE = 1000L * 30L;
	private static final long TIMEOUT_DISCOVER = 1000L * 30L;

	public boolean isAnswered = false;

	private GoogleApiClient mGoogleApiClient;
	private MyListDialog mMyListDialog;

	/**
	 * The current state of the application
	 **/
	@NearbyConnectionState
	private int mState = STATE_IDLE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection);

		setRequestedOrientation(isPatient() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


		findViewById(R.id.content_container);
		fab = (FloatingActionButton) findViewById(R.id.fab_send);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				replaceFragment(FillInQuestionFragment.newInstance());
			}
		});
		if (!isPatient()) {
			fab.setVisibility(View.VISIBLE);
		}
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Nearby.CONNECTIONS_API)
				.build();

		replaceFragment(isPatient() ? new QuestionFragment() : new AskFragment());
	}

	private void replaceFragment(Fragment fragment) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_container, fragment).commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGoogleApiClient.reconnect();
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");

		// Disconnect the Google API client and stop any ongoing discovery or advertising. When the
		// GoogleAPIClient is disconnected, any connected peers will get an onDisconnected callback.
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
	}

	public Question getQuestion() {
		if (mQuestion == null) {
			mQuestion = new Question();
		}
		return mQuestion;
	}

	/**
	 * Check if the device is connected (or connecting) to a WiFi network.
	 *
	 * @return true if connected or connecting, false otherwise.
	 */
	private boolean isConnectedToNetwork() {
		ConnectivityManager connManager = (ConnectivityManager)
				getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return (info != null && info.isConnectedOrConnecting());
	}

	/**
	 * Begin advertising for Nearby Connections, if possible.
	 */
	private void startAdvertising() {
		debugLog("startAdvertising");
		if (!isConnectedToNetwork()) {
			debugLog("startAdvertising: not connected to WiFi network.");
			return;
		}

		// Advertising with an AppIdentifer lets other devices on the network discover
		// this application and prompt the user to install the application.
		List<AppIdentifier> appIdentifierList = new ArrayList<>();
		appIdentifierList.add(new AppIdentifier(getPackageName()));
		AppMetadata appMetadata = new AppMetadata(appIdentifierList);

		// Advertise for Nearby Connections. This will broadcast the service id defined in
		// AndroidManifest.xml. By passing 'null' for the name, the Nearby Connections API
		// will construct a default name based on device model such as 'LGE Nexus 5'.
		String name = null;
		Nearby.Connections.startAdvertising(mGoogleApiClient, name, appMetadata, TIMEOUT_ADVERTISE,
				this).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
			@Override
			public void onResult(Connections.StartAdvertisingResult result) {
				Log.d(TAG, "startAdvertising:onResult:" + result);
				if (result.getStatus().isSuccess()) {
					debugLog("startAdvertising:onResult: SUCCESS");
				} else {
					debugLog("startAdvertising:onResult: FAILURE ");

					// If the user hits 'Advertise' multiple times in the timeout window,
					// the error will be STATUS_ALREADY_ADVERTISING
					int statusCode = result.getStatus().getStatusCode();
					if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING) {
						debugLog("STATUS_ALREADY_ADVERTISING");
					} else {
						updateViewVisibility(STATE_READY);
					}
				}
			}
		});
	}

	/**
	 * Begin discovering devices advertising Nearby Connections, if possible.
	 */
	private void startDiscovery() {
		debugLog("startDiscovery");
		if (!isConnectedToNetwork()) {
			debugLog("startDiscovery: not connected to WiFi network.");
			return;
		}

		// Discover nearby apps that are advertising with the required service ID.
		String serviceId = getString(R.string.nearby_service_id);
		Nearby.Connections.startDiscovery(mGoogleApiClient, serviceId, TIMEOUT_DISCOVER, this)
				.setResultCallback(new ResultCallback<Status>() {
					@Override
					public void onResult(Status status) {
						if (status.isSuccess()) {
							debugLog("startDiscovery:onResult: SUCCESS");

							updateViewVisibility(STATE_DISCOVERING);
						} else {
							debugLog("startDiscovery:onResult: FAILURE");

							// If the user hits 'Discover' multiple times in the timeout window,
							// the error will be STATUS_ALREADY_DISCOVERING
							int statusCode = status.getStatusCode();
							if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_DISCOVERING) {
								debugLog("STATUS_ALREADY_DISCOVERING");
							} else {
								updateViewVisibility(STATE_READY);
							}
						}
					}
				});
	}

	/**
	 * Send a connection request to a given endpoint.
	 *
	 * @param endpointId   the endpointId to which you want to connect.
	 * @param endpointName the name of the endpoint to which you want to connect. Not required to
	 *                     make the connection, but used to display after success or failure.
	 */
	private void connectTo(String endpointId, final String endpointName) {
		debugLog("connectTo:" + endpointId + ":" + endpointName);

		// Send a connection request to a remote endpoint. By passing 'null' for the name,
		// the Nearby Connections API will construct a default name based on device model
		// such as 'LGE Nexus 5'.
		String myName = null;
		byte[] myPayload = null;
		Nearby.Connections.sendConnectionRequest(mGoogleApiClient, myName, endpointId, myPayload,
				new Connections.ConnectionResponseCallback() {
					@Override
					public void onConnectionResponse(String endpointId, Status status,
													 byte[] bytes) {
						Log.d(TAG, "onConnectionResponse:" + endpointId + ":" + status);
						if (status.isSuccess()) {
							debugLog("onConnectionResponse: " + endpointName + " SUCCESS");
							Toast.makeText(ConnectionActivity.this, "Connected to " + endpointName,
									Toast.LENGTH_SHORT).show();

							mOtherEndpointId = endpointId;
							updateViewVisibility(STATE_CONNECTED);
						} else {
							debugLog("onConnectionResponse: " + endpointName + " FAILURE");
						}
					}
				}, this);
	}

	@Override
	public void onConnectionRequest(final String endpointId, String deviceId, String endpointName,
									byte[] payload) {
		debugLog("onConnectionRequest:" + endpointId + ":" + endpointName);

		// This device is advertising and has received a connection request. Show a dialog asking
		// the user if they would like to connect and accept or reject the request accordingly.
		AlertDialog connectionRequestDialog = new AlertDialog.Builder(this)
				.setTitle("Connection Request")
				.setMessage("Do you want to connect to " + endpointName + "?")
				.setCancelable(false)
				.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						byte[] payload = null;
						Nearby.Connections.acceptConnectionRequest(mGoogleApiClient, endpointId,
								payload, ConnectionActivity.this)
								.setResultCallback(new ResultCallback<Status>() {
									@Override
									public void onResult(Status status) {
										if (status.isSuccess()) {
											debugLog("acceptConnectionRequest: SUCCESS");

											mOtherEndpointId = endpointId;
											updateViewVisibility(STATE_CONNECTED);
										} else {
											debugLog("acceptConnectionRequest: FAILURE");
										}
									}
								});
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Nearby.Connections.rejectConnectionRequest(mGoogleApiClient, endpointId);
					}
				}).create();

		connectionRequestDialog.show();
	}

	@Override
	public void onMessageReceived(String endpointId, byte[] payload, boolean isReliable) {
		// A message has been received from a remote endpoint.
		debugLog("onMessageReceived:" + endpointId + ":" + new String(payload));
		Parcelable unmarshall = ParcelableUtils.unmarshall(payload, Question.CREATOR);
		Question question = (Question) unmarshall;
		if (isPatient()) {
			isAnswered = false;
			replaceFragment(QuestionFragment.newInstance(question));
		} else {
			AnswerDialog.newInstance(question).show(getSupportFragmentManager(), "answer_dialog");
		}
	}

	@Override
	public void onDisconnected(String endpointId) {
		debugLog("onDisconnected:" + endpointId);

		updateViewVisibility(STATE_READY);
	}

	@Override
	public void onEndpointFound(final String endpointId, String deviceId, String serviceId,
								final String endpointName) {
		Log.d(TAG, "onEndpointFound:" + endpointId + ":" + endpointName);

		// This device is discovering endpoints and has located an advertiser. Display a dialog to
		// the user asking if they want to connect, and send a connection request if they do.
		if (mMyListDialog == null) {
			// Configure the AlertDialog that the MyListDialog wraps
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setTitle("Endpoint(s) Found")
					.setCancelable(true)
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mMyListDialog.dismiss();
						}
					});

			// Create the MyListDialog with a listener
			mMyListDialog = new MyListDialog(this, builder, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String selectedEndpointName = mMyListDialog.getItemKey(which);
					String selectedEndpointId = mMyListDialog.getItemValue(which);

					ConnectionActivity.this.connectTo(selectedEndpointId, selectedEndpointName);
					mMyListDialog.dismiss();
				}
			});
		}

		mMyListDialog.addItem(endpointName, endpointId);
		mMyListDialog.show();
	}

	@Override
	public void onEndpointLost(String endpointId) {
		debugLog("onEndpointLost:" + endpointId);

		// An endpoint that was previously available for connection is no longer. It may have
		// stopped advertising, gone out of range, or lost connectivity. Dismiss any dialog that
		// was offering a connection.
		if (mMyListDialog != null) {
			mMyListDialog.removeItemByValue(endpointId);
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		debugLog("onConnected");
		updateViewVisibility(STATE_READY);
	}

	@Override
	public void onConnectionSuspended(int i) {
		debugLog("onConnectionSuspended: " + i);
		updateViewVisibility(STATE_IDLE);

		// Try to re-connect
		mGoogleApiClient.reconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		debugLog("onConnectionFailed: " + connectionResult);
		updateViewVisibility(STATE_IDLE);
	}

	@Deprecated
	private void disconnect() {
		debugLog("disconnect from:" + mOtherEndpointId);
		if (!TextUtils.isEmpty(mOtherEndpointId)) {
			Nearby.Connections.disconnectFromEndpoint(mGoogleApiClient, mOtherEndpointId);
		}
	}

	/**
	 * Change the application state and update the visibility on on-screen views '
	 * based on the new state of the application.
	 *
	 * @param newState the state to move to (should be NearbyConnectionState)
	 */
	private void updateViewVisibility(@NearbyConnectionState int newState) {
		mState = newState;
		switch (mState) {
			case STATE_IDLE:
			case STATE_READY:
				ConnectionActivity.this.getWindow().setBackgroundDrawableResource(R.drawable.bg_not_connected);
				break;
			case STATE_ADVERTISING:
				debugLog("STATE_ADVERTISING");
				ConnectionActivity.this.getWindow().setBackgroundDrawableResource(R.drawable.bg_advertising);
				break;
			case STATE_DISCOVERING:
				debugLog("STATE_DISCOVERING");
				ConnectionActivity.this.getWindow().setBackgroundDrawableResource(R.drawable.bg_discovering);
				break;
			case STATE_CONNECTED:
				debugLog("CONNECTED");
				ConnectionActivity.this.getWindow().setBackgroundDrawableResource(R.drawable.bg_main);
				break;
		}
	}

	/**
	 * Print a message to the DEBUG LogCat as well as to the on-screen debug panel.
	 *
	 * @param msg the message to print and display.
	 */
	private void debugLog(String msg) {
		Log.d(TAG, msg);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_connection, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
//            case android.R.id.home:
//                drawerLayout.openDrawer(GravityCompat.START);
//                return true;
			case R.id.action_change_role:
				startActivity(new Intent(this, MainActivity.class));
				finish();
				return true;
			case R.id.action_advertise:
				if (isPatient()) {
					startAdvertising();
				} else {
					startDiscovery();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onAnswerSelected(Question question) {
		isAnswered = true;
		sendViaNearBy(question);
	}


	@Override
	public void onClickAddQuestion() {
		replaceFragment(FillInQuestionFragment.newInstance());
	}

	@Override
	public void onClickHistory() {
//        mQuestion = new Question("Question full text?");
//        mQuestion.setAnswerFirst(new Answer("Answer1"));
//        mQuestion.setAnswerSecond(new Answer("Answer2"));
//        mQuestion.setAnswerThird(new Answer("Answer3"));
//        mQuestion.setAnswerFourth(new Answer("Answer4"));
		replaceFragment(QuestionFragment.newInstance(mQuestion));
	}

	private void sendViaNearBy(Question question) {
		Nearby.Connections.sendReliableMessage(mGoogleApiClient, mOtherEndpointId, ParcelableUtils.marshall(question));
	}

	@Override
	public void onQuestionAsked(Question question) {
		replaceFragment(FillInAnswersFragment.newInstance(question));
	}

	@Override
	public void onAnswersAsked(Question question) {
		sendViaNearBy(question);
	}
}
