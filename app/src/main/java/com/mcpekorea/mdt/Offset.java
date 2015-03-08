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

		if(bytes.length != SIZE){
			byte[] curBin = getBytes();
			if(curBin != null){
				byte[] tmp = new byte[4];
				System.arraycopy(curBin, 0, tmp, Math.max(SIZE - bytes.length, 0), (bytes.length < SIZE) ? bytes.length : SIZE);
				bytes = tmp;
			}else{
				byte[] tmp = new byte[SIZE];
				for(int i = 0; i < bytes.length; i++){
					tmp[i] = bytes[i];
				}
				System.arraycopy(bytes, 0, tmp, Math.max(SIZE - bytes.length, 0), (bytes.length < SIZE) ? bytes.length : SIZE);
			}
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