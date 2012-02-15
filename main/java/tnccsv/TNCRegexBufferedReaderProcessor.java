/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tnccsv;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author wb385924
 */
public class TNCRegexBufferedReaderProcessor implements TNCBufferedReaderProcessor {

    private static final Logger log = Logger.getLogger(TNCRegexBufferedReaderProcessor.class.getName());
//    final String linkPattern = "(?i)href\\s*=\\s*\"[\\w\\/]+\">";
    final String linkPattern = "(?i)href\\s*=\\s*\"[\\w\\/\\.]+\">";
    final String pathPattern = "\"[\\w\\/\\.]+\"";
    private DataFileHandler dfh = new DataFileHandler();

    public DataFileHandler getDfh() {
        return dfh;
    }

    public void readFromBuffer(String parentUrl, BufferedReader br) throws IOException {
        dfh.clear();

        parentUrl = parentUrl.substring(0, parentUrl.indexOf("/",8));
        log.fine("parent url should be " + parentUrl);
      
        String line = null;
        
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);

        }
        Pattern p = Pattern.compile(linkPattern);
        Pattern path = Pattern.compile(pathPattern);
        String pageText = sb.toString();
        Matcher matcher = p.matcher(pageText);
        log.fine(pageText);
        
        while (matcher.find()) {
            String link = matcher.group();
            log.log(Level.FINE, "found link {0}", link);
            Matcher pathMatcher = path.matcher(link);
            String pathText = null;
            StringBuilder stringBuilder = new StringBuilder();
            if (pathMatcher.find()) {

                pathText = pathMatcher.group();
                pathText = pathText.substring(1, pathText.length() - 1);
                stringBuilder.append(parentUrl);
                stringBuilder.append(pathText);
                dfh.logLine(stringBuilder.toString());
                
                stringBuilder.delete(0, stringBuilder.length() - 1);
                
                log.log(Level.FINE, "found path {0}", parentUrl + pathText);
            } else {
                log.log(Level.WARNING, "found link but not internal path {0}", link);
            }

        }

    }
}
