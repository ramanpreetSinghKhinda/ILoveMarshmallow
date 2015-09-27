package search.raman.ilovemarshmallow.Gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import search.raman.ilovemarshmallow.Activities.ClientLoginActivity;
import search.raman.ilovemarshmallow.Activities.DetailViewActivity;
import search.raman.ilovemarshmallow.R;
import search.raman.ilovemarshmallow.Utilities.Globals;

/********************************************************************************************************************************
 * Developer : Ramanpreet Singh Khinda
 * <p/>
 * This {@code IntentService} does the actual handling of the GCM message.
 * <p/>
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work.
 * <p/>
 * When the service is finished, it calls {@code completeWakefulIntent()} to release the wake lock.
 ********************************************************************************************************************************/
public class GcmIntentService extends IntentService {
    GcmActivity gcmObject;
    public static final int NOTIFICATION_ID = 1;
    private static final int DEFAULT_ALL = -1;
    private NotificationManager mNotificationManager;

    private int productRating;
    private String new_user_email_id, senderEmailId, receiverEmailId, msg_type, action, msg, asinId,
            productName, brandName, productPrice, productImageUrlString, clearNotificationTo;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        // The getMessageType() intent parameter must be the intent you receive in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            /*
            * Filter messages based on message type.Since it is likely that GCM will be
            * extended in the future with new message types, just ignore any message types you 're
            * not interested in, or that you don 't recognize.
            */

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString(), null);

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString(), null);

                // If it's a regular GCM message, do some work
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message
                String message = extras.toString();

                if (Globals.ACTION_CLEAR_NOTIFICATION.equals(extras.getString("action"))) {
                    clearNotification();
                } else if (Globals.ACTION_NOTIFICATION.equals(extras.getString("action"))) {
                    sendNotification("Received: " + message, extras);
                } else {
                    stopProgressBarIfRequired(extras);
                }
                Log.v(Globals.TAG, "Received: " + message);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /**
     * Put the message into a notification and post it.
     * This is just one simple example of what you might choose to do with
     * a GCM message.
     *
     * @param msg
     * @param bundle
     */
    public void sendNotification(String msg, Bundle bundle) {
        Log.v(Globals.TAG, "GcmIntentService Broadcasting Notification");

        if (bundle == null)
            return;

        action = bundle.getString(Globals.ACTION);

        if (action != null && (Globals.ACTION_NOTIFICATION.equals(action) || Globals.ACTION_CLEAR_NOTIFICATION.equals(action))) {
            msg_type = action.substring(action.lastIndexOf(".") + 1);

            msg = bundle.getString(Globals.MESSAGE);

            if (msg.contains(Globals.NOT_REGISTERED_2)) {
                //if we shared the product with someone who is not registered with our GCM Server
                pushNotification(msg, false);
            } else if (msg.contains(Globals.NEW_DEVICE_REGISTERED)) {
                pushNotification(msg, true);

            } else {
                senderEmailId = bundle.getString(Globals.SENDER_EMAIL_ID);
                receiverEmailId = bundle.getString(Globals.RECEIVER_EMAIL_ID);
                asinId = bundle.getString(Globals.ASIN_ID);
                productName = bundle.getString(Globals.PRODUCT_NAME);
                brandName = bundle.getString(Globals.BRAND_NAME);
                productPrice = bundle.getString(Globals.PRODUCT_PRICE);
                productImageUrlString = bundle.getString(Globals.PRODUCT_IMAGE_URL);

                productRating = 0;
                try {
                    productRating = Integer.parseInt(bundle.getString(Globals.PRODUCT_RATING));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                clearNotificationTo = senderEmailId;

                if (bundle.containsKey("notification_key") && (bundle.getString("notification_key") != null)) {
                    clearNotificationTo = bundle.getString("notification_key");
                }
                pushNotification(msg, senderEmailId, productImageUrlString, asinId, productName, brandName, productPrice, productRating, receiverEmailId, clearNotificationTo);
            }
        }
    }

    public void pushNotification(String msg, Boolean setPendingIntent) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.mipmap.fashion_app_logo);

        Intent notifIntent;
        PendingIntent pendingIntent = null;

        if (setPendingIntent) {
            notifIntent = new Intent(this, ClientLoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setLargeIcon(icon)
                        .setColor(R.color.primary)
                        .setSmallIcon(R.mipmap.ic_trending)
                        .setContentTitle("Marshmallow Notification")
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setPriority(Notification.FLAG_HIGH_PRIORITY)
                        .setDefaults(DEFAULT_ALL);

        if (setPendingIntent) {
            mBuilder.setContentIntent(pendingIntent);
        }

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    public void pushNotification(String msg, String senderEmailId, String productImageUrlString, String asinId, String productName, String brandName, String productPrice, int productRating, String receiverEmailId, String clearNotificationTo) {
        Bitmap remote_picture = null;

        NotificationCompat.BigPictureStyle notiStyle = new
                NotificationCompat.BigPictureStyle();
        notiStyle.setBigContentTitle("from : " + senderEmailId);
        notiStyle.setSummaryText(msg);

        try {
            remote_picture = BitmapFactory.decodeStream(
                    (InputStream) new URL(productImageUrlString).getContent());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the big picture to the style.
        notiStyle.bigPicture(remote_picture);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.mipmap.fashion_app_logo);

        Intent notifIntent = new Intent(this, DetailViewActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notifIntent.putExtra(Globals.ASIN_ID, asinId);
        notifIntent.putExtra(Globals.PRODUCT_NAME, productName);
        notifIntent.putExtra(Globals.BRAND_NAME, brandName);
        notifIntent.putExtra(Globals.PRODUCT_PRICE, productPrice);
        notifIntent.putExtra(Globals.PRODUCT_RATING, productRating);
        notifIntent.putExtra(Globals.CURRENT_USER_EMAIL_ID, receiverEmailId);
        notifIntent.putExtra(Globals.SEND_CLEAR_NOTIFICATION_TO, clearNotificationTo);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setLargeIcon(icon)
                        .setColor(R.color.primary)
                        .setSmallIcon(R.mipmap.ic_trending)
                        .setContentTitle("Marshmallow Notification")
                        .setContentText("a marshmallow lover shared a product with you")
                        .setAutoCancel(true)
                        .setStyle(notiStyle)
                        .setPriority(Notification.FLAG_HIGH_PRIORITY)
                        .setDefaults(DEFAULT_ALL);


        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    // Implement clearNotification()
    public void clearNotification() {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    /**
     * Stop the running progress bar and start the app if Action is REGISTER
     */
    public void stopProgressBarIfRequired(Bundle extras) {
        action = extras.getString(Globals.ACTION);
        msg_type = action.substring(action.lastIndexOf(".") + 1);
        msg = extras.getString(Globals.MESSAGE);
        new_user_email_id = extras.getString(Globals.NEW_USER_EMAIL_ID);

        gcmObject = GcmActivity.getInstance();

        if (gcmObject != null && (msg_type.equals("REGISTER"))) {
            gcmObject.sendMessage(Globals.REGISTER_UNREGISTER_LISTENER, msg, new_user_email_id);
        }
    }
}