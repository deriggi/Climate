/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tnccsv;

import export.util.FileExportHelper;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class PageFetcher {

    private static final Logger log = Logger.getLogger(PageFetcher.class.getName());
//    private int attempts = 0;
    private static final int MAX_ATTEMPTS = 3;

    public static InputStream getInputStream(String pageUrl) {
        if (pageUrl == null) {
            log.warning("ignoring null url");
            return null;
        }


        URLConnection con = null;
        log.fine("about to pass off to data processor");
        int connectionAttempts = 0;
        while (connectionAttempts++ < MAX_ATTEMPTS) {
            try {


                URL url = new URL(pageUrl);
                con = url.openConnection();
                return con.getInputStream();


            } catch (IOException ex) {
                Logger.getLogger(PageFetcher.class.getName()).log(Level.SEVERE, null, ex);
                if (ex.getMessage().indexOf("Connection timed out") == -1) {
                    connectionAttempts = MAX_ATTEMPTS;
                } else {
                    log.log(Level.INFO, "trying to connect again to {0}", pageUrl);
                    sleepForABit((connectionAttempts+1) * 60);
                }
            }
        }
        return null;

    }

    public static void readPage(String pageUrl, TNCBufferedReaderProcessor dataProcessor) {
        if (pageUrl == null) {
            log.warning("ignoring null url");
            return;
        }

        BufferedReader br = null;
        URLConnection con = null;
        log.fine("about to pass off to data processor");
        int connectionAttempts = 0;
        while (connectionAttempts++ < MAX_ATTEMPTS) {
            try {

                URL url = new URL(pageUrl);
                con = url.openConnection();
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                dataProcessor.readFromBuffer(pageUrl, br);
                return;

            } catch (FileNotFoundException fnfe) {
                connectionAttempts = MAX_ATTEMPTS;
                fnfe.printStackTrace();
                FileExportHelper.appendToFile("badurls.txt", pageUrl);

            } catch (MalformedURLException ex) {
                connectionAttempts = MAX_ATTEMPTS;
                Logger.getLogger(PageFetcher.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PageFetcher.class.getName()).log(Level.SEVERE, null, ex);
                if ((ex.getMessage().indexOf("Connection timed out") == -1) || (ex.getMessage().indexOf("504") == -1)  ) {
                    connectionAttempts = MAX_ATTEMPTS;
                } else {
                    log.log(Level.INFO, "trying to connect again to {0}", pageUrl);
                    sleepForABit((connectionAttempts+1) * 60);
                }
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }

                } catch (IOException ex) {
                    Logger.getLogger(PageFetcher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void sleepForABit(int seconds) {
        try {
            Thread.sleep(1000 * seconds);

        } catch (InterruptedException ex) {
            Logger.getLogger(PageFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
