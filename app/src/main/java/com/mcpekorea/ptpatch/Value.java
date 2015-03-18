package com.mcpekorea.ptpatch;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @since 2015-03-06
 * @author ChalkPE <amato0617@gmail.com>
 */
public class Value {
	private byte[] bytes;
	public static final byte[] BLANK = new byte[]{};

	public Value(byte[] bytes){
		setBytes(bytes);
	}

    public Value(String hexString){
        if(hexString == null || hexString.equals("")) {
            setBytes(BLANK);
            return;
        }

        List<String> valueStrings = splitEqually(hexString, 2);
        byte[] valueBytes = new byte[valueStrings.size()];
        for(int i = 0; i < valueStrings.size(); i++){
            valueBytes[i] = (byte) Integer.parseInt(valueStrings.get(i), 16);
        }

        setBytes(valueBytes);
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

    public static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>>  8),
                (byte)  value
        };
    }

    public static int byteArrayToInt(byte[] values) {
        return (values[0] << 24) | ((values[1] & 0xFF) << 16) | ((values[2] & 0xFF) << 8) | (values[3] & 0xFF);
    }

	public static List<String> splitEqually(String text, int size) {
        for(int i = 0; i < text.length() % size; i++){
            text = "0" + text;
        }
		List<String> list = new ArrayList<>((text.length() + size - 1) / size);

		for (int start = 0; start < text.length(); start += size) {
			list.add(text.substring(start, Math.min(text.length(), start + size)));
		}
		return list;
	}
}