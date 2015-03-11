package com.mcpekorea.mdt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import com.mcpekorea.hangul.Hangul;

public class CreatePatchActivity extends ActionBarActivity {
    private int patchIndex;
    private EditText offsetArea, valueArea, memoArea;
	private String beforeOffset = "", beforeValue = "", beforeMemo = "";
	private boolean beforeExcluded = false;
    private CheckBox isExcludedBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_patch);

        offsetArea = (EditText) findViewById(R.id.create_patch_offset);
        valueArea = (EditText) findViewById(R.id.create_patch_value);
        memoArea = (EditText) findViewById(R.id.create_patch_memo);
        isExcludedBox = (CheckBox) findViewById(R.id.create_patch_is_excluded);

        offsetArea.setTypeface(WorkspaceActivity.inconsolata);
        valueArea.setTypeface(WorkspaceActivity.inconsolata);

        Bundle bundle = getIntent().getExtras();
        patchIndex = bundle.getInt("patchIndex", -1);

        if(patchIndex >= 0){ //Edit mode
            setTitle(R.string.title_activity_edit_patch);

	        beforeOffset = bundle.getString("offsetString");
            offsetArea.setText(beforeOffset);

	        beforeValue = bundle.getString("valueString");
            valueArea.setText(beforeValue);

	        beforeMemo = bundle.getString("memo");
            memoArea.setText(beforeMemo);

	        beforeExcluded = bundle.getBoolean("isExcluded", false);
            isExcludedBox.setChecked(beforeExcluded);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate((patchIndex >= 0) ? R.menu.menu_edit_patch : R.menu.menu_create_patch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_save:
                String offsetString = offsetArea.getText().toString();
                String valueString = valueArea.getText().toString();
                String memo = memoArea.getText().toString();

                if(offsetString == null || offsetString.equals("")){
                    offsetArea.setError(Hangul.format(getText(R.string.error_empty).toString(), getText(R.string.create_patch_offset).toString()));
                    return true;
                }

                if(valueString == null || valueString.equals("")){
                    valueString = getText(R.string.default_value).toString();
                }

                if(memo == null || memo.equals("")){
                    memo = "";
                }

                Bundle bundle = new Bundle();
                bundle.putInt("patchIndex", patchIndex);
                bundle.putString("offsetString", offsetString.toUpperCase());
                bundle.putString("valueString", valueString.toUpperCase());
                bundle.putString("memo", memo.trim());
                bundle.putBoolean("isExcluded", isExcludedBox.isChecked());
                bundle.putBoolean("deleted", false);

                Intent intent = new Intent();
                intent.putExtras(bundle);

                setResult(RESULT_OK, intent);
                finish();
                return true;

            case R.id.menu_delete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_title_confirm_delete)
                        .setMessage(R.string.dialog_message_confirm_patch_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int i) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("patchIndex", patchIndex);
                                bundle.putBoolean("deleted", true);

                                Intent intent = new Intent();
                                intent.putExtras(bundle);

                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;

            case R.id.menu_cancel:
	            if(!(beforeOffset.equals(offsetArea.getText().toString()) && beforeValue.equals(valueArea.getText().toString()) && beforeExcluded == isExcludedBox.isChecked())) {
		            showCancelDialog();
	            }else{
		            setResult(RESULT_CANCELED);
		            finish();
	            }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(!(beforeOffset.equals(offsetArea.getText().toString()) && beforeValue.equals(valueArea.getText().toString()) && beforeExcluded == isExcludedBox.isChecked())) {
				showCancelDialog();
			}else{
				setResult(RESULT_CANCELED);
				finish();
			}
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