package zm.co.hobbiton.mobicom.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class MessageReceiver extends BroadcastReceiver {
    public static final String Tag = "MessageReceiver";
    private static MessageListener sListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for (int i=0; i<pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
//            String message = "Sender : "+ smsMessage.getDisplayOriginatingAddress()
//                    + "Email From "+ smsMessage.getEmailFrom()
//                    + "Email Body: "+ smsMessage.getEmailBody()
//                    + "Display message body: "+ smsMessage.getDisplayMessageBody()
//                    + "Time in millisecond: " + smsMessage.getTimestampMillis()
//                    + "Message: " + smsMessage.getMessageBody();

            String message = smsMessage.getDisplayMessageBody();
//            if (smsMessage.getDisplayOriginatingAddress().equals("+2609723272420")){
//                Log.d("Message receiver", "got it "+smsMessage.getDisplayOriginatingAddress());
//                sListener.messageReceived(message);
//            } else {
//                Log.d("Message receiver", "Not my business mr "+smsMessage.getDisplayOriginatingAddress());
//                sListener.messageReceived(message);
//            }
//            Log.d("Message receiver", "sender: "+smsMessage.getDisplayOriginatingAddress());

            sListener.messageReceived(message);

        }
    }

    public static void bindListener(MessageListener listener){
        sListener = listener;
    }
}
