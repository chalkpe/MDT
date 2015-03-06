package com.mcpekorea.mdt;

import java.util.List;

/**
 * @since 2015-03-06
 * @author ChalkPE <amato0617@gmail.com>
 */
public class Project {
    private String name;
    private String author;
    private List<Patch> patches;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Patch> getPatches() {
        return patches;
    }

    public void setPatches(List<Patch> patches) {
        this.patches = patches;
    }

    public void addPatch(Patch patch){
        this.patches.add(patch);
    }

    public void addPatch(int position, Patch patch){
        this.patches.add(position, patch);
    }

    public void removePatch(Patch patch){
        this.patches.remove(patch);
    }

    public void removePatch(int position){
        this.patches.remove(position);
    }

    public int getPatchesCount(){
        return this.patches.size();
    }
}