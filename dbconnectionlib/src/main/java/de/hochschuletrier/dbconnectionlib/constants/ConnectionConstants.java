package de.hochschuletrier.dbconnectionlib.constants;

/**
 * Created by simon on 11/16/14.
 */
public class ConnectionConstants {

    public static final String SYNC_URL = "http://test.app1.raschel.org/php/db.php";
    public static final String SYNC_TAG = "sync";

    public static final String WRITE_URL = SYNC_URL;
    public static final String WRITE_TAG = "write";

    public static final String COMMIT_CHORE_DONE_TAG = "commitChoreDone";
    public static final String COMMIT_CHORE_STEP_DONE_TAG = "commitChoreStepDone";

    public static final String COMMIT_BLACKBOARD_MESSAGE_REMOVE_TAG = "commitBlackboardMessageRemove";
    public static final String COMMIT_BLACKBOARD_MESSAGE_ADD_TAG = "commitBlackboardMessageAdd";
    public static final String COMMIT_BLACKBOARD_MESSAGE_EDIT_TAG = "commitBlackboardMessageEdit";

    public static final String COMMIT_GROCERY_ITEM_ADD_TAG = "commitGroceryItemAdd";

    public static final String COMMIT_SHOPPING_LIST_ITEM_BOUGHT_TAG = "commitShoppingListItemBought";
    public static final String COMMIT_SHOPPING_LIST_ITEM_ADD_TAG = "commitShoppingListItemAdd";
    public static final String COMMIT_SHOPPING_LIST_ITEM_REMOVE_TAG = "commitShoppingListItemRemove";

    // URL of the PHP API
    public static final String LOGIN_URL = "http://test.app1.raschel.org/php/login.php";
    public static final String LOGIN_TAG = "login";

    /*public static final String REGISTER_URL = loginUrl;
    public static final String REGISTER_TAG = "register";*/

    public static final String FORGOT_PASSWORD_URL = LOGIN_URL;
    public static final String FORGOT_PASSWORD_TAG = "forpass";

    public static final String CHANGE_PASSWORD_URL = LOGIN_URL;
    public static final String CHANGE_PASSWORD_TAG = "chgpass";

}
