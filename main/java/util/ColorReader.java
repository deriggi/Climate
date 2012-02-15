/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import export.util.FileExportHelper;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class ColorReader {

    public static void main(String[] args) {
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream("C:\\Users\\wb385924\\colorinput\\colorramps.csv"));
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    int r = Integer.parseInt(parts[2]);
                    int g = Integer.parseInt(parts[3]);
                    int b = Integer.parseInt(parts[4]);
                    String s = getHexString(new Color(r, g, b));
//                    FileExportHelper.appendToFile("C:\\Users\\wb385924\\colorinput\\coloroutputPound2.csv", parts[0] + "," + parts[1] + ",#" + s.substring(2));
                    FileExportHelper.appendToFile("C:\\Users\\wb385924\\colorinput\\coloroutput2.html", "<div style='height:200px;background-color:#" + s.substring(2)+"'>"+ parts[0]+"</div>");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ColorReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                isr.close();
            } catch (IOException ex) {
                Logger.getLogger(ColorReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static String getHexString(Color c) {
        return Integer.toHexString(c.getRGB());
    }
}
