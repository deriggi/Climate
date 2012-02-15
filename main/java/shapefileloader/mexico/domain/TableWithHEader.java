/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader.mexico.domain;

import java.util.HashMap;
import sdnis.wb.util.BasicAverager;
import shapefileloader.gcm.P_Config;

/**
 *
 * @author wb385924
 */
public class TableWithHEader {

    private HashMap<String, BasicAverager> table;

    public TableWithHEader(HashMap<String, BasicAverager> table, P_Config config) {
        this.table = table;
        this.config = config;
    }
    private P_Config config;

    public TableWithHEader() {
    }

    public P_Config getConfig() {
        return config;
    }

    public void setConfig(P_Config config) {
        this.config = config;
    }

    public HashMap<String, BasicAverager> getTable() {
        return table;
    }

    public void setTable(HashMap<String, BasicAverager> table) {
        this.table = table;
    }

    
}
