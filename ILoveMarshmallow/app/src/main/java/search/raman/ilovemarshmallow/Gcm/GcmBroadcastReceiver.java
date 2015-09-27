package search.raman.ilovemarshmallow.Gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;


/*************************************************************************************************************
 * Developer : Ramanpreet Singh Khinda
 * <p/>
 * GCM SDK which if integrated with any app can help it leverage the GCM Push Notification functionality to go live for Android
 * <p/>
 * GcmBroadcastReceiver :
 * This {@code WakefulBroadcastReceiver} takes care of creating and managing a partial wake lock for your app.
 * <p/>
 * It passes off the work of processing the GCM message to an {@code IntentService},
 * while ensuring that the device does not go back to sleep in the transition.
 * <p/>
 * The {@code IntentService} calls {@code GcmBroadcastReceiver.completeWakefulIntent()}
 * when it is ready to release the wake lock.
 **************************************************************************************************************/
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
