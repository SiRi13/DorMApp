package de.hochschuletrier.dormapp;

/**
 * Created by simon on 11/16/14.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hochschuletrier.dbconnectionlib.constants.ConnectionConstants;
import de.hochschuletrier.dbconnectionlib.functions.UserHandler;
import de.hochschuletrier.dbconnectionlib.helper.AuthCredentials;
import de.hochschuletrier.dormapp.common.Constants;
import de.hochschuletrier.dormapp.common.InitAppSync;
import de.hochschuletrier.dormapp.common.Log;
import de.hochschuletrier.dormapp.common.LogFragment;

public class MainActivity extends Activity implements ActionBar.TabListener {

    public static final String TAG_PREFIX = "de.hochschuletrier.dormapp.";
    public static final String TAG = TAG_PREFIX + "MainActivity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a {@link android.support.v13.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this
     * becomes too memory intensive, it may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /**
     * Reference to the fragment showing events, so we can clear it with a button
     * as necessary.
     */
    private LogFragment mLogFragment;

    public static AuthCredentials loggedIn;

    public static SecurePreferences getSecPrefs() {
        return secPrefs;
    }

    protected static SecurePreferences secPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create new securePreferences if not there
        if (secPrefs == null) {
            secPrefs = new SecurePreferences(this);
        }

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }

        initApp();
    }

    private void initApp() {
        // get logged in user creds if there are any
        MainActivity.loggedIn =  UserHandler.loggedInUser(getSecPrefs());

        // check if s/o is logged in ...
        if (loggedIn == null || loggedIn.isEmpty()) {
            // ... if not goto login fragment
            initLogin();
        }
        else {
            // ... sync for init ( e.g. who, what chores or new entries )
            InitAppSync initApp = new InitAppSync(this, getSecPrefs(), loggedIn);
            // create params to put as post-request payload
            List<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>();
            postParams.add(new BasicNameValuePair("tag", ConnectionConstants.SYNC_TAG));
            postParams.add(new BasicNameValuePair("email", loggedIn.getEmail()));
            postParams.add(new BasicNameValuePair("password", loggedIn.getPassword()));
            postParams.add(new BasicNameValuePair("table", de.hochschuletrier.dbconnectionlib.constants.Constants.VIEW_INIT_APP));

            initApp.execute(postParams);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void initLogin() {
        checkNetworkConnection();
        if (wifiConnected || mobileConnected) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, Constants.LOGIN_ACTIVITY_REQUEST_CODE);
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Leider keine aktive Internetverbindung gefunden. Bitte später noch mal versuchen",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.itmLogin:
                initLogin();
                break;
//            case R.id.itmSettings:
//                intent = new Intent(this, R.layout.fra);
//                startActivity(intent);
//                break;
            case R.id.itemSync:
                initApp();
                break;
            default:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // EMPTY
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // EMPTY
    }

//    public void onBlackboardEditItem(final String editMsg) {
//        //TODO
//        final Intent blackboardEditMsgIntent = new Intent(this, BlackboardFragmentActivity.class);
//        blackboardEditMsgIntent.putExtra(BlackboardFragmentActivity.BLACKBOARD_EDIT_TEXT, editMsg);
//        startActivityForResult(blackboardEditMsgIntent, Constants.BLACKBOARD_ACTIVITY_REQUEST_CODE);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.BLACKBOARD_ACTIVITY_REQUEST_CODE:
                if (resultCode == Constants.ACTIVITY_RESULT_OK) {
                    // TODO
                }
                else if (resultCode == Constants.ACTIVITY_RESULT_ERROR) {
                    // TODO SOMETHING HAPPENED
                }
                break;

            case Constants.LOGIN_ACTIVITY_REQUEST_CODE:
                if (resultCode == Constants.ACTIVITY_RESULT_OK) {
                    // TODO
                    this.recreate();
                }
                else if (resultCode == Constants.ACTIVITY_RESULT_ERROR) {
                    // TODO SOMETHING HAPPENED
                }
                break;

            default:
                break;
        }
    }

    // BEGIN_INCLUDE(connect)
    /**
     * Check whether the device is connected, and if so, whether the connection
     * is wifi or mobile (it could be something else).
     */
    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;

    private void checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if(wifiConnected) {
                Log.i(TAG, getString(R.string.wifi_connection));
            } else if (mobileConnected){
                Log.i(TAG, getString(R.string.mobile_connection));
            }
        } else {
            Log.i(TAG, getString(R.string.no_wifi_or_mobile));
        }
    }
    // END_INCLUDE(connect)


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class
            // below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_sectionUebersicht).toUpperCase(l);
                case 1:
                    return getString(R.string.title_sectionPutzplan).toUpperCase(l);
                case 2:
                    return getString(R.string.title_sectionEinkaufslist).toUpperCase(l);
                case 3:
                    return getString(R.string.title_sectionBlackboard).toUpperCase(l);
                case 4:
                    return getString(R.string.title_sectionKalender).toUpperCase(l);

            }
            return null;
        }
    }
}