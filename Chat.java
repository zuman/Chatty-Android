package com.zap.chatty;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class Chat extends Activity {
	
	String id;
	String name;
	EditText msg;
	ImageButton send;
	SharedPreferences pref;
	IntentFilter intentFilter;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chats);
		
		msg=(EditText)findViewById(R.id.message);
		send=(ImageButton)findViewById(R.id.send);
		
		pref= getSharedPreferences(MainActivity.pref,
				Context.MODE_PRIVATE);
		
		Bundle extras=getIntent().getExtras();
		if(extras!=null)
		{
			id=extras.getString("id");
			name=extras.getString("name");
			Toast.makeText(this, "Press Send button to scroll to bottom...", Toast.LENGTH_SHORT).show();
			this.setTitle(name);
		}
		reloadChat();
		toBottom();
	}
	
	public void onResume()
	{
		super.onResume();
		intentFilter=new IntentFilter();
		intentFilter.addAction("REFRESH");
		registerReceiver(intentReciever,intentFilter);
	}
	
	public void onPause()
	{
		super.onPause();
		unregisterReceiver(intentReciever);
	}
	
	private BroadcastReceiver intentReciever= new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			LinearLayout lay = (LinearLayout)findViewById(R.id.linear);
			lay.removeAllViews();
			reloadChat();
		}
	};
	
	public void onsend(View v)
	{
		if(!msg.getText().toString().isEmpty())
		{

			Time t=new Time();
			t.setToNow();
			String time=t.year+"-"+(t.month+1)+"-"+t.monthDay+" "+t.hour+":"+t.minute+":"+t.second;
			
		ChattyData db =new ChattyData(getApplicationContext());
		db.addMessage(id, msg.getText().toString(), "unsent");
		addMsg(true,msg.getText().toString(),time,"unsent");
		msg.setText("");
		}
		toBottom();
	}

	void toBottom()
	{
		ScrollView scroll=(ScrollView)findViewById(R.id.scroll);
		scroll.fullScroll(View.FOCUS_DOWN);
	}
	
	private void addMsg(boolean right, String msg, String time, String status)
	{
		LinearLayout lay = (LinearLayout)findViewById(R.id.linear);
		LinearLayout lay2 = new LinearLayout(this);
		lay2.setOrientation(LinearLayout.VERTICAL);
		LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		TextView tv1=new TextView(this);
		TextView tv2=new TextView(this);
		//TextView tv3=new TextView(this);
		TextView tv4;
		
		tv1.setTextSize(16);
		tv2.setTextSize(12);
		//tv3.setTextSize(12);
		tv1.setText(msg);
		tv2.setText(time);
		//tv3.setText(status);

		if(right)
		{
			lay2.setGravity(Gravity.RIGHT);
			lay2.setBackgroundColor(Color.DKGRAY);
			
		 tv1.setGravity(Gravity.RIGHT);	tv1.setTextColor(Color.WHITE);
		 tv2.setGravity(Gravity.RIGHT);	tv2.setTextColor(Color.WHITE);
		 //tv3.setGravity(Gravity.RIGHT);	tv3.setTextColor(Color.WHITE);
		}
		else
		{
			lay2.setGravity(Gravity.LEFT);
			lay2.setBackgroundColor(Color.LTGRAY);
			tv1.setTextColor(Color.BLACK);
			tv2.setTextColor(Color.BLACK);
			//tv3.setTextColor(Color.BLACK);

		}
		
		lparams.setMargins(15, 0, 15, 0);
		tv1.setLayoutParams(lparams);
		tv2.setLayoutParams(lparams);
		//tv3.setLayoutParams(lparams);

		lay2.addView(tv1);
		
		if(right)
		{
		tv4=new TextView(this);
		tv4.setText("");
		lay2.addView(tv4);
		}
		
		lay2.addView(tv2);
		//lay2.addView(tv3);
		
		tv4=new TextView(this);
		tv4.setText("");
		lay2.addView(tv4);
		
		lay.addView(lay2);
	}

	private void reloadChat()
	{
		ChattyData db =new ChattyData(getApplicationContext());
		Cursor c=db.getMessages(id);
		
		if(c!=null)
		{c.moveToFirst();
			do{
				String msg=c.getString(1);
				String time=c.getString(2);
				String status=c.getString(3);
				boolean right=false;
				if(status.equals("unsent") || status.equals("unread") || status.equals("read"))	right=true;
				addMsg(right, msg, time, status);
				toBottom();
			}while(c.moveToNext());
		}
		db.close();
		toBottom();
	}
}