package zm.co.hobbiton.mobicom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import zm.co.hobbiton.mobicom.activities.PayCashActivity;
import zm.co.hobbiton.mobicom.fragments.HomeFragment;
import zm.co.hobbiton.mobicom.fragments.ReportsFragment;
import zm.co.hobbiton.mobicom.fragments.TransactionHistoryFragment;
import zm.co.hobbiton.mobicom.services.MessageListener;
import zm.co.hobbiton.mobicom.services.MessageReceiver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.nbbse.mobiprint3.Printer;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MessageListener {
    public static final String TAG = "MainActivity";
    private static String API_URL;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;
    private TextView mAccountBalanceTxt, mFirstNameTxt, mLastNameTxt, mPhoneNumberTxt, mLogoutTxt;
    private Printer mPrinter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MessageReceiver.bindListener(this);
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        Intent intent = getIntent();
        int itemIndex = intent.getIntExtra(PayCashActivity.EXTRA_CASH_PAYMENT, 0);
        mPrinter = Printer.getInstance();
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_AGENT, MODE_PRIVATE);
        String agentNumber = prefs.getString("number", "undefined");
        checkAuthStatus(agentNumber);

        API_URL = "https://mobicom-pilot.herokuapp.com/agent-kyc/float-balance/" + agentNumber;
        initializeViews();
        toggleDrawer();
        Log.d(TAG, "onCreate: item index: "+itemIndex);
        initializeDefaultFragment(savedInstanceState, itemIndex);
    }

    private void checkAuthStatus(String agentNumber) {
        if (agentNumber.equals("undefined")) {
            onAgentLogout();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getFloatBalance();
    }

    private void getFloatBalance() {
        mProgressDialog.show();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressDialog.dismiss();
                        try {
                            JSONArray objArray = new JSONArray(response);
                            JSONObject obj = objArray.getJSONObject(0);
                            mToolbar.setSubtitle("K" +obj.optString("balance"));
                            mAccountBalanceTxt.setText(String.format("K%s", obj.optString("balance")));
                            mFirstNameTxt.setText(obj.optString("first_name"));
                            mLastNameTxt.setText(obj.optString("last_name"));
                            mPhoneNumberTxt.setText(String.format("+%s", obj.optString("MSISDN")));
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Balance Unavailable",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     * Initialize all widgets
     */
    private void initializeViews() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.app_name);
        // mToolbar.setSubtitle("Loading...");
        setSupportActionBar(mToolbar);
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mNavigationView = findViewById(R.id.navView);
        mNavigationView.setNavigationItemSelectedListener(this);

        // Set header views
        View header = mNavigationView.getHeaderView(0);
        mAccountBalanceTxt = header.findViewById(R.id.txtAccountBalance);
        mFirstNameTxt = header.findViewById(R.id.txtFirstName);
        mLastNameTxt = header.findViewById(R.id.txtLastName);
        mPhoneNumberTxt = header.findViewById(R.id.txtPhoneNumber);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

        mLogoutTxt = findViewById(R.id.logout);
        mLogoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onAgentLogout();
            }
        });
    }

    public void onAgentLogout() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Checks if the savedInstanceState is null - onCreate() is ran
     * If so, display fragment of navigation drawer menu at position itemIndex and
     * set checked status as true
     *
     * @param savedInstanceState
     * @param itemIndex
     */
    private void initializeDefaultFragment(Bundle savedInstanceState, int itemIndex) {
        if (savedInstanceState == null) {
            MenuItem menuItem = mNavigationView.getMenu().getItem(itemIndex).setChecked(true);
            onNavigationItemSelected(menuItem);
        }
    }

    /**
     * Creates an instance of the ActionBarDrawerToggle class:
     * 1) Handles opening and closing the navigation drawer
     * 2) Creates a hamburger icon in the toolbar
     * 3) Attaches listener to open/close drawer on icon clicked and rotates the icon
     */
    private void toggleDrawer() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.homeItem:
                getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new HomeFragment()).commit();
                closeDrawer();
                break;
//            case R.id.floatManItem:
//                getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new FloatManagementFragment()).commit();
//                closeDrawer();
//                break;
            case R.id.reportsItem:
                getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new ReportsFragment()).commit();
                closeDrawer();
                break;
            case R.id.transactionHistItem:
                getSupportFragmentManager().beginTransaction().replace(R.id.flContent, new TransactionHistoryFragment()).commit();
                closeDrawer();
                break;
        }
        deSelectCheckedState();
        menuItem.setChecked(true);
        return true;
    }

    /**
     * Checks if the navigation drawer is open - if so, close it
     */
    private void closeDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void deSelectCheckedState() {
        int noOfItems = mNavigationView.getMenu().size();
        for (int i = 0; i < noOfItems; i++) {
            mNavigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    public void messageReceived(final String message) {
        new AlertDialog.Builder(this)
                .setTitle("Cash Payment")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Print", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Print receipt
                        mPrinter.printText("********************************");
                        mPrinter.printText("******** Hobbiton Tech *********");
                        mPrinter.printText("********************************");
                        mPrinter.printText("Cash Payment",2);
                        mPrinter.printText(message);
                        mPrinter.printEndLine();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setIcon(R.drawable.ic_monetization_white)
                .show();
    }
}