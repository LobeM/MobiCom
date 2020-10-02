package zm.co.hobbiton.mobicom.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import zm.co.hobbiton.mobicom.LoginActivity;
import zm.co.hobbiton.mobicom.R;
import zm.co.hobbiton.mobicom.services.MessageListener;
import zm.co.hobbiton.mobicom.services.MessageReceiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nbbse.mobiprint3.Printer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PayBillActivity extends AppCompatActivity implements View.OnClickListener, MessageListener {
    private static final String TAG = "PayBillActivity";
    private int mServiceID, mLogoID;
    private EditText mPhoneInput, mAmountInput, mMeterNumberInput;
    private ImageView mLogoImg;
    private Button mPayBillBtn;
    private Printer mPrinter;
    private InputStream mReceiptLogo;
    private static final String API_URL = "https://mobicom-pilot.herokuapp.com/service-request/paybills-request";
    private static final String KYC_URL = "https://mobicom-pilot.herokuapp.com/client-kyc/account-details";
    private ProgressDialog mProgressDialog;
    private String mAgentNumber;
    private String billName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        setContentView(R.layout.activity_pay_bill);
        MessageReceiver.bindListener(this);
        Intent intent = getIntent();
        mServiceID = intent.getIntExtra(BillPaymentsActivity.EXTRA_ID, 0);
        mLogoID = intent.getIntExtra(BillPaymentsActivity.EXTRA_LOGO, R.drawable.ic_launcher_foreground);
        mPrinter = Printer.getInstance();
        mReceiptLogo = getResources().openRawResource(R.raw.des1);
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
        mPhoneInput = findViewById(R.id.inputCustomerNumber);
        mPhoneInput.setShowSoftInputOnFocus(false);
        mAmountInput = findViewById(R.id.inputAmount);
        mAmountInput.setShowSoftInputOnFocus(false);
        mMeterNumberInput = findViewById(R.id.inputMeterNumber);
        mMeterNumberInput.setShowSoftInputOnFocus(false);
        mLogoImg = findViewById(R.id.imgBillLogo);
        mLogoImg.setImageResource(mLogoID);
        mPayBillBtn = findViewById(R.id.btnPayBill);
        mPayBillBtn.setOnClickListener(this);

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
        final String meterNumber = mMeterNumberInput.getText().toString();
        if (mServiceID != 0) {
            if (phoneNumber.length() == 0) {
                mPhoneInput.requestFocus();
                mPhoneInput.setError("Phone number required");
            } else if (amount.length() == 0) {
                mAmountInput.requestFocus();
                mAmountInput.setError("Amount required");
            } else if (meterNumber.length() == 0) {
                mMeterNumberInput.requestFocus();
                mMeterNumberInput.setError("Meter number required");
            }
            else if (phoneNumber.matches("[a-zA-Z ]+")){
                mPhoneInput.requestFocus();
                mPhoneInput.setError("Enter numbers only");
            } else if (amount.matches("[a-zA-Z ]+")){
                mAmountInput.requestFocus();
                mAmountInput.setError("Enter numbers only");
            } else if (meterNumber.matches("[a-zA-Z ]+")){
                mMeterNumberInput.requestFocus();
                mMeterNumberInput.setError("Enter numbers only");
            } else {
                if (mPrinter.getPaperStatus() == 1){
//                    payBill("26"+phoneNumber, amount, meterNumber);
                    confirmPayment("26"+phoneNumber, amount, meterNumber);
                }else {
                    Toast.makeText(getApplicationContext(), "Load the printer", Toast.LENGTH_SHORT).show();
                }

            }
        } else {
            Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_SHORT).show();
        }

    }

    private void confirmPayment(final String phoneNumber, final String amount, final String meterNumber) {
        mProgressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest request = new StringRequest(Request.Method.POST, KYC_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                Log.d("API Bills", response);
                JSONObject obj;
                try {
                    obj = new JSONObject(response);

                    String message;

                    if (mServiceID == 4) {
                        billName = "Zesco";
                    } else if (mServiceID == 8) {
                        billName = "Lusaka Water";
                    } else {
                        billName = "null";
                    }
                    if (obj.getJSONObject("transaction").optString("description").equals("SUCCESS")) {
                        message = "Confirm payment of ZMW "+amount+ " for "+billName+" bill. to "+obj.getJSONObject("transaction").optString("customername");
                        new AlertDialog.Builder(PayBillActivity.this)
                                .setTitle("Confirm Payment")
                                .setMessage(message)
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        payBill(phoneNumber, amount, meterNumber);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                }).show();
                    } else {
                        message = "Account not found!";
                        new AlertDialog.Builder(PayBillActivity.this)
                                .setTitle("Confirm Payment")
                                .setMessage(message)
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                }).show();
                    }
                } catch (JSONException e) {
                    Log.d("bill_kyc", "json: " + response);
                    e.printStackTrace();
                }
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
                map.put("serviceId", String.valueOf(mServiceID));
                map.put("account_number", meterNumber);
                return map;
            }
        };
        queue.add(request);
    }

    private void payBill(final String phoneNumber, final String amount, final String meterNumber) {
        mProgressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest request = new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressDialog.dismiss();
                Log.d("API Bills", response);
                JSONObject obj;
                try {
                    obj = new JSONObject(response);
                    if (obj.getJSONObject("transaction").optString("description").equals("SUCCESS")) {
                        JSONObject transObj = obj.getJSONObject("transaction");
                        Toast.makeText(getApplicationContext(), "Purchase Successful", Toast.LENGTH_SHORT).show();
                        // Get date
                        Date c = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                        String formattedDate = df.format(c);

                        // Print receipt
                        // mPrinter.printBitmap(mReceiptLogo);
                        mPrinter.printText("********************************");
                        mPrinter.printText("******** Hobbiton Tech *********");
                        mPrinter.printText("********************************");
                        mPrinter.printText("Description:");
                        mPrinter.printText(billName+ " Payment", 2);
                        mPrinter.printText("Token:");
                        mPrinter.printText(transObj.optString("token"), 2);
                        mPrinter.printText("Amount:");
                        mPrinter.printText("K"+amount, 2);
                        mPrinter.printText("Units:");
                        mPrinter.printText(transObj.optString("units"), 2);
                        mPrinter.printText("Meter Number:");
                        mPrinter.printText(transObj.optString("meternumber"), 2);
                        mPrinter.printText("Customer Name:");
                        mPrinter.printText(transObj.optString("customername"), 2);
                        mPrinter.printText("Customer Address:");
                        mPrinter.printText(transObj.optString("customeraddress"), 2);
                        mPrinter.printText("Date:");
                        mPrinter.printText(formattedDate, 2);
                        mPrinter.printEndLine();
                    }
//                    Toast.makeText(getApplicationContext(),obj.optString("message"),Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    Log.d("PayBills", "json: " + response);
                    Toast.makeText(getApplicationContext(), "Insufficient Funds", Toast.LENGTH_SHORT).show();
                    mPrinter.printText("********************************");
                    mPrinter.printText("******** Hobbiton Tech *********");
                    mPrinter.printText("********************************");
                    mPrinter.printText("Insufficient Funds");
                    mPrinter.printEndLine();
                    e.printStackTrace();
                }
                mPhoneInput.setText(null);
                mAmountInput.setText(null);
                mMeterNumberInput.setText(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Service Unavailable",Toast.LENGTH_SHORT).show();
                error.printStackTrace();

                // Print receipt
                // mPrinter.printBitmap(mReceiptLogo);
                mPrinter.printText("********************************");
                mPrinter.printText("******** Hobbiton Tech *********");
                mPrinter.printText("********************************");
                mPrinter.printText("Service unavailable");
                mPrinter.printEndLine();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("customer_msisdn", phoneNumber);
                map.put("amount", amount);
                map.put("status", "105");
                map.put("serviceId", String.valueOf(mServiceID));
                map.put("agent_msisdn", mAgentNumber);
                map.put("meter_number", meterNumber);
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