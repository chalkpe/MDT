package com.mcpekorea.mdt;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private ListView listView;
	private WorkspaceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.main_list);

	    adapter = new WorkspaceAdapter(this, null);
        listView.setAdapter(adapter);

        findViewById(R.id.main_fab_add).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(v.getContext(), CreateWorkspaceActivity.class), 0);
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Project project = new Project(data.getStringExtra("projectName"), data.getStringExtra("authorName"));

            adapter.addProject(project);
            adapter.notifyDataSetChanged();
        }
    }
}