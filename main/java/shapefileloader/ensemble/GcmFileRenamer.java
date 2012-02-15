/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.ensemble;

import ascii.tnc.FileReWriter;
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
public class GcmFileRenamer {
    private String newdirpath = "C:\\renamedannualregion\\";

    public static void main(String[] args){
        new GcmFileRenamer().loadFromRoot("C:\\Users\\wb385924\\Dropbox\\clusteroutput\\region 6\\region\\gcm\\annual\\anom\\");
    }
    private String rename(String filename) {

        String name = filename;
        System.out.println(name);
        if(name.startsWith("_")){
            name = name.substring(1);
        }
        name = name.replaceAll("_anom_", "_anom.");
        name = name.replaceAll("_pr_", ".pr_");
        name = name.replaceAll("_tas_", ".tas_");
        name = name.replaceAll("b1_1", "b1.1");
        name = name.replaceAll("b1_2", "b1.2");
        name = name.replaceAll("a2_1", "a1.1");
        name = name.replaceAll("a2_2", "a2.2");
        name = name.replaceAll("20_2", "20-2");
        name = name.replaceAll("40_2", "40-2");
        name = name.replaceAll("60_2", "60-2");
        name = name.replaceAll("80_2", "80-2");
        return name;

    }

    public void loadFromRoot(String basePath){
        File root = new File(basePath);
        File[] files = root.listFiles();

        for(File f: files){
            rewriteFile( f);
        }
    }


    private void rewriteFile(File f) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {

            fis = new FileInputStream(f);
            String newName = rename(f.getName());
            fos = new FileOutputStream(new File( (newdirpath+"//"+newName)));
            

            byte[] buf = new byte[1024];
            int len;
            while ((len = fis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }


        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileReWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileReWriter.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(FileReWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
