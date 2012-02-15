/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loadtest;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class LoadTester {

    public void testLoad() {
        DataInputStream dis = null;
        try {
            
            URL oracle = new URL("http://localhost:8080/climateweb/rest/v1/country/annualanom/ensemble/ppt_days/2046/2065/rwa");
            URLConnection yc = oracle.openConnection();
            dis = new DataInputStream(yc.getInputStream());
            byte[] bytes = new byte[1024];
            int bytesRead = 0;
            int offset = 0;
            int length = 1024;
            while ((bytesRead = dis.read(bytes)) != -1) {
                System.out.print(new String(bytes, 0, bytesRead));
            }

        } catch (IOException ex) {
            Logger.getLogger(LoadTester.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                dis.close();
            } catch (IOException ex) {
                Logger.getLogger(LoadTester.class.getName()).log(Level.SEVERE, null, ex);
            }
        }




    }

    public static void main(String[] args) {
        LoadTester lt = new LoadTester();
        int i = 0;
        long t0 = Calendar.getInstance().getTimeInMillis();
        while(i ++ < 10000){
            lt.testLoad();
        }
        long t1 = Calendar.getInstance().getTimeInMillis();
        System.out.println((t1 - t0)/1000.0 + " seconds ");
    }
}
