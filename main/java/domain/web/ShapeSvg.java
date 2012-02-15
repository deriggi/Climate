/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain.web;



/**
 *
 * @author wb385924
 */

public class ShapeSvg {

    public ShapeSvg(String shape) {
        this.svg = shape;
    }

    public ShapeSvg(String shape, String name) {
        this.svg = shape;
        this.name = name;
    }

    public ShapeSvg(String shape, String name, float min, float max) {
        this.svg = shape;
        this.name = name;
        this.min = min;
        this.max = max;
    }

    public ShapeSvg() {
    }
    private float min, max;
    private String svg;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSvg() {
        return svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }
}
