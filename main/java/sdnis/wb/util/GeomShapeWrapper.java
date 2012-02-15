/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sdnis.wb.util;

import com.vividsolutions.jts.geom.Geometry;
import java.util.HashMap;

/**
 *
 * @author wb385924
 */
public class GeomShapeWrapper extends ShapeWrapper{

    private Geometry geom = null;

    public Geometry getGeom() {
        return geom;
    }

    
    public GeomShapeWrapper(Geometry geom, HashMap<String, String> propMap) {
        super(geom.toText(), propMap);
        
        this.geom = geom;
            
    }

    

    
}
