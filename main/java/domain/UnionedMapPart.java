/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package domain;

import com.vividsolutions.jts.geom.Geometry;

/**
 *
 * @author wb385924
 */
public class UnionedMapPart {
    private double min;
    private double max;
    private transient Geometry shape;
    private String svg;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("min ");
        sb.append(min);
        sb.append(" max ");
        sb.append(max);

        sb.append("svg");
        sb.append(svg);
        return sb.toString();
    }


    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public Geometry getShape() {
        return shape;
    }

    public void setShape(Geometry shape) {
        this.shape = shape;
    }

    public String getSvg() {
        return svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }





}
