package zm.co.hobbiton.mobicom.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransactionItem {
    private int amount;
    private int serviceID;
    private String customerMSISN;
    private String message;
    private String dateCreated;

    public TransactionItem(int amount, int serviceID, String customerMSISN, String message, String dateCreated) {
        this.amount = amount;
        this.serviceID = serviceID;
        this.customerMSISN = customerMSISN;
        this.message = message;
        this.dateCreated = dateCreated;
    }

    public String getAmount() {

        return "K"+ amount;
    }

    public String getServiceID() {
        switch (serviceID) {
            case 1:
                return "Airtel Airtime";
            case 2:
                return "MTN Airtime";
            case 3:
                return "Zamtel Airtime";
            case 4:
                return "Zesco Units";
            case 5:
                return "DSTV";
            case 6:
                return "GoTV";
            case 7:
                return "TopStar";
            case 8:
                return "LWSC";
            case 50:
                return "Airtel Money";
            case 51:
                return "MTN Momo";
            case 52:
                return "Zamtel Kwacha";
            default:
                return "Unknown Transaction";
        }
    }

    public String getCustomerMSISN() {
        return "+" + customerMSISN;
    }

    public String getMessage() {
        return message;
    }

    public String getDateCreated() {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateCreated);
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String formattedDate = df.format(date);
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }
}
