/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cru.precip;

import ascii.AsciiDataLoader;
import asciipng.CellMapMaker;
import asciipng.CollectGeometryAsciiAction;
import asciipng.GridCell;
import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdnis.wb.util.BasicAverager;
import shapefileloader.ShapeFileParserGeometryExtractor;

/**
 *
 * @author Johnny
 */
public class GeometryCruPNGMapMaker {

    public static void main(String[] args) {
        new GeometryCruPNGMapMaker().createMapFromShapefileRegion();
    }

    public void createMapFromShapefileRegion() {
//        String starterFile = "C:\\Users\\Johnny\\Dropbox\\CRU\\pre\\pre\\cru_ts_3_10.1901.2009.pre_1961_1.asc";
        String starterFile = "C:\\Users\\Johnny\\Dropbox\\CRU\\tmp\\tmp\\";
        String shapeFilePath = "C:\\Users\\Johnny\\BoundaryData\\countries\\COL_adm\\COL_adm0.shp";
        String shapeFileAtttribute = "ISO";
        String shapeFileAtttributeValue = "COL";
        String outputFile = "C:\\Users\\Johnny\\outputMaps\\Columbia\\";
        String[] subs = new File(starterFile).list();
        HashMap<String, Geometry> geometryMap = new ShapeFileParserGeometryExtractor().readShapeFile(shapeFilePath, shapeFileAtttribute);
         Geometry areaGeometry = null;

        
        System.out.println("================");
        
        double allTimeMax = 296, allTimeMin = 106;
        
        System.out.println("================");
        
        for (String subfile : subs) {
            // get grid cells
            Set<GridCell> cruCells = getGridCells(starterFile + subfile);
            areaGeometry = geometryMap.get(shapeFileAtttributeValue);
            ArrayList<GridCell> itnersectingCells = getIntersectingGridCells(areaGeometry, cruCells);
            try {
                new CellMapMaker().drawToStream(itnersectingCells, new FileOutputStream(outputFile + subfile + ".png"), areaGeometry, allTimeMax, allTimeMin);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GeometryCruPNGMapMaker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void averageCells(ArrayList<GridCell> cells, BasicAverager ba) {
        for (GridCell cell : cells) {
            ba.update(cell.getValue());
        }

    }

    private ArrayList<GridCell> getIntersectingGridCells(Geometry areaGeometry, Collection<GridCell> cells) {

        Iterator<GridCell> cellIterator = cells.iterator();
        ArrayList<GridCell> gridCellCollection = new ArrayList<GridCell>();
        while (cellIterator.hasNext()) {
            GridCell gc = cellIterator.next();
            if (areaGeometry != null && areaGeometry.intersects(gc.getPolygon())) {
                gridCellCollection.add(gc);
            }
        }
        return gridCellCollection;

    }
    private static Logger log = Logger.getLogger(GeometryCruPNGMapMaker.class.getName());

    private Set<GridCell> getGridCells(String cruPath) {
        CollectGeometryAsciiAction caa = new CollectGeometryAsciiAction();
        try {
            long t0 = Calendar.getInstance().getTimeInMillis();
            new AsciiDataLoader(caa).parseAsciiFile(null, new FileInputStream(new File(cruPath)), null);
            long t1 = Calendar.getInstance().getTimeInMillis();
            log.log(Level.INFO, "collecting cells took :  {0} seconds", (t1 - t0) / 1000.0);
            log.log(Level.INFO, "have {0}", caa.getSize());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return caa.getGridCells();
    }
}
