/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tnccsv;

import domain.DerivativeStats;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author wb385924
 */
public class DataFileHandler {
    public static final String PNG_FILE = "png";
    public static final String ASCII_FILE = "asc";
    public static final String CSV_FILE = "csv";
    
    // extensions
//    private final String ASC_EXT = "asc";
//    private final String PNG_EXT = "png";
//    private final String CSV_EXT = "csv";

    // starts with
//    public String TABLE_YEARLY_AR4 = "table_yearly_AR4";
//    private final String TABLE_YEARLY_BASELINE = "table_yearly_baseline_AR4";
//    private final String TABLE_YEARLY_DEPART = "table_yearly_depart_AR4";

    

    private HashMap<String,ArrayList<String>> files = new HashMap<String,ArrayList<String>>();
    private int numFiles = 0;

    public int getSize(){
        return numFiles;
    }

    public ArrayList<String> getFiles(String key){
        if(!files.containsKey(key)){
            return new ArrayList<String>(0);
        }
        return files.get(key);
    }


    public void clear(){
        files.clear();
        numFiles = 0;
    }
    public void logLine(String filePath){
        if(filePath.endsWith(ASCII_FILE)){
            addFile(ASCII_FILE,filePath);
        }else if(filePath.endsWith(PNG_FILE)){
            addFile(PNG_FILE,filePath);
        }else if(filePath.endsWith(CSV_FILE)){
            addFile(CSV_FILE,filePath);
        }
    }

    public ArrayList<String> getFiles(String fileExtension, DerivativeStats.file_name type){
        ArrayList<String> requestedFiles = new ArrayList<String>();
        if(!files.containsKey(fileExtension)){
            return requestedFiles;
        }
        
        for(String s: files.get(fileExtension)){
            String shorts = s.substring(s.lastIndexOf("/"));
            
            if(shorts.indexOf(type.toString()) != -1){
                requestedFiles.add(s);
            }
        }
        return requestedFiles;
    }


    private void addFile(String key, String filePath){
        if(!files.containsKey(key)){
            files.put(key, new ArrayList<String>());
        }
        
        files.get(key).add(filePath);
        numFiles++;
    }

}
