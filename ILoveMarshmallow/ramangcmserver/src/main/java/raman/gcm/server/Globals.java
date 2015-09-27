package raman.gcm.server;

/**
 * **************************************************************************************************************************
 * Developer : Ramanpreet Singh Khinda
 * <p/>
 * This class contains all the Global fields
 ***************************************************************************************************************************/
public class Globals {
    public static final String GCM_SENDER_ID = "170703016108";
    public static final String GCM_SERVER = "gcm.googleapis.com";
    public static final String GCM_SERVER_KEY = "AIzaSyBxiJ1BErodrR2ovE3CKeCmBI13jBo-fak";
    public static final String GCM_ELEMENT_NAME = "gcm";
    public static final String GCM_NAMESPACE = "google:mobile:data";

    public static final int GCM_PORT = 5235;
    public static final long GCM_TIME_TO_LIVE = 60L * 60L * 24L * 7L * 4L; // 4 Weeks

    //intents extra values
    public static final String BUNDLE = "bundle";
    public static final String MESSAGE = "message";
    public static final String MESSAGE_TYPE = "msg_type";
    public static final String SENDER_EMAIL_ID = "sender_email_id";
    public static final String RECEIVER_EMAIL_ID = "receiver_email_id";
    public static final String CURRENT_USER_EMAIL_ID = "current_user_email_id";
    public static final String NEW_USER_EMAIL_ID = "new_user_email_id";
    public static final String CURRENT_USER_DEVICE_ID = "user_device_id";
    public static final String NEW_USER_DEVICE_ID = "new_user_device_id";

    //actions for GCM
    public static final String ACTION = "action";
    public static final String ACTION_REGISTER = "search.raman.ilovemarshmallow.Gcm.REGISTER";
    public static final String ACTION_NOTIFICATION = "search.raman.ilovemarshmallow.Gcm.NOTIFICATION";
    public static final String ACTION_BROADCAST = "search.raman.ilovemarshmallow.Gcm.BROADCAST";
    public static final String ACTION_ECHO = "search.raman.ilovemarshmallow.Gcm.ECHO";
    public static final String ACTION_CLEAR_NOTIFICATION = "search.raman.ilovemarshmallow.Gcm.CLEAR_NOTIFICATION";
}

