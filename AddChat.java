package com.zap.chatty;

import java.util.List;

import org.apache.http.NameValuePair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

class AddChat extends AsyncTask<Object,Object,String>
{
	ProgressDialog dialog;
	Context con;
	protected AddChat(Context context)
	{		con=context;dialog = new ProgressDialog(con);	}
	
	protected void onPreExecute()
	{
		dialog.setMessage("Getting ready...");
		dialog.setCancelable(true);
		dialog.show();
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(Object... params) {
		publishProgress("Searching for the user...");
		String back=Net.HttpPostURL("http://i-chatty.cu.cc/app/addchat.php",
			(List<NameValuePair>)params[0]);
		publishProgress("Getting results...");
		try{back=back.substring(0, back.length()-151);}
		catch(IndexOutOfBoundsException e){}
		return back;
	}
	
	protected void onProgressUpdate(Object... values) {
		dialog.setMessage(values[0].toString());
	}
	
	protected void onPostExecute(String res) {
		String result[]=res.split("\n");
		dialog.dismiss();
		if(!result[0].equals("good"))
		{
			AlertDialog.Builder alert=new AlertDialog.Builder(con);
			alert.setTitle("Connection Error!");
			alert.setMessage(R.string.netcheckmsg);
			alert.setNeutralButton("OK", null);
			alert.show();
		}
		else
		{
			if(result[1].equals("no user"))
			{
				AlertDialog.Builder alert=new AlertDialog.Builder(con);
				alert.setTitle("No user");
				alert.setMessage("No user exists by this ID.");
				alert.setNeutralButton("OK", null);
				alert.show();
			}
			else if(result[1].equals("user"))
			{
				ChattyData db =new ChattyData(con.getApplicationContext());
				db.addUser(result[2],result[3]);
				db.close();
				Intent i = new Intent(con,Chat.class);
				i.putExtra("id", result[2]);
				i.putExtra("name", result[3]);
				con.startActivity(i);
			}
			else
			{
				AlertDialog.Builder alert=new AlertDialog.Builder(con);
				alert.setTitle("Unknown Error:");
				String err="";
				for(int i=1;i<result.length;i++)
					err+=result[i]+"\n";
				alert.setMessage(err);
				alert.setNeutralButton("OK", null);
				alert.show();
			}
		}
	}
}

class AddChatInbox extends AsyncTask<Object,Object,String>
{
	Context con;
	protected AddChatInbox(Context context)
	{		con=context;	}
	
	protected void onPreExecute()
	{		super.onPreExecute();	}
	
	@Override
	protected String doInBackground(Object... params) {
		String back=Net.HttpPostURL("http://i-chatty.cu.cc/app/addchat.php",
			(List<NameValuePair>)params[0]);
		try{back=back.substring(0, back.length()-151);}
		catch(IndexOutOfBoundsException e){}
		return back;
	}
	
	protected void onPostExecute(String res) {
		String result[]=res.split("\n");
		if(!result[0].equals("good"));
		else
		{
			if(result[1].equals("no user"));
			else if(result[1].equals("user"))
			{
				ChattyData db =new ChattyData(con.getApplicationContext());
				db.addUser(result[2],result[3]);
				db.close();
			}
			else;
		}
	}
}