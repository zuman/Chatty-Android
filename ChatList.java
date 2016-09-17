package com.zap.chatty;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ChatList extends Activity {

	SharedPreferences pref;
	ListView list;
	ChatList chatList=null;
	ChatListAdapter adapter;
    public  ArrayList<ChatLayout> CustomListViewValuesArr = new ArrayList<ChatLayout>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatlist);
		pref= getSharedPreferences(MainActivity.pref,
				Context.MODE_PRIVATE);
		String id=pref.getString("id", "123");
		if(!id.equals("123"))
			this.setTitle(pref.getString("name", ""));
		
		if(!isMyServiceRunning(CheckOnlineInbox.class))
			startService(new Intent(getBaseContext(),CheckOnlineInbox.class));
	}
	
	public void onResume()
	{
		super.onResume();
		chatList = this;
		reloadListData();
		
        //TODO
	}
	
	void reloadListData()
	{
		/******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
        setListData();
         
        Resources res =getResources();
        list= ( ListView )findViewById( R.id.list );  // List defined in XML ( See Below )
         
        /**************** Create Custom Adapter *********/
        adapter=new ChatListAdapter( chatList, CustomListViewValuesArr,res );
        list.setAdapter( adapter );
	}
	
	/****** Function to set data in ArrayList *************/
    public void setListData()
    {
    	CustomListViewValuesArr.clear();
    	ChattyData db=new ChattyData(getApplicationContext());
    	Cursor c=db.getList();
    	
    	if(c!=null)
		{c.moveToFirst();
			do{
				final ChatLayout sched = new ChatLayout();
                
	              /******* Firstly take data in model object ******/
	               sched.setName(c.getString(0));
	               sched.setId(c.getString(1));
	               sched.setMsg(c.getString(2));
	               sched.setTime(c.getString(3));
	               
	               /******** Take Model Object in ArrayList **********/
	               CustomListViewValuesArr.add( sched );
			}while(c.moveToNext());
		}
		db.close();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuItem mnu1=menu.add(0,0,0,"Add Chat");
		mnu1.setIcon(R.drawable.ic_action_add_person);
		mnu1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		MenuItem mnu2=menu.add(0,1,1,"Logout");
		mnu2.setIcon(R.drawable.abc_ic_clear_normal);
		
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case 0:
			final EditText id=new EditText(this);
			id.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
			id.setHint("Username or e-mail");
			new AlertDialog.Builder(this)
			.setIcon(R.drawable.ic_action_person)
			.setTitle("Add Chat")
			.setMessage("Enter a Chatty ID or email:")
			.setView(id)
			.setPositiveButton("Add", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					if(id.getText().toString().isEmpty())
						Toast.makeText(getBaseContext(), "Please enter a username or email.",
								Toast.LENGTH_LONG).show();
					else if(!(Tools.isValidUser(id.getText().toString()) || Tools.isValidEmail(id.getText().toString())))
						Toast.makeText(getBaseContext(), "Please enter a valid user id or e-mail.",
								Toast.LENGTH_LONG).show();
					else
					{
						List<NameValuePair> nameValuePairs= new ArrayList<NameValuePair>(1);
						nameValuePairs.add(new BasicNameValuePair("id", id.getText().toString()));
						addChat(nameValuePairs);
					}
				}
			}).show();
			return true;
			
		case 1:
			Editor editor = pref.edit();
			editor.clear();
			editor.commit();
			stopService(new Intent(getBaseContext(),CheckOnlineInbox.class));
			ChattyData db=new ChattyData(this);
			db.delete();
			db.close();
			Intent i = new Intent(this,MainActivity.class);
	        startActivity(i);
	        this.finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	public void addChat(List<NameValuePair> nameValuePairs)
	{
		AddChat myTask = new AddChat(this);
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
		    myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,nameValuePairs);
		else
		    myTask.execute(nameValuePairs);
	}
	
	public boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	public void onItemClick(int mPosition) {
		ChatLayout tempValues = ( ChatLayout ) CustomListViewValuesArr.get(mPosition);
		Intent i = new Intent(this,Chat.class);
		i.putExtra("id", tempValues.getId());
		i.putExtra("name", tempValues.getName());
		startActivity(i);
	}
}
class ChatLayout
{
	String name="",id="",msg="",time="";
	public void setName(String name)
    {        this.name = name;    }
	public void setId(String id)
    {        this.id = id;    }
	public void setMsg(String msg)
    {        this.msg = msg;    }
	public void setTime(String time)
    {        this.time = time;    }

	public String getName()
    {        return name;    }
	public String getId()
    {        return id;    }
	public String getMsg()
    {        return msg;    }
	public String getTime()
    {        return time;    }
}