/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.userdefinedregions;

import asciipng.GeometryBuilder;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class UserDefinedRegionService {

    private static UserDefinedRegionService service = null;
    private static final Logger log = Logger.getLogger(UserDefinedRegionService.class.getName());

    public synchronized static UserDefinedRegionService get() {
        if (service == null) {
            service = new UserDefinedRegionService();
        }
        return service;
    }

    public double[][] convertPointStringToArray(String commaSeparated) {
        String[] coordPairs = commaSeparated.split("\\|");
        double[][] coordinates = new double[coordPairs.length][2];

        int i = 0;
        for (String s : coordPairs) {
            String[] coord = s.split("\\,");
            if (coord == null || coord.length != 2) {
                log.warning("bad coord in coord string");
            } else {
                try {
                    coordinates[i][0] = Double.parseDouble(coord[0]);
                    coordinates[i][1] = Double.parseDouble(coord[1]);
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }
            i++;
        }
        return coordinates;
    }

    public List<Point> convertArrayToGeometry(double[][] points){
        List<Point> pointList = new ArrayList<Point>();
        if (points == null){
            return pointList;
        }
        
        for(double[] coord : points){
            Point p = GeometryBuilder.createPointFromCoords(coord[0], coord[1]);
            if(p != null){
                pointList.add(p);
            }
            
        }
        
        return pointList;
    }
}
