/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader.gcm;

import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author wb385924
 */
public interface P_ConfigAreaDao {

    public void insertAreaValue(int configId, int areaId, double value);

    public HashMap<Integer, Double> getAreaDataForTime(P_Config config, boolean isAnnual);

    public TreeMap<Integer,HashMap<Integer, Double>> getAreaDataForStartYearRange(P_Config config, int fromStartYear, int toStartYear, boolean isAnnual);

}
