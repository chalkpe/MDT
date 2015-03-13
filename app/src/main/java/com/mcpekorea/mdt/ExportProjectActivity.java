package com.mcpekorea.mdt;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mcpekorea.ptpatch.Project;
import com.mcpekorea.ptpatch.ProjectExporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ExportProjectActivity extends ActionBarActivity {
    private Project project;

    private Spinner typeSpinner;
    private EditText directoryArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_project);

        Bundle bundle = getIntent().getExtras();
        int projectIndex = bundle.getInt("projectIndex", -1);
        if(projectIndex == -1){
            finish();
        }
        project = WorkspaceActivity.projects.get(projectIndex);

        typeSpinner = (Spinner) findViewById(R.id.export_project_type_spinner);
        directoryArea = (EditText) findViewById(R.id.export_project_directory);

        directoryArea.setText(WorkspaceActivity.getSharedPreference().getString("lastExportDirectory", WorkspaceActivity.EXPORT_DIRECTORY.getAbsolutePath()));

        findViewById(R.id.export_project_select).setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                directoryArea.setText(WorkspaceActivity.EXPORT_DIRECTORY.getAbsolutePath());
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_export_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_export:
                export();
                finish();
                break;

            case R.id.menu_cancel:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            directoryArea.setText(data.getStringExtra("path"));
        }
    }

    public void selectDirectory(View view){
        Intent intent = new Intent(this, FileChooserActivity.class);
        intent.putExtra("directorySelectMode", true);
        intent.putExtra("hasFilter", true);
        intent.putExtra("allowedExtensions", new String[]{"mod"});
        startActivityForResult(intent, 0);
    }

    public void export(){
        if(this.project.getPatchesCount() == 0){
            WorkspaceActivity.toast(R.string.toast_project_empty, Toast.LENGTH_LONG);
            return;
        }

        if(this.project.getIncludedPatchesCount() == 0){
            WorkspaceActivity.toast(R.string.toast_project_nothing_to_export, Toast.LENGTH_LONG);
            return;
        }

        if(this.project.hasOverlappedPatches()){
            WorkspaceActivity.toast(R.string.toast_project_has_overlapped_patches, Toast.LENGTH_LONG);
            return;
        }

        String type =  WorkspaceActivity.exportTypes[this.typeSpinner.getSelectedItemPosition()].toLowerCase();

        File exportDirectory = null;
        String directory = directoryArea.getText().toString();

        if(directory != null && !directory.equals("")) {
            exportDirectory = new File(directory);
            if(!exportDirectory.canWrite()){
                exportDirectory = null;
            }
        }
        if(exportDirectory == null){
            exportDirectory = WorkspaceActivity.EXPORT_DIRECTORY;
        }
        WorkspaceActivity.getSharedPreference().edit().putString("lastExportDirectory", exportDirectory.getAbsolutePath()).apply();
        exportDirectory.mkdirs();

        File file = new File(exportDirectory, project.getName() + "-" + type + ".mod");
        ProjectExporter exporter = new ProjectExporter(this.project);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            fos.write(exporter.create());

            WorkspaceActivity.toast(R.string.toast_project_exported, Toast.LENGTH_LONG);
        }catch(IOException e){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title_error_occurred)
                    .setMessage(e.getMessage())
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }finally{
            try{
                if(fos != null){
                    fos.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}