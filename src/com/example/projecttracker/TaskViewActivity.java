package com.example.projecttracker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.dbhelper.DBAdapter;
import com.example.dbhelper.Task;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class TaskViewActivity extends Activity {
	private String task_id;
	private String project_id;
	private Button button_task_view_edit;
	private Button button_task_view_delete;
	private Button button_task_view_done;
	private Button button_task_view_export;
	private TextView textview_task_view_taskdesription;
	private TextView textview_task_view_datetime;
	private ListView task_view_listView_student;
	private ListView task_view_listView_task_schedule;
	private List<String> liststudentanme;
	private List<String> task_schedule_list;
	private	DBAdapter db;  
	private Map<String,String> task;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_task);
		
		db = new DBAdapter(this);
		
		Intent intent = getIntent();
	    Bundle bundle = intent.getExtras();
	    task_id = bundle.getString("task_id");
	    project_id = bundle.getString("project_id");
	    task=getTask(task_id);
		liststudentanme=stringToList(task.get("list_of_student"));
		task_schedule_list=stringToList(task.get("taskschedule"));	
		
		button_task_view_edit=(Button)findViewById(R.id.button_task_view_edit); 
		button_task_view_delete=(Button)findViewById(R.id.button_task_view_delete);
		button_task_view_done=(Button)findViewById(R.id.button_task_view_done);
		button_task_view_export=(Button)findViewById(R.id.button_task_view_export);
		
		TabHost tabHost = (TabHost)findViewById(R.id.tabhost1);            
		tabHost.setup();  
		
		tabHost.addTab(tabHost.newTabSpec("tab01").setIndicator("List of Student").setContent(R.id.tab11));
		tabHost.addTab(tabHost.newTabSpec("tab02").setIndicator("Task Schedule").setContent(R.id.tab12));		
	
		textview_task_view_taskdesription=(TextView)findViewById(R.id.textview_task_view_taskdesription);
		textview_task_view_datetime=(TextView)findViewById(R.id.textview_task_view_datetime);
		task_view_listView_student=(ListView)findViewById(R.id.task_view_listView_student);
		task_view_listView_task_schedule=(ListView)findViewById(R.id.task_view_listView_task_schedule);
		
		task_view_listView_student.setAdapter(new ArrayAdapter<String>(TaskViewActivity.this, android.R.layout.simple_expandable_list_item_1,liststudentanme));               		        
		task_view_listView_task_schedule.setAdapter(new ArrayAdapter<String>(TaskViewActivity.this, android.R.layout.simple_expandable_list_item_1,task_schedule_list));               		
		
		textview_task_view_taskdesription.setText("Task Desription:"+task.get("task_desription"));	
		textview_task_view_datetime.setText("Date/Time:"+task.get("date_time"));
			
		button_task_view_edit.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {				
						Intent intent2 = new Intent();
						intent2.putExtra("task_id", task_id);
						intent2.putExtra("project_id", project_id);
						intent2.setClass(TaskViewActivity.this, EditTaskActivity.class);		
					    startActivity(intent2);
						finish();
					}
				});	
				
		button_task_view_delete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				  new AlertDialog.Builder(TaskViewActivity.this).setTitle("Delete").setMessage("Are you sure to delete this Task?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int which) {
						db.open();
					    db.deleteTask(task_id);
					    db.close();
					    Toast.makeText(TaskViewActivity.this, "Delete success!", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.putExtra("project_id", project_id);
						intent.setClass(TaskViewActivity.this, TasksListActivity.class);
					    startActivity(intent);
						finish();
					  }})
					  .setNegativeButton("No",null).show(); 
			}
		});	
		
		button_task_view_done.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				new AlertDialog.Builder(TaskViewActivity.this).setTitle("Done").setMessage("Are you sure to change this task's state to \"true\" ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int which) {
						db.open();
						Task task=db.getTask(task_id);				
						task.setState("true");
						db.updateTask(task);
						db.close();
					    Toast.makeText(TaskViewActivity.this, "Save success!", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.putExtra("project_id", project_id);
						intent.setClass(TaskViewActivity.this, TasksListActivity.class);
					    startActivity(intent);
						finish();
					  }})
					  .setNegativeButton("No",null).show(); 	
			}
		});	
		
		button_task_view_export.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/task.txt");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
//				map.put("task_id", listp.getTask_id());  
//		        map.put("task_desription", listp.getTask_desription());  
//		        map.put("date_time", listp.getDate_time());  
//		        map.put("list_of_student", listp.getList_of_student());  
//		        map.put("project_id", listp.getProject_id());  
//		        map.put("taskschedule", listp.getTaskschedule());  
//		        map.put("state", listp.getState());    
				
		      	String myString=task.get("task_desription")+"\r\n"+
		      			task.get("date_time")+"\r\n"+
		      			task.get("list_of_student")+"\r\n"+
		      			task.get("taskschedule")+"\r\n"+
		      			task.get("state");				
				byte buf[]=myString.getBytes();
				try {
					out.write(buf);
					Toast.makeText(TaskViewActivity.this, "Export success! File address:"+Environment.getExternalStorageDirectory()+"/task.txt", Toast.LENGTH_LONG).show();						
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
		});	
		
	}
	
	 @Override 
	    public void onBackPressed() { 
	        super.onBackPressed(); 
	    	Intent intent = new Intent();
			intent.putExtra("project_id", project_id);
			intent.setClass(TaskViewActivity.this, TasksListActivity.class);
		    startActivity(intent);
			finish();
	    }
	
	
	public Map<String,String> getTask(String taskid)
	{
		Map<String, String> map = new HashMap<String, String>();        	
		db.open();
		Task listp= db.getTask(taskid);					
		map.put("task_id", listp.getTask_id());  
        map.put("task_desription", listp.getTask_desription());  
        map.put("date_time", listp.getDate_time());  
        map.put("list_of_student", listp.getList_of_student());  
        map.put("project_id", listp.getProject_id());  
        map.put("taskschedule", listp.getTaskschedule());  
        map.put("state", listp.getState());       
		db.close();
		return map;
	}
	
	
	public String listToString(List<String> student)
	{
		String studentnames="";
		for(int i=0;i<student.size();i++)
		{
			studentnames=studentnames+student.get(i)+"|";
		}
		return studentnames;
	}
		
	public List<String> stringToList(String names)	
	{
		List<String> students=new ArrayList<String>();
		String[] str=names.split("\\|");
		for(int i=0;i<str.length;i++)
		{
			students.add(str[i]);
		}
		return students;
	}
	

}
