package com.mcpekorea.peanalyzer;

@SuppressWarnings("unused")
public class ContentLine extends Line {
	private static final long serialVersionUID = -3507147148809513557L;
	
	private final String hex;
	private final String code;
	private final String comment;
	
	
	public ContentLine(UnsignedInteger offset, String hex, String code) {
		this(offset, hex, code, "");
	}
	
	public ContentLine(UnsignedInteger offset, String hex, String code, String comment) {
		super(offset);
		this.hex = hex;
		this.code = code;
		this.comment = comment == null ? "" : comment;
	}

	public String getHex() {
		return hex;
	}

	public String getCode() {
		return code;
	}

	public String getComment() {
		return comment;
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof ContentLine && super.equals(o) && this.hex.equals(((ContentLine) o).hex) && this.code.equals(((ContentLine) o).code) && this.comment.equals(((ContentLine) o).comment);
	}
	
	@Override
	public String toString(){
		String str = String.format("  %s:\t%s\t%s", super.toString(6), this.hex, this.code);
		if(!comment.equals("")){
			str += "\t; " + this.comment;
		}
		return str;
	}
}