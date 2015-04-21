package com.example.projecttracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import com.example.dbhelper.DBAdapter;
import com.example.dbhelper.Task;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class NewTaskActivity extends Activity implements View.OnTouchListener{
	private Button task_add;
	private Button add_student;
	private Button add_schedule;
	private EditText task_discription;
	private EditText date_time;
	private ListView student_listview;
	private ListView task_schedule_listview;
	private List<String> liststudentanme;
	private List<String> task_schedule_list;
	private String project_id="";
	private	DBAdapter db;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_task);
		
		db = new DBAdapter(this);
		
		Intent intent1 = getIntent();
	    Bundle bundle = intent1.getExtras();
	    project_id = bundle.getString("project_id");
		
		task_add=(Button)findViewById(R.id.button_addtask);
		add_student=(Button)findViewById(R.id.button_addstudent);
		add_schedule=(Button)findViewById(R.id.button_addschedule);
		
		TabHost tabHost = (TabHost)findViewById(R.id.tabhost);            
		tabHost.setup();  
		
		tabHost.addTab(tabHost.newTabSpec("tab01").setIndicator("List of Student").setContent(R.id.tab1));
		tabHost.addTab(tabHost.newTabSpec("tab02").setIndicator("Task Schedule").setContent(R.id.tab2));		
	
		task_discription=(EditText)findViewById(R.id.task_desription_EditText);
		date_time=(EditText)findViewById(R.id.date_time_EditText);
		student_listview=(ListView)findViewById(R.id.listView_student);
		task_schedule_listview=(ListView)findViewById(R.id.listView_task_schedule);
		
		liststudentanme=new ArrayList<String>();
		task_schedule_list=new ArrayList<String>();		
		student_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,liststudentanme));
		task_schedule_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,task_schedule_list));
		
		date_time.setOnTouchListener(this); 	
		
		//添加任务按钮监听
		task_add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				db.open();					
				Task task=new Task();
				task.setDate_time(date_time.getText().toString());
				task.setList_of_student(listToString(liststudentanme));
				task.setProject_id(project_id);
				task.setState("false");
				task.setTask_desription(task_discription.getText().toString());
				task.setTask_id(UUID.randomUUID().toString());
				task.setTaskschedule(listToString(task_schedule_list));				
				db.insertTask(task);
				Toast.makeText(NewTaskActivity.this, "Add success!", Toast.LENGTH_SHORT).show();
		      	db.close();
				Intent intent2 = new Intent();
				intent2.putExtra("project_id", project_id);
				intent2.setClass(NewTaskActivity.this, TasksListActivity.class);		
			    startActivity(intent2);
				finish();
			}
		});	
		
		//添加学生按钮监听
		add_student.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				  AlertDialog.Builder builder = new AlertDialog.Builder(NewTaskActivity.this); 
		          View view = View.inflate(NewTaskActivity.this, R.layout.add_student, null); 
		          final  EditText student_name = (EditText) view.findViewById(R.id.student_name_editText); 
		          builder.setView(view);       
		          builder.setTitle("Add Student"); 
		          builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 	   
		                  @Override
		                  public void onClick(DialogInterface dialog, int which) {  
		                	  liststudentanme.add(student_name.getText().toString());
		                	  student_listview.setAdapter(new ArrayAdapter<String>(NewTaskActivity.this, android.R.layout.simple_expandable_list_item_1,liststudentanme));               		
		                      dialog.cancel(); 
		                  } 
		              }); 
		          builder.setNegativeButton("Back", new DialogInterface.OnClickListener() { 	   
		              @Override 
		              public void onClick(DialogInterface dialog, int which) { 
		                  dialog.cancel(); 
		              } 
		          });                  
		          Dialog dialog = builder.create(); 
		          dialog.show(); 
			}
		});	
		//添加日程按钮监听
		add_schedule.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				 AlertDialog.Builder builder = new AlertDialog.Builder(NewTaskActivity.this); 
		          View view1 = View.inflate(NewTaskActivity.this, R.layout.add_taskschedule, null); 
		          final  EditText task_schedule = (EditText) view1.findViewById(R.id.task_schedule_editText); 
		          builder.setView(view1);       
		          builder.setTitle("Add Task Schedule"); 
		          builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 	   
		                  @Override
		                  public void onClick(DialogInterface dialog, int which) {  
		                	  task_schedule_list.add(task_schedule.getText().toString());
		                	  task_schedule_listview.setAdapter(new ArrayAdapter<String>(NewTaskActivity.this, android.R.layout.simple_expandable_list_item_1,task_schedule_list));               		
		                      dialog.cancel(); 
		                  } 
		              }); 
		          builder.setNegativeButton("Back", new DialogInterface.OnClickListener() { 	   
		              @Override 
		              public void onClick(DialogInterface dialog, int which) { 
		                  dialog.cancel(); 
		              } 
		          });                  
		          Dialog dialog = builder.create(); 
		          dialog.show();
			}
		});	
		
		student_listview.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0,
					View arg1, int arg2, long arg3) {
				 final int index=arg2;
				// TODO Auto-generated method stub
				      new AlertDialog.Builder(NewTaskActivity.this).setTitle("Delete").setMessage("Are you sure to delete this student?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int which) {
						  liststudentanme.remove(index);
						  student_listview.setAdapter(new ArrayAdapter<String>(NewTaskActivity.this, android.R.layout.simple_expandable_list_item_1,liststudentanme));               				                   
					  }})
					  .setNegativeButton("No",null).show(); 
				return true;
			}
			
		});
		
		task_schedule_listview.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0,
					View arg1, int arg2, long arg3) {
				 final int index=arg2;
				// TODO Auto-generated method stub
				      new AlertDialog.Builder(NewTaskActivity.this).setTitle("Delete").setMessage("Are you sure to delete this schedule?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int which) {
						  task_schedule_list.remove(index);
						  task_schedule_listview.setAdapter(new ArrayAdapter<String>(NewTaskActivity.this, android.R.layout.simple_expandable_list_item_1,liststudentanme));               				                   
					  }})
					  .setNegativeButton("No",null).show(); 
				return true;
			}
			
		});

	}
	
     @Override 
     public void onBackPressed() { 
        super.onBackPressed(); 
        Intent intent = new Intent();
		intent.putExtra("project_id", project_id);
		intent.setClass(NewTaskActivity.this, TasksListActivity.class);		
	    startActivity(intent);
		finish();     
     }

	 @Override 
	 public boolean onTouch(View v, MotionEvent event) { 
        if (event.getAction() == MotionEvent.ACTION_DOWN) {    
            AlertDialog.Builder builder2 = new AlertDialog.Builder(NewTaskActivity.this); 
            View view = View.inflate(NewTaskActivity.this, R.layout.date_time_dialog, null); 
            final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker); 
            final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker); 
            builder2.setView(view); 
   
            Calendar cal = Calendar.getInstance(); 
            cal.setTimeInMillis(System.currentTimeMillis()); 
            datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null); 
   
            timePicker.setIs24HourView(true); 
            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY)); 
            timePicker.setCurrentMinute(Calendar.MINUTE); 
   
            if (v.getId() == R.id.date_time_EditText) { 
                final int inType = date_time.getInputType(); 
                date_time.setInputType(InputType.TYPE_NULL); 
                date_time.onTouchEvent(event); 
                date_time.setInputType(inType); 
                date_time.setSelection(date_time.getText().length()); 
                   
                builder2.setTitle("Date/Time"); 
                builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() { 	   
                    @Override 
                    public void onClick(DialogInterface dialog, int which) { 
   
                        StringBuffer sb = new StringBuffer(); 
                        sb.append(String.format("%d-%02d-%02d",  
                                datePicker.getYear(),  
                                datePicker.getMonth() + 1, 
                                datePicker.getDayOfMonth())); 
                        sb.append("  "); 
                        sb.append(timePicker.getCurrentHour()) 
                        .append(":").append(timePicker.getCurrentMinute());   
                        date_time.setText(sb); 	                           
                        dialog.cancel(); 
                    } 
                }); 	                   
            } 	               
            Dialog dialog = builder2.create(); 
            dialog.show(); 
        } 	   
        return true; 
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
