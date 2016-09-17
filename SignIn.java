package com.zap.chatty;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class SignIn extends Fragment {
	
	static EditText id, pass;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.signin, container, false);
        
        id=(EditText)v.findViewById(R.id.id);
        pass=(EditText)v.findViewById(R.id.pass);
        
        return v;
	}
	public static void signin(View v)
	{
		if(id.getText().toString().isEmpty())
			Toast.makeText(v.getContext(), "Please enter a username or email.",
					Toast.LENGTH_LONG).show();
		else if(pass.getText().toString().isEmpty())
			Toast.makeText(v.getContext(), "Please enter a password to continue.",
					Toast.LENGTH_LONG).show();
		else if(!(Tools.isValidUser(id.getText().toString()) || Tools.isValidEmail(id.getText().toString())))
			Toast.makeText(v.getContext(), "Please enter a valid user id or e-mail.",
					Toast.LENGTH_LONG).show();
		else
		{
			List<NameValuePair> nameValuePairs= new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("id", id.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("pass", pass.getText().toString()));
			new SignInTask(v.getContext()).execute(nameValuePairs);
		}
	}
}

class SignInTask extends AsyncTask<Object,Object,String>
{
	ProgressDialog dialog;
	Context con;
	public SignInTask(Context context)
	{		con=context;dialog = new ProgressDialog(con);
	dialog.setMessage("Getting ready...");
	dialog.setCancelable(true);	}
	
	public void onPreExecute()
	{
		dialog.show();
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(Object... params) {
		publishProgress("Verifying...");
		String back=Net.HttpPostURL("http://i-chatty.cu.cc/app/signin.php",
			(List<NameValuePair>)params[0]);
		publishProgress("Logging in...");
		try{back=back.substring(0, back.length()-151);}
		catch(IndexOutOfBoundsException e){}
		return back;
	}
	
	protected void onProgressUpdate(Object... values) {
		dialog.setMessage(values[0].toString());
	}
	
	public void onPostExecute(String res) {
		Toast.makeText(con, res, Toast.LENGTH_LONG).show();
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
			if(result[1].equals("no login"))
			{
				AlertDialog.Builder alert=new AlertDialog.Builder(con);
				alert.setTitle("No login");
				alert.setMessage("Username and password do not match!");
				alert.setNeutralButton("OK", null);
				alert.show();
			}
			else if(result[1].equals("login"))
			{
				SharedPreferences pref= con.getSharedPreferences(MainActivity.pref,
																Context.MODE_PRIVATE);
				Editor editor = pref.edit();
			    String u = SignIn.id.getText().toString();
			    editor.putString("id", result[2]);
			    editor.putString("name", result[3]);
			    editor.commit();
			    Intent i = new Intent(con,MainActivity.class);
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