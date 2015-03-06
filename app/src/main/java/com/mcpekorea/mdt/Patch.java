package com.mcpekorea.mdt;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @since 2015-03-06
 * @author ChalkPE <amato0617@gmail.com>
 */
public class Patch {
    private Offset offset;
    private Value value;

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

    @Override
    public String toString() {
        return this.offset.toString() + " : " + this.value.toString();
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
