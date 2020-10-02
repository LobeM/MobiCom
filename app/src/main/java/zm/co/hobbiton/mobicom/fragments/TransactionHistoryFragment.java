package zm.co.hobbiton.mobicom.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import zm.co.hobbiton.mobicom.LoginActivity;
import zm.co.hobbiton.mobicom.R;
import zm.co.hobbiton.mobicom.adapters.TransactionHistoryAdapter;
import zm.co.hobbiton.mobicom.models.TransactionItem;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class TransactionHistoryFragment extends Fragment {
    public static final String TAG = "Transactions";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog mProgressDialog;
    private static String API_URL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        recyclerView = view.findViewById(R.id.transactionHistRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        SharedPreferences prefs = getActivity().getSharedPreferences(LoginActivity.PREFS_AGENT, MODE_PRIVATE);
        String agentNumber = prefs.getString("number", "undefined");
        checkAuthStatus(agentNumber);
        API_URL = "https://mobicom-pilot.herokuapp.com/records/sucessful-agent-transactions/"+agentNumber;
        initializeViews();
        ArrayList<TransactionItem> transactionItems = getTransactionItems();
        // specify an adapter (see also next example)
        mAdapter = new TransactionHistoryAdapter(transactionItems);

        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private void initializeViews() {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    private ArrayList<TransactionItem> getTransactionItems() {
        final ArrayList<TransactionItem> transactionItemsList = new ArrayList<>();
//        mProgressDialog.show();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        mProgressDialog.dismiss();
                        try {
                            JSONArray objArray = new JSONArray(response);
                            for (int i = 0; i < objArray.length(); i++) {
                                JSONObject obj = objArray.getJSONObject(i);

                                transactionItemsList.add(
                                        new TransactionItem(
                                                obj.optInt("amount"),
                                                obj.optInt("serviceId"),
                                                obj.optString("customer_msisdn"),
                                                obj.optString("message"),
                                                obj.optString("date_created")
                                                )

                                );
                                Log.d(TAG, "onResponse customer msisn: "+transactionItemsList.get(i).getCustomerMSISN());
                            }
                            mAdapter.notifyDataSetChanged();


                            Log.d(TAG, "onResponse datanigga: "+transactionItemsList.size());

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Toast.makeText(getContext(),"Transactions Unavailable",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        return transactionItemsList;
    }

    private void checkAuthStatus(String agentNumber) {
//        Intent intent = new Intent(getContext(), LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        Log.d("TransHist", "checkAuthStatus: "+agentNumber);
    }
}