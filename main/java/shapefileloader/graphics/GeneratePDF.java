/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.graphics;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.DefaultFontMapper;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGraphics2D;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.vividsolutions.jts.geom.Envelope;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import org.apache.fop.svg.GraphicsConfiguration;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContext;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;

/**
 *
 * @author wb385924
 */
public class GeneratePDF {

    public static void generatePDF(MapContext mapContext) {
        int width = 2382;
        int height = 3369;
        Rectangle pageSize = new Rectangle(0, 0, width, height);
        Document document = new Document(pageSize);
        document.setMargins(0, 0, 0, 0);


        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("filename.pdf"));
            document.open();
            document.add(new Paragraph("Hi, this is your PDF file!"));
            PdfContentByte cb = writer.getDirectContent();
            PdfTemplate tp = cb.createTemplate(width, height);
            PdfGraphics2D graphic = (PdfGraphics2D) tp.createGraphics(width, height, new DefaultFontMapper());
      

            StreamingRenderer renderer = new StreamingRenderer();
            renderer.setContext(mapContext);
            // TODO: expose the generalization distance as a param
            // ((StreamingRenderer) renderer).setGeneralizationDistance(0);

            RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            renderer.setJava2DHints(hints);

            // we already do everything that the optimized data loading does...
            // if we set it to true then it does it all twice...
            java.util.Map rendererParams = new HashMap();
            rendererParams.put("optimizedDataLoadingEnabled", new Boolean(true));

            // we need the renderer to draw everything on the batik provided graphics object
            rendererParams.put(StreamingRenderer.OPTIMIZE_FTS_RENDERING_KEY, Boolean.FALSE);
            // render everything in vector form if possible
            rendererParams.put(StreamingRenderer.VECTOR_RENDERING_KEY, Boolean.TRUE);

            renderer.setRendererHints(rendererParams);



            // enforce no more than x rendering errors


            // render the map
            renderer.paint(graphic, new java.awt.Rectangle(0, 0, width, height), mapContext.getLayerBounds());



            // cleanup
            graphic.dispose();
            cb.addTemplate(tp, 0, 0);
            document.newPage();
            PdfPCell cell = new PdfPCell();
            cell.addElement(new Paragraph("This is the color of each cell, it means precip"));
            cell.setBackgroundColor(new BaseColor(8, 48, 107));
            PdfPTable table = new PdfPTable(3);
            PdfPCell c1 = new PdfPCell(new Phrase("Table Header 1"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Table Header 2"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Table Header 3"));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
            table.setHeaderRows(1);

            table.addCell(cell);
            table.addCell("1.1");
            table.addCell("1.2");
            table.addCell("2.1");
            table.addCell("2.2");
            table.addCell("2.3");

            document.add(table);
            //cleanup
            document.close();
            writer.close();
        } catch (DocumentException de) {
            System.err.println(de.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        document.close();
    }
}
