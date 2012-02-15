/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.userdefinedregions;

import asciipng.CellMapMaker;
import asciipng.GeometryBuilder;
import asciipng.GridCell;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.List;

/**
 *
 * @author wb385924
 */
public class TestUserDefinedRegionClasses {
    private final String userRegion = "131.0183188781739,-25.316381987171052 | 131.08595346069342, -25.361684629596247 | 131.10414956665045, -25.30148426413426 | 131.0183188781739, -25.316381987171052 ";
    
    public static void main(String[] args) {
        TestUserDefinedRegionClasses test = new TestUserDefinedRegionClasses();
        test.testUserDefinedService();
        test.testConvertToPolygon();
        test.testShapeFileFilter();
        
    }

    public void testUserDefinedService() {
        double[][] points = UserDefinedRegionService.get().convertPointStringToArray(userRegion); // should be five points
        System.out.println(points.length);
    }

    public void testConvertArrayToPoints() {
        UserDefinedRegionService service = UserDefinedRegionService.get();
        
        double[][] points = service.convertPointStringToArray(userRegion); // should be five points
        List<Point> pointList= service.convertArrayToGeometry(points);
        System.out.println(pointList.size());
        for(Point p : pointList){
            System.out.println(p.toText());
        }
    }

    public void testConvertToPolygon() {
        UserDefinedRegionService service = UserDefinedRegionService.get();

        double[][] points = service.convertPointStringToArray(userRegion); // should be five points
        List<Point> pointList= service.convertArrayToGeometry(points);
        Polygon poly =GeometryBuilder.createPolygonFromCoordList(pointList);
        System.out.println("simple? " + poly.isValid());
    }

    public void testShapeFileFilter() {
        UserDefinedRegionService service = UserDefinedRegionService.get();

        double[][] points = service.convertPointStringToArray(userRegion); // should be five points
        List<Point> pointList= service.convertArrayToGeometry(points);
        Polygon poly =GeometryBuilder.createPolygonFromCoordList(pointList);

        UserDefinedRegionShapeFileFilter shapeFileFilter = new UserDefinedRegionShapeFileFilter();
        List<GridCell> cells = shapeFileFilter.readShapeFileForAnnualData("C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\GCM\\GCM_long_clim_annual.shp\\GCM_long_clim_annual.shp\\bccr_bcm2_0\\pcmdi_long_clim_annual.bccr_bcm2_0.pr_20c3m.1940-1959.shp", poly);
        System.out.println("number of intersecting cells " + cells.size() );
        new CellMapMaker().draw(cells, "funcells.png");
        
    }
}
