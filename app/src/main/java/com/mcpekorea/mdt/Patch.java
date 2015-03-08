package com.mcpekorea.mdt;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @since 2015-03-06
 * @author ChalkPE <amato0617@gmail.com>
 * @author onebone<jyc0410@naver.com>
 */
public class Patch {
	private Offset offset;
	private Value value;
	private boolean isExcluded;

    public Patch(Offset offset, Value value) {
        this.offset = offset;
        this.value = value;
	    this.isExcluded = false;
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

	public void exclude(){
		this.isExcluded = true;
	}

	public void include(){
		this.isExcluded = false;
	}

	public boolean isExcluded(){
		return this.isExcluded;
	}

	@Override
	public String toString() {
		return this.offset.toString() + " : " + this.value.toString();
	}

    public static Patch createFromJSON(JSONObject object){
        try{
            Offset offset = Offset.createFromJSON(object.getJSONArray("offset"));
            Value value = Offset.createFromJSON(object.getJSONArray("value"));

            return new Patch(offset, value);
        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }
    }

	public JSONObject toJSON(){
		JSONObject object = new JSONObject();
		try {
			object.put("offset", this.offset.toJSON());
			object.put("value", this.value.toJSON());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}
