package com.example.projecttracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.util.EncodingUtils;

import com.example.dbhelper.DBAdapter;
import com.example.dbhelper.Project;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private ListView listview;
	private List<Map<String, String>> listData;
	private int itemindex;
	private	DBAdapter db;  
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		db=new DBAdapter(this); 
        try {
			CreateText();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		listview=(ListView)findViewById(R.id.list_projects);		
		listData=getProjects();
		listview.setAdapter(new SimpleAdapter(this,
				listData,
        android.R.layout.simple_list_item_2,new String[]{"title", "description"},new int[]{android.R.id.text1, android.R.id.text2}));
		
        listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();  
				intent.putExtra("project_id",listData.get(arg2).get("project_id"));  
				intent.setClass(MainActivity.this,ProjectViewActivity.class); 
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
	
	 // 长按菜单响应函数 
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
                        android.R.layout.simple_list_item_2,new String[]{"title", "description"},new int[]{android.R.id.text1, android.R.id.text2}));
                		
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
                    android.R.layout.simple_list_item_2,new String[]{"title", "description"},new int[]{android.R.id.text1, android.R.id.text2}));
            		
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
			 System.exit(0);
	    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.	
		 menu.add(0, 0, 0, "New Project"); 
		 menu.add(0, 1, 0, "Inport Project");
		 menu.add(0, 2, 0, "about"); 
		return true;
	}
	
	@Override  
	// 菜单被选中时触发的事件  
	public boolean onOptionsItemSelected(MenuItem item) {  
	 
	  switch(item.getItemId()) { 
	  case 0: 
	   // 设置Activity的Title 
		  Intent intent = new Intent();
		  intent.setClass(MainActivity.this, NewProjectActivity.class);		
		  this.startActivity(intent);
		  finish();
	   break;
	  case 1:		 
		  Project project= readFileData(Environment.getExternalStorageDirectory()+"/project.txt");
		  db.open();
		  if(project.getProject_number()!=null)
		  {
		  db.insertProject(project);
		  }
		  listData=getProjects();
	      listview.setAdapter(new SimpleAdapter(this,
					listData,
	      android.R.layout.simple_list_item_2,new String[]{"title", "description"},new int[]{android.R.id.text1, android.R.id.text2}));
	      Toast.makeText(MainActivity.this, "Inport success!", Toast.LENGTH_SHORT).show();		
		  break;
	  case 2: 
		  AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); 
          View view = View.inflate(MainActivity.this, R.layout.about, null);       
          builder.setView(view);       
          builder.setTitle("About"); 
          builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 	   
                  @Override
                  public void onClick(DialogInterface dialog, int which) {  
                	  dialog.cancel(); 
                  } 
              });                   
          Dialog dialog = builder.create(); 
          dialog.show(); 
	   break; 	 
	  } 
	  return true;  
	}  
	
	public List<Map<String, String>> getProjects()
	{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();  
        DBAdapter db = new DBAdapter(this); 		
		db.open();
		List<Project> listproject= db.getAllProjects();
		listData = new ArrayList<Map<String, String>>();  		
		for(int i=0;i<listproject.size();i++)
		{
			Map<String, String> map = new HashMap<String, String>();  
			map.put("project_id", listproject.get(i).getProject_id());  
            map.put("title", listproject.get(i).getProject_number());  
            map.put("description", listproject.get(i).getProject_description()+"     Due Time:"+listproject.get(i).getDue_date());  
            list.add(map);  
		}
		db.close();
		return list;
	}
		
 public Project readFileData(String fileName) {  
        Project project=new Project();
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
		        project.setProject_id(UUID.randomUUID().toString());
		         while ((line = reader.readLine()) != null) {                
                    if(count==0)
		        	{
                    	project.setProject_number(line.trim());
                    }
                    if(count==1)
		        	{
                    	project.setCourse_title(line.trim());
                    }
                    if(count==2)
		        	{
                    	project.setCourse_number(line.trim());
                    }
                    if(count==3)
		        	{
                    	project.setInstructor_name(line.trim());
                    }
                    if(count==4)
		        	{
                    	project.setDue_date(line.trim());
                    }
                    if(count==5)
		        	{
                    	project.setProject_description(line.trim());
                    }
                    count++;                   
		        }  
		         fin.close(); 
		    } catch (IOException e) {  
		    	fin.close(); 
		        e.printStackTrace();  
		    }  
		    return project;           
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
        return project; 
    } 
 
 //创建文件夹及文件  
 public void CreateText() throws IOException {  
     File dir = new File(Environment.getExternalStorageDirectory()+"/project.txt");  
     File dir2=new File(Environment.getExternalStorageDirectory()+"/task.txt");  
     if (!dir.exists()) {  
           try {  
               //在指定的文件夹中创建文件  
               dir.createNewFile();  
         } catch (Exception e) {  
         }  
     }  
     if (!dir2.exists()) {  
         try {  
             //在指定的文件夹中创建文件  
             dir.createNewFile();  
       } catch (Exception e) {  
       }  
   }  
 }  
	
}
