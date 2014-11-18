package de.hochschuletrier.dbconnectionlib.helper;

/**
 * Created by simon on 11/16/14.
 */
public class AuthCredentials {

    private String uid;

    private String uname;

    private String password;

    private String firstname, lastname;
    private String email;
    private String crea;

    public AuthCredentials(String uid, String uname, final String password) {
        this.uid = uid;
        this.uname = uname;
        this.password = password;
    }

    public String getUname()
    {
        return uname;
    }

    public void setUname(String uname)
    {
        this.uname = uname;
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

}
