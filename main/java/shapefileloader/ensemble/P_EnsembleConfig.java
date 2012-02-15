/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.ensemble;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import shapefileloader.gcm.P_Config;
import shapefileloader.gcm.P_ConfigAreaDao;
import shapefileloader.gcm.P_ConfigDao;
import shapefileloader.gcm.P_GcmStatsProperties;
import shapefileloader.gcm.P_GcmStatsProperties.climatestat;
import shapefileloader.gcm.P_GcmStatsProperties.gcm;
import shapefileloader.gcm.P_GcmStatsProperties.scenario;
import shapefileloader.gcm.P_GcmStatsProperties.stat_type;

/**
 *
 * @author wb385924
 */
public class P_EnsembleConfig implements P_Config {

    private final String fwrdSlash = "/";

    public String toUrlPart() {
        StringBuilder sb = new StringBuilder();

        sb.append(getScenario().toString());
        sb.append(fwrdSlash);

        sb.append(getStat().toString());
        sb.append(fwrdSlash);

        sb.append(getfYear());
        sb.append(fwrdSlash);

        sb.append(gettYear());
        sb.append(fwrdSlash);

        sb.append(getPercentile());
        sb.append(fwrdSlash);

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final P_EnsembleConfig other = (P_EnsembleConfig) obj;
        if (this.isAnnual != other.isAnnual) {
            return false;
        }
        if (this.month != other.month) {
            return false;
        }
        if (this.scenario != other.scenario) {
            return false;
        }
        if (this.stat != other.stat && (this.stat == null || !this.stat.equals(other.stat))) {
            return false;
        }
        if (this.statType != other.statType) {
            return false;
        }
        if (this.areaId != other.areaId) {
            return false;
        }
        if ((this.iso3 == null) ? (other.iso3 != null) : !this.iso3.equals(other.iso3)) {
            return false;
        }
        if (this.percentile != other.percentile) {
            return false;
        }
        if (this.fYear != other.fYear) {
            return false;
        }
        if (this.tYear != other.tYear) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.isAnnual ? 1 : 0);
        hash = 41 * hash + this.month;
        hash = 41 * hash + (this.scenario != null ? this.scenario.hashCode() : 0);
        hash = 41 * hash + (this.stat != null ? this.stat.hashCode() : 0);
        hash = 41 * hash + (this.statType != null ? this.statType.hashCode() : 0);
        hash = 41 * hash + this.areaId;
        hash = 41 * hash + (this.iso3 != null ? this.iso3.hashCode() : 0);
        hash = 41 * hash + this.percentile;
        hash = 41 * hash + this.fYear;
        hash = 41 * hash + this.tYear;
        return hash;
    }
    private static final Logger log = Logger.getLogger(P_EnsembleConfig.class.getName());
    private static final HashMap<String, Integer> monthMap = new HashMap<String, Integer>();

    static {
        monthMap.put("Jan", 1);
        monthMap.put("Feb", 2);
        monthMap.put("Mar", 3);
        monthMap.put("Apr", 4);
        monthMap.put("May", 5);
        monthMap.put("Jun", 6);
        monthMap.put("July", 7);
        monthMap.put("Aug", 8);
        monthMap.put("Sep", 9);
        monthMap.put("Oct", 10);
        monthMap.put("Nov", 11);
        monthMap.put("Dec", 12);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String comma = ",";

        sb.append(getStatType());
        sb.append(comma);



        sb.append(getScenario());
        sb.append(comma);

        sb.append(getValue());
        sb.append(comma);

        sb.append(getStat());
        sb.append(comma);

        sb.append(getfYear());
        sb.append(comma);

        sb.append(gettYear());
        sb.append(comma);


        sb.append(getMonth());
        sb.append(comma);

        return sb.toString();
    }

    public String toCompactString() {
        StringBuilder sb = new StringBuilder();
        String comma = "_";

        sb.append(getStat());
        sb.append(comma);

        sb.append(getStatType());
        sb.append(comma);



        sb.append(getScenario());
        sb.append(comma);


        sb.append(getfYear());
        sb.append(comma);

        sb.append(gettYear());
        sb.append(comma);

        sb.append(getPercentile());

        return sb.toString();
    }
    private boolean isAnnual = false;

    public boolean isIsAnnual() {
        return isAnnual;
    }

    private void setIsAnnual(boolean isAnnual) {
        this.isAnnual = isAnnual;

    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
        if (month == -1) {
            setIsAnnual(true);
        }
    }
    private int month = -1;

    public scenario getScenario() {
        return scenario;
    }

    public void setScenario(scenario scenario) {
        this.scenario = scenario;
    }
    private P_GcmStatsProperties.scenario scenario = null;
    private P_GcmStatsProperties.climatestat stat = null;
    private P_GcmStatsProperties.stat_type statType = null;
    private int areaId = -1;
    private String iso3 = null;
    private int percentile = -1;

    public int getPercentile() {
        return percentile;
    }

    public void setPercentile(int percentile) {
        this.percentile = percentile;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public stat_type getStatType() {
        return statType;
    }

    public void setStatType(stat_type statType) {
        this.statType = statType;
    }

    public climatestat getStat() {
        return stat;
    }

    public void setStat(climatestat stat) {
        this.stat = stat;
    }
    private int fYear, tYear;
    private Double value = null;

    public P_EnsembleConfig() {
    }

    public P_EnsembleConfig(stat_type statType, scenario scenario, climatestat stat, int fYear, int tYear, int percentile) {
        this.percentile = percentile;
        this.scenario = scenario;
        this.stat = stat;
        this.fYear = fYear;
        this.tYear = tYear;
        this.statType = statType;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public int getfYear() {
        return fYear;
    }

    public void setfYear(int fYear) {
        this.fYear = fYear;
    }

    public int gettYear() {
        return tYear;
    }

    public void settYear(int tYear) {
        this.tYear = tYear;
    }

    public boolean isComplete() {
        if (this.percentile == -1) {
            return false;
        }
        if (this.value == null) {
            return false;
        }
        if (this.fYear >= tYear) {
            return false;
        }

        if (this.iso3 == null) {
            return false;
        }

        if (this.areaId == -1) {
            return false;
        }

        if (this.month == -1 && this.isAnnual == false) {
            return false;
        }

        if (this.month != -1 && this.isAnnual == true) {
            log.log(Level.SEVERE, "impossible configuration of month = {0} and annual  = {1}", new Object[]{month, this.isAnnual});
        }

        if (this.scenario == null) {
            return false;
        }

        if (this.stat == null) {
            return false;
        }

        if (this.statType == null) {
            return false;
        }

        if (this.value == null) {
            return false;
        }
        return true;


    }

    public boolean isCompleteIgnoringAreaValueMonth() {
        if (this.percentile == -1) {
            return false;
        }
        if (this.fYear >= tYear) {
            return false;
        }


        if (this.month != -1 && this.isAnnual == true) {
            log.log(Level.SEVERE, "impossible configuration of month = {0} and annual  = {1}", new Object[]{month, this.isAnnual});
        }

        if (this.scenario == null) {
            return false;
        }

        if (this.stat == null) {
            return false;
        }

        if (this.statType == null) {
            return false;
        }


        return true;


    }

    public gcm getGcm() {
        log.warning("should not be asking ensemble for gcm");
        return null;
    }

    public void setGcm(gcm gcm) {

        log.warning("should not setting gcm for ensemble ");
    }

    public P_ConfigDao getConfigDao() {
        P_EnsembleConfigDao dao = P_EnsembleConfigDao.get();
        return dao;
    }

    public P_ConfigAreaDao getConfigAreaDao() {
        P_EnsembleConfigAreaDao areaDao = P_EnsembleConfigAreaDao.get();
        return areaDao;
    }
}
