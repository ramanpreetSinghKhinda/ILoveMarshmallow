package raman.gcm.server;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/*****************************************************************************************************************************
 * Developer : Ramanpreet Singh Khinda
 * <p/>
 * This class represents fields in the User table
 *****************************************************************************************************************************/
@DatabaseTable(tableName = "users")
public class User {
    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private int userId;

    @DatabaseField
    private String email_id;

    @DatabaseField
    private String device_id;

    @DatabaseField
    private String gcm_id;

    public User() {
    }

    public User(String email_id, String device_id, String gcm_id) {
        this.email_id = email_id;
        this.device_id = device_id;
        this.gcm_id = gcm_id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmailId() {
        return email_id;
    }

    public void setEmailId(String email_id) {
        this.email_id = email_id;
    }

    public String getDeviceId() {
        return device_id;
    }

    public void setDeviceId(String device_id) {
        this.device_id = device_id;
    }

    public String getGcmId() {
        return gcm_id;
    }

    public void setGcmId(String gcm_id) {
        this.gcm_id = gcm_id;
    }

    @Override
    public String toString() {
        return userId + " : " + email_id + " : " + device_id + " : " + gcm_id;
    }
}
