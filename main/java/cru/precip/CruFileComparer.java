/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cru.precip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class CruFileComparer {

    private static final Logger log = Logger.getLogger(CruFileComparer.class.getName());
    boolean foundYet = false;
    double[] target = new double[3];
    int count = 0;

    public static void main(String[] args) {
        new CruFileComparer().compareCSV("MOZ", "C:\\Users\\wb385924\\cruoutput\\");
        new CruFileComparer().compareCSV("SDN", "C:\\Users\\wb385924\\cruoutput\\");
        new CruFileComparer().compareCSV("JPN", "C:\\Users\\wb385924\\cruoutput\\");
    }

    public void compareCSV(String iso3, String rootPath) {
        String[] children = new File(rootPath).list();
        for (String child : children) {
            readFile(rootPath + child, iso3);
        }
        log.log(Level.INFO, "logged {0} files", count);
    }

    private void readFile(String filePath, String iso3) {
        InputStreamReader isr = null;
        BufferedReader br = null;
        String commma = "\\,";

        try {
            isr = new InputStreamReader(new FileInputStream(new File(filePath)));
            br = new BufferedReader(isr);
            String line = null;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(commma);
                String foundIso = parts[0];
                if (foundIso.equals(iso3)) {
                    if (!foundYet) {
//                        target[0] = Double.parseDouble(parts[1]);
//                        target[1] = Double.parseDouble(parts[2]);
                        target[0] = Double.parseDouble(parts[4]);
                        foundYet = true;
                        log.log(Level.FINE, "found {0} ", new Object[]{target[0]});
                    } else {
                        log.log(Level.FINE, "examining {0} {1}  ", new Object[]{target[0], parts[4]});

                        if (target[0] != Double.parseDouble(parts[4])) {
                            log.log(Level.WARNING, "found difference with data in {0}", filePath);
                        }
                        count++;
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CruFileComparer.class.getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(CruFileComparer.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                isr.close();
            } catch (IOException ex) {
                Logger.getLogger(CruFileComparer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
