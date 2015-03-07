package com.mcpekorea.mdt;
import java.util.List;

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

	@Override
	public void setBytes(byte[] bytes) {
		if(bytes == null){
			throw new NullPointerException("bytes must not be null");
		}

		if(bytes.length != 4){
			byte[] curBin = getBytes();
			if(curBin != null){
				byte[] tmp = new byte[4];
				System.arraycopy(curBin, 0, tmp, Math.max(4 - bytes.length, 0), (bytes.length < 4) ? bytes.length : 4);
				bytes = tmp;
			}else{
				byte[] tmp = new byte[4];
				for(int i = 0; i < bytes.length; i++){
					tmp[i] = bytes[i];
				}
				System.arraycopy(bytes, 0, tmp, Math.max(4 - bytes.length, 0), (bytes.length < 4) ? bytes.length : 4);
			}
		}

		super.setBytes(bytes);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Offset && super.equals(o);
	}
}