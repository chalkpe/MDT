package com.mcpekorea.mdt;
import org.json.JSONArray;

/**
 * @since 2015-03-06
 * @author ChalkPE <amato0617@gmail.com>
 */
public class Offset extends Value {
	public static final int SIZE = 4;

	public Offset(byte[] bytes){
		super(bytes);
		setBytes(bytes);
	}

    public Offset(Value value){
        super(value.getBytes());
        setBytes(value.getBytes());
    }

	@Override
	public void setBytes(byte[] bytes) {
		if(bytes == null){
			throw new NullPointerException("bytes must not be null");
		}

        if(bytes.length > SIZE){
            byte[] newBytes = new byte[SIZE];
            System.arraycopy(bytes, bytes.length - SIZE, newBytes, 0, 4);
            bytes = newBytes;
        }else if(bytes.length < SIZE){
            byte[] newBytes = {0x00, 0x00, 0x00, 0x00};
            System.arraycopy(bytes, 0, newBytes, SIZE - bytes.length, bytes.length);
            bytes = newBytes;
        }
		super.setBytes(bytes);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Offset && super.equals(o);
	}

    public static Offset createFromJSON(JSONArray array){
        return new Offset(Value.createFromJSON(array));
    }
}