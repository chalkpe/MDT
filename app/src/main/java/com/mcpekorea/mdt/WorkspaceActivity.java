package com.mcpekorea.mdt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mcpekorea.hangul.Hangul;
import com.mcpekorea.peanalyzer.PEAnalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class WorkspaceActivity extends ActionBarActivity implements View.OnClickListener {
    public static WorkspaceActivity that;

    public static final File ROOT_DIRECTORY = new File(Environment.getExternalStorageDirectory(), "MDT");
    public static final File PROJECTS_DIRECTORY = new File(ROOT_DIRECTORY, "projects");
    public static final File EXPORT_DIRECTORY = new File(ROOT_DIRECTORY, "export");

    public static Typeface inconsolata, inconsolataBold;
    public static String[] samples, exportTypes;

    public static ArrayList<Project> projects;
	private WorkspaceAdapter adapter;
    public static PEAnalyzer analyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace);

        that = this;

        initResources();
        initDirectories();

        findViewById(R.id.workspace_fab_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(v.getContext(), CreateProjectActivity.class), 0);
            }
        });

        final ListView listView = (ListView) findViewById(R.id.workspace_list);

        File[] projectFiles = PROJECTS_DIRECTORY.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.length() > 0 && file.getName().toLowerCase().endsWith(".json");
            }
        });

        if(projectFiles == null || projectFiles.length == 0){
            projects = new ArrayList<>();
        }else{
            projects = new ArrayList<>(projectFiles.length);
            for(File file : projectFiles){
                try{
                    projects.add(Project.createFromJSON(new FileInputStream(file)));
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }

        sortProjects();

	    adapter = new WorkspaceAdapter(this, projects, this);
        listView.setAdapter(adapter);

	    listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
		    @Override
		    public void onItemClick(AdapterView<?> a, View v, final int position, long l) {
                Intent intent = new Intent(WorkspaceActivity.this, ProjectActivity.class);
                intent.putExtra("projectIndex", position);
                startActivityForResult(intent, 1);
		    }
	    });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_workspace, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.menu_settings){
	        startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0 && resultCode == RESULT_OK){
            Project project = new Project(data.getStringExtra("projectName"), data.getStringExtra("authorName"));

            projects.add(project);
            adapter.notifyDataSetChanged();
            sortProjects();
            saveProjects();
        }else if(requestCode == 1 && resultCode == RESULT_OK){
            adapter.notifyDataSetChanged();
            saveProjects();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        saveProjects();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.list_item_image:
                final int position = (Integer) v.getTag();
                final Project project = projects.get(position);

                new AlertDialog.Builder(WorkspaceActivity.this)
                        .setTitle(R.string.dialog_title_confirm_delete)
                        .setMessage(Hangul.format(getResources().getString(R.string.dialog_message_confirm_project_delete), project.getName()))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int i) {
                                projects.remove(position);
                                adapter.notifyDataSetChanged();

                                File file = new File(PROJECTS_DIRECTORY, project.getName() + ".json");

                                boolean succeed = false;
                                if (file.exists()) {
                                    succeed = file.delete();
                                }

                                if (succeed) {
                                    Toast.makeText(WorkspaceActivity.this, Hangul.format(getString(R.string.toast_project_deleted), project.getName()), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                break;
        }
    }

    public void initResources(){
        inconsolata = Typeface.createFromAsset(getAssets(), "Inconsolata/Inconsolata-Regular.ttf");
        inconsolataBold = Typeface.createFromAsset(getAssets(), "Inconsolata/Inconsolata-Bold.ttf");

        samples = getResources().getStringArray(R.array.samples);
        exportTypes = getResources().getStringArray(R.array.export_types);

        try{
            analyzer = new PEAnalyzer(new File(this.getCacheDir(), getText(R.string.default_cacheDirectory).toString()));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void initDirectories(){
        boolean succeed = false;

        if(!ROOT_DIRECTORY.exists()) {
            succeed = ROOT_DIRECTORY.mkdir();
        }

        if(!PROJECTS_DIRECTORY.exists()) {
            succeed = succeed && PROJECTS_DIRECTORY.mkdir();
        }

        if(!EXPORT_DIRECTORY.exists()) {
            succeed = succeed && EXPORT_DIRECTORY.mkdir();
        }

        try {
            //.mod isn't video file
            succeed = succeed || new File(ROOT_DIRECTORY, ".nomedia").createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }

        if(succeed){
            Log.d(getText(R.string.app_name).toString(), "Directories are created!");
        }
    }

    public void sortProjects(){
        Collections.sort(projects, new Comparator<Project>() {
            @Override
            public int compare(Project a, Project b) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
    }

    public void saveProjects(){
        for(Project project : projects){
            File projectFile = new File(PROJECTS_DIRECTORY, project.getName() + ".json");
            BufferedWriter bw = null;

            try{
                bw = new BufferedWriter(new FileWriter(projectFile));
                bw.write(project.toJSON().toString());
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                try{
                    if(bw != null){
                        bw.close();
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void toast(final int resId, final int length){
        that.runOnUiThread(new Runnable(){
            public void run(){
                Toast.makeText(that, resId, length).show();
            }
        });
    }
}