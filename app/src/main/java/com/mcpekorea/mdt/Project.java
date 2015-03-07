package com.mcpekorea.mdt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

/**
 * @since 2015-03-06
 * @author ChalkPE <amato0617@gmail.com>
 */
public class Project {
	private String name;
	private String author;
	private List<Patch> patches;

	public Project(){
		patches = new ArrayList<Patch>();
		this.name = "";
		this.author = "";
	}

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

	public JSONObject toJSON(){
		JSONObject object = new JSONObject();
		try {
			object.put("name", this.name);
			object.put("author", this.author);

			JSONArray array = new JSONArray();
			for(Patch patch : patches){
				array.put(patch.toJSON());
			}
			object.put("patches", array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
}