package com.mcpekorea.ptpatch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
		this("", "");
	}

	public Project(String name){
		this(name, "");
	}

	public Project(String name, String author){
		this(name, author, new ArrayList<Patch>());
	}

    public Project(String name, String author, List<Patch> patches){
        this.name = name;
        this.author = author;
        this.patches = patches;
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

    public void setPatch(int position, Patch patch){
        this.patches.set(position, patch);
    }

	public void addPatch(Patch patch){
		this.patches.add(patch);
	}

	public void removePatch(int position){
		this.patches.remove(position);
	}

	public int getPatchesCount(){
		return this.patches.size();
	}

    public List<Patch> getIncludedPatches(){
        List<Patch> included = new ArrayList<>();
        for(Patch patch : this.patches){
            if(!patch.isExcluded()){
                included.add(patch);
            }
        }
        return included;
    }

    public int getIncludedPatchesCount(){
        return getIncludedPatches().size();
    }

    public boolean hasOverlappedPatches(){
        for(Patch patch : this.patches){
            if(patch.isOverlapped()){
                return true;
            }
        }
        return false;
    }

    public static Project createFromJSON(InputStream inputStream){
        BufferedReader br = null;
        try{
            br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            StringBuilder sb = new StringBuilder();
            String read;
            while((read = br.readLine()) != null){
                sb.append(read);
            }

            JSONObject object = new JSONObject(sb.toString());
            String name = object.getString("name");
            String author = object.getString("author");

            JSONArray array = object.getJSONArray("patches");
            ArrayList<Patch> patches = new ArrayList<>(array.length());
            for(int i = 0; i < array.length(); i++){
                patches.add(Patch.createFromJSON(array.getJSONObject(i)));
            }

            return new Project(name, author, patches);
        }catch(IOException | JSONException e){
            e.printStackTrace();
            return null;
        }finally{
            try{
                if(br != null){
                    br.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

	public JSONObject toJSON(){
		JSONObject object = new JSONObject();
		try{
			object.put("name", this.name);
			object.put("author", this.author);

			JSONArray array = new JSONArray();
			for(Patch patch : patches){
				array.put(patch.toJSON());
			}
			object.put("patches", array);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return object;
	}
}