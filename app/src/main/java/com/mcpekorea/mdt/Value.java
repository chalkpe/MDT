package com.mcpekorea.mdt;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for(Byte b : bytes){
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Value && this.bytes.equals(((Value) o).bytes);
	}
	public JSONArray toJSON(){
		JSONArray array = new JSONArray();
		for(Byte b : bytes){
			array.put(b);
		}
		return array;
	}
}
