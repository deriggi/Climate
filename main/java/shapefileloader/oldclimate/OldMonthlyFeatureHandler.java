/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader.oldclimate;

import dao.GeoDao;
import database.DBUtils;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdnis.wb.util.ShapeWrapper;
import shapefileloader.FeatureHandler;

/**
 *
 * @author wb385924
 */
public class OldMonthlyFeatureHandler implements FeatureHandler{
    private static final Logger log = Logger.getLogger(OldMonthlyFeatureHandler.class.getName());
    

     private String convertMultiPolygonToPolygon(String gon){
        StringBuilder sb = new StringBuilder();
        String geom = gon.substring(gon.indexOf("("),gon.lastIndexOf(")"));
        sb.append("POLYGON");
        sb.append(geom.substring(1));

        return sb.toString();
    }

    public void handleFeature( ShapeWrapper wrapper) {
        OldMonthlyNameParser nameParser = new OldMonthlyNameParser();

        HashMap<String,String> map = wrapper.getPropertyMap();

        // map size should be at least twelve
        
        Set<String> keys = map.keySet();
        for(String s: keys){
           double value = Double.parseDouble(map.get(s));
           String geomString =wrapper.getShapeString();
//           log.info();
           Connection c = DBUtils.getConnection();
           String polygon = convertMultiPolygonToPolygon(geomString);
           // cell id should not be -1
           long cellId = GeoDao.getGeometryBoundsId(c, polygon, "o_cell", "geom");
           if (cellId == -1){
            log.log(Level.WARNING," came across a point unknown {0}",polygon);
            cellId = GeoDao.storeGeometry(c, "o_cell", "geom", polygon);
           }
//           OldMonthlyCellularConfig config = nameParser.parsePathName(path);
//           config.setValue(value);
//           config.setCellId(cellId);
//           config.setMonth(OldMonthlyCellularConfig.monthMap.get(s));
//           OldMonthlyDao.get().saveMonthlyData(config);
          


//           if(!GeoDao.doesGeometryExist(c, polygon, "o_cell","geom" )){
//
//           }
           
           DBUtils.close(c);
        }
        
    }

}
