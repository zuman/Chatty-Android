package com.zap.chatty;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ChattyData extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 2;  
    private static final String DATABASE_NAME = "Chatty.db";  
    private static final String TABLE_NAME = "msg";
    private static final String USER_TABLE = "users";
    private static final String SNO = "sno";
    private static final String ID = "id";  
    private static final String NAME = "name";
    private static final String MSG = "msg";
    private static final String TIMESTAMP = "time";
    private static final String STATUS = "status";

	public ChattyData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String create="CREATE TABLE "+TABLE_NAME+" ("+SNO+" integer primary key autoincrement, "+ID+" TEXT NOT NULL,	"+MSG+" TEXT NOT NULL, "
						+TIMESTAMP+" TEXT NOT NULL, "+STATUS+" TEXT NOT NULL);";
		db.execSQL(create);
		
		create="CREATE TABLE "+USER_TABLE+" ( "+ID+" TEXT NOT NULL, "+NAME+" TEXT NOT NULL,	PRIMARY KEY("+ID+"));";
		db.execSQL(create);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists "+TABLE_NAME);
		db.execSQL("drop table if exists "+USER_TABLE);
		onCreate(db);
	}
	
	public long addUser(String id, String name) {
		try{
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues value=new ContentValues();
		value.put(ID, id);
		value.put(NAME, name);
		long status=db.insert(USER_TABLE, null, value);
		db.close();
		return status;
		}
		catch(SQLiteConstraintException e)
		{			return -1;		}
	}

	public void addMessage(String id, String message, String status) {
		try{
		SQLiteDatabase db=this.getWritableDatabase();
		String str="insert into msg("+ID+", "+MSG+", "+TIMESTAMP+", "+STATUS+
				") values('"+id+"','"+message+"',(SELECT datetime('now','+330 minutes')),'"+status+"');";
		db.execSQL(str);
		db.close();
		}
		catch(SQLException e){	}
	}
	
	public Cursor getMessages(String id)
	{
		try{
		SQLiteDatabase db = this.getReadableDatabase(); 
		String qq="select "+ID+", "+MSG+", "+TIMESTAMP+", "+STATUS+" from "+TABLE_NAME+" where "+ID+"='"+id+"' order by time asc";
		Log.d("Data",qq);
		Cursor c=db.rawQuery(qq, null);
		if (c.getCount()==0) return null;
		return c;
		}
		catch(SQLException e){return null;	}
		catch(CursorIndexOutOfBoundsException e){return null;	}
	}
	public Cursor getList()
	{
		try{
			SQLiteDatabase db = this.getReadableDatabase(); 
		String qq="select u."+NAME+", u."+ID+", m."+MSG+", max(m."+TIMESTAMP+") as "+TIMESTAMP+", "+STATUS+
				" from "+USER_TABLE+" u, "+TABLE_NAME+" m where u."+ID+"=m."+ID+" group by u."+ID+" order by "+TIMESTAMP+" desc;";
		Log.d("Data",qq);
		Cursor c=db.rawQuery(qq, null);
		if (c.getCount()==0) return null;
		return c;
		}
		catch(SQLException e){return null;	}
	}

	public Cursor getUnsent() {
		try{
			SQLiteDatabase db = this.getReadableDatabase(); 
		String qq="select "+SNO+", "+ID+", "+MSG+", min("+TIMESTAMP+") as "+TIMESTAMP+", "+STATUS+" from "+TABLE_NAME+" where "+STATUS+"='unsent';";
		Log.d("Data",qq);
		Cursor c=db.rawQuery(qq, null);
		if (c.getCount()==0) return null;
		return c;
		}
		catch(SQLException e){return null;	}
	}

	public void setUnread(int sno) {
		try{
			SQLiteDatabase db=this.getWritableDatabase();
			String str="update "+TABLE_NAME+" set "+STATUS+"='unread' where "+SNO+"="+sno+";";
			db.execSQL(str);
			db.close();
			}
			catch(SQLException e){	}
	}

	public void newMsg(String id, String msg, String timestamp) {
		try{
			SQLiteDatabase db=this.getWritableDatabase();
			String str="insert into msg("+ID+", "+MSG+", "+TIMESTAMP+", "+STATUS+
					") values('"+id+"','"+msg+"','"+timestamp+"','new');";
			db.execSQL(str);
			db.close();
			}
			catch(SQLException e){	}
	}

	public void delete() {
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("drop table if exists "+TABLE_NAME);
		db.execSQL("drop table if exists "+USER_TABLE);
		onCreate(db);
		db.close();
	}
}
