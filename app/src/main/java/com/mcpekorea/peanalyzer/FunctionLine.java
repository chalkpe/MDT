package com.mcpekorea.peanalyzer;

@SuppressWarnings("unused")
public class FunctionLine extends Line {
	private static final long serialVersionUID = 5146433579874960194L;
	
	private final String function;
	
	public FunctionLine(UnsignedInteger offset, String function) {
		super(offset);
		this.function = function;
	}

	public String getFunction() {
		return function;
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof FunctionLine && super.equals(o) && this.function.equals(((FunctionLine) o).function);
	}
	
	@Override
	public String toString(){
		return String.format("%s %s:", super.toString(), this.function);
	}
}