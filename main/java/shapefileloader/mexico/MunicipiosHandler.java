/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader.mexico;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.geometry.jts.WKTReader2;
import sdnis.wb.util.ShapeWrapper;
import shapefileloader.FeatureHandler;
import shapefileloader.mexico.domain.Municipio;

/**
 *
 * @author wb385924
 */
public class MunicipiosHandler implements FeatureHandler{
    private List<Municipio> municipios = new ArrayList<Municipio>();

    public List<Municipio> getMunicipios() {
        return municipios;
    }

    public void handleFeature( ShapeWrapper wrapper) {
        try {
            WKTReader2 reader = new WKTReader2();
            Geometry geom = reader.read(wrapper.getShapeString());
            Municipio municipio = new Municipio();
            municipio.setFromMap(wrapper.getPropertyMap());
            municipio.setGeometry(geom);
            municipios.add(municipio);
            //
        } catch (ParseException ex) {
            Logger.getLogger(MunicipiosHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
