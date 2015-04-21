package com.example.projecttracker;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.dbhelper.DBAdapter;
import com.example.dbhelper.Task;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class WarningActivity extends Activity {
	private  ListView warninglist;
	private  Button btn_OK;
	private MediaPlayer mMediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.warninglist);   
		warninglist=(ListView)findViewById(R.id.listView_warning);
		btn_OK=(Button)findViewById(R.id.button_ok);
		
      List<Map<String, String>> warninglistData=new  ArrayList<Map<String, String>>();
      try {
			warninglistData=getwarningList();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
      warninglist.setAdapter(new SimpleAdapter(WarningActivity.this,
      		warninglistData,
      android.R.layout.simple_list_item_2,new String[]{"title", "description"},new int[]{android.R.id.text1, android.R.id.text2}));
		
  	//添加任务按钮监听
		btn_OK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {								
				Intent intent = new Intent();			
				intent.setClass(WarningActivity.this, MainActivity.class);		
			    startActivity(intent);		      
				finish();			  		         
			}
		});	
		 Thread music=new Thread(){  
	        	public void run(){   
	        		  mMediaPlayer = MediaPlayer.create(WarningActivity.this, R.raw.music);	
	        		  mMediaPlayer.start();
	        		}  
	             };
	         music.start();
	}

	public List<Map<String, String>> getwarningList() throws ParseException
	{	
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();  
        DBAdapter db = new DBAdapter(this); 		
		db.open();
		List<Task> listwarningtasks= db.getwarningTasks();
		for(int i=0;i<listwarningtasks.size();i++)
		{
			Map<String, String> map = new HashMap<String, String>();  	
            map.put("title", listwarningtasks.get(i).getTask_desription());  
            map.put("description","Due Time:"+listwarningtasks.get(i).getDate_time());  
            list.add(map);  
		}
		db.close();
		return list;
	}
	
}
