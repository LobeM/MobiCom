package zm.co.hobbiton.mobicom.activities;

import androidx.appcompat.app.AppCompatActivity;
import zm.co.hobbiton.mobicom.LoginActivity;
import zm.co.hobbiton.mobicom.R;
import zm.co.hobbiton.mobicom.services.MessageListener;
import zm.co.hobbiton.mobicom.services.MessageReceiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BuyAirtimeActivity extends AppCompatActivity implements View.OnClickListener, MessageListener {
    private static final String TAG = "BuyAirtime";
    private static final String API_URL = "https://mobicom-pilot.herokuapp.com/service-request/airtime-request";
    private int mServiceID, mLogo;
    private ImageView mLogoImg;
    private EditText mPhoneInput, mAmountInput;
    private Button mBuyAirtimeBtn;
    private ProgressDialog mProgressDialog;

    public String mAgentNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        setContentView(R.layout.activity_buy_airtime);
        MessageReceiver.bindListener(this);
        Intent intent = getIntent();
        mServiceID = intent.getIntExtra(AirtimeActivity.EXTRA_ID, 0);
        mLogo = intent.getIntExtra(AirtimeActivity.EXTRA_LOGO, R.drawable.ic_launcher_foreground);
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_AGENT, MODE_PRIVATE);
        mAgentNumber = prefs.getString("number", "undefined");
        checkAuthStatus(mAgentNumber);
        initializeViews();
        hideKeyboard(this);
    }

    private void checkAuthStatus(String agentNumber) {
        if (agentNumber.equals("undefined")) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @SuppressLint("NewApi")
    private void initializeViews() {
        mLogoImg = findViewById(R.id.imgAirtimeLogo);
        mLogoImg.setImageResource(mLogo);
        mPhoneInput = findViewById(R.id.inputCustomerNumber);
        mPhoneInput.setShowSoftInputOnFocus(false);
        mAmountInput = findViewById(R.id.inputAmount);
        mAmountInput.setShowSoftInputOnFocus(false);
        mBuyAirtimeBtn = findViewById(R.id.btnBuyAirtime);
        mBuyAirtimeBtn.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Processing");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        final String phoneNumber = mPhoneInput.getText().toString();
        final String amount= mAmountInput.getText().toString();
        if (mServiceID != 0) {
            if (phoneNumber.length() == 0) {
                mPhoneInput.requestFocus();
                mPhoneInput.setError("Phone number required");
            } else if (amount.length() == 0) {
                mAmountInput.requestFocus();
                mAmountInput.setError("Amount required");
            } else if (phoneNumber.matches("[a-zA-Z ]+")){
                mPhoneInput.requestFocus();
                mPhoneInput.setError("Enter numbers only");
            } else if (amount.matches("[a-zA-Z ]+")){
                mAmountInput.requestFocus();
                mAmountInput.setError("Enter numbers only");
            } else {
                buyAirtime( "26" + phoneNumber, amount);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void buyAirtime(final String phoneNumber, final String amount) {
        mProgressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest request = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: ");
                mProgressDialog.dismiss();

                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                    if (obj.getJSONObject("status").optString("description").equals("Success")) {
                        Toast.makeText(getApplicationContext(), "Purchase Successful", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Insufficient Funds", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                mPhoneInput.setText(null);
                mAmountInput.setText(null);
            }
        }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Service Unavailable",Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("customer_msisdn", phoneNumber);
                map.put("amount", amount);
                map.put("serviceId", String.valueOf(mServiceID));
                map.put("agent_msisdn", mAgentNumber);
                return map;
            }
        };
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(request);
    }

    @Override
    public void messageReceived(String message) {

    }
}