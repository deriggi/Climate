/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.userdefinedregions;

import asciipng.GeometryBuilder;
import asciipng.GridCell;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;

/**
 *
 * @author wb385924
 */
public class UserDefinedRegionShapeFileFilter {

    private final String annual = "annual";

    public List<GridCell> readShapeFileForAnnualData(String path, Geometry userRegion) {

        SimpleFeatureIterator fi = null;
        List<GridCell> gridCells = new ArrayList<GridCell>();
        try {

            FileDataStore store = FileDataStoreFinder.getDataStore(new File(path));

            SimpleFeatureSource featureSource = store.getFeatureSource();
            SimpleFeatureCollection fc = featureSource.getFeatures();

            fi = fc.features();
            int count = 0;

            int numContainingPoints = 0;
            while (fi.hasNext()) {
                SimpleFeature f = fi.next();
                if (userRegion.intersects((Geometry) f.getDefaultGeometry())) {
                    numContainingPoints++;
                    Double d = extractAnnualProperty(f);
                    
                    Polygon cell = GeometryBuilder.createPolygonFromGeometry((Geometry) f.getDefaultGeometry());
                    if (d != null) {
                        gridCells.add(new GridCell(cell,d));
                    }
                }
                count++;
            }

        } catch (IOException e) {
            e.printStackTrace();
            
        }
        return gridCells;
    }

    private Double extractAnnualProperty(SimpleFeature feature) {
        try {
            Double annualVal = ((Double) feature.getAttribute(annual));
            return annualVal;
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return null;
    }
}
