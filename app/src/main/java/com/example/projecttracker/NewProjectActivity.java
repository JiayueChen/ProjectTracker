package com.example.projecttracker;

import java.util.Calendar;
import java.util.UUID;

import com.example.dbhelper.DBAdapter;
import com.example.dbhelper.Project;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class NewProjectActivity extends Activity implements View.OnTouchListener{
	private Button  add_button ;
	private EditText course_title;
	private EditText course_number;
	private EditText instructor_name;
	private EditText project_number; 
	private EditText project_description;
	private EditText due_date;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_project);
		add_button=(Button)findViewById(R.id.button_addproject);
		
		course_title=(EditText)findViewById(R.id.course_title_EditText);
		course_number=(EditText)findViewById(R.id.course_num_EditText);
		instructor_name=(EditText)findViewById(R.id.instructor_name_EditText);
		project_number=(EditText)findViewById(R.id.project_umber_editText);
		project_description=(EditText)findViewById(R.id.project_description_EditText);		
		due_date=(EditText)findViewById(R.id.due_date_editText);
		
		due_date.setOnTouchListener(this); 
		
		add_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DBAdapter db = new DBAdapter(NewProjectActivity.this);  
				db.open();
				Project project =new Project();
				project.setCourse_number( course_number.getText().toString());
				project.setCourse_title(course_title.getText().toString());
				project.setDue_date(due_date.getText().toString());
				project.setInstructor_name(instructor_name.getText().toString());
				project.setProject_description( project_description.getText().toString());
				project.setProject_id(UUID.randomUUID().toString());
				project.setProject_number( project_number.getText().toString());
				
				db.insertProject(project);
				db.close();
				Toast.makeText(NewProjectActivity.this, "Add success!", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(NewProjectActivity.this, MainActivity.class);		
			    startActivity(intent);
				finish();
			}
		});					
	}
	

	  @Override 
	    public void onBackPressed() { 
	        super.onBackPressed(); 
	        Intent intent = new Intent();
			intent.setClass(NewProjectActivity.this, MainActivity.class);	
		    startActivity(intent);
			finish();       
	    }
	
	 @Override 
	    public boolean onTouch(View v, MotionEvent event) { 
	        if (event.getAction() == MotionEvent.ACTION_DOWN) { 
	   
	            AlertDialog.Builder builder = new AlertDialog.Builder(this); 
	            View view = View.inflate(this, R.layout.date_time_dialog, null); 
	            final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker); 
	            final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker); 
	            builder.setView(view); 
	   
	            Calendar cal = Calendar.getInstance(); 
	            cal.setTimeInMillis(System.currentTimeMillis()); 
	            datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null); 
	   
	            timePicker.setIs24HourView(true); 
	            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY)); 
	            timePicker.setCurrentMinute(Calendar.MINUTE); 
	   
	            if (v.getId() == R.id.due_date_editText) { 
	                final int inType = due_date.getInputType(); 
	                due_date.setInputType(InputType.TYPE_NULL); 
	                due_date.onTouchEvent(event); 
	                due_date.setInputType(inType); 
	                due_date.setSelection(due_date.getText().length()); 
	                   
	                builder.setTitle("Due Time"); 
	                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 	   
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
	                        due_date.setText(sb); 	                           
	                        dialog.cancel(); 
	                    } 
	                }); 	                   
	            } 	               
	            Dialog dialog = builder.create(); 
	            dialog.show(); 
	        } 	   
	        return true; 
	    } 

}
