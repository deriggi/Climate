/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.mexico;

import com.vividsolutions.jts.geom.MultiPolygon;
import export.util.FileExportHelper;
import java.awt.Color;
import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import sdnis.wb.util.BasicAverager;
import shapefileloader.gcm.P_Config;
import shapefileloader.mexico.domain.IntersectingCell;
import shapefileloader.mexico.domain.Municipio;
import shapefileloader.mexico.domain.TableWithHEader;
import shapefileloader.graphics.GeneratePNG;
import shapefileloader.graphics.GenerateSVG;

/**

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN'
'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>



 * Given data, this thing should produce an html package
 * @author wb385924
 */
public class MuniciposHTMLProducer {
//<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>

    private String header = " <!DOCTYPE html><html lang='en'> <head>  <title>Charts and Tables</title> <link rel='stylesheet' href='http://twitter.github.com/bootstrap/1.3.0/bootstrap.min.css'> <script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>     <script type=\"text/javascript\" >  google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});  </script> </head> <body><div class='container'>";
    private StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
    private FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(null);
    private Color[] colors = new Color[9];
    ArrayList<String> monthNames = new ArrayList<String>();

    public MuniciposHTMLProducer() {

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


        colors[0] = new Color(247, 251, 255);
        colors[1] = new Color(222, 235, 247);
        colors[2] = new Color(198, 219, 239);
        colors[3] = new Color(158, 202, 225);
        colors[4] = new Color(107, 174, 214);
        colors[5] = new Color(66, 146, 198);
        colors[6] = new Color(33, 113, 181);
        colors[7] = new Color(8, 81, 156);
        colors[8] = new Color(8, 48, 107);

    }

    public void exportData(Municipio municipio, List<TableWithHEader> headers, List<Municipio> municipios) {
        
        FileOutputStream fos = null;
        
        String outName = "C:/Users/wb385924/Mexico/svgout/" + municipio.getEstado() + " - " + municipio.getNombre() + ".html";
        String imagePath = "C:/Users/wb385924/Mexico/svgout/" + municipio.getEstado() + " - " + municipio.getNombre() + ".png";
        String csvBase = "C:/Users/wb385924/Mexico/svgout/" + municipio.getEstado() + " - " + municipio.getNombre();
        try {
            // create boundary collection
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(createFeatureType());
            featureBuilder.add(municipio.getGeometry());
            SimpleFeatureCollection municipioCollection = FeatureCollections.newCollection();
            municipioCollection.add(featureBuilder.buildFeature(null));
            // create cell collection
            SimpleFeatureCollection cellCollection = FeatureCollections.newCollection();
            List<IntersectingCell> getCells = municipio.getIntersectingCells();
            for (IntersectingCell cell : getCells) {
                SimpleFeatureBuilder cellFeatureBuilder = new SimpleFeatureBuilder(createFeatureType());
                cellFeatureBuilder.add(cell.getGeom());
                cellCollection.add(cellFeatureBuilder.buildFeature(null));
            }
            SimpleFeatureCollection otherMunicipiosCollection = FeatureCollections.newCollection();
            for (Municipio m : municipios) {
                SimpleFeatureBuilder otherMunicipBuilder = new SimpleFeatureBuilder(createFeatureType());
                if (!m.equals(municipio)) {
                    otherMunicipBuilder.add(m.getGeometry());
                    otherMunicipiosCollection.add(otherMunicipBuilder.buildFeature(null));
                }
            }
            MapContext map = new DefaultMapContext();
            map.setTitle("Quickstart");
            map.addLayer(cellCollection, getGridCellStyle(0));
            map.addLayer(otherMunicipiosCollection, createSubCountryStyle());
            map.addLayer(municipioCollection, createCountryStyle());


            FileExportHelper.appendToFile(outName, header);
            StringBuilder sb = new StringBuilder();
            sb.append("<div class=page-header>");
            sb.append("<h1>");
            sb.append(municipio.getEstado());
            sb.append(" <small> - ");
            sb.append(municipio.getNombre());
            sb.append("</small>");
            sb.append("</h1>");
            sb.append("</div>");

            FileExportHelper.appendToFile(outName, sb.toString());
            fos = new FileOutputStream(new File(outName), true);

//            writeXML(map, fos);

            FileExportHelper.appendToFile(outName, "<div><img src=\"" + municipio.getEstado() + " - " + municipio.getNombre() + ".png\"</div>");
            GeneratePNG.saveImage(map, imagePath, new Double(map.getLayerBounds().getWidth() * 20).intValue());
            map.dispose();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MuniciposHTMLProducer.class.getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(MuniciposHTMLProducer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(MuniciposHTMLProducer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        int i = 0;
        for (TableWithHEader t : headers) {
            
            FileExportHelper.appendToFile(outName, convertNameToHTML(t.getConfig()));
            FileExportHelper.appendToFile(outName, getJavascriptChartFromTable(t.getTable(), i++));
            FileExportHelper.appendToFile(outName, getHTMLTableFromHashMap(t.getTable()));
            writeCSV(t,csvBase);
            writeCSVLink(t, outName,municipio);
            FileExportHelper.appendToFile(outName, "<br/>");
        }

        FileExportHelper.appendToFile(outName, "</div></body></html>");



    }

    private void writeCSVLink(TableWithHEader table,String outName, Municipio m){
        FileExportHelper.appendToFile(outName,"<div><a href=\"" +  m.getEstado() + " - " + m.getNombre()+"_"+table.getConfig().toCompactString()+".csv " +"\"  >csv</a></div>");
        
    }

    private void writeCSV(TableWithHEader table, String csvBase) {
        StringBuilder sb = new StringBuilder();
        String comma = ",";
        for (int i = 0; i < monthNames.size(); i++) {

            sb.append((monthNames.get(i)));
            if (i < monthNames.size() - 1) {
                sb.append(comma);
            }

        }
        sb.append(System.getProperty("line.separator"));
        for (int i = 0; i < monthNames.size(); i++) {

            sb.append(new Double(table.getTable().get(monthNames.get(i)).getAvg()).floatValue());
            if (i < monthNames.size() - 1) {
                sb.append(comma);
            }

        }
        FileExportHelper.appendToFile(csvBase+"_"+table.getConfig().toCompactString()+".csv", sb.toString());
    }

    private void writeXML(MapContext map, FileOutputStream fos) {
        try {
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            GenerateSVG.exportSVG(map, map.getLayerBounds(), boas, new Dimension(new Double(map.getLayerBounds().getWidth() * 25).intValue(), new Double(new Double(map.getLayerBounds().getHeight() * 25)).intValue()));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setEntityResolver(new EntityResolver() {

                public org.xml.sax.InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    if (systemId.contains("http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd")) {
                        return new org.xml.sax.InputSource(new StringReader(""));
                    } else {
                        return null;
                    }
                }
            });
            Document d = db.parse(new ByteArrayInputStream(boas.toByteArray()));
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            Result result = new StreamResult(fos);
            xformer.transform(new DOMSource(d), result);
        } catch (TransformerException ex) {
            Logger.getLogger(MuniciposHTMLProducer.class.getName()).log(Level.SEVERE, null, ex);

        } catch (SAXException ex) {
            Logger.getLogger(MuniciposHTMLProducer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MuniciposHTMLProducer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(MuniciposHTMLProducer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**              <div class="row">
    <div class="span13 " style="height:200px">
    <table>
    <thead><tr><th>Jan</th><th>Feb</th><th>Mar</th><th>Apr</th><th>May</th><th>Jun</th><th>Jul</th><th>Aug</th><th>Sep</th><th>Oct</th><th>Nov</th><th>Dec</th></tr></thead>
    <tbody id="databody">
    <tr><td id="m_1">20.003</td><td id="m_2">20.233</td><td id="m_3">19.664</td><td id="m_4">18.024</td><td id="m_5">15.394</td><td id="m_6">12.580</td><td id="m_7">13.257</td><td id="m_8">16.622</td><td id="m_9">19.469</td><td id="m_10">19.655</td><td id="m_11">19.858</td><td id="m_12">19.665</td></tr>
    </tbody>
    </table>
    </div>
    </div>
     **/
    private String getHTMLTableFromHashMap(HashMap<String, BasicAverager> ba) {
        StringBuilder sb = new StringBuilder();

        sb.append("<table>");
        sb.append("<thead>");
        sb.append("<tr>");

        for (String s : monthNames) {
            sb.append("<th>");
            sb.append(s);
            sb.append("</th>");
        }

        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody>");
        sb.append("<tr>");

        for (String s : monthNames) {
            sb.append("<td>");
            sb.append(new Double(ba.get(s).getAvg()).floatValue());
            sb.append("</td>");
        }

        sb.append("</tr>");
        sb.append("</tbody>");
        sb.append("</table>");


        return sb.toString();

    }

    private String getJavascriptChartFromTable(HashMap<String, BasicAverager> ba, int distinguisher) {
        StringBuilder sb = new StringBuilder();
        String nl = System.getProperty("line.separator");

        sb.append("<div id='chart_div").append(distinguisher).append("'></div>");
        sb.append(nl);
        sb.append("<script type=\"text/javascript\">");
        sb.append(nl);


        sb.append(" var data").append(distinguisher).append(" = new google.visualization.DataTable();");
        sb.append(nl);
        sb.append("data").append(distinguisher).append(".addColumn('string', 'Month');");
        sb.append(nl);
        sb.append("data").append(distinguisher).append(".addColumn('number', 'Precip');");
        sb.append(nl);
        sb.append("data").append(distinguisher).append(".addRows(").append(ba.size()).append(");");
        sb.append(nl);
        int counter = 0;
        for (String s : monthNames) {
            sb.append("data").append(distinguisher).append(".setValue(");
            sb.append(counter);
            sb.append(",");
            sb.append("0,");
            sb.append("'" + s + "');");
            sb.append(nl);

            sb.append("data").append(distinguisher).append(".setValue(");
            sb.append(counter);
            sb.append(",");
            sb.append("1,");

            sb.append(ba.get(s).getAvg());
            sb.append(");");
            sb.append(nl);
            counter++;
        }
        sb.append("var chart").append(distinguisher).append(" = new google.visualization.LineChart(document.getElementById('chart_div").append(distinguisher).append("'));");
        sb.append(nl);
        sb.append("chart").append(distinguisher).append(".draw(data").append(distinguisher).append(", {width: 800, height: 340, title: 'Precipitation'})");
        sb.append(nl);
        sb.append("</script>");
        sb.append(nl);
        return sb.toString();

    }

    private String convertNameToHTML(P_Config config) {


        StringBuilder sb = new StringBuilder();
        sb.append("<div class=page-header>");
        sb.append("<h2>");
        sb.append(config.getfYear());
        sb.append(" ");

        sb.append(config.gettYear());

        sb.append("<small>");
        sb.append(config.getGcm().toString());
        sb.append(" ");

        sb.append(config.getScenario().toString());
        sb.append(" ");
        sb.append("</small>");

        sb.append("</h2>");
        sb.append("</div>");
        return sb.toString();

    }

    private Style getGridCellStyle(int classIndex) {

        // create a partially opaque outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(new Color(8, 48, 107)),
                filterFactory.literal(0),
                filterFactory.literal(0.5));

        // create a partial opaque fill
        Fill fill = styleFactory.createFill(
                filterFactory.literal(new Color(8, 48, 107)),
                filterFactory.literal(0.5));

        // create a partially opaque outline stroke
//        Stroke stroke = styleFactory.createStroke(
//                filterFactory.literal(colors[classIndex]),
//                filterFactory.literal(0));
//
//        // create a partial opaque fill
//        Fill fill = styleFactory.createFill(
//                filterFactory.literal(colors[classIndex]),
//                filterFactory.literal(1));


        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);

        Rule rule = styleFactory.createRule();

        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    private Style createCountryStyle() {

        // create a partially opaque outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.WHITE),
                filterFactory.literal(2),
                filterFactory.literal(0.8));

        // create a partial opaque fill
        Fill fill = styleFactory.createFill(
                filterFactory.literal(new Color(8, 48, 107)),
                filterFactory.literal(0.0));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);

        Rule rule = styleFactory.createRule();

        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    private Style createSubCountryStyle() {

        // create a partially opaque outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.LIGHT_GRAY),
                filterFactory.literal(1),
                filterFactory.literal(0.8));

        // create a partial opaque fill
        Fill fill = styleFactory.createFill(
                filterFactory.literal(new Color(8, 48, 107)),
                filterFactory.literal(0.0));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);

        Rule rule = styleFactory.createRule();

        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    private static SimpleFeatureType createFeatureType() {

        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system

        // add attributes in order
        builder.add("Location", MultiPolygon.class);
        builder.length(15).add("Name", String.class); // <- 15 chars width for name field

        // build the type
        final SimpleFeatureType LOCATION = builder.buildFeatureType();

        return LOCATION;
    }
}
