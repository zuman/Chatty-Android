package com.zap.chatty;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends Fragment {
	
	static EditText name, id, email, pass, conpass;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.signup, container, false);
		
		name=(EditText)v.findViewById(R.id.id);
		id=(EditText)v.findViewById(R.id.pass);
		email=(EditText)v.findViewById(R.id.editText3);
		pass=(EditText)v.findViewById(R.id.editText4);
		conpass=(EditText)v.findViewById(R.id.editText5);
				
        return v;
	}
	
	public static void signup(View v)
	{
		if(name.getText().toString().isEmpty())
			Toast.makeText(v.getContext(), "Empty Name field",
					Toast.LENGTH_LONG).show();
		
		else if(!Tools.isValidUser(id.getText().toString()))
			Toast.makeText(v.getContext(), "Invalid Username entered.\n"
				+"Minimum 3 characters, no space,\nno special characters except . and - and _",
				Toast.LENGTH_LONG).show();
		
		else if(!Tools.isValidEmail(email.getText().toString()))
			Toast.makeText(v.getContext(), "Invalid E-mail address entered.",
					Toast.LENGTH_LONG).show();
		
		else if(!(pass.getText().toString().length()>=4 &&
					pass.getText().toString().equals(conpass.getText().toString())))
			Toast.makeText(v.getContext(), "Error: Passwords do not match\n"+
					"or password length less than 4 characters!",
					Toast.LENGTH_LONG).show();
		else
		{
			List<NameValuePair> nameValuePairs= new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("name", name.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("id", id.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("email", email.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("pass", pass.getText().toString()));
			new SignUpTask(v.getContext()).execute(nameValuePairs);
		}
	}
}

class SignUpTask extends AsyncTask<Object,Object,String>
{
	ProgressDialog dialog;
	Context con;
	
	SignUpTask(Context con)
	{	this.con=con;
		dialog = new ProgressDialog(con);
		dialog.setMessage("Getting ready...");
		dialog.setCancelable(true);
	}
	
	public void onPreExecute()
	{
		dialog.show();
		super.onPreExecute();
	}
	
	protected String doInBackground(Object... params) {
		publishProgress("Registering...");
		String back=Net.HttpPostURL("http://i-chatty.cu.cc/app/signup.php",
						(List<NameValuePair>)params[0]);
		publishProgress("Recieving response...");
		try{back=back.substring(0, back.length()-151);}
		catch(IndexOutOfBoundsException e){}
		return back;
	}
	
	protected void onProgressUpdate(Object... values) {
		dialog.setMessage(values[0].toString());
	}

	public void onPostExecute(String res) {
		String result[]=res.split("\n");
		dialog.dismiss();
		AlertDialog.Builder alert=new AlertDialog.Builder(con);
		if(!result[0].equals("good"))
		{
			alert.setTitle("Connection Error!");
			alert.setMessage(R.string.netcheckmsg);
		}
		else
		{
			if(result[1].equals("id exists"))
			{
				alert.setTitle("Username exists!");
				alert.setMessage("This username has already been taken.\n"+
									"Please try another one.");
			}
			else if(result[1].equals("email exists"))
			{
				alert.setTitle("E-mail exists!");
				alert.setMessage("This email has been used in another Chatty account.");
			}
			else if(result[1].equals("everything ok"))
			{
				alert.setTitle("Account created successfully!");
				alert.setMessage("Now swipe left and log in with your username and password "+
									"to start using Chatty ! :)");
				SignUp.name.setText("");
				SignUp.id.setText("");
				SignUp.email.setText("");
				SignUp.pass.setText("");
				SignUp.conpass.setText("");
			}
			else
			{
				alert.setTitle("Unknown Error:");
				String err="";
				for(int i=1;i<result.length;i++)
					err+=result[i]+"\n";
				alert.setMessage(err);
			}
		}
		alert.setNeutralButton("OK", null);
		alert.show();
	}
}