package com.zap.chatty;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class CheckOnlineInbox extends Service {

	//ChecksInbox check;
	int i=1,sno=-1;
	//boolean b=true;
	Timer timer;
	ChattyData db;
	SharedPreferences pref;
	List<NameValuePair> nameValuePairs;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		//check = new ChecksInbox(this.getBaseContext());
		//check.execute("");
		timer=new Timer();
		pref= getSharedPreferences(MainActivity.pref,Context.MODE_PRIVATE);
		Toast.makeText(this, "Checking started..."+i, Toast.LENGTH_SHORT).show();
		doSomethingRepeatedly(this);
		
		return START_STICKY;
	}
	
	private void doSomethingRepeatedly(final Context con)
	{
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d("shows","shw "+i++);

				//SendMsg
				nameValuePairs= new ArrayList<NameValuePair>(3);
				db=new ChattyData(con);
				Cursor c=db.getUnsent();
				if(c!=null)
				{
				 c.moveToFirst();
				 try{c.getString(1).equals("");
				 sno=c.getInt(0);
					Log.d("sno",sno+"");
				 nameValuePairs.add(new BasicNameValuePair("from", pref.getString("id", "")));
				 nameValuePairs.add(new BasicNameValuePair("to", c.getString(1)));
				 nameValuePairs.add(new BasicNameValuePair("msg", c.getString(2)));
				 SendMsg sendMsg=new SendMsg(con,sno);
					if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
						sendMsg.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,nameValuePairs);
					else	sendMsg.execute(nameValuePairs);
				 }
				 catch(Exception e){}
				}
				db.close();
				Log.d("shows","sno "+sno+"");
				
				//NewMsg
				nameValuePairs= new ArrayList<NameValuePair>(1);
				try{
					nameValuePairs.add(new BasicNameValuePair("id", pref.getString("id", "")));
					NewMsg newMsg=new NewMsg(con);
					if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
						newMsg.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,nameValuePairs);
					else	newMsg.execute(nameValuePairs);
				}catch(Exception e){}
				
				
				//new ReadMsg().execute();
				
				/*AddChat myTask = new AddChat(this);
				if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
				    myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,nameValuePairs);
				else
				    myTask.execute(nameValuePairs);*/
			}
		}, 0, 5000);
	}

	public void onDestroy()
	{
		//check.cancel(true);
		if(timer!=null)	timer.cancel();
		Toast.makeText(this.getBaseContext(), "Checking stopped...", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}
	
	class SendMsg extends AsyncTask<Object, Void, String>
	{
		Context con;
		int sno;
		public SendMsg(Context con,int sno) {
			this.con=con; this.sno=sno;
		}
		
		public void onPreExecute()
		{		super.onPreExecute();	}

		protected String doInBackground(Object... params) {
			String back=Net.HttpPostURL("http://i-chatty.cu.cc/app/sendmsg.php",(List<NameValuePair>)params[0]);
			try{back=back.substring(0, back.length()-151);}
			catch(IndexOutOfBoundsException e){}
			return back;
		}
		public void onPostExecute(String res) {
			Log.d("Result",res);
			String result[]=res.split("\n");
			
			if(!result[0].equals("good"));
			else if(result[1].equals("1"))
				{
					db=new ChattyData(con);
					db.setUnread(sno);
					db.close();
				}
			sno=-1;
		}
	}
	
	class NewMsg extends AsyncTask<Object, Void, String>
	{
		Context con;
		public NewMsg(Context con) {
			this.con=con;
		}
		
		public void onPreExecute()
		{		super.onPreExecute();	}

		protected String doInBackground(Object... params) {
			String back=Net.HttpPostURL("http://i-chatty.cu.cc/app/newmsg.php",(List<NameValuePair>)params[0]);
			try{back=back.substring(0, back.length()-151);}
			catch(IndexOutOfBoundsException e){}
			return back;
		}
		public void onPostExecute(String res) {
			Log.d("Result",res);
			String result[]=res.split("\n");
			
			if(!result[0].equals("good"));
			else if(!result[1].equals("0"))
				{
					final String id=result[1];
					String timestamp=result[2];
					String msg="";
					for(int z=3;z<result.length;z++)
						msg+=(result[z]+"\n");
					db=new ChattyData(con);
					db.newMsg(id, msg, timestamp);
					db.close();
					
			            	Log.d("Runnable","in runnable");
			            	List<NameValuePair> nameValuePairs= new ArrayList<NameValuePair>(1);
							nameValuePairs.add(new BasicNameValuePair("id", id));
							AddChatInbox myTask = new AddChatInbox(con);
							if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
							    myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,nameValuePairs);
							else
							    myTask.execute(nameValuePairs);
							
						Intent broadcastIntent=new Intent();
						broadcastIntent.setAction("REFRESH");
						con.sendBroadcast(broadcastIntent);
				}
		}
	}
}