/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.regions;

import dao.GeoDao;
import database.DBUtils;
import java.sql.Connection;
import sdnis.wb.util.ShapeWrapper;
import shapefileloader.FeatureHandler;

/**
 *
 * @author wb385924
 */
public class RegionFeatureHandler implements FeatureHandler {

    int regionId = -1;
    public RegionFeatureHandler(int regionId){
        this.regionId = regionId;
    }

    public void handleFeature( ShapeWrapper wrapper) {
        String regionShape = wrapper.getShapeString();
        if (regionShape != null) {
            Connection c = DBUtils.getConnection();
            GeoDao.storeGeometryChild(c, "region_boundary", "shape", regionId, "region", regionShape);
            GeoDao.updateSimplifiedGeometryChild(c, "region_boundary", "simple", regionId, "region", regionShape);
            System.out.println("loaded " + regionShape);
            DBUtils.close(c);
        }
    }
}
