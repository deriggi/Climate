/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asciipng;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.List;
import org.geotools.geometry.jts.JTSFactoryFinder;

/**
 *
 * @author wb385924
 */
public class GeometryBuilder {
    
    public static Polygon createGridCellFromLowerLeftPoint(double longitude, double latitude, double cellSize) {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

        Coordinate[] coords =
                new Coordinate[]{
                new Coordinate(longitude, latitude),
                new Coordinate(longitude, latitude + cellSize),
                new Coordinate(longitude + cellSize, latitude + cellSize),
                new Coordinate(longitude + cellSize, latitude),
                new Coordinate(longitude, latitude)};

        
        LinearRing ring = geometryFactory.createLinearRing(coords);
        Polygon polygon = geometryFactory.createPolygon(ring, null);
        return polygon;
    }

    public static Polygon createPolygonFromGeometry(Geometry g){
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        LinearRing ring = geometryFactory.createLinearRing(g.getCoordinates());
        Polygon polygon = geometryFactory.createPolygon(ring, null);
        return polygon;
    }
    public static Point createPointFromCoords(double longitude, double latitude) {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        Point point = geometryFactory.createPoint((new Coordinate(longitude,latitude)));
        
        return point;
    }

    public static Polygon createPolygonFromCoordList(List<Point> points) {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        Coordinate[] coords = new Coordinate[points.size()];
        int i = 0;

        for(Point p : points){
            coords[i++] = new Coordinate(p.getX(), p.getY());
        }
        
        LinearRing ring = geometryFactory.createLinearRing(coords);
        Polygon polygon = geometryFactory.createPolygon(ring, null);
        return polygon;
    }

     
}
