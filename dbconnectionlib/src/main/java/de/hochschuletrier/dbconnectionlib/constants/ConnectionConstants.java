package de.hochschuletrier.dbconnectionlib.constants;

/**
 * Created by simon on 11/16/14.
 */
public class ConnectionConstants {

    public static final String SYNC_URL = "http://app1.raschel.org/tp/php/db.php";
    public static final String SYNC_TAG = "sync";

    public static final String WRITE_URL = SYNC_URL;
    public static final String WRITE_TAG = "write";


    // URL of the PHP API
    public static final String LOGIN_URL = "http://app1.raschel.org/tp/php/login.php";
    public static final String LOGIN_TAG = "login";

    /*public static final String REGISTER_URL = loginUrl;
    public static final String REGISTER_TAG = "register";*/

    public static final String FORGOT_PASSWORD_URL = LOGIN_URL;
    public static final String FORGOT_PASSWORD_TAG = "forpass";

    public static final String CHANGE_PASSWORD_URL = LOGIN_URL;
    public static final String CHANGE_PASSWORD_TAG = "chgpass";

}
