/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader.regions;

import java.util.ArrayList;
import shapefileloader.ShapeFileParser;

/**
 *
 * @author wb385924
 */
public class RegionLoader {

    public static void main(String[] args){
        new RegionLoader().loadRegion("S:\\REGIONAL\\AFRICA\\ADMIN\\Africa_region_dissolved.shp");
    }

    public void loadRegion(String shapeFilePath){
        ShapeFileParser parser = new ShapeFileParser();
        ArrayList<String> attrs = new ArrayList<String>();
        attrs.add("AFR");
        parser.readShapeFile(shapeFilePath, attrs, null, new RegionFeatureHandler(9137));

    }



}
