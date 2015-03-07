package com.mcpekorea.mdt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private ListView listView;
	private WorkspaceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

	    adapter = new WorkspaceAdapter(this, null);
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
        if(id == R.id.menu_add_item){
	        final LinearLayout layout = (LinearLayout) View.inflate(this, R.layout.workspace_creator_layout, null);
	        new AlertDialog.Builder(this)
			.setView(layout)
			.setTitle("Project specification")
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface d, int position) {
					EditText nameArea = (EditText) layout.findViewById(R.id.name_area);
					String name = nameArea.getText().toString();
					if (name.equals("")) {
						Toast.makeText(MainActivity.this, "Project name cannot be blank.", Toast.LENGTH_LONG).show();
						return;
					}

					EditText authorArea = (EditText)layout.findViewById(R.id.author_area);
					String author = authorArea.getText().toString();
					Project project = new Project(name, author);

					adapter.addProject(project);
					adapter.notifyDataSetChanged();
				}
			}).show();
	        return true;
        }
        return super.onOptionsItemSelected(item);
    }
}