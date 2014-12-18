package de.hochschuletrier.dbconnectionlib.helper;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import de.hochschuletrier.dbconnectionlib.constants.EnumSqLite;

/**
 * Created by simon on 11/16/14.
 */
public class AuthCredentials implements Parcelable{

    private String uid;

    private String password;

    private String firstname, lastname;
    private String email;
    private String crea;

    public AuthCredentials(String uid, String email, final String password) {
        this.uid = uid;
        this.email = email;
        this.password = password;
    }

    private AuthCredentials(Parcel in) {
        Bundle authBundle = in.readBundle();
        setUid(authBundle.getString(EnumSqLite.KEY_UID.getName()));
        setPassword(authBundle.getString(EnumSqLite.KEY_PASSWORD.getName()));
        setEmail(authBundle.getString(EnumSqLite.KEY_EMAIL.getName()));
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getFirstname()
    {
        return firstname;
    }

    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }

    public String getLastname()
    {
        return lastname;
    }

    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getCrea()
    {
        return crea;
    }

    public void setCrea(String crea)
    {
        this.crea = crea;
    }

    public Boolean isEmpty() {

        return ((getEmail() != null & getEmail().isEmpty()) && (getPassword() != null & getPassword().isEmpty()) && (getUid() != null & getUid().isEmpty()));
    }

    public Bundle getAuthBundle() {
        Bundle retBund = new Bundle();
        retBund.putString(EnumSqLite.KEY_UID.getName(), getUid());
        retBund.putString(EnumSqLite.KEY_EMAIL.getName(), getEmail());
        retBund.putString(EnumSqLite.KEY_PASSWORD.getName(), getPassword());
        return retBund;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBundle(getAuthBundle());
    }

    public static final Parcelable.Creator<AuthCredentials> CREATOR
            = new Parcelable.Creator<AuthCredentials>() {
        public AuthCredentials createFromParcel(Parcel in) {
            return new AuthCredentials(in);
        }

        public AuthCredentials[] newArray(int size) {
            return new AuthCredentials[size];
        }
    };
}
