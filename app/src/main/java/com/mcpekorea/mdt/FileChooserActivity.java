package com.mcpekorea.mdt;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FileChooserActivity extends ActionBarActivity {
    private FileChooserAdapter adapter;

    private boolean directorySelectMode = false;
    private boolean hasFilter = false;
    private List<String> allowedExtension = new ArrayList<>();
    private File rootDirectory;

    private FileFilter filter;
    private File currentDirectory;
    private File[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);

        Bundle bundle = getIntent().getExtras();
        directorySelectMode = bundle.getBoolean("directorySelectMode", false);

        hasFilter = bundle.getBoolean("hasFilter", false);
        String[] allowed = bundle.getStringArray("allowedExtension");
        if(allowed != null && allowed.length > 0){
            allowedExtension = Arrays.asList(allowed);
        }

        this.filter = new FileFilter(){
            @Override
            public boolean accept(File file){
                if(directorySelectMode){
                    return file.isDirectory() || (hasFilter && allowedExtension.contains(getExtension(file.getName())));
                }
                return !hasFilter || allowedExtension.contains(getExtension(file.getName()));
            }
        };

        this.rootDirectory = new File(bundle.getString("rootDirectory", Environment.getExternalStorageDirectory().getAbsolutePath()));

        this.open(this.rootDirectory);
        this.adapter = new FileChooserAdapter(this, this.files);

        ListView listView = (ListView) findViewById(R.id.file_chooser_list);
        listView.setAdapter(this.adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> a, View v, final int position, long l){
                open(files[position]);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(this.directorySelectMode){
            getMenuInflater().inflate(R.menu.menu_file_chooser, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(this.directorySelectMode && item.getItemId() == R.id.action_select){
            Intent intent = new Intent();
            intent.putExtra("path", this.currentDirectory.getAbsolutePath());
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(this.currentDirectory.getAbsolutePath().equalsIgnoreCase(this.rootDirectory.getAbsolutePath())){
                setResult(RESULT_CANCELED);
                finish();
                return true;
            }else{
                this.currentDirectory = this.currentDirectory.getParentFile();
                open(this.currentDirectory);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public static String getExtension(String name){
        int index = name.lastIndexOf(".");
        return index == -1 ? null : name.substring(index + 1).toLowerCase();
    }

    public void open(File file){
        if(file.isFile()){
            Intent intent = new Intent();
            intent.putExtra("path", file.getAbsolutePath());
            setResult(RESULT_OK);
            finish();
        }

        this.currentDirectory = file;
        this.files = this.currentDirectory.listFiles(this.filter);

        this.setTitle(this.currentDirectory.getAbsolutePath());
        this.adapter.notifyDataSetChanged();
    }
}