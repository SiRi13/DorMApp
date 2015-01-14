package de.hochschuletrier.dormapp;

/**
 * Created by simon on 11/16/14.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import java.util.Locale;

import de.hochschuletrier.dbconnectionlib.constants.EnumSqLite;
import de.hochschuletrier.dbconnectionlib.constants.MessageConstants;
import de.hochschuletrier.dbconnectionlib.functions.UserHandler;
import de.hochschuletrier.dbconnectionlib.helper.AuthCredentials;
import de.hochschuletrier.dbconnectionlib.messenger.MessengerService;
import de.hochschuletrier.dormapp.common.Constants;
import de.hochschuletrier.dormapp.common.Log;
import de.hochschuletrier.dormapp.dialogfragments.ItemBuyDialogFragment;
import de.hochschuletrier.dormapp.dialogfragments.RemoveDialogFragment;
import de.hochschuletrier.dormapp.fragments.BlackboardFragment;
import de.hochschuletrier.dormapp.fragments.PlaceholderFragment;

public class MainActivity extends Activity implements ActionBar.TabListener {

    private final String TAG = Constants.TAG_PREFIX + getClass().getName();

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

    public static AuthCredentials loggedIn;

    public static MainActivity getInstance() {
        return instance;
    }

    protected static MainActivity instance;

    protected static SecurePreferences secPrefs;

    public static FragmentManager getFragMngr() {
        return fragMngr;
    }

    protected static FragmentManager fragMngr;

    private Messenger mService = null;

    private Messenger reService = new Messenger(new ReplyIncomingHandler());

    private boolean mBound = false;

    public int counter;

    @Override
    protected void onStart() {
        super.onStart();
        // bind messenger service
        bindService(new Intent(this, MessengerService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initApp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        // create new securePreferences if not there
        if (secPrefs == null) {
            secPrefs = new SecurePreferences(this);
        }

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        fragMngr = getFragmentManager();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragMngr());

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

        counter = 0;

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (loggedIn != null && !loggedIn.isEmpty()) {
            UserHandler.storeCredentials(secPrefs, loggedIn);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // unbind messenger service
        unbindService(mConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
            case R.id.itemSync:
                initSync();
                break;
            case R.id.itmTest:
//                Toast.makeText(this, "No Test ATM", Toast.LENGTH_LONG).show();
//                saveToDb();
//                syncDynData();
//                showDialog(null);
                refreshTest();
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
        fragmentTransaction.remove(mSectionsPagerAdapter.getItem(tab.getPosition()));
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // EMPTY

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.LOGIN_ACTIVITY_REQUEST_CODE:
                if (resultCode == Constants.ACTIVITY_RESULT_OK) {
                    // TODO
                    initSync();
//                    this.recreate();
                }
                else if (resultCode == Constants.ACTIVITY_RESULT_ERROR) {
                    // TODO SOMETHING HAPPENED
                    // clearDataBase();
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
                Log.v(TAG, getString(R.string.wifi_connection));
            } else if (mobileConnected){
                Log.v(TAG, getString(R.string.mobile_connection));
            }
        } else {
            Log.v(TAG, getString(R.string.no_wifi_or_mobile));
        }
    }
    // END_INCLUDE(connect)


/*
* Basic Methods
* */

    private void refreshTest() {
        if (counter++ > 4) {
            recreate();
        }



/*        FragmentTransaction fragTx = fragMngr.beginTransaction();
        Fragment oFrag = new OverviewFragment();
        fragTx.replace(R.layout.fragment_main, oFrag);
        fragTx.addToBackStack(null);
        fragTx.commit();*/

//        mSectionsPagerAdapter.notifyDataSetChanged();
        /*BlackboardFragment blackboardFrag = (BlackboardFragment) mSectionsPagerAdapter.getItem(3);
        Assert.assertNotNull(blackboardFrag);

        if ( blackboardFrag == null) {
            blackboardFrag = BlackboardFragment.getInstance();
        }
        if (blackboardFrag != null) {
            blackboardFrag.bbAdapter.notifyDataSetChanged();
            blackboardFrag.bbAdapter.notifyDataSetInvalidated();
        }*/

    }

    private void syncDynData() {
        Bundle bundle = new Bundle();
        if (loggedIn == null || loggedIn.isEmpty()) {
            loggedIn = UserHandler.loggedInUser(getSecPrefs());
        }
        bundle.putParcelable(de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_CREDENTIALS, loggedIn);
        Message msg = Message.obtain(null, MessageConstants.MSG_DYN_DATA_SYNC);
        msg.setData(bundle);
        Log.d(TAG, "sendMessage( MSG_DYN_DATA_SYNC )");
        sendMessage(mService, msg);
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
            // TODO only dynamic data sync
            initSync();
        }
        mSectionsPagerAdapter.notifyDataSetChanged();
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

    private void initSync() {
        Bundle bundle = new Bundle();
        if (loggedIn == null || loggedIn.isEmpty()) {
            loggedIn = UserHandler.loggedInUser(getSecPrefs());
        }
        bundle.putParcelable(de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_CREDENTIALS, loggedIn);
        bundle.putString(EnumSqLite.KEY_UID.getName(), loggedIn.getUid());
        bundle.putString(EnumSqLite.KEY_EMAIL.getName(), loggedIn.getEmail());
        bundle.putString(EnumSqLite.KEY_PASSWORD.getName(), loggedIn.getPassword());
        Message msg = Message.obtain(null, MessageConstants.MSG_INIT_SYNC);
        msg.setData(bundle);
        Log.d(TAG, "sendMessage( MSG_INIT_SYNC )");
        sendMessage(mService, msg);
    }

    private void sendMessage(Messenger _service, Message _msg) {
        try {
            if (_service != null) {
                _service.send(_msg);
            }
        } catch (RemoteException rex) {
            Log.e(TAG, "RemoteException: " + rex.getLocalizedMessage());
            rex.printStackTrace();
        }
    }

    public void sendMessage(Message _msg) {
        if (mBound) {
            if (loggedIn != null && !loggedIn.isEmpty()) {
                Bundle _bundle;

                if (_msg.peekData() != null) {
                    _bundle = _msg.getData();
                    _bundle.putString(EnumSqLite.KEY_EMAIL.getName(), loggedIn.getEmail());
                    _bundle.putString(EnumSqLite.KEY_UID.getName(), loggedIn.getUid());
                    _bundle.putString(EnumSqLite.KEY_PASSWORD.getName(), loggedIn.getPassword());
                    _msg.setData(_bundle);
                }
                else {
                    _msg.setData(loggedIn.getAuthBundle());
                }
            }
            sendMessage(mService, _msg);
        }
    }

    private void sendMessage(int _msgCommand) {
        if (mBound) {
            Message msg = Message.obtain(null, _msgCommand);
            if (loggedIn != null && !loggedIn.isEmpty()) {
                msg.setData(loggedIn.getAuthBundle());
            }
            sendMessage(mService, msg);
        }
    }

    public void showDialog(final DialogFragment newFrag) {
        if (fragMngr == null) {
            fragMngr = getFragmentManager();
        }
        RemoveDialogFragment newFragement = new RemoveDialogFragment().newInstance("Dialog to fire missiles...", "Fire missiles?", "FIRE!");
        newFragement.show(fragMngr, ItemBuyDialogFragment.FRAGMENT_TAG);
    }

/*
* Getter and Setter for MainActivity
* */

    public static SecurePreferences getSecPrefs() {
        return secPrefs;
    }

/*
* Inner Classes For MainActiviy
* */
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment
            Fragment frag = PlaceholderFragment.newInstance(position);

            return frag;
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 4;
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

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            Log.d(TAG, "service bound");
            mService = new Messenger(binder);
            mBound = true;
            Message msg = Message.obtain(null, MessageConstants.MSG_REBIND);
            msg.replyTo = reService;
            sendMessage(mService, msg);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "service unbound");
            Message msg = Message.obtain(null, MessageConstants.MSG_UNREBIND);
            sendMessage(mService, msg);
            mService = null;
            mBound = false;
        }
    };

    public class ReplyService extends Service {

        private final String TAG = Constants.TAG_PREFIX + getClass().getName();

        @Override
        public IBinder onBind(Intent intent) {
            Log.d(TAG, "ReplyService binding");
            return reService.getBinder();
        }
    }

    public class ReplyIncomingHandler extends Handler {
        public static final String TAG = Constants.TAG_PREFIX + "ReplyIncomingHandler";

        @Override
        public void handleMessage(Message msg) {
            // TODO
            switch (msg.what) {
                case MessageConstants.MSG_SAY_HELLO:
                    Log.d(TAG, "Reply from SyncService via Message");
                    break;
                case MessageConstants.MSG_INIT_SYNC_REMOTE_DONE:
                    Log.d(TAG, "Reply from SyncService via Message RemoteSync done");
                    break;
                case MessageConstants.MSG_BLACKBOARD_SYNC_REMOTE_DONE:
                    Log.d(TAG, "Reply from SyncService: RemoteBlackboardSync done");
                    break;
                case MessageConstants.MSG_BLACKBOARD_SYNC_DONE:
                    Log.d(TAG, "Reply from SyncService: BlackboardSync done!");
                    BlackboardFragment bbFrag = BlackboardFragment.getInstance();
                    if (bbFrag != null) {
                        bbFrag.onResume();
                        ((EditText) bbFrag.getView().findViewById(R.id.editTextBlackboardNewMessage)).setText("");
                        bbFrag.bbAdapter.notifyDataSetChanged();
                    }
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    break;
                case MessageConstants.MSG_INIT_SYNC_LOCAL_DONE:
                    Log.d(TAG, "Reply from SyncService via Message LocalSync done");
                    break;
                case MessageConstants.MSG_INIT_SYNC_DONE:
//                    MainActivity.instance.recreate();
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Reply from SyncService via Message all done");
                    break;
                case MessageConstants.MSG_COMMIT_LOCAL_DONE:
                    int arg1 = msg.arg1;
                    Message reMsg;
                    switch (arg1) {
                        case MessageConstants.MSG_COMMIT_SHOPPING_LIST_ITEM_ADD:
                        case MessageConstants.MSG_COMMIT_SHOPPING_LIST_ITEM_BOUGHT:
                        case MessageConstants.MSG_COMMIT_SHOPPING_LIST_ITEM_REMOVE:
                            MainActivity.this.sendMessage(MessageConstants.MSG_DYN_DATA_SYNC);
                            break;
                        case MessageConstants.MSG_COMMIT_BLACKBOARD_EDIT:
                        case MessageConstants.MSG_COMMIT_BLACKBOARD_REMOVE:
                        case MessageConstants.MSG_COMMIT_BLACKBOARD_ADD:
                            MainActivity.this.sendMessage(MessageConstants.MSG_BLACKBOARD_SYNC);
                            break;
                        case MessageConstants.MSG_COMMIT_CHORE_DONE:
                            MainActivity.this.sendMessage(MessageConstants.MSG_CHORE_PLAN_SYNC);
                            break;
                        default:
                            MainActivity.this.sendMessage(MessageConstants.MSG_DYN_DATA_SYNC);
                    }
                    break;
                case MessageConstants.MSG_ERROR_LOCAL_SYNC:
                    Toast.makeText(getApplicationContext(),
                        ((Bundle) msg.obj).getString(de.hochschuletrier.dbconnectionlib.constants.Constants.BUNDLE_ERROR),
                        Toast.LENGTH_LONG).show();
                    break;
                case MessageConstants.MSG_PROGRESS_LOCAL_SYNC:
                    Toast.makeText(getApplicationContext(),
                            ((Bundle) msg.obj).getString(de.hochschuletrier.dbconnectionlib.constants.Constants.BUNDLE_PROGRESS),
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "MSG_PROGRESS_LOCAL_SYNC");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
