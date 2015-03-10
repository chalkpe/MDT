package com.mcpekorea.mdt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.mcpekorea.hangul.Hangul;

import java.util.Random;


public class CreateProjectActivity extends ActionBarActivity {
    EditText projectNameArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        projectNameArea = (EditText) findViewById(R.id.create_project_project_name);
        projectNameArea.setText(WorkspaceActivity.samples[new Random().nextInt(WorkspaceActivity.samples.length)]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_create:
                EditText authorNameArea = (EditText) findViewById(R.id.create_project_author_name);

                String projectName = projectNameArea.getText().toString();
                String authorName = authorNameArea.getText().toString();

                if(projectName == null || projectName.equals("")){
                    projectNameArea.setError(Hangul.format(getText(R.string.error_empty).toString(), getText(R.string.create_project_project_name).toString()));
                    return true;
                }

                for(Project project : WorkspaceActivity.projects){
                    if(project.getName().equalsIgnoreCase(projectName)){
                        projectNameArea.setError(Hangul.format(getText(R.string.error_project_already_exists).toString(), projectName));
                        return true;
                    }
                }

                if(authorName == null || authorName.equals("")){
                    authorName = getText(R.string.default_authorName).toString();
                }

                Bundle bundle = new Bundle();
                bundle.putString("projectName", projectName);
                bundle.putString("authorName", authorName);

                Intent intent = new Intent();
                intent.putExtras(bundle);

                setResult(RESULT_OK, intent);
                finish();
                return true;

            case R.id.menu_cancel:
                showCancelDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showCancelDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showCancelDialog(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_close)
                .setIcon(R.drawable.ic_clear_black_48dp)
                .setMessage(R.string.dialog_message_close_without_saving)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}