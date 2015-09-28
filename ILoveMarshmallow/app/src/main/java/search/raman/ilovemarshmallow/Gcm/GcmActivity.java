package search.raman.ilovemarshmallow.Gcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import search.raman.ilovemarshmallow.Utilities.Globals;
import search.raman.ilovemarshmallow.Utilities.Utility;

/********************************************************************************************************************************
 * Developer : Ramanpreet Singh Khinda
 * <p/>
 * This class is responsible for handling all GCM related functionality, like making connection and registering device
 ********************************************************************************************************************************/
public class GcmActivity {
    private Activity context;
    private Utility utility;
    private AtomicInteger msgId = new AtomicInteger();
    public static GcmActivity myGcmObject = null;
    private GoogleCloudMessaging gcmInstance;

    public GcmActivity(Activity context) {
        this.context = context;
        utility = new Utility();
    }

    public static GcmActivity getInstance(Activity context) {
        if (myGcmObject == null) {
            myGcmObject = new GcmActivity(context);
        }
        return myGcmObject;
    }

    public static GcmActivity getInstance() {
        return myGcmObject;
    }

    public void registerAndLogin(String userEmailId, String userDeviceId) {
        String[] result = getRegistrationId(context);


        if (checkPlayServices()) {
            if (gcmInstance == null) {
                gcmInstance = GoogleCloudMessaging.getInstance(context);
            }

            if (Globals.REGISTRATION_ID == "" || (result[1] != null && !result[1].equalsIgnoreCase(userEmailId))) {
                registerInBackground(userEmailId, userDeviceId);
            } else {
                sendMessage(Globals.REGISTER_UNREGISTER_LISTENER, Globals.ALREADY_REGISTERED);
            }

        } else {
            sendMessage(Globals.REGISTER_UNREGISTER_LISTENER, Globals.GOOGLE_PLAY_SERVICE_NOT_VALID);
        }
    }


    public boolean sendNotification(String senderEmailId, String receiverEmailId, String msg, String asinId, String productName, String productImageUrlString, String brandName, String productPrice, int productRating) {
        if (utility.isValidEmail(receiverEmailId)) {
            sendMessage(senderEmailId, receiverEmailId, msg, asinId, productName, productImageUrlString, brandName, productPrice, productRating, Globals.TYPE_NOTIFICATION);
            return true;
        } else {
            sendMessage(Globals.GCM_NOTIFICATION_BROADCAST_LISTENER, Globals.EMAIL_NOT_VALID);
            return false;
        }
    }

    /**
     * Upstream a GCM message up to the 3rd party server
     *
     * @param message
     */
    private void sendMessage(String senderEmailId, String receiverEmailId, String message, String asinId, String productName, String productImageUrlString, String brandName, String productPrice, int productRating, String msg_type) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                String send_to = Globals.GCM_SENDER_ID + "@gcm.googleapis.com";

                try {
                    Bundle data = new Bundle();

                    if (params[9].equals("Echo")) {
                        data.putString(Globals.ACTION, Globals.ACTION_ECHO);

                    } else if (params[9].equals("Broadcast")) {
                        data.putString(Globals.ACTION, Globals.ACTION_BROADCAST);

                    } else if (params[9].equals("Notification")) {
                        data.putString(Globals.ACTION, Globals.ACTION_NOTIFICATION);
                    }

                    data.putString(Globals.SENDER_EMAIL_ID, params[0]);
                    data.putString(Globals.RECEIVER_EMAIL_ID, params[1]);
                    data.putString(Globals.MESSAGE, params[2]);
                    data.putString(Globals.ASIN_ID, params[3]);
                    data.putString(Globals.PRODUCT_NAME, params[4]);
                    data.putString(Globals.PRODUCT_IMAGE_URL, params[5]);
                    data.putString(Globals.BRAND_NAME, params[6]);
                    data.putString(Globals.PRODUCT_PRICE, params[7]);
                    data.putString(Globals.PRODUCT_RATING, params[8]);

                    String id = Integer.toString(msgId.incrementAndGet());
                    gcmInstance.send(send_to, id, Globals.GCM_TIME_TO_LIVE, data);
                    msg = "Sent message";

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                sendMessage(Globals.GCM_NOTIFICATION_BROADCAST_LISTENER, msg);
            }
        }.execute(senderEmailId, receiverEmailId, message, asinId, productName, productImageUrlString, brandName, productPrice, String.valueOf(productRating), msg_type);
    }

    /**
     * If this activity was started or brought to the front using an intent from a notification type
     * GCM message inform other devices the message was handled
     *
     * Upstream a GCM message letting other devices know to clear the notification as
     * it has been handled on this device
     *
     * @param notification_key The GCM registered notification key for the user's devices
     */
    public void sendNotificationClearMessage(String notification_key) {
        if (Globals.REGISTRATION_ID == null || Globals.REGISTRATION_ID.equals("")) {
            Log.v(Globals.TAG, "You must register first to send clear notification message");
            return;
        }
        if(notification_key == null || (notification_key != null && "".equals(notification_key))){
            Log.v(Globals.TAG, "Notification key does not exist : "+notification_key);
            return;
        }
        
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                try {
                    Bundle data = new Bundle();
                    data.putString(Globals.ACTION, Globals.ACTION_CLEAR_NOTIFICATION);
                    String id = Integer.toString(msgId.incrementAndGet());
                    gcmInstance.send(params[0], id, Globals.GCM_TIME_TO_LIVE, data);
                    msg = "Sent notification clear message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                } catch (IllegalArgumentException ex){
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.v(Globals.TAG,msg);
            }
        }.execute(notification_key);

    }

    private void registerInBackground(String userEmailId, String userDeviceId) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                try {
                    Globals.REGISTRATION_ID = gcmInstance.register(Globals.GCM_SENDER_ID);
                    msg = "Device registered, registration ID=" + Globals.REGISTRATION_ID;

                    // You should send the registration ID and User ID to your server over
                    // HTTP, so it can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdsToBackend(params[0], params[1]);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.v(Globals.TAG, "Error: " + msg);
                }

                Log.v(Globals.TAG, "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.v(Globals.TAG, "Sent Registration request to GCM Server." + msg);
            }
        }.execute(userEmailId, userDeviceId, null);
    }


    /**
     * Sends the registration ID and User ID to the 3rd party server via an upstream
     * GCM message. Ideally this would be done via HTTP to guarantee success or failure
     * immediately, but it would require an HTTP endpoint.
     */
    private void sendRegistrationIdsToBackend(String userEmailId, String userDeviceId) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";

                try {
                    Bundle data = new Bundle();
                    data.putString(Globals.ACTION, Globals.ACTION_REGISTER);
                    data.putString(Globals.NEW_USER_EMAIL_ID, params[0]);
                    data.putString(Globals.NEW_USER_DEVICE_ID, params[1]);
                    data.putString(Globals.REGISTRATION_ID, params[2]);
                    String id = Integer.toString(msgId.incrementAndGet());
                    gcmInstance.send(Globals.GCM_SENDER_ID + "@gcm.googleapis.com", id, Globals.GCM_TIME_TO_LIVE, data);
                    msg = Globals.SENT_REGISTRATION_REQUEST;
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(userEmailId, userDeviceId, Globals.REGISTRATION_ID);
    }

    /**
     * Gets the current registration ID for application on GCM service, if there
     * is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String[] getRegistrationId(Context context) {
        String[] result = new String[2];

        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(Globals.PREFS_PROPERTY_REG_ID, "");
        String registeredEmailId = prefs.getString(Globals.CURRENT_USER_EMAIL_ID, "");

        if (registrationId == null || registrationId.equals("")) {
            Log.v(Globals.TAG, "Registration not found.");
            result[0] = "";
            result[1] = "";
            return result;
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(Globals.APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.v(Globals.TAG, "App version changed.");
            result[0] = "";
            result[1] = "";
            return result;
        }

        result[0] = registrationId;
        result[1] = registeredEmailId;

        return result;
    }

    private void storeRegistrationId(String userEmailId) {
        int appVersion = getAppVersion();
        Log.v(Globals.TAG, "Saving regId and email Id on app version " + appVersion);

        final SharedPreferences prefs = getGcmPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        String[] result = getRegistrationId(context);
        if (result[0] != "") {
            editor.putString(Globals.CURRENT_USER_EMAIL_ID, userEmailId);
        } else {
            editor.putString(Globals.PREFS_PROPERTY_REG_ID, Globals.REGISTRATION_ID);
            editor.putString(Globals.CURRENT_USER_EMAIL_ID, userEmailId);
        }

        editor.putInt(Globals.APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        return context.getSharedPreferences(Globals.PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private int getAppVersion() {
        try {
            PackageInfo packageInfo;
            packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.v(Globals.TAG, "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, context, Globals.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.v(Globals.TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    public void sendMessage(String action, String msg) {
        Log.v(Globals.TAG, "Sender Broadcasting message");

        Intent intent = new Intent(action);
        intent.putExtra(Globals.MESSAGE, msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void sendMessage(String action, String msg, String newUserEmailId) {
        Log.v(Globals.TAG, "Sender Broadcasting message");

        if (!msg.equals(Globals.NOT_REGISTERED)) {
            //user is verified hence persist the Email ID for future login
            storeRegistrationId(newUserEmailId);
            sendMessage(action, msg);
        }

    }

}
