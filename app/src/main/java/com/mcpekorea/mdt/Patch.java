package com.mcpekorea.mdt;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @since 2015-03-06
 * @author ChalkPE <amato0617@gmail.com>
 * @author onebone <jyc0410@naver.com>
 */
public class Patch {
	private Offset offset;
	private Value value;
    private String memo;
	private boolean isExcluded;
    private boolean isOverlapped = false;

    public Patch(Offset offset, Value value) {
        this.offset = offset;
        this.value = value;
	    this.isExcluded = false;
	    this.memo = "";
    }

	public Patch(Offset offset, Value value, String memo, boolean isExcluded){
		this.offset = offset;
		this.value = value;
        this.memo = memo;
		this.isExcluded = isExcluded;
	}

    public Offset getOffset() {
		return offset;
	}

	public void setOffset(Offset offset) {
		this.offset = offset;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

    public String getMemo(){
        return this.memo;
    }

    public void setMemo(String memo){
        this.memo = memo;
    }

    public boolean isExcluded(){
        return this.isExcluded;
    }

	public void setExcluded(boolean isExcluded){
		this.isExcluded = isExcluded;
	}

    public boolean isOverlapped() {
        return isOverlapped;
    }

    public void setOverlapped(boolean isOverlapped) {
        this.isOverlapped = isOverlapped;
    }

    public int getPatchStart(){
        return ProjectExporter.byteArrayToInt(this.offset.getBytes());
    }

    public int getPatchEnd(){
        return this.getPatchStart() + this.value.getBytesLength() + 1;
    }

	@Override
	public String toString() {
		return this.offset.toString() + " : " + this.value.toString();
	}

    public static Patch createFromJSON(JSONObject object){
        try{
            Offset offset = Offset.createFromJSON(object.getJSONArray("offset"));
            Value value = Value.createFromJSON(object.getJSONArray("value"));

            String memo = object.getString("memo");
			boolean isExcluded = object.getBoolean("isExcluded");

            return new Patch(offset, value, memo, isExcluded);
        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }
    }

	public JSONObject toJSON(){
		JSONObject object = new JSONObject();
		try{
			object.put("offset", this.offset.toJSON());
			object.put("value", this.value.toJSON());
			object.put("memo", this.memo);
            object.put("isExcluded", this.isExcluded);
		}catch (JSONException e){
			e.printStackTrace();
		}
		return object;
	}
}