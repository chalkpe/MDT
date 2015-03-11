package com.mcpekorea.mdt;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;

/**
 * @since 2015-03-06
 * @author ChalkPE <amato0617@gmail.com>
 */
public class Value {
	private byte[] bytes;

	public Value(byte[] bytes){
		setBytes(bytes);
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		if(bytes == null){
			throw new IllegalArgumentException("bytes must not be null");
		}
		this.bytes = bytes;
	}

    public int getBytesLength(){
        return this.bytes.length;
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for(Byte b : bytes){
			sb.append(String.format("%02X", b & 0xff));
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Value && Arrays.equals(this.bytes, ((Value) o).bytes);
	}

    public static Value createFromJSON(JSONArray array){
        try{
            byte[] bytes = new byte[array.length()];
            for(int i = 0; i < array.length(); i++){
                bytes[i] = (byte) array.getInt(i);
            }
            return new Value(bytes);
        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }
    }

	public JSONArray toJSON(){
		JSONArray array = new JSONArray();
		for(byte b : bytes){
			array.put(b);
		}
		return array;
	}
}
