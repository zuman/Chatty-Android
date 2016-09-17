package com.zap.chatty;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ChatListAdapter extends BaseAdapter   implements OnClickListener {
          
         /*********** Declare Used Variables *********/
         private Activity activity;
         private ArrayList data;
         private static LayoutInflater inflater=null;
         public Resources res;
         ChatLayout tempValues=null;
         int i=0;
          
         /*************  CustomAdapter Constructor *****************/
         public ChatListAdapter(Activity a, ArrayList d,Resources resLocal) {
              
                /********** Take passed values **********/
                 activity = a;
                 data=d;
                 res = resLocal;
              
                 /***********  Layout inflator to call external xml layout () ***********/
                  inflater = ( LayoutInflater )activity.
                                              getSystemService(Context.LAYOUT_INFLATER_SERVICE);
              
         }
      
         /******** What is the size of Passed Arraylist Size ************/
         public int getCount() {
              
             if(data.size()<=0)
                 return 1;
             return data.size();
         }
      
         public Object getItem(int position) {
             return position;
         }
      
         public long getItemId(int position) {
             return position;
         }

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
		public static class ViewHolder{
            
            public TextView id;
            public TextView name;
            public TextView msg;
            public TextView time;
     
        }
		/****** Depends upon data size called for each row , Create each ListView row *****/
        public View getView(int position, View convertView, ViewGroup parent) {
             
            View vi = convertView;
            ViewHolder holder;
             
            if(convertView==null){
                 
                /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
                vi = inflater.inflate(R.layout.chatlayout, null);
                 
                /****** View Holder Object to contain tabitem.xml file elements ******/

                holder = new ViewHolder();
                holder.name = (TextView) vi.findViewById(R.id.name);
                holder.msg=(TextView)vi.findViewById(R.id.msg);
                holder.time=(TextView)vi.findViewById(R.id.time);
                 
               /************  Set holder with LayoutInflater ************/
                vi.setTag( holder );
            }
            else 
                holder=(ViewHolder)vi.getTag();
             
            if(data.size()<=0)
            {
                holder.name.setText("No Chats");
                 
            }
            else
            {
                /***** Get each Model object from Arraylist ********/
                tempValues=null;
                tempValues = ( ChatLayout ) data.get( position );
                 
                /************  Set Model values in Holder elements ***********/

                 holder.name.setText( tempValues.getName() );
                 holder.msg.setText( tempValues.getMsg() );
                 holder.time.setText( tempValues.getTime() );
                 
                  
                 /******** Set Item Click Listner for LayoutInflater for each row *******/

                 vi.setOnClickListener(new OnItemClickListener( position ));
            }
            return vi;
        }
        private class OnItemClickListener  implements OnClickListener{           
            private int mPosition;
             
            OnItemClickListener(int position){
                 mPosition = position;
            }
             
            @Override
            public void onClick(View arg0) {

       
              ChatList sct = (ChatList)activity;

             /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

                sct.onItemClick(mPosition);
            }               
        }   
}