/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ascii;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class AsciiDataLoader {
    

 
    private static final Logger log = Logger.getLogger(AsciiDataLoader.class.getName());
    
    private final String NCOLS = "ncols";
    private final String NROWS = "nrows";
    private final String XLLCORNER = "xllcorner";
    private final String YLLCORNER = "yllcorner";
    private final String CELLSIZE = "cellsize";
    private final String NODATA = "NODATA_value";
    private final int STANDARD_HEADER_SIZE = 6;
    private int numCols;
    private int numRows;
    private double xllcorner;
    private double yllcorner;
    private double cellsize;
    private int nullValue;
    private AsciiAction action = null;
//    private File f = null;
    private Connection con = null;
//    public AsciiDataLoader(File f) {
//        // set date, numCols, etc...
//        this.f = f;
//    }

    public AsciiDataLoader(AsciiAction action) {
        this.action = action;
    }

    public void parseAsciiFile(Date fileDate, InputStream is, Date minDate) {

        parseHeader(fileDate, is, minDate);

    }

  

    

    private String getHeaderType(String line) {
        log.log(Level.FINE, "about to get header from {0}", line);
        if (line.indexOf(NCOLS) != -1) {
            return NCOLS;
        }

        if (line.indexOf(NROWS) != -1) {
            return NROWS;
        }

        if (line.indexOf(XLLCORNER) != -1) {
            return XLLCORNER;
        }

        if (line.indexOf(YLLCORNER) != -1) {
            return YLLCORNER;
        }

        if (line.indexOf(NODATA) != -1) {
            return NODATA;
        }

        if (line.indexOf(CELLSIZE) != -1) {
            return CELLSIZE;
        }

        return null;

    }

    private Double getHeaderValue(String line) {
        if (line == null) {
            return null;
        }
        log.log(Level.FINE, "about to get value from {0}", line);
        String[] parts = line.split("\\s");
        for (String p : parts) {
//            System.out.println(partCounter++ +  " " + p);
        }
        log.log(Level.FINE, "parts length is {0}", parts.length);
//        if(parts.length !=2){
//            return null;
//        }

        return Double.parseDouble(parts[parts.length - 1]);
    }

    private void initParams(HashMap<String, Object> header) {
        this.nullValue = ((Double) header.get(NODATA)).intValue();
        this.numCols = ((Double) header.get(NCOLS)).intValue();
        this.numRows = ((Double) header.get(NROWS)).intValue();
        this.xllcorner = ((Double) header.get(XLLCORNER));
        this.yllcorner = ((Double) header.get(YLLCORNER));
        this.cellsize = ((Double) header.get(CELLSIZE));
    }

    private void validateParams() {
        if (nullValue == 0) {
            log.warning("null value not defined");
        }

        if (numCols == 0) {
            log.warning("num cols not defined");
        }

        if (numRows == 0) {
            log.warning("num rows not defined");
        }

        if (xllcorner == 0) {
            log.fine("xllcorner not defined");
        }

        if (yllcorner == 0) {
            log.warning("yllcorner not defined");
        }

        if (cellsize == 0) {
            log.warning("cellsize not defined");
        }
    }

    private void parseHeader(Date fileDate, InputStream f, Date minDate) {
        InputStreamReader isr = null;
        BufferedReader br = null;
        HashMap<String, Object> header = new HashMap<String, Object>();

        if (fileDate != null) {

            if (minDate != null && fileDate.before(minDate)) {
                log.log(Level.INFO, "skipping {0}", fileDate);
                return;
            }
        }
        try {
            isr = new InputStreamReader(f);
            br = new BufferedReader(isr);
            String line = null;
            int lineCounter = 0;
            int headerSize;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                if (lineCounter < STANDARD_HEADER_SIZE) {


                    String type = getHeaderType(line);
                    Double d = getHeaderValue(line);
                    log.log(Level.FINE, "type and value {0} {1}", new Object[]{type, d});

                    if (type != null && d != null) {
                        header.put(type, d);
                    } else {
                        log.log(Level.WARNING, "error processing header line: ", line);
                    }
                    if (header.size() == STANDARD_HEADER_SIZE) {
                        initParams(header);
                    }
                } else {
                    validateParams();
                    handleLine(line, numRows, xllcorner, yllcorner, lineCounter, cellsize, fileDate);
                }
                lineCounter++;
//                System.out.println(lineCounter);
            }
            headerSize = header.size();
            if (headerSize != STANDARD_HEADER_SIZE) {
                log.log(Level.WARNING, "header object is now size {0}", header.size());
            }


        } catch (IOException ex) {
            Logger.getLogger(AsciiDataLoader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                isr.close();
            } catch (IOException ex) {
                Logger.getLogger(AsciiDataLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

//    private int getOrAddPoint(double longitude, double latitude) {
//        int id = GeoDao.getGeometryBoundsId(getCon(), "POINT(" + longitude + " " + latitude + ")", "cru_pr_point", "geom");
//        if (id == -1) {
//            id = GeoDao.storeGeometry(getCon(), "cru_pr_point", "geom", "POINT(" + longitude + " " + latitude + ")");
//        }
//        return id;
//
//    }

    private void handleLine(String line, int numRows,  double xmin, double ymin, int lineNumber, double cellSize, Date date) {
        String[] lineParts = line.split("\\s+");
        if (lineParts[0].trim().length() > 0) {
            log.log(Level.FINE, "{0} first:{1} last: {2}", new Object[]{lineParts.length, lineParts[0], lineParts[lineParts.length - 1]});
        }

        int colNumber = 0;
        log.log(Level.FINE, "col width: {0}", lineParts.length);
        for (String s : lineParts) {


            if (s.trim().length() > 0) {
                double rowOffset = (numRows - (lineNumber - STANDARD_HEADER_SIZE)) * cellSize;
                double y = ymin + rowOffset-cellSize;

                double colOffset = (colNumber) * cellSize;
                double x = xmin + colOffset;
                double val = Double.parseDouble(s);
                if (val != nullValue) {
                    this.action.handleNonNullData(y, x,  date, val);
//                    log.log(Level.INFO,"{0}" + " " + "y: " + "{1} x: {2}", new Object[]{s, y, x});
                    log.log(Level.FINE, "row: {0} col: {1}  x: {2}  y:{3}", new Object[]{lineNumber - STANDARD_HEADER_SIZE, colNumber, x, y});
//                    TreeMap<String, Object> extraData = new TreeMap<String, Object>();
//                    extraData.put("data", val);
//                    extraData.put("date", date);
//                    extraData.put("point_id", getOrAddPoint(x, y));
//
//
//                    GeoDao.storeEntityData(getCon(), "cru_pr", extraData);

                }
                colNumber++;
            }

        }

    }
}
