/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader.mexico.domain;

import com.vividsolutions.jts.geom.Geometry;

/**
 *
 * @author wb385924
 */
public class IntersectingCell {
    private Geometry geom;
    private double value;
    private int classIndex;

    public IntersectingCell(Geometry geom, double value, int classIndex) {
        this.geom = geom;
        this.value = value;
        this.classIndex = classIndex;
    }

    public int getClassIndex() {
        return classIndex;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }

    public Geometry getGeom() {
        return geom;
    }

    public void setGeom(Geometry geom) {
        this.geom = geom;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    


}
