/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ascii.tnc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class FileReWriter {

    private String newRoot = "C:\\Users\\wb385924\\ensembleAnnualAscFlat";

    public static void main(String[] args){
        new FileReWriter().readFromRoot("C:\\Users\\wb385924\\ensembleAnnualAsc\\");
    }

    public void readFromRoot(String rootDirectory) {
        File[] files = new File(rootDirectory).listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                handleSubRoot(f);
            }
        }
    }

    private void handleSubRoot(File dir) {
        File[] files = dir.listFiles();
        for (File f : files) {
            rewriteFile(dir.getName(), f);
        }
    }

    private void rewriteFile(String parent, File f) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {

            fis = new FileInputStream(f);
            fos = new FileOutputStream(new File(newRoot + "\\" + parent + "_" + f.getName()));
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

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
