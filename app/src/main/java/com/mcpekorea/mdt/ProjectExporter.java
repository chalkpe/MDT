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

    public byte[] create() throws IOException {
        int length = 0;

        String author = project.getAuthor();
        int offsetShift = author.length();

        int header = HEADER_END + offsetShift;

        List<Patch> patches = this.project.getIncludedPatches();
        length += (header + (Offset.SIZE * patches.size()));
        for(Patch patch : patches){
            length += (patch.getValue().getBytesLength() + Offset.SIZE);
        }

        byte[] mod = new byte[length];

        mod = writeBytes(mod, new byte[]{(byte) 0xFF, 0x50, 0x54, 0x50, 0x00}, 0);
        mod[5] = (byte) patches.size();

        int patchStart = header + (Offset.SIZE * patches.size());

        mod = writeBytes(mod, author.getBytes("UTF-8"), HEADER_END + 1);

        for(Patch patch : patches){
            Offset offset = patch.getOffset();
            Value value = patch.getValue();

            mod = writeBytes(mod, intToByteArray(header), header);
            mod = writeBytes(mod, offset.getBytes(), patchStart);
            patchStart += Offset.SIZE + (mod = writeBytes(mod, value.getBytes(), patchStart + 4)).length;
            header += Offset.SIZE;
        }
        return mod;
    }

    public static byte[] writeBytes(byte[] src, byte[] dst, int start){
        System.arraycopy(dst, 0, src, start, dst.length);
        return src;
    }

    public static byte[] intToByteArray(int value) {
        return new byte[] {
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>>  8),
            (byte)  value
        };
    }

    public static int byteArrayToInt(byte[] bytes) {
        return (bytes[0] << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    }
}