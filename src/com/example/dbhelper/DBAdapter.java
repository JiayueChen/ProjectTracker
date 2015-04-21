package com.example.dbhelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;  
import android.database.Cursor;  
import android.database.SQLException;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;  
  
public class DBAdapter {  
    static final String TAG = "DBAdapter";        
    static final String DATABASE_NAME = "MyDB";  
    static final int DATABASE_VERSION = 1;  
      
    static final String DATABASE_CREATE_PROJECT =   
            "create table projects( project_id text primary key, " +   
            "course_title text not null, course_number text not null," +
            "instructor_name text not null, project_number text not null," +
            "project_description text not null, due_date text not null);";  
    static final String DATABASE_CREATE_TASK=
    		"create table tasks( task_id text primary key, " +   
    	    "task_desription text not null, date_time text not null," +
    	    "list_of_student text not null,taskschedule text not null," +
    	    "project_id text not null,state text not null);"; 
       
    final Context context;  
      
    DatabaseHelper DBHelper;  
    SQLiteDatabase db;  
      
    public DBAdapter(Context cxt)  
    {  
        this.context = cxt;  
        DBHelper = new DatabaseHelper(context);  
    }  
      
    private static class DatabaseHelper extends SQLiteOpenHelper  
    {  
  
        DatabaseHelper(Context context)  
        {  
            super(context, DATABASE_NAME, null, DATABASE_VERSION);  
        }  
        @Override  
        public void onCreate(SQLiteDatabase db) {  
            // TODO Auto-generated method stub  
            try  
            {  
                db.execSQL(DATABASE_CREATE_PROJECT);  
                db.execSQL(DATABASE_CREATE_TASK);  
            }  
            catch(SQLException e)  
            {  
                e.printStackTrace();  
            }  
        }  
  
        @Override  
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
            // TODO Auto-generated method stub        
            db.execSQL("DROP TABLE IF EXISTS projects");  
            onCreate(db);  
        }  
    }  
      
    //open the database  
    public DBAdapter open() throws SQLException  
    {  
        db = DBHelper.getWritableDatabase();  
        return this;  
    }  
    //close the database  
    public void close()  
    {  
        DBHelper.close();  
    }  
      
    //insert a project into the database  
    public boolean insertProject(Project project)  
    {     	
    	try{
    	 db.execSQL("INSERT INTO projects VALUES (?,?,?,?,?,?,?)", new Object[]{project.getProject_id(),
    			 project.getCourse_title(), project.getCourse_number(),
    			 project.getInstructor_name(), project.getProject_number(),
    			 project.getProject_description(), project.getDue_date()});     
    	}catch(Exception e)
    	{
    		
    	}
        return true;  
    }  
    
    //delete a particular contact  
    public boolean deleteProject(String project_id)  
    {  
    	 db.execSQL("DELETE FROM projects WHERE project_id =?", new Object[]{project_id});    
        return true;  
    }  
    
    //retreves all the contacts  
    public List<Project> getAllProjects()  
    {  
    	 List<Project> allProjects=new ArrayList<Project>();    	
    	 Cursor c = db.rawQuery("SELECT * FROM projects", null);  
         while (c.moveToNext()) {
        	 Project project=new Project();
        	 project.setProject_id(c.getString(c.getColumnIndex("project_id")));  
        	 project.setCourse_number(c.getString(c.getColumnIndex("course_number")));
        	 project.setCourse_title(c.getString(c.getColumnIndex("course_title")));
        	 project.setDue_date(c.getString(c.getColumnIndex("due_date")));
        	 project.setInstructor_name(c.getString(c.getColumnIndex("instructor_name")));
        	 project.setProject_description(c.getString(c.getColumnIndex("project_description")));
        	 project.setProject_number(c.getString(c.getColumnIndex("project_number")));
        	 allProjects.add(project);
         }     	
        return allProjects;  
    }  
    
    //retreves a particular Project  
    public Project getProject(String project_id) throws SQLException  
    {  
    	 Project project=new Project();    	
    	 Cursor c = db.rawQuery("SELECT * FROM projects WHERE project_id= ?", new String[]{project_id});                	              
         if (c != null)  
         {
             c.moveToFirst();  
	         project.setProject_id(c.getString(c.getColumnIndex("project_id")));  
		   	 project.setCourse_number(c.getString(c.getColumnIndex("course_number")));
		   	 project.setCourse_title(c.getString(c.getColumnIndex("course_title")));
		   	 project.setDue_date(c.getString(c.getColumnIndex("due_date")));
		   	 project.setInstructor_name(c.getString(c.getColumnIndex("instructor_name")));
		   	 project.setProject_description(c.getString(c.getColumnIndex("project_description")));
		   	 project.setProject_number(c.getString(c.getColumnIndex("project_number")));
         }
        return project;  
    }  
    
    //updates a project
    public boolean updateProject(Project project)  
    {      	
   	 db.execSQL("UPDATE projects SET course_title=?,course_number=?," +
   	 		"instructor_name=?,project_number=?,project_description=?,due_date=? WHERE project_id=?" ,
   	 		new Object[]{
			 project.getCourse_title(), project.getCourse_number(),
			 project.getInstructor_name(), project.getProject_number(),
			 project.getProject_description(), project.getDue_date(),project.getProject_id()});  
   	 return true;
    } 
    
    
 //////////////////////////////////////////////////////////////////////////////////////////////   

    //insert a Task into the database  
    public boolean insertTask(Task task)  
    {     	
    	try{
       	 db.execSQL("INSERT INTO tasks VALUES (?,?,?,?,?,?,?)", new Object[]{task.getTask_id(),
       			task.getTask_desription(), task.getDate_time(),
       			task.getList_of_student(), task.getTaskschedule(),
       			task.getProject_id(), task.getState()});     
       	}catch(Exception e)
       	{
       		
       	}
           return true;         
    }
    
    //delete a particular Task  
    public boolean deleteTask(String task_id)  
    {  
    	 db.execSQL("DELETE FROM tasks WHERE task_id =?", new Object[]{task_id});       	
        return true;  
    }  
     
    //get all the Tasks  for Project
    public List<Task> getAllTasksForProject(String project_id)  
    {  
    	List<Task> allTasksForProject=new ArrayList<Task>();    	
   	    Cursor c = db.rawQuery("SELECT * FROM tasks WHERE project_id=?", new String[]{project_id});  
        while (c.moveToNext()) {
         Task task=new Task();
         task.setTask_id(c.getString(c.getColumnIndex("task_id")));  
         task.setDate_time(c.getString(c.getColumnIndex("date_time")));
         task.setList_of_student(c.getString(c.getColumnIndex("list_of_student")));
         task.setProject_id(c.getString(c.getColumnIndex("project_id")));
         task.setState(c.getString(c.getColumnIndex("state")));
         task.setTask_desription(c.getString(c.getColumnIndex("task_desription")));
         task.setTaskschedule(c.getString(c.getColumnIndex("taskschedule")));
         allTasksForProject.add(task);
        }     	
        return allTasksForProject;  
    }  
    
    //retreves a particular Task,
    public Task getTask(String task_id) throws SQLException  
    {  
    	Task task=new Task();    	
   	    Cursor c = db.rawQuery("SELECT * FROM tasks WHERE task_id=?", new String[]{task_id});  
   	 if (c != null)  
   	 {
         c.moveToFirst(); 
         task.setTask_id(c.getString(c.getColumnIndex("task_id")));  
         task.setDate_time(c.getString(c.getColumnIndex("date_time")));
         task.setList_of_student(c.getString(c.getColumnIndex("list_of_student")));
         task.setProject_id(c.getString(c.getColumnIndex("project_id")));
         task.setState(c.getString(c.getColumnIndex("state")));
         task.setTask_desription(c.getString(c.getColumnIndex("task_desription")));
         task.setTaskschedule(c.getString(c.getColumnIndex("taskschedule")));
   	 }        
        return task;  
    }    
    
    //updates a Task
    public boolean updateTask(Task task)  
    {     	
    	 db.execSQL("UPDATE tasks SET task_desription = ?, date_time = ?,list_of_student = ?, taskschedule = ?,project_id=?,state=? WHERE task_id=?", new Object[]{
        			task.getTask_desription(), task.getDate_time(),
        			task.getList_of_student(), task.getTaskschedule(),
        			task.getProject_id(), task.getState(),task.getTask_id()});      
    	 return true;
    }     
 ////////////////////////////////////////////////////////////////////////////////////////////////////////    
	public List<Task> getwarningTasks() throws ParseException {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 
		List<Task> tasks=new ArrayList<Task>();    	
   	    Cursor c = db.rawQuery("SELECT * FROM tasks", null);     	
        while(c.moveToNext())
        {        	
        	 //get the date
        	 String datetime=c.getString(c.getColumnIndex("date_time"));       	
             int days=daysBetween(sdf.format(new Date()),datetime.split(" ")[0].trim());  
             if(days<=2&&days>=0)
             {
            	 Task task=new Task();
            	 task.setTask_id(c.getString(c.getColumnIndex("task_id")));  
                 task.setDate_time(c.getString(c.getColumnIndex("date_time")));
                 task.setList_of_student(c.getString(c.getColumnIndex("list_of_student")));
                 task.setProject_id(c.getString(c.getColumnIndex("project_id")));
                 task.setState(c.getString(c.getColumnIndex("state")));
                 task.setTask_desription(c.getString(c.getColumnIndex("task_desription")));
                 task.setTaskschedule(c.getString(c.getColumnIndex("taskschedule")));
                 tasks.add(task);
             }                   
        }            
		return tasks;
	}
    
	 public static int daysBetween(String smdate,String bdate) throws ParseException{  
	        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
	        Calendar cal = Calendar.getInstance();    
	        cal.setTime(sdf.parse(smdate));    
	        long time1 = cal.getTimeInMillis();                 
	        cal.setTime(sdf.parse(bdate));    
	        long time2 = cal.getTimeInMillis();         
	        long between_days=(time2-time1)/(1000*3600*24);  
	            
	       return Integer.parseInt(String.valueOf(between_days));     
	    }  
	
}  