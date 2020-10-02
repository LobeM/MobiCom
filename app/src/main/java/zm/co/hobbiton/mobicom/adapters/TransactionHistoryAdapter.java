package zm.co.hobbiton.mobicom.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import zm.co.hobbiton.mobicom.R;
import zm.co.hobbiton.mobicom.models.TransactionItem;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.MyViewHolder>  {
    public static final String TAG = "TransAdapter";
    ArrayList<TransactionItem> mDataSet;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView amountTxt;
        public TextView customerMSISNTxt;
        public TextView serviceTxt;
        public TextView dateTxt;

        public MyViewHolder(View v) {
            super(v);
            serviceTxt = v.findViewById(R.id.txtService);
            amountTxt = v.findViewById(R.id.txtAmount);
            customerMSISNTxt = v.findViewById(R.id.txtCustomerMSIN);
            dateTxt = v.findViewById(R.id.txtDate);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TransactionHistoryAdapter(ArrayList<TransactionItem> myDataset) {
        mDataSet = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TransactionHistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TransactionItem item = mDataSet.get(position);
        holder.serviceTxt.setText(item.getServiceID());
        holder.customerMSISNTxt.setText(item.getCustomerMSISN());
        holder.amountTxt.setText(item.getAmount());
        holder.dateTxt.setText(item.getDateCreated());
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+mDataSet.size());
        return mDataSet.size();
    }
}
