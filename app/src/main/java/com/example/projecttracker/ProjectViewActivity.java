package com.example.projecttracker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.example.dbhelper.DBAdapter;
import com.example.dbhelper.Project;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;

public class ProjectViewActivity extends Activity {

	private Button  button_project_view_edit ;
	private Button  button_project_view_delete ;
	private Button  button_project_view_tasks ;
	private Button  button_project_view_export ;
	
	private TextView textview_project_view_projectnumber;	
	private TextView textview_project_view_coursetitle;	
	private TextView textview_project_view_coursenumber;
	private TextView textview_project_view_instructorname; 
	private TextView textview_project_view_duedate;
	private EditText editText_project_view_projectdescription;
	private String project_id;
	private Map<String,String> project;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_project);
		
		button_project_view_edit=(Button)findViewById(R.id.button_project_view_edit);
		button_project_view_delete=(Button)findViewById(R.id.button_project_view_delete);
		button_project_view_tasks=(Button)findViewById(R.id.button_project_view_tasks);
		button_project_view_export=(Button)findViewById(R.id.button_project_view_Export);
		
		textview_project_view_projectnumber=(TextView)findViewById(R.id.textview_project_view_projectnumber);
		textview_project_view_coursetitle=(TextView)findViewById(R.id.textview_project_view_coursetitle);
		textview_project_view_coursenumber=(TextView)findViewById(R.id.textview_project_view_coursenumber);
		textview_project_view_instructorname=(TextView)findViewById(R.id.textview_project_view_instructorname);
		textview_project_view_duedate=(TextView)findViewById(R.id.textview_project_view_duedate);		
		editText_project_view_projectdescription=(EditText)findViewById(R.id.editText_project_view_projectdescription);		
		editText_project_view_projectdescription.setEnabled(false);
		editText_project_view_projectdescription.setFocusable(false);
		
		 Intent intent = getIntent();
	     Bundle bundle = intent.getExtras();
	     project_id = bundle.getString("project_id");
	     project=getProject(project_id);
	     
	     textview_project_view_coursetitle.setText("Course Title:"+project.get("course_title"));	     
	     textview_project_view_coursenumber.setText("Course Number:"+project.get("course_number"));	   	     
	     textview_project_view_instructorname.setText("Instructor Name:"+project.get("instructor_name"));	     
	     textview_project_view_projectnumber.setText("Project Number:"+project.get("project_number"));	     	  
	     textview_project_view_duedate.setText("Due Date:"+project.get("due_date"));	   
	     editText_project_view_projectdescription.setText(project.get("project_description"));
	     
	     
	     button_project_view_edit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				Intent intent1 = new Intent();
				intent1.putExtra("project_id", project_id);
				intent1.setClass(ProjectViewActivity.this, EditProjectActivity.class);
			    startActivity(intent1);
				finish();
			}
		});		
	     	     
	     button_project_view_delete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				  new AlertDialog.Builder(ProjectViewActivity.this).setTitle("Delete").setMessage("Are you sure to delete this project?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int which) {
					    deleteproject(project_id);
					    Toast.makeText(ProjectViewActivity.this, "Delete success!", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(ProjectViewActivity.this, MainActivity.class);
					    startActivity(intent);
						finish();
					  }})
					  .setNegativeButton("No",null).show(); 
			}
		});		
	     
	     button_project_view_tasks.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {				
					Intent intent = new Intent();  
					intent.putExtra("project_id",project_id);  
					intent.setClass(ProjectViewActivity.this,TasksListActivity.class); 
					startActivity(intent);	
					finish();
				}
			});		
	     
	     button_project_view_export.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {					
					FileOutputStream out = null;
					try {
						out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/project.txt");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			      	String myString=project.get("project_number")+"\r\n"+
					 project.get("course_title")+"\r\n"+
					 project.get("course_number")+"\r\n"+
					 project.get("instructor_name")+"\r\n"+
					 project.get("due_date")+"\r\n"+
					 project.get("project_description");
					byte buf[]=myString.getBytes();
					try {
						out.write(buf);
						Toast.makeText(ProjectViewActivity.this, "Export success! File address:"+Environment.getExternalStorageDirectory()+"/project.txt", Toast.LENGTH_LONG).show();						
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
			intent.setClass(ProjectViewActivity.this, MainActivity.class);
		    startActivity(intent);
			finish();
	    }
	
	public boolean deleteproject(String id)
	{
        DBAdapter db = new DBAdapter(this); 		
		db.open();
		db.deleteProject(id);
		db.close();
		return true;
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

}
