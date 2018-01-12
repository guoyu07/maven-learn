package com.shimh;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 */
@Mojo(name = "count")
public class CountMojo extends AbstractMojo{

	private static final String[] INCLUDES_DEFAULT = {"java", "xml", "properties"}; 
	
	@Parameter(defaultValue="${project.basedir}", readonly = true, required = true)
	private File basedir; 
	
	@Parameter(defaultValue="${project.build.sourceDirectory}", readonly = true, required = true)
	private File sourceDirectory;  
	
	@Parameter(defaultValue="${project.build.testSourceDirectory}", readonly = true, required = true)
	private File testSourceDirectory; 
	
	@Parameter(defaultValue="${project.build.resources}", readonly = true, required = true)
	private List<Resource> resources;     
	
	@Parameter(defaultValue="${project.build.testResources}", readonly = true, required = true)
	private List<Resource> testResources;   
	
	@Parameter
	private String[] includes; 
	
    public void execute()throws MojoExecutionException{
    	
    	if(includes == null || includes.length == 0){
    		includes = INCLUDES_DEFAULT;
    	}
    	
    	getLog().info("项目绝对路径" + basedir.getAbsolutePath());
    	
    	try{
    		
    		countDir(sourceDirectory);
    		countDir(testSourceDirectory);
    		
    		for(Resource resource:resources){
    			countDir(new File(resource.getDirectory()));
    		}
    		for(Resource resource:testResources){
    			countDir(new File(resource.getDirectory()));
    		}
    		
    	}catch(Exception e){
    		throw new MojoExecutionException("Unable to count lines of code.",e);
    	}


    }

	private void countDir(File dir) throws Exception {

		if(!dir.exists()){
			return;
		}
		
		List<File> collected = new ArrayList<File>();
		collectFiles(collected, dir);
		int lines = 0;
		
		for(File sourceFile:collected){
			lines += countLine(sourceFile);
		}
		
		String path = dir.getAbsolutePath().substring(basedir.getAbsolutePath().length());
		
		getLog().info(path + ":" + lines + "lines of code in " + collected.size() + " files");
		
		
		
		
	}

	private int countLine(File file) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		int line = 0;
		try{
			while(reader.ready()){
				reader.readLine();
				line ++;
			}
			
		}finally{
			reader.close();
		}
		
		
		
		return line;
	}

	private void collectFiles(List<File> collected, File file) {
		if(file.isFile()){
			for(String include:includes){
				if(file.getName().endsWith("." + include)){
					collected.add(file);
					break;
				}
			}
		}else{
			for(File sub:file.listFiles()){
				collectFiles(collected, sub);
			}
		}
	}
	
	
	
	
	
	
	
}
