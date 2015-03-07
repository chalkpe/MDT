package com.mcpekorea.mdt;

import java.io.IOException;
import java.util.List;

/**
 * @since 2015-03-07
 * @author onebone <jyc0410@naver.com>
 */
public class ProjectExporter {
	private Project project;

	public static final int HEADER_END = 6;

	public ProjectExporter(Project project){
		this.project = project;
	}

	public byte[] create() throws IOException{
		int length = 0;

		String author = project.getAuthor();
		int offsetShift = author.length();

		List<Patch> patches = project.getPatches();
		length += (HEADER_END + offsetShift + (4 * patches.size()));
		for(Patch patch : patches){
			byte[] valueBin = patch.getValue().getBytes();
			length += (valueBin.length + 4);
		}
		byte[] ret = new byte[length];
		ret = writeBytes(ret, new byte[]{(byte)0xff, 0x50, 0x54, 0x50, 0x00}, 0);

		ret[5] =(byte) patches.size();
		int patchStart = HEADER_END + offsetShift + (4*patches.size());
		int header = HEADER_END + offsetShift;

		byte[] authorBin = author.getBytes();

		ret = writeBytes(ret, authorBin, HEADER_END + 1);

		for(Patch patch : patches){
			Offset offset = patch.getOffset();
			Value value = patch.getValue();

			byte[] offsetBin = offset.getBytes();
			byte[] locBin = intToByteArray(header);
			ret = writeBytes(ret, locBin, header);
			ret = writeBytes(ret, offsetBin, patchStart);

			byte[] valueBin = value.getBytes();
			ret = writeBytes(ret, valueBin, patchStart + 4);

			patchStart += (4 + valueBin.length);
			header += 4;
		}

		return ret;
	}

	public static final byte[] writeBytes(byte[] src, byte[] c, int start){
		for(int i = start; i < c.length + start; i++){
			src[i] = c[i - start];
		}
		return src;
	}

	public static final byte[] intToByteArray(int value) {
		return new byte[] {
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}

	public static final int byteArrayToInt(byte [] b) {
		return (b[0] << 24)
				+ ((b[1] & 0xFF) << 16)
				+ ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF);
	}
}
