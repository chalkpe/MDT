package com.mcpekorea.mdt;

import java.util.List;

/**
 * @since 2015-03-06
 * @author ChalkPE <amato0617@gmail.com>
 */
public class Value {
    private List<Byte> bytes;

    public Value(List<Byte> bytes){
        setBytes(bytes);
    }

    public List<Byte> getBytes() {
        return bytes;
    }

    public void setBytes(List<Byte> bytes) {
        if(bytes == null){
            throw new NullPointerException("bytes must not be null");
        }
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(bytes.size() * 2);
        for(Byte b : bytes){
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Value && this.bytes.equals(((Value) o).bytes);
    }
}
