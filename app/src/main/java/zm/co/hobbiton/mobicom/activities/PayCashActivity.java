package zm.co.hobbiton.mobicom.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import zm.co.hobbiton.mobicom.LoginActivity;
import zm.co.hobbiton.mobicom.MainActivity;
import zm.co.hobbiton.mobicom.R;
import zm.co.hobbiton.mobicom.services.MessageListener;
import zm.co.hobbiton.mobicom.services.MessageReceiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nbbse.mobiprint3.Printer;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class PayCashActivity extends AppCompatActivity implements View.OnClickListener, MessageListener {
    public static final String TAG = "PayCash";
    public static final String CHANNEL_ID = "CashPayment";
    public static final String EXTRA_CASH_PAYMENT = "CashPayment";
    private static final String API_URL = "https://mobicom-pilot.herokuapp.com/request-payment/cash-payment";
    private int mServiceID, mLogo;
    private ImageView mImageView;
    private EditText mPhoneInput, mAmountInput;
    private Button mBuyAirtimeBtn;
    private ProgressDialog mProgressDialog;
    private Printer mPrinter;
    public String mAgentNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_cash);
        // Register sms listener
        MessageReceiver.bindListener(this);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        Intent intent = getIntent();
        mServiceID = intent.getIntExtra(CashPaymentsActivity.EXTRA_ID, 0);
        mLogo = intent.getIntExtra(CashPaymentsActivity.EXTRA_LOGO, R.drawable.ic_launcher_foreground);

        mPrinter = Printer.getInstance();
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_AGENT, MODE_PRIVATE);
        mAgentNumber = prefs.getString("number", "undefined");
        checkAuthStatus(mAgentNumber);
        initializeViews();
        hideKeyboard(this);
    }

    @Override
    public void messageReceived(final String message) {
//        messageDialogue(message, this, mPrinter);
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

//    public static void messageDialogue(final String message, Context context, final Printer printer) {
//        new AlertDialog.Builder(context)
//                .setTitle("Cash Payment")
//                .setMessage(message)
//                .setCancelable(true)
//                .setPositiveButton("Print", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        // Print receipt
//                        printer.printText("********************************");
//                        printer.printText("******** Hobbiton Tech *********");
//                        printer.printText("********************************");
//                        printer.printText("Cash Payment",2);
//                        printer.printText(message);
//                        printer.printEndLine();
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.cancel();
//                    }
//                })
//                .setIcon(R.drawable.ic_monetization_white)
//                .show();
//    }

    private void createNotification() {
        Log.d(TAG, "createNotification: called");
        final String phoneNumber = mPhoneInput.getText().toString();
        final String amount= mAmountInput.getText().toString();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_monetization_white)
                .setContentTitle("Cash Payment")
                .setContentText(phoneNumber+" paid "+amount)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_CASH_PAYMENT, 2);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
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
        mImageView = findViewById(R.id.imgAirtimeLogo);
        mImageView.setImageResource(mLogo);
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
                payCash("26" +phoneNumber, amount);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void payCash(final String phoneNumber, final String amount) {
        mProgressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest request = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();

                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                    Toast.makeText(getApplicationContext(),obj.optString("responseDescription"),Toast.LENGTH_SHORT).show();
                    createNotification();
                } catch (JSONException e) {
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
                map.put("narration", "Made a cash payment");
                map.put("serviceId", String.valueOf(mServiceID));
                map.put("agent_msisdn", mAgentNumber);
                return map;
            }
        };
        queue.add(request);


    }
}