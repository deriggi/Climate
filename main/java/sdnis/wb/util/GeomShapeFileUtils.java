/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sdnis.wb.util;

import com.vividsolutions.jts.geom.Geometry;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

/**
 *
 * @author wb385924
 */
public class GeomShapeFileUtils {
    private static final Logger log = Logger.getLogger(GeomShapeFileUtils.class.getName());
    
    Pattern pattern = null;
//    FeatureCollection fc = null;
    List<String> propNames = null;

    public GeomShapeFileUtils( String propertyPattern, List<String> propertyNames) {
        if(propertyPattern != null){
            this.pattern = Pattern.compile(propertyPattern);
        }
//        this.fc = featureCollection;
        this.propNames = propertyNames;
    }

    public GeomShapeWrapper extractFeatureProperties(Feature f) {
        HashMap<String, String> propMap = new HashMap<String, String>();
        Geometry geom = (Geometry)f.getDefaultGeometryProperty().getValue();

        Collection<Property> props = f.getProperties();
        Iterator<Property> pi = props.iterator();

        while (pi.hasNext()) {
            Property p = pi.next();
            String name = p.getName().toString();
            //System.out.println(name);
            if (pattern != null) {
                if (propNames.contains(name) || pattern.matcher(name).find()) {
                    propMap.put(p.getName().toString(), p.getValue().toString());
                }
            }else{
                if (propNames.contains(name) ) {
                    try{
                        propMap.put(p.getName().toString(), p.getValue().toString());
                    } catch (Exception e){
                        log.log(Level.WARNING, "{0} prop name  {1}", new Object[]{e.getMessage(), name});
                    }
                }
            }
        }
        return new GeomShapeWrapper(geom, propMap);
    }

    //
//    public List<ShapeWrappers> extractProperties() {
//        FeatureIterator fi = fc.features();
//        List<ShapeWrappers> wrappers = new ArrayList<ShapeWrappers>();
//        Feature f = null;
//        while (fi.hasNext()) {
//            f = fi.next();
//            HashMap<String, String> propMap = new HashMap<String, String>();
//            String shapeText = f.getDefaultGeometryProperty().getValue().toString();
//            Collection<Property> props = f.getProperties();
//            Iterator<Property> pi = props.iterator();
//
//            while (pi.hasNext()) {
//                Property p = pi.next();
//                String name = p.getName().toString();
//                if (propNames.contains(name) || pattern.matcher(name).find()) {
//                    propMap.put(p.getName().toString(), p.getValue().toString());
//                }
//            }
//            wrappers.add(new ShapeWrappers(shapeText, propMap));
//        }
//        return wrappers;
//    }
}
