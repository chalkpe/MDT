package com.mcpekorea.mdt;

import java.io.IOException;
import java.util.List;

/**
* @since 2015-03-07
* @author onebone <jyc0410@naver.com>
*/
public class ProjectExporter {
    private Project project;

    public static final int HEADER_LENGTH = 6;

    public ProjectExporter(Project project){
        this.project = project;
    }

    public byte[] create() throws IOException {
        List<Patch> patches = this.project.getIncludedPatches();

        int length = 0;
        length += HEADER_LENGTH;

        int indicesLength = Offset.SIZE * patches.size();
        length += indicesLength;

        byte[] metadataBytes = project.getAuthor().getBytes("UTF-8");
        length += metadataBytes.length;

        length += (Offset.SIZE * patches.size());

        for(Patch patch : patches){
            length += patch.getValue().getBytesLength();
        }

        byte[] mod = new byte[length];

        writeBytes(mod, new byte[]{(byte) 0xFF, 0x50, 0x54, 0x50, 0x00, (byte) patches.size()}, 0);
        writeBytes(mod, metadataBytes, HEADER_LENGTH + indicesLength);

        int patchStart = HEADER_LENGTH + indicesLength + metadataBytes.length;

        for(int i = 0; i < patches.size(); i++){
            Patch patch = patches.get(i);

            writeBytes(mod, intToByteArray(patchStart), HEADER_LENGTH + Offset.SIZE * i);
            int offsetBytesLength = writeBytes(mod, patch.getOffset().getBytes(), patchStart);
            int valueBytesLength = writeBytes(mod, patch.getValue().getBytes(), patchStart + Offset.SIZE);

            patchStart += (offsetBytesLength + valueBytesLength);
        }
        return mod;
    }

    public static int writeBytes(byte[] dst, byte[] src, int start){
        System.arraycopy(src, 0, dst, start, src.length);
        return src.length;
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