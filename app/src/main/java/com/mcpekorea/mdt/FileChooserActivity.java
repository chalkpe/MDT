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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FileChooserActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private FileChooserAdapter adapter;

    private boolean directorySelectMode = false;
    private boolean hasFilter = false;
    private List<String> allowedExtensions = new ArrayList<>();
    private File rootDirectory;

    private FileFilter filter;
    private File currentDirectory;
    private List<File> files;

    public static final Comparator<File> COMPARATOR = new Comparator<File>(){
        @Override
        public int compare(File a, File b){
            if(a.isDirectory() && b.isFile()){
                return -1;
            }
            if(a.isFile() && b.isDirectory()){
                return 1;
            }
            return a.getName().compareToIgnoreCase(b.getName());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);

        Bundle bundle = getIntent().getExtras();
        directorySelectMode = bundle.getBoolean("directorySelectMode", false);

        hasFilter = bundle.getBoolean("hasFilter", false);
        String[] allowed = bundle.getStringArray("allowedExtensions");
        if(allowed != null && allowed.length > 0){
            allowedExtensions = Arrays.asList(allowed);
        }

        this.filter = new FileFilter(){
            @Override
            public boolean accept(File file){
                if(directorySelectMode){
                    return file.isDirectory() || (hasFilter && allowedExtensions.contains(getExtension(file.getName())));
                }
                return !hasFilter || allowedExtensions.contains(getExtension(file.getName()));
            }
        };

        this.rootDirectory = new File(bundle.getString("rootDirectory", Environment.getExternalStorageDirectory().getAbsolutePath()));

        this.files = new ArrayList<>();
        this.adapter = new FileChooserAdapter(this, this.files);

        open(this.rootDirectory);

        ListView listView = (ListView) findViewById(R.id.file_chooser_list);
        listView.setAdapter(this.adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        if(this.directorySelectMode){
            getMenuInflater().inflate(R.menu.menu_file_chooser, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_select:
                if(this.directorySelectMode) {
                    Intent intent = new Intent();
                    intent.putExtra("path", this.currentDirectory.getAbsolutePath());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            goUpper();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static String getExtension(String name){
        int index = name.lastIndexOf(".");
        return index == -1 ? null : name.substring(index + 1).toLowerCase();
    }

    @Override
    public void onItemClick(AdapterView<?> a, View v, final int position, long l){
        File file = files.get(position);
        if(file.isDirectory()) {
            open(file);
        }else if(!this.directorySelectMode){
            Intent intent = new Intent();
            intent.putExtra("path", file.getAbsolutePath());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void open(File file){
        if(!file.isDirectory()){
            return;
        }

        this.currentDirectory = file;
        this.setTitle(this.currentDirectory.getAbsolutePath());

        this.files.clear();
        File[] fileArray = this.currentDirectory.listFiles(this.filter);
        if(fileArray.length > 0){
            this.files.addAll(Arrays.asList(fileArray));
            Collections.sort(this.files, FileChooserActivity.COMPARATOR);
        }
        this.adapter.notifyDataSetChanged();
    }

    public void goUpper(){
        if(this.currentDirectory.getAbsolutePath().equalsIgnoreCase(this.rootDirectory.getAbsolutePath())){
            setResult(RESULT_CANCELED);
            finish();
        }else{
            this.currentDirectory = this.currentDirectory.getParentFile();
            open(this.currentDirectory);
        }
    }
}