package zm.co.hobbiton.mobicom;


import androidx.appcompat.app.AppCompatActivity;
import zm.co.hobbiton.mobicom.services.MessageListener;
import zm.co.hobbiton.mobicom.services.MessageReceiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, MessageListener {
    public static final String PREFS_AGENT = "AgentSharedPreferences";
    public static final String TAG = "Login";
    private EditText mNumberInput, mPinInput;
    private Button mLoginBtn;
    private ProgressDialog mProgressDialog;
    private static String API_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MessageReceiver.bindListener(this);
        initializeViews();
        hideKeyboard(this);
    }



    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @SuppressLint("NewApi")
    private void initializeViews() {
        mNumberInput = findViewById(R.id.inputPhoneNumber);
        mNumberInput.setShowSoftInputOnFocus(false);
        mPinInput = findViewById(R.id.inputPin);
        mPinInput.setShowSoftInputOnFocus(false);
        mLoginBtn = findViewById(R.id.btnLogin);
        mLoginBtn.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
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
        final String phoneNumber = mNumberInput.getText().toString();
        final String pin= mPinInput.getText().toString();
        if (phoneNumber.length() == 0) {
            mNumberInput.requestFocus();
            mNumberInput.setError("Phone number required");
        } else if (pin.length() == 0) {
            mPinInput.requestFocus();
            mPinInput.setError("Pin required");
        } else if (phoneNumber.matches("[a-zA-Z ]+")){
            mNumberInput.requestFocus();
            mNumberInput.setError("Enter numbers only");
        } else if (pin.matches("[a-zA-Z ]+")){
            mPinInput.requestFocus();
            mPinInput.setError("Enter numbers only");
        } else {
            loginUser("26" + phoneNumber, pin);
//            onLoginAgent("260964714308");
        }
    }

    private void loginUser(String phoneNumber, String pin) {
        mProgressDialog.show();
//        try {
//            byte[] data = pin.getBytes("UTF-8");
//            String pinBase64 = Base64.encodeToString(data, Base64.DEFAULT);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        API_URL = "https://mobicom-pilot.herokuapp.com/mini-auth/agent-details/" + phoneNumber + "/" +pin;
        Log.d("LOGIN", "API URL: "+ API_URL);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean isLoggedIn = obj.optBoolean("login");
                            if (isLoggedIn) {
                                String agentNumber = obj.optString("MSISDN");
                                onLoginAgent(agentNumber);
                            } else {
                                Toast.makeText(getApplicationContext(),"Incorrect Credentials",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void onLoginAgent(String agentNumber) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_AGENT, MODE_PRIVATE).edit();
        editor.putString("number", agentNumber);
        editor.apply();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void messageReceived(String message) {

    }
}