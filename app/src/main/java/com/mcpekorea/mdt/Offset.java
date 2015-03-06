package com.mcpekorea.mdt;

import java.util.List;

/**
 * @since 2015-03-06
 * @author ChalkPE <amato0617@gmail.com>
 */
public class Offset extends Value {
    public static final int SIZE = 3;

    public Offset(List<Byte> bytes){
        super(bytes);
    }

    @Override
    public void setBytes(List<Byte> bytes) {
        if(bytes == null){
            throw new NullPointerException("bytes must not be null");
        }

        if(bytes.size() > SIZE){
            throw new IllegalArgumentException("offset must be less than " + SIZE + " bytes");
        }

        if(bytes.size() < SIZE){
            for(int i = 0; i <  SIZE - bytes.size(); i++){
                bytes.add(0, (byte) 0);
            }
        }

        super.setBytes(bytes);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Offset && super.equals(o);
    }
}