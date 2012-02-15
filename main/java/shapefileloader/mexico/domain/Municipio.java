/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.mexico.domain;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author wb385924
 */
public class Municipio {

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Municipio other = (Municipio) obj;
        if ((this.nombre == null) ? (other.nombre != null) : !this.nombre.equals(other.nombre)) {
            return false;
        }
        if ((this.estado == null) ? (other.estado != null) : !this.estado.equals(other.estado)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.nombre != null ? this.nombre.hashCode() : 0);
        hash = 17 * hash + (this.estado != null ? this.estado.hashCode() : 0);
        return hash;
    }
    private Geometry geometry = null;


    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
    private int clave;
    private int numero;
    private String nombre;
    private int cantPol;
    private String estado;
    private int oid;
    private List<IntersectingCell> intersectingCells = new ArrayList<IntersectingCell>();

    public List<IntersectingCell> getIntersectingCells() {
        return intersectingCells;
    }

    public final static String CLAVE_KEY = "CLAVE";
    public final static String NUMERO_KEY = "NUMERO";
    public final static String NOMBRE_KEY = "NOMBRE";
    public final static String CANT_POL_KEY = "CANT_POL";
    public final static String ESTADO_KEY = "ESTADO";
    public final static String OID_KEY = "OID_1";


    public final static Logger log = Logger.getLogger(Municipio.class.getName());


    public void addCells(Collection<IntersectingCell> cells){
        intersectingCells.addAll(cells);
    }
    public void setFromMap(HashMap<String, String> attributes) {
        if (attributes.containsKey(CLAVE_KEY)) {
            try {
                clave = Integer.parseInt(attributes.get(CLAVE_KEY));
            } catch (NumberFormatException nfe) {
                log.log(Level.SEVERE, "no parsey {0}", attributes.get(CLAVE_KEY));
            }
        }

        if (attributes.containsKey(NUMERO_KEY)) {
            try {
                numero = Integer.parseInt(attributes.get(NUMERO_KEY));
            } catch (NumberFormatException nfe) {
                log.log(Level.SEVERE, "no parsey {0}", attributes.get(NUMERO_KEY));
            }
        }

        if (attributes.containsKey(NOMBRE_KEY)) {
            nombre = attributes.get(NOMBRE_KEY);
        }

        if (attributes.containsKey(CANT_POL_KEY)) {
            try {
                cantPol = Integer.parseInt(attributes.get(CANT_POL_KEY));
            } catch (NumberFormatException nfe) {
                log.log(Level.SEVERE, "no parsey {0}", attributes.get(CANT_POL_KEY));
            }
        }

        if (attributes.containsKey(ESTADO_KEY)) {
            estado = attributes.get(ESTADO_KEY);
        }

        if (attributes.containsKey(OID_KEY)) {
            try {
                oid = Integer.parseInt(attributes.get(OID_KEY));
            } catch (NumberFormatException nfe) {
                log.log(Level.SEVERE, "no parsey {0}", attributes.get(OID_KEY));
            }
        }



    }

    public int getCantPol() {
        return cantPol;
    }

    public void setCantPol(int cantPol) {
        this.cantPol = cantPol;
    }

    public int getClave() {
        return clave;
    }

    public void setClave(int clave) {
        this.clave = clave;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }
}
