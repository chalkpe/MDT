package com.mcpekorea.peanalyzer;

import android.widget.Toast;

import com.mcpekorea.mdt.R;
import com.mcpekorea.mdt.WorkspaceActivity;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/*
 * Copyright 2015 ChalkPE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class PEAnalyzer /* for MDT */ {
	private File cacheDirectory;
	private HashMap<UnsignedInteger, Line> lines;

    private boolean cached = false;
    private boolean isCreating = false;

	public PEAnalyzer(File cacheDirectory) throws IOException {
		this.cacheDirectory = cacheDirectory;
		this.lines = new HashMap<>(1048576); //2^20
		this.loadCache();
	}
	
	private void loadCache(){
		new Thread(){
			@Override
			public void run(){
				cached = cacheDirectory.exists();
				
				if(!cached){
                    createCache();
					return;
				}
				
				File[] subdirectories = cacheDirectory.listFiles(new FileFilter(){
					@Override
					public boolean accept(File file){
						return file.isDirectory();
					}
				});
				
				if(subdirectories.length == 0){
                    createCache();
					return;
				}
				
				for(File subdirectory : subdirectories){
					for(File file : subdirectory.listFiles()){
						load(file);
					}
				}
				WorkspaceActivity.toast(R.string.workspace_cache_loaded, Toast.LENGTH_SHORT);
			}
		}.start();
	}

    public void createCache(){
        if(isCreating){
            return;
        }

        new Thread(){
            @Override
            public void run(){
                isCreating = true;

                if(!cacheDirectory.exists() || cacheDirectory.list().length == 0){
                    cacheDirectory.mkdirs();

                    ZipInputStream zis = null;
                    try{
                        zis = new ZipInputStream(WorkspaceActivity.that.getAssets().open("PEAnalyzerCache.zip"));
                        ZipEntry ze;

                        while((ze = zis.getNextEntry()) != null){
                            File out = new File(cacheDirectory, ze.getName());
                            if(ze.isDirectory()){
                                zis.closeEntry();
                                out.mkdir();
                                continue;
                            }
                            FileOutputStream fos = new FileOutputStream(out);

                            byte[] buffer = new byte[1024];
                            int length;

                            while((length = zis.read(buffer)) != -1){
                                fos.write(buffer, 0, length);
                            }
                            zis.closeEntry();
                            fos.close();
                        }
                        WorkspaceActivity.toast(R.string.workspace_cache_created, Toast.LENGTH_SHORT);
                    }catch(IOException e){
                        e.printStackTrace();
                    }finally{
                        if(zis != null){
                            try{
                                zis.close();
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
                isCreating = false;
            }
        }.start();
    }

	public void put(Line line){
		this.lines.put(line.getOffset(), line);
		this.save(line);
	}
	
	public void save(Line line){
		File file = line.getOffset().getFile(cacheDirectory);
		ObjectOutputStream oos = null;
		
		if(file.exists()){
			return;
		}
		
		try{
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(line);
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(oos != null){
				try{
					oos.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public Line get(UnsignedInteger offset){
		Line line = this.lines.get(offset);
		if(line == null){
			return load(offset);
		}else{
			return line;
		}
	}
	
	public Line load(UnsignedInteger offset){
		return this.load(offset.getFile(cacheDirectory));
	}
	
	public Line load(File file){
		ObjectInputStream ois = null;
		
		if(!file.exists()){
			return null;
		}
		
		try{
			ois = new ObjectInputStream(new FileInputStream(file));
			
			Line line = (Line) ois.readObject();
			this.put(line);

			return line;
		}catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
			return null;
		}finally{
			if(ois != null){
				try{
					ois.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
}