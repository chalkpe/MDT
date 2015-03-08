package com.mcpekorea.mdt;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    public static final File ROOT_DIRECTORY = new File(Environment.getExternalStorageDirectory(), "MDT");
    public static final File PROJECTS_DIRECTORY = new File(ROOT_DIRECTORY, "projects");
    public static final File EXPORT_DIRECTORY = new File(ROOT_DIRECTORY, "export");

    private ListView listView;
	private WorkspaceAdapter adapter;
    private ArrayList<Project> projects;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!ROOT_DIRECTORY.exists()) {
            ROOT_DIRECTORY.mkdirs();
        }

        try {
            //.mod isn't video file
            new File(ROOT_DIRECTORY, ".nomedia").createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }

        findViewById(R.id.main_fab_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(v.getContext(), CreateWorkspaceActivity.class), 0);
            }
        });

        listView = (ListView) findViewById(R.id.main_list);

        File[] projectFiles = PROJECTS_DIRECTORY.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().endsWith(".json");
            }
        });

        projects = new ArrayList<Project>(projectFiles.length);
        for(File file : projectFiles){
            try{
                projects.add(Project.createFromJSON(new FileInputStream(file)));
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }

	    adapter = new WorkspaceAdapter(this, projects);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.menu_settings){
            Toast.makeText(getApplicationContext(), "Settings is not available now", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            Project project = new Project(data.getStringExtra("projectName"), data.getStringExtra("authorName"));

            adapter.addProject(project);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(Project project : projects){
            File projectFile = new File(PROJECTS_DIRECTORY, project.getName() + ".json");
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(projectFile));
                bw.write(project.toJSON().toString(4));
            }catch(IOException | JSONException e){
                e.printStackTrace();
            }
        }
    }
}