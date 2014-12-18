package de.hochschuletrier.dormapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import de.hochschuletrier.dbconnectionlib.constants.Constants;
import de.hochschuletrier.dbconnectionlib.constants.EnumSqLite;
import de.hochschuletrier.dbconnectionlib.functions.UserHandler;
import de.hochschuletrier.dbconnectionlib.helper.AuthCredentials;
import de.hochschuletrier.dormapp.common.Log;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

    public static final String TAG = Constants.TAG_PREFIX + "LoginActivity";
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     *
     *  
     *    private static final String[] DUMMY_CREDENTIALS = new String[]
     *         { "foo@example.com:hello", "bar@example.com:world" };
     */

    /**
     * The default email to populate the email field with.
     */
    public static final String EXTRA_EMAIL = Constants.TAG_PREFIX + "LoginActivit.extra.EMAIL";
    private static final String EXTRA_SUCCESS = Constants.TAG_PREFIX + "LoginActivit.extra.SUCCESS";
    private static final String EXTRA_ERROR = Constants.TAG_PREFIX + "LoginActivit.extra.ERROR";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // Values for email and password at the time of the login attempt.
    private String mEmail;

    private String mPassword;

    // UI references.
    private EditText mEmailView;

    private EditText mPasswordView;

    private View mLoginFormView;

    private View mLoginStatusView;

    private TextView mLoginStatusMessageView;

    private Intent resultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setText(mEmail);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        resultIntent = getIntent();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        else if (!mEmail.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        }
        else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Boolean, Boolean> {

        public ProcessLogin procLogin;

        @Override
        protected void onPreExecute() {
            // attempt authentication against a network service.
            procLogin = new ProcessLogin();
            procLogin.execute();

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            while (!procLogin.isCancelled()) {
                if (procLogin.getStatus() == AsyncTask.Status.FINISHED) {
                    return true;
                }
            }

            publishProgress(false);
            return false;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if (!values[0]) {
                // show toast to register new account here.
                Toast.makeText(getApplicationContext(),
                        "Zum Registrieren bitte mit dem Verwalter der WG Kontakt aufnehmen.",
                        Toast.LENGTH_LONG).show();
                resultIntent.putExtra(EXTRA_ERROR, "Zum Registrieren bitte mit dem Verwalter der WG Kontakt aufnehmen.");
                setResult(de.hochschuletrier.dormapp.common.Constants.ACTIVITY_RESULT_ERROR, resultIntent);
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                resultIntent.putExtra(EXTRA_SUCCESS, true);
                setResult(de.hochschuletrier.dormapp.common.Constants.ACTIVITY_RESULT_OK, resultIntent);
                finish();
            }
            else {
                resultIntent.putExtra(EXTRA_ERROR, "Fehler beim Anmelden... Bitte noch mal versuchen.");
                setResult(de.hochschuletrier.dormapp.common.Constants.ACTIVITY_RESULT_ERROR, resultIntent);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Async Task to get and send data to My Sql database through JSON response.
     **/
    public class ProcessLogin extends AsyncTask<String, Boolean, JSONObject> {
//        private ProgressDialog pDialog;
        private UserHandler userFunction = new UserHandler();
        
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject json = userFunction.loginUser(mEmail, mPassword);

            try {
                // simulate long server response time
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                Log.e(TAG, "InterruptedException:" + ie.getLocalizedMessage());
                ie.printStackTrace();
            }

            return json;
        }
        
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json != null && json.getString(Constants.JSON_SUCCESS) != null) {
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    String res = json.getString(Constants.JSON_SUCCESS);
                    if (Integer.parseInt(res) == 1) {

                        JSONObject json_user = json.getJSONObject("user");
                        
                        AuthCredentials creds = new AuthCredentials(json_user.getString(EnumSqLite.KEY_UID.getName()),
                                                        mEmail, mPassword);

                        userFunction.storeCredentials(MainActivity.getSecPrefs(), creds);

                        json_user = new JSONObject();
                        creds = new AuthCredentials("", "", "");
                        mPassword = "";
                        
                    }
                    else {
                        resultIntent.putExtra(Constants.LOGIN_RESULT, Boolean.FALSE);
                        resultIntent.putExtra(Constants.LOGIN_MESSAGE, "Something went pretty wrong :O I am very sorry!");
                        this.cancel(true);
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                setResult(de.hochschuletrier.dormapp.common.Constants.ACTIVITY_RESULT_ERROR, resultIntent);
            }
        }
    }
}
