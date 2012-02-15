/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package randomtask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class ZipFileMover {

    private int counter = 0;
    public static void main(String[] args){
        new ZipFileMover().movestuff(new File("D:\\ASTGTM_V2\\"));
    }
    public void movestuff(File directory){
        File[] children = directory.listFiles();
        for(File child: children){
            if(child.isDirectory()){
                movestuff(child);
            }else{
                writeFileToFlatDirectory(child);
            }

        }

    }

    public void writeFileToFlatDirectory(File zipFile){
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
           
            fis = new FileInputStream(zipFile);
            fos = new FileOutputStream("D:/flatfiles/" + counter++ + zipFile.getName());
            byte b[]  = new byte[1024];
            int bytesRead = 0;
            while((bytesRead = fis.read(b)) > 0){
                fos.write(b, 0, bytesRead);
            }

        } catch (IOException ex) {
            Logger.getLogger(ZipFileMover.class.getName()).log(Level.SEVERE, null, ex);
        }  finally{
            try {
                fis.close();
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(ZipFileMover.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
