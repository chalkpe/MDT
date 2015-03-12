package com.mcpekorea.mdt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.mcpekorea.hangul.Hangul;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreatePatchActivity extends ActionBarActivity {
    public static final byte[] DEFAULT_VALUE = new byte[]{0x70, 0x47};
    public static final byte[] BLANK = new byte[]{};

    private int patchIndex;
    private EditText offsetArea, valueAreaHex, valueAreaString, memoArea;
    private CheckBox isExcludedBox;

    private String oldOffsetString = "", oldValueString = "", oldMemo = "";
    private boolean oldIsExcluded = false;

    private int currrentValueType = R.id.create_patch_value_type_hex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_patch);

        offsetArea = (EditText) findViewById(R.id.create_patch_offset);
        valueAreaHex = (EditText) findViewById(R.id.create_patch_value_hex);
        valueAreaString = (EditText) findViewById(R.id.create_patch_value_string);
        memoArea = (EditText) findViewById(R.id.create_patch_memo);
        isExcludedBox = (CheckBox) findViewById(R.id.create_patch_is_excluded);

        offsetArea.setTypeface(WorkspaceActivity.inconsolata);
        valueAreaHex.setTypeface(WorkspaceActivity.inconsolata);
        valueAreaString.setTypeface(WorkspaceActivity.inconsolata);

        ((RadioGroup) findViewById(R.id.create_patch_value_type_group)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (currrentValueType != checkedId) {
                    switch (checkedId) {
                        case R.id.create_patch_value_type_hex:
                            valueAreaHex.setText(new Value(getValueBytes()).toString());
                            valueAreaHex.setVisibility(View.VISIBLE);
                            valueAreaString.setVisibility(View.INVISIBLE);
                            break;
                        case R.id.create_patch_value_type_unicode:
                            try{
                                valueAreaString.setText(new String(getValueBytes(), "UTF-8"));
                            }catch(UnsupportedEncodingException e){
                                e.printStackTrace();
                            }
                            valueAreaString.setVisibility(View.VISIBLE);
                            valueAreaHex.setVisibility(View.INVISIBLE);
                            break;
                    }
                    currrentValueType = checkedId;
                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        patchIndex = bundle.getInt("patchIndex", -1);

        if(patchIndex >= 0){ //Edit mode
            setTitle(R.string.title_activity_edit_patch);

            oldOffsetString = bundle.getString("offsetString");
            offsetArea.setText(oldOffsetString);

            oldValueString = bundle.getString("valueString");
            valueAreaHex.setText(oldValueString);

            oldMemo = bundle.getString("memo");
            memoArea.setText(oldMemo);

            oldIsExcluded = bundle.getBoolean("isExcluded", false);
            isExcludedBox.setChecked(oldIsExcluded);
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
                String memo = memoArea.getText().toString();

                if(offsetString == null || offsetString.equals("")){
                    offsetArea.setError(Hangul.format(getText(R.string.error_empty).toString(), getText(R.string.create_patch_offset).toString()));
                    return true;
                }

                if(memo == null || memo.equals("")){
                    memo = "";
                }

                byte[] valueBytes = getValueBytes();

                if(Arrays.equals(valueBytes, BLANK)){
                    valueBytes = DEFAULT_VALUE;
                }

                List<String> offsetStrings = splitEqually(offsetString, 2);

                byte[] offsetBytes = new byte[offsetStrings.size()];

                for(int i = 0; i < offsetStrings.size(); i++){
                    offsetBytes[i] = (byte) Integer.parseInt(offsetStrings.get(i), 16);
                }

                Bundle bundle = new Bundle();
                bundle.putInt("patchIndex", patchIndex);
                bundle.putByteArray("offsetBytes", offsetBytes);
                bundle.putByteArray("valueBytes", valueBytes);
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

    public EditText getCurrrentValueArea(){
        switch(currrentValueType){
            case R.id.create_patch_value_type_hex:
                return valueAreaHex;
            case R.id.create_patch_value_type_unicode:
                return valueAreaString;
            default:
                return null;
        }
    }

    public byte[] getValueBytes(){
        switch(currrentValueType){
            case R.id.create_patch_value_type_hex:
                String hexString = valueAreaHex.getText().toString();
                if(hexString == null || hexString.equals("")){
                    return BLANK;
                }

                List<String> valueStrings = splitEqually(hexString, 2);
                byte[] valueBytes = new byte[valueStrings.size()];
                for(int i = 0; i < valueStrings.size(); i++){
                    valueBytes[i] = (byte) Integer.parseInt(valueStrings.get(i), 16);
                }
                return valueBytes;

            case R.id.create_patch_value_type_unicode:
                String unicodeString = valueAreaString.getText().toString();
                if(unicodeString == null || unicodeString.equals("")){
                    return BLANK;
                }

                try{
                    return unicodeString.getBytes("UTF-8");
                }catch(UnsupportedEncodingException e){
                    e.printStackTrace();
                    return BLANK;
                }
            default:
                return BLANK;
        }
    }

    public static List<String> splitEqually(String text, int size) {
        List<String> list = new ArrayList<>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            list.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return list;
    }

    public void showCancelDialog(){
        if(oldOffsetString.equalsIgnoreCase(offsetArea.getText().toString()) &&
                oldValueString.equalsIgnoreCase(getCurrrentValueArea().getText().toString()) &&
                oldMemo.equals(memoArea.getText().toString()) &&
                oldIsExcluded == isExcludedBox.isChecked()){
            setResult(RESULT_CANCELED);
            finish();
        }else{
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
}