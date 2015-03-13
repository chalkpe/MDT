package com.mcpekorea.peanalyzer;

import java.io.Serializable;

public abstract class Line implements Serializable {
	private static final long serialVersionUID = 1725591682831760841L;
	
	private final UnsignedInteger offset;
	
	public Line(UnsignedInteger offset){
		this.offset = offset;
	}
	
	public UnsignedInteger getOffset() {
		return offset;
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof Line && this.offset.equals(((Line) o).getOffset());
	}
	
	@Override
	public String toString(){
		return offset.toString();
	}
	
	public String toString(int length){
		return offset.toString(length);
	}
	
	@Override
	public int hashCode(){
		return offset.hashCode();
	}
}