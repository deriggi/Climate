/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csvloader;

import asciipng.GeometryBuilder;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Point;
import java.text.ParseException;
import java.util.HashMap;

/**
 * A utility for helping work with spatial data in csv files
 * @author Johnny
 */
public class GeoCSVUtil {

//    private int longitudeColumnIndex = -1;
//    private int latitudeColumnIndex = -1;
//    // the keys correspond to the index of the table 
//    private HashMap<Integer, Double> dataTable = new HashMap<Integer, Double>();

//    public GeoCSVUtil(int latColumnIndex, int lonColumnIndex) {
//    }

    public GeoCSVUtil() {
    }

    public static Point getPointFromRow(String line, int latColumnIndex, int lonColumnIndex) {

        if (line == null) {
            return null;
        }

        String[] lineParts = line.split("\\,");
        if (latColumnIndex > lineParts.length - 1 || lonColumnIndex > lineParts.length - 1) {
            return null;
        }
        double latitude, longitude;
        try {
            longitude = Double.parseDouble(lineParts[lonColumnIndex]);
            latitude = Double.parseDouble(lineParts[latColumnIndex]);
        } catch (NumberFormatException pe) {
            pe.printStackTrace();
            return null;
        }
        
        
        Point pointFromLine = GeometryBuilder.createPointFromCoords(longitude, latitude);
        return pointFromLine;


    }
    
    public static HashMap<Integer, String> getRowAsTable(String line) {

        if (line == null) {
            return null;
        }

        HashMap<Integer, String> columnValues = new HashMap<Integer, String>();
        
        String[] lineParts = line.split("\\,");
        int i = 0;
        for(String s: lineParts){
            columnValues.put(i++, s);
        }
        
        return columnValues;


    }
}
