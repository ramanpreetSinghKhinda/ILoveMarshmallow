package search.raman.ilovemarshmallow.Utilities;

/********************************************************************************************************************************
 * Developer : Ramanpreet Singh Khinda
 * <p/>
 * This class is Gloabal class for declaring all final and static values
 ********************************************************************************************************************************/
public class Globals {
    public static final String TAG = "Raman_Zappos";

    public static final String ROMANTIC_FONT = "fonts/romantic.ttf";

    public static final String ASIN_ID = "asin_id";
    public static final String PRODUCT_NAME = "product_name";
    public static final String PRODUCT_IMAGE_URL = "product_url";
    public static final String BRAND_NAME = "brand_name";
    public static final String PRODUCT_PRICE = "product_price";
    public static final String PRODUCT_RATING = "product_rating";

    public static final String SEARCH_URL = "https://zappos.amazon.com/mobileapi/v1/search?term=";
    public static final String PRODUCT_URL = "https://zappos.amazon.com/mobileapi/v1/product/asin/";

    public static final String GCM_SENDER_ID = "170703016108";
    public static final long GCM_TIME_TO_LIVE = 60L * 60L * 24L * 7L * 4L; // 4 Weeks
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String PREFS_NAME = "Raman_ILoveMarshmallows_Gcm";
    public static final String PREFS_PROPERTY_REG_ID = "registration_id";
    public static final String APP_VERSION = "appVersion";

    public static String REGISTRATION_ID;

    public static String ALREADY_REGISTERED = "You are already a registered user";
    public static String NOT_REGISTERED = "could not be registered with GCM";
    public static String NOT_REGISTERED_2 = "not registered with GCM";
    public static String NEW_DEVICE_REGISTERED = "A new device has been registered to Marshmallow lovers with your email id";

    public static String GOOGLE_PLAY_SERVICE_NOT_VALID = "No valid Google Play Services APK found";
    public static String EMAIL_NOT_VALID = "Email Id is not valid";
    public static String SENT_REGISTRATION_REQUEST = "Sent Registration request server";

    public static final String REGISTER_UNREGISTER_LISTENER = "register_unregister_listener";
    public static final String GCM_NOTIFICATION_BROADCAST_LISTENER = "gcm_notification_broadcast_listener";

    // intents extra values
    public static final String BUNDLE = "bundle";
    public static final String MESSAGE = "message";
    public static final String MESSAGE_TYPE = "msg_type";
    public static final String SENDER_EMAIL_ID = "sender_email_id";
    public static final String RECEIVER_EMAIL_ID = "receiver_email_id";

    public static final String CURRENT_USER_EMAIL_ID = "current_user_email_id";
    public static final String CURRENT_USER_DEVICE_ID = "current_user_device_id";

    public static final String NEW_USER_EMAIL_ID = "new_user_email_id";
    public static final String NEW_USER_DEVICE_ID = "new_user_device_id";

    public static final String SEND_CLEAR_NOTIFICATION_TO = "send_clear_notification_to";

    // message type
    public static final String TYPE_NOTIFICATION = "Notification";

    // actions for GCM
    public static final String ACTION = "action";
    public static final String ACTION_REGISTER = "search.raman.ilovemarshmallow.Gcm.REGISTER";
    public static final String ACTION_NOTIFICATION = "search.raman.ilovemarshmallow.Gcm.NOTIFICATION";
    public static final String ACTION_BROADCAST = "search.raman.ilovemarshmallow.Gcm.BROADCAST";
    public static final String ACTION_ECHO = "search.raman.ilovemarshmallow.Gcm.ECHO";
    public static final String ACTION_CLEAR_NOTIFICATION = "search.raman.ilovemarshmallow.Gcm.CLEAR_NOTIFICATION";
}
