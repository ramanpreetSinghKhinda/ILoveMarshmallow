package raman.gcm.server;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/*****************************************************************************************************************************
 * Developer : Ramanpreet Singh Khinda
 * <p/>
 * This class represents fields in the NotificationKey table
 *****************************************************************************************************************************/
@DatabaseTable(tableName = "notification_key")
public class NotificationKey {
    @DatabaseField(id = true)
    private String email_id;

    @DatabaseField
    private String notification_key;

    public NotificationKey() {
    }

    public NotificationKey(String email_id, String notification_key) {
        this.email_id = email_id;
        this.notification_key = notification_key;
    }

    public String getEmailId() {
        return email_id;
    }

    public void setEmailId(String email_id) {
        this.email_id = email_id;
    }

    public String getNotification_key() {
        return notification_key;
    }

    public void setNotification_key(String notification_key) {
        this.notification_key = notification_key;
    }

    @Override
    public String toString() {
        return email_id + " : " +  notification_key;
    }

}
