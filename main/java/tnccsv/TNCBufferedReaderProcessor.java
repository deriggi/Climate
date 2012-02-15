/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tnccsv;

import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author wb385924
 */
public interface TNCBufferedReaderProcessor {

    
    public void readFromBuffer(String parentURL, BufferedReader br) throws IOException;
}
