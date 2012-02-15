/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.mexico;

import com.vividsolutions.jts.geom.Geometry;
import export.util.FileExportHelper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;

import org.opengis.feature.simple.SimpleFeature;
import sdnis.wb.util.BasicAverager;
import sdnis.wb.util.ShapeFileUtils;
import shapefileloader.ShapeFileParser;
import shapefileloader.graphics.ClassifierHelper;
import shapefileloader.mexico.domain.IntersectingCell;
import shapefileloader.mexico.domain.Municipio;
import shapefileloader.mexico.domain.TableWithHEader;
import shapefileloader.gcm.P_NameParser;

/**
 *
 * @author wb385924
 */
public class MunicipiosShapeFileReader {

    private final static Logger log = Logger.getLogger(MunicipiosShapeFileReader.class.getName());
    private ArrayList<String> municipiosPropertyNames = null;
    private ArrayList<String> monthNames = null;

    public static void createHTMLPackage() {
        String gridPath = "C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\gcm_long_clim_monthly\\GCM_long_clim_monthly.shp\\bccr_bcm2_0\\pcmdi_long_clim.bccr_bcm2_0.pr_sresa2.2020-2039.shp";
        String gridPath1 = "C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\gcm_long_clim_monthly\\GCM_long_clim_monthly.shp\\bccr_bcm2_0\\pcmdi_long_clim.bccr_bcm2_0.pr_sresa2.2040-2059.shp";
        String gridPath2 = "C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\gcm_long_clim_monthly\\GCM_long_clim_monthly.shp\\bccr_bcm2_0\\pcmdi_long_clim.bccr_bcm2_0.pr_sresa2.2060-2079.shp";


        String baseLineGridPath = "C:\\Users\\wb385924\\OLD_CLIMATE_DATA\\gcm_long_clim_monthly\\GCM_long_clim_monthly.shp\\bccr_bcm2_0\\pcmdi_long_clim.bccr_bcm2_0.pr_20c3m.1960-1979.shp";




        String municiposShapePath = "C:/Users/wb385924/Mexico/Municipios_Mexico.shp";
        MunicipiosShapeFileReader municipiosShapeReader = new MunicipiosShapeFileReader();
        List<Municipio> municipios = municipiosShapeReader.collectAllMunicipios(municiposShapePath);
        int counter = 0;
        for (Municipio municipio : municipios) {
//            Municipio municipio = municipios.get(0);
            

            HashMap<String, BasicAverager> monthAverages = municipiosShapeReader.getMonthlyAveragesForMunicipio(municipio, gridPath);
            HashMap<String, BasicAverager> monthAverages1 = municipiosShapeReader.getMonthlyAveragesForMunicipio(municipio, gridPath1);
            HashMap<String, BasicAverager> monthAverages2 = municipiosShapeReader.getMonthlyAveragesForMunicipio(municipio, gridPath2);
            List<TableWithHEader> headers = new ArrayList<TableWithHEader>();
            P_NameParser parser = new P_NameParser();

            HashMap<String, BasicAverager> baseLineAverages = municipiosShapeReader.getMonthlyAveragesForMunicipio(municipio, baseLineGridPath);

            headers.add(new TableWithHEader(baseLineAverages, parser.parsePathName(baseLineGridPath)));
            headers.add(new TableWithHEader(monthAverages, parser.parsePathName(gridPath)));
            headers.add(new TableWithHEader(monthAverages1, parser.parsePathName(gridPath1)));
            headers.add(new TableWithHEader(monthAverages2, parser.parsePathName(gridPath2)));

            double max = BasicAverager.maxOfTheAveragers(monthAverages.values());
            double min = BasicAverager.minOfTheAveragers(monthAverages.values());
            log.log(Level.INFO, "max and min are {0} and  {1}", new Object[]{new Double(max), new Double(min)});
            double[][] bounds = ClassifierHelper.getEqualIntervalBounds(min, max, 10);
            log.log(Level.INFO, "max and min are {0} and  {1} to {2} and {3}", new Object[]{bounds[0][0], bounds[0][1], bounds[1][0], bounds[1][1]});


            municipio.addCells(municipiosShapeReader.exportCellsAccordingToClassRange(municipio, gridPath, bounds, "Jan"));

            MuniciposHTMLProducer producer = new MuniciposHTMLProducer();
            producer.exportData(municipio, headers, municipios);

        }
    }
    public static void main(String[] args){
        MunicipiosShapeFileReader.createHTMLPackage();
    }


    public void createIndexHtml(){
        String municiposShapePath = "C:/Users/wb385924/Mexico/Municipios_Mexico.shp";
        List<Municipio> municipios = new MunicipiosShapeFileReader().collectAllMunicipios(municiposShapePath);
        StringBuilder sb = new StringBuilder();
        sb.append("<table><tbody>");
         for (Municipio municipio : municipios) {
              String outName = "<tr><td><a href=\"" + municipio.getEstado() + " - " + municipio.getNombre() + ".html\">" + municipio.getEstado() + " - " + municipio.getNombre()+"</a></tr></td>";
              sb.append(outName);
         }
         sb.append("</tbody></table>");

         FileExportHelper.appendToFile("C:/Users/wb385924/Mexico/svgout/indexhtml.txt", sb.toString());
    }

    /**
     * returns class [index] , [out of ]
     * @param val
     * @param bounds
     * @return
     */
   

    public MunicipiosShapeFileReader() {
        municipiosPropertyNames = new ArrayList<String>();
        municipiosPropertyNames.add(Municipio.CLAVE_KEY);
        municipiosPropertyNames.add(Municipio.NUMERO_KEY);
        municipiosPropertyNames.add(Municipio.NOMBRE_KEY);
        municipiosPropertyNames.add(Municipio.CANT_POL_KEY);
        municipiosPropertyNames.add(Municipio.ESTADO_KEY);
        municipiosPropertyNames.add(Municipio.OID_KEY);

        monthNames = new ArrayList<String>();
        monthNames.add("Jan");
        monthNames.add("Feb");
        monthNames.add("Mar");
        monthNames.add("Apr");
        monthNames.add("May");
        monthNames.add("Jun");
        monthNames.add("July");
        monthNames.add("Aug");
        monthNames.add("Sep");
        monthNames.add("Oct");
        monthNames.add("Nov");
        monthNames.add("Dec");
    }

    private HashMap<String, BasicAverager> getAveragerMap() {
        HashMap<String, BasicAverager> map = new HashMap<String, BasicAverager>();
        for (String month : monthNames) {
            map.put(month, new BasicAverager());
        }
        return map;
    }

    public List<Municipio> collectAllMunicipios(String fullPath) {
        ShapeFileParser parser = new ShapeFileParser();
        MunicipiosHandler handler = new MunicipiosHandler();
        parser.readShapeFile(fullPath, municipiosPropertyNames, null, handler);
        List<Municipio> municipios = handler.getMunicipios();
        log.log(Level.INFO, "we have {0} municipios ", municipios.size());

        return municipios;
    }

    public HashMap<String, BasicAverager> getMonthlyAveragesForMunicipio(Municipio municipio, String gridPath) {
        HashMap<String, BasicAverager> municipioAverager = getAveragerMap();
        try {
            Geometry municipioShape = municipio.getGeometry();


            FileDataStore store = FileDataStoreFinder.getDataStore(new File(gridPath));
            SimpleFeatureSource featureSource = store.getFeatureSource();
            SimpleFeatureCollection gridFeatureCollection = featureSource.getFeatures();
            SimpleFeatureIterator fi = gridFeatureCollection.features();
            int numContainingPoints = 0;
            ShapeFileUtils shapeUtil = new ShapeFileUtils( null, monthNames);
            while (fi.hasNext()) {
                SimpleFeature gridCell = fi.next();
                if (municipioShape.intersects((Geometry) gridCell.getDefaultGeometry())) {
                    processGridCellMonths(municipioAverager, shapeUtil.extractFeatureProperties(gridCell).getPropertyMap());
                    numContainingPoints++;
                }
            }


        } catch (IOException ex) {
            Logger.getLogger(MunicipiosShapeFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return municipioAverager;
    }

    public List<IntersectingCell> exportCellsAccordingToClassRange(Municipio municipio, String gridPath, double[][] bounds, String month) {

        List<IntersectingCell> intersectingCells = new ArrayList<IntersectingCell>();
        try {
            Geometry municipioShape = municipio.getGeometry();


            FileDataStore store = FileDataStoreFinder.getDataStore(new File(gridPath));
            SimpleFeatureSource featureSource = store.getFeatureSource();
            SimpleFeatureCollection gridFeatureCollection = featureSource.getFeatures();
            SimpleFeatureIterator fi = gridFeatureCollection.features();

            ShapeFileUtils shapeUtil = new ShapeFileUtils( null, monthNames);
            while (fi.hasNext()) {
                SimpleFeature gridCell = fi.next();
                Geometry gridCellGeom = (Geometry) gridCell.getDefaultGeometry();
                if (municipioShape.intersects(gridCellGeom)) {
                    HashMap<String, String> cellProps = shapeUtil.extractFeatureProperties(gridCell).getPropertyMap();
                    double monthVal = toDouble(cellProps.get(month));
                    int[][] classOutOf = ClassifierHelper.getClass(monthVal, bounds);
                    System.out.println("this cell is in class " + classOutOf[0][0] + " with their val of " + monthVal);
                    intersectingCells.add(new IntersectingCell(gridCellGeom, monthVal, classOutOf[0][0]));
                }
            }


        } catch (IOException ex) {
            Logger.getLogger(MunicipiosShapeFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return intersectingCells;
    }

    private Double toDouble(String d) {
        if (d == null) {
            return null;
        }
        try {
            return Double.parseDouble(d);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            return null;
        }
    }

    private void processGridCellMonths(HashMap<String, BasicAverager> municipioAverager, HashMap<String, String> gridCellAttributes) {
        // take each month and add it to the month avgs for the entire municipio
        Set<String> keys = municipioAverager.keySet();
        for (String monthKey : keys) {
            Double val = toDouble(gridCellAttributes.get(monthKey));
            if (val != null) {
                municipioAverager.get(monthKey).update(val);
            }
        }
    }
}
