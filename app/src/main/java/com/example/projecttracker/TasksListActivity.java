package com.example.projecttracker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.dbhelper.DBAdapter;
import com.example.dbhelper.Project;
import com.example.dbhelper.Task;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TasksListActivity extends Activity {
    private TextView textView;
    private ListView listview;
	private String project_id ;
	private List<Map<String, String>> listData;
	private int itemindex;
	private  DBAdapter db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tasks_list);
		db = new DBAdapter(this); 	
		textView=(TextView)findViewById(R.id.TextView_Tasks);
		listview=(ListView)findViewById(R.id.listview_Tasks);
		
		Intent intent = getIntent();
	    Bundle bundle = intent.getExtras();
	    project_id = bundle.getString("project_id");
	    Map<String,String> project=getProject(project_id);
	    textView.setText(project.get("project_number"));
	    
	    listData=getTasks(project_id);
		listview.setAdapter(new SimpleAdapter(this,
				listData,
        android.R.layout.simple_list_item_2,new String[]{"task_desription", "description"},new int[]{android.R.id.text1, android.R.id.text2}));
		  
	    listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();  
				intent.putExtra("task_id",listData.get(arg2).get("task_id"));  
				intent.putExtra("project_id",project_id);
				intent.setClass(TasksListActivity.this,TaskViewActivity.class); 
				startActivity(intent);
				finish();
			}       
        });
	    
	    listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() { 
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
			        ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				 menu.setHeaderTitle("Action");
				 menu.add(0, 0, 0, "up"); 
				 menu.add(0, 1, 0, "down"); 
				 AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
				 itemindex=	info.position;				
			} 
    }); 
	    
}

	 // �����˵���Ӧ���� 
	 @Override 
   public boolean onContextItemSelected(MenuItem item) { 
        
           switch (item.getItemId()) { 
           case 0: 
   //  Toast.makeText(MainActivity.this,  "up:"+itemindex, Toast.LENGTH_SHORT).show(); 
                   if(itemindex>0)
                   {
                   	Map<String, String> data=listData.get(itemindex);
                   	listData.remove(itemindex);
                   	listData.add(itemindex-1, data);
                   	listview.setAdapter(new SimpleAdapter(this,
               				listData,
                       android.R.layout.simple_list_item_2,new String[]{"task_desription", "description"},new int[]{android.R.id.text1, android.R.id.text2}));
               		
                   }                                                       
                   break; 
           case 1: 
           	if(itemindex<listData.size()-1)
               {
               	Map<String, String> data=listData.get(itemindex);
               	listData.remove(itemindex);
               	listData.add(itemindex+1, data);
               	listview.setAdapter(new SimpleAdapter(this,
           				listData,
                   android.R.layout.simple_list_item_2,new String[]{"task_desription", "description"},new int[]{android.R.id.text1, android.R.id.text2}));
           		
               }      
                   break;        
           default: 
                   break; 
           } 

           return super.onContextItemSelected(item); 

   } 
	
	  @Override 
	    public void onBackPressed() { 
	        super.onBackPressed(); 
	        Intent intent = new Intent();
			intent.setClass(TasksListActivity.this, ProjectViewActivity.class);
			intent.putExtra("project_id",project_id);			
		    startActivity(intent);
			finish();       
	    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, 0, 0, "New Task"); 
		menu.add(0, 1, 0, "Import Task");
		return true;
	}
	
	@Override  
	// �˵���ѡ��ʱ�������¼�  
	public boolean onOptionsItemSelected(MenuItem item) {   
	  switch(item.getItemId()) {
	  case 0: 
	   // ����Activity��Title 
		  Intent intent = new Intent();
		  intent.setClass(TasksListActivity.this, NewTaskActivity.class);	
		  intent.putExtra("project_id",project_id);  
		  startActivity(intent);
		  finish();
	   break; 	 
	  case 1: 
		  Task task= readFileData(Environment.getExternalStorageDirectory()+"/task.txt");
		  db.open();
		  if(task.getTask_desription()!=null)
		  {
		      db.insertTask(task);
		  }
		  listData=getTasks(project_id);
	      listview.setAdapter(new SimpleAdapter(this,
					listData,
	      android.R.layout.simple_list_item_2,new String[]{"task_desription", "description"},new int[]{android.R.id.text1, android.R.id.text2}));
	      Toast.makeText(TasksListActivity.this, "Import success!", Toast.LENGTH_SHORT).show();
		   break; 	 
	  }
	  return true;  
	}  	
	
	public List<Map<String, String>> getTasks(String projectid)
	{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();        
		db.open();
		List<Task> listp= db.getAllTasksForProject(projectid);
		listData = new ArrayList<Map<String, String>>();  		
		for(int i=0;i<listp.size();i++)
		{
			Map<String, String> map = new HashMap<String, String>();
			map.put("task_id", listp.get(i).getTask_id());
            map.put("task_desription", listp.get(i).getTask_desription());
            map.put("description","Time:"+listp.get(i).getDate_time()  +"       Done:"+listp.get(i).getState());  
            list.add(map);  
		}
		db.close();
		return list;
	}
		
	public Map<String,String> getProject(String id)
	{
		Map<String, String> map = new HashMap<String, String>();  
        DBAdapter db = new DBAdapter(this); 	
		db.open();		
		Project project= db.getProject(id);		
			
			map.put("project_id", project.getProject_id());  
            map.put("course_title", project.getCourse_title());  
            map.put("course_number", project.getCourse_number());  
            map.put("instructor_name", project.getInstructor_name());  
            map.put("project_number", project.getProject_number());  
            map.put("project_description", project.getProject_description()); 
            map.put("due_date", project.getDue_date()); 		
		db.close();
		return map;
	}

	
	public Task readFileData(String fileName) {  
		Task task=new Task();
        try { 
        	FileInputStream  fin = new FileInputStream (fileName); 
            InputStreamReader inputStreamReader = null;  			   
		    try {  
		        inputStreamReader = new InputStreamReader(fin, "utf-8");  
		    } catch (UnsupportedEncodingException e1) {  
		        e1.printStackTrace();  
		    } 
            BufferedReader reader = new BufferedReader(inputStreamReader);   
            String line;  
		    try {
		    	int count=0;
		    	task.setTask_id(UUID.randomUUID().toString());
		    	
//		      	String myString=task.get("task_desription")+"\r\n"+
//		      			task.get("date_time")+"\r\n"+
//		      			task.get("list_of_student")+"\r\n"+
//		      			task.get("taskschedule")+"\r\n"+
//		      			task.get("state");		
		         while ((line = reader.readLine()) != null) {                
                    if(count==0)
		        	{
                    	task.setTask_desription(line.trim());
                    }
                    if(count==1)
		        	{
                    	task.setDate_time(line.trim());
                    }
                    if(count==2)
		        	{
                    	task.setList_of_student(line.trim());
                    }
                    if(count==3)
		        	{
                    	task.setTaskschedule(line.trim());
                    }
                    if(count==4)
		        	{
                    	task.setState(line.trim());
                    }
                    task.setProject_id(project_id);                 
                    count++;                   
		        }  
		         fin.close(); 
		    } catch (IOException e) {  
		    	fin.close(); 
		        e.printStackTrace();  
		    }  
		    return task;           
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
        return task; 
    } 
	
}
