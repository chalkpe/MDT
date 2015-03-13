package com.mcpekorea.peanalyzer;

import java.io.File;
import java.io.Serializable;

@SuppressWarnings("unused")
public class UnsignedInteger implements Serializable{
	private static final long serialVersionUID = -8305238326702939027L;
	
	private int unsignedValue;

	public UnsignedInteger(UnsignedInteger UnsignedInteger){
		this.unsignedValue = UnsignedInteger.getUnsignedValue();
	}
	
	public UnsignedInteger(int singedValue){
		this.unsignedValue = singedValue - 1 - Integer.MAX_VALUE;
	}
	
	public UnsignedInteger(Integer integer){
		this(integer.intValue());
	}
	
	public UnsignedInteger(long singedValue){
		this((int) singedValue);
	}
	
	public UnsignedInteger(String singedValue, int radix){
		this(Long.parseLong(singedValue, radix));
	}
	
	public UnsignedInteger(String singedValue){
		this(singedValue, 16);
	}
	
	public int getUnsignedValue(){
		return unsignedValue;
	}
	
	public long getSignedValue(){
		return ((long) unsignedValue) + 1 + Integer.MAX_VALUE;
	}
	
	public File getFile(File cacheDirectory){
		String str = this.toString();
		
		File subdirectory = new File(cacheDirectory, str.substring(0, 4));
		subdirectory.mkdirs();
		
		return new File(subdirectory, str.substring(4));
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof UnsignedInteger && ((UnsignedInteger) o).getUnsignedValue() == this.unsignedValue;
	}
	
	@Override
	public String toString(){
		return this.toString(8);
	}
	
	public String toString(int length){
		return String.format("%0"+ length + "x", getSignedValue());
	}
	
	@Override
	public int hashCode(){
		return unsignedValue >>> 4;
	}
}