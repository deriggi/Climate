/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.deriveddata;

import dao.country.CountryDao;
import database.DBUtils;

import domain.Country;
import domain.DerivativeStats;
import domain.web.MinimizedData;
import export.util.FileExportHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author wb385924
 */
public class DerivedDataDao {

    private static final Logger log = Logger.getLogger(DerivedDataDao.class.getName());
    private static DerivedDataDao dao = null;

    private DerivedDataDao() {
    }

    

    public static DerivedDataDao get() {
        if (dao == null) {
            dao = new DerivedDataDao();
        }
        return dao;
    }

    /**
     * @TODO - derive the time period based on the data date.  get timestamp for server
     *
     *
     * @param gcm
     * @param climateStat
     * @param scenario
     * @param type
     * @param value
     * @param run
     * @param areaId
     * @param dataDate
     */
    public void saveDerivedStat(
            DerivativeStats.gcm gcm,
            DerivativeStats.climatestat climateStat,
            DerivativeStats.scenario scenario,
            DerivativeStats.stat_type type,
            DerivativeStats.temporal_aggregation tempAgg,
            double value,
            int run,
            int areaId,
            Date dataDate) {

        Connection c = DBUtils.getConnection();
        PreparedStatement ps = null;
        try {
            ps = c.prepareStatement("insert into derived_data (derived_data_scenario_id, derived_data_statistic_type_id, derived_data_climate_Statistic_type_id, derived_data_area_id, derived_data_temporal_aggregation_id, derived_data_gcm_id, derived_data_value, derived_data_run,   derived_data_date, derived_data_time_inserted) values(?,?,?,?,?,?,?,?,?,(select current_timestamp))");
            ps.setInt(1, scenario.getId());
            ps.setInt(2, type.getId());
            ps.setInt(3, climateStat.getId());
            ps.setInt(4, areaId);
            ps.setInt(5, tempAgg.getId());
            ps.setInt(6, gcm.getGcmId());
            ps.setDouble(7, value);
            ps.setInt(8, run);
            ps.setDate(9, new java.sql.Date(dataDate.getTime()));
            ps.executeUpdate();


        } catch (SQLException ex) {
//            Logger.getLogger(DerivedDataDao.class.getName()).log(Level.SEVERE, null, ex);
            log.log(Level.FINE, "failed on unique constraint{0} {1} {2} {3} {4} {5} {6} {7}", new Object[]{type, climateStat, areaId, tempAgg, gcm, value, run, new java.sql.Date(dataDate.getTime())});
        } finally {
            DBUtils.close(c);
        }
    }

    public List<MinimizedData> getDerivedStatByCountryTimePeriod(
            DerivativeStats.gcm gcm,
            DerivativeStats.climatestat climateStat,
            DerivativeStats.scenario scenario,
            DerivativeStats.stat_type type,
            DerivativeStats.temporal_aggregation tempAgg,
            Date fromDate,
            Date toDate,
            int run,
            int areaId) {

        Connection c = DBUtils.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String derived_data_value = "derived_data_value";
        String derived_data_date = "derived_data_date";
        List<MinimizedData> minData = new ArrayList<MinimizedData>();

        try {

            ps = c.prepareStatement("select * from derived_data where derived_data_statistic_type_id = ? AND derived_data_climate_statistic_type_id = ? and derived_data_area_id = ? and derived_data_temporal_aggregation_id = ? and derived_data_run = ?  and derived_data_scenario_id = ? and derived_data_gcm_id = ? and derived_data_date >= ? and derived_data_date <= ?");
            ps.setInt(1, type.getId());
            ps.setInt(2, climateStat.getId());
            ps.setInt(3, areaId);
            ps.setInt(4, tempAgg.getId());
            ps.setInt(5, run);
            ps.setInt(6, scenario.getId());
            ps.setInt(7, gcm.getGcmId());
            ps.setDate(8, new java.sql.Date(fromDate.getTime()));
            ps.setDate(9, new java.sql.Date(toDate.getTime()));
            rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                minData.add(new MinimizedData(rs.getDouble(derived_data_value), rs.getDate(derived_data_date)));


//                log.log(Level.INFO, "derived data value{0}",  + " " + );
                count++;

            }

            log.log(Level.INFO, "number of records is : {0} for {1}", new Object[]{count, climateStat.toString()});


        } catch (SQLException ex) {
//            Logger.getLogger(DerivedDataDao.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } finally {
            DBUtils.close(c);
        }
        return minData;
    }

    public List<MinimizedData> getDerivedStatByCountry(
            DerivativeStats.gcm gcm,
            DerivativeStats.climatestat climateStat,
            DerivativeStats.scenario scenario,
            DerivativeStats.stat_type type,
            DerivativeStats.temporal_aggregation tempAgg,
            int run,
            int areaId) {

        Connection c = DBUtils.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String derived_data_value = "derived_data_value";
        String derived_data_date = "derived_data_date";
        List<MinimizedData> minData = new ArrayList<MinimizedData>();

        try {

            ps = c.prepareStatement("select * from derived_data where derived_data_statistic_type_id = ? AND derived_data_climate_statistic_type_id = ? and derived_data_area_id = ? and derived_data_temporal_aggregation_id = ? and derived_data_run = ?  and derived_data_scenario_id = ? and derived_data_gcm_id = ?");
            ps.setInt(1, type.getId());
            ps.setInt(2, climateStat.getId());
            ps.setInt(3, areaId);
            ps.setInt(4, tempAgg.getId());
            ps.setInt(5, run);
            ps.setInt(6, scenario.getId());
            ps.setInt(7, gcm.getGcmId());
            rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                minData.add(new MinimizedData(rs.getDouble(derived_data_value), rs.getDate(derived_data_date)));


//                log.log(Level.INFO, "derived data value{0}",  + " " + );
                count++;

            }
            if (count == 0) {
                log.log(Level.INFO, "number of records is : {0} for {1}", new Object[]{count, climateStat.toString()});
            }

        } catch (SQLException ex) {
//            Logger.getLogger(DerivedDataDao.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } finally {
            DBUtils.close(c, ps, null);
        }
        return minData;
    }

    public static void main(String[] args) {
        DerivedDataDao dao = DerivedDataDao.get();
        Calendar fromDate = Calendar.getInstance();
        fromDate.set(Calendar.YEAR, 2046);
        fromDate.set(Calendar.MONTH, 0);
        fromDate.set(Calendar.DATE, 1);

        Calendar toDate = Calendar.getInstance();
        toDate.set(Calendar.YEAR, 2081);
        toDate.set(Calendar.MONTH, 0);
        toDate.set(Calendar.DATE, 1);

        DerivativeStats.getInstance();
        long t0 = new Date().getTime();
        List<MinimizedData> data = dao.getDerivedStatByCountryTimePeriod(DerivativeStats.gcm.cnrm_cm3, DerivativeStats.precipstat.pr, DerivativeStats.scenario.a1b, DerivativeStats.stat_type.mean, DerivativeStats.temporal_aggregation.monthly, fromDate.getTime(), toDate.getTime(), 1, 712);
        long t1 = new Date().getTime();
        log.log(Level.INFO, "query took {0} seconds ", (t1 - t0) / 1000.0);
        for(MinimizedData mdd : data){
            log.info(mdd.toString());
        }
    }

    public static void exportCountryMonthlyStats(String iso) {
        DerivedDataDao dao = DerivedDataDao.get();
        DerivativeStats ds = DerivativeStats.getInstance();
        int count = 0;
        Calendar c = Calendar.getInstance();
//        c.roll(Calendar.DATE, -30000);
        double sum = 0;
        int recordsCount = 0;

        CountryDao countryDao = CountryDao.get();
        HashMap<String, Country> countryMap = countryDao.getCountriesAsMap(countryDao.getCountriesFromRegionName("Africa_MENA"));
        
        int countryId = countryMap.get(iso).getId();

        // generate header
        String comma = ",";
        DerivativeStats.climatestat[] tempstats = DerivativeStats.tempstat.values();
        DerivativeStats.climatestat[] prstats = DerivativeStats.precipstat.values();
        ArrayList<DerivativeStats.climatestat> stats = new ArrayList<DerivativeStats.climatestat>();

        // remove monthly stats
        stats.addAll(Arrays.asList(tempstats));
        stats.addAll(Arrays.asList(prstats));
        Iterator<DerivativeStats.climatestat> statsIterator = stats.iterator();
        while (statsIterator.hasNext()) {
            DerivativeStats.climatestat cstat = statsIterator.next();
            if (cstat.getId() == DerivativeStats.precipstat.cdd.getId()
                    || cstat.getId() == DerivativeStats.precipstat.cdd5.getId()
                    || cstat.getId() == DerivativeStats.precipstat.r5d.getId()
                    || cstat.getId() == DerivativeStats.tempstat.tx90.getId()
                    || cstat.getId() == DerivativeStats.tempstat.hwdi.getId()) {

                statsIterator.remove();
            }
        }

        StringBuilder header = new StringBuilder();

        header.append("gcm");
        header.append(comma);

        header.append("scenario");
        header.append(comma);

        header.append("date");
        header.append(comma);

        for (DerivativeStats.climatestat climatestat : stats) {
            header.append(climatestat);
            header.append(comma);
        }
        FileExportHelper.appendToFile(iso + ".csv", header.toString());



        List<DerivativeStats.gcm> gcms = Arrays.asList(DerivativeStats.gcm.values());
        log.log(Level.INFO, "looking at {0} gcms", gcms.size());

        HashMap<Date, ArrayList<Double>> datedValues = null;
        for (DerivativeStats.gcm gcm : gcms) {

            for (DerivativeStats.scenario scenario : DerivativeStats.scenario.values()) {
                datedValues = new HashMap<Date, ArrayList<Double>>();
//                List<DerivativeStats.climatestat> statsheader = new ArrayList<DerivativeStats.climatestat>();
                for (DerivativeStats.climatestat climatestat : stats) {

                    List<MinimizedData> data = dao.getDerivedStatByCountry(gcm, climatestat, scenario, DerivativeStats.stat_type.mean, DerivativeStats.temporal_aggregation.monthly, 1, countryId);
                    if (data.isEmpty()) {
                        log.log(Level.INFO, "found  {0} stats for {1} {2}", new Object[]{data.size(), gcm, climatestat.toString()});
                    }
                    for (MinimizedData minData : data) {

                        if (!datedValues.containsKey(minData.getDate())) {
                            datedValues.put(minData.getDate(), new ArrayList<Double>());
                        }
                        datedValues.get(minData.getDate()).add(minData.getValue());

                    }


                }
                // write everything out


                Set<Date> dateSet = datedValues.keySet();

                for (Date keyDate : dateSet) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(gcm.name());
                    sb.append(comma);

                    sb.append(scenario.name());
                    sb.append(comma);

                    sb.append(keyDate.toString());
                    sb.append(comma);

                    for (Double d : datedValues.get(keyDate)) {
//                        log.info(datedValues.get(keyDate).size() + " is the size of the list at " + keyDate);
                        sb.append(d);
                        sb.append(comma);
                    }
                    String line = sb.toString();
                    FileExportHelper.appendToFile(iso + ".csv", line);
                    log.fine(line);
                }


            }
        }

        StringBuilder sb1 = new StringBuilder();

        for (DerivativeStats.climatestat s : stats) {
            sb1.append(s);
            sb1.append(comma);
        }
        log.info(sb1.toString());




//        while (count++ < 1000000) {
//            long t0 = new Date().getTime();
//            Date d = c.getTime();
//
////            dao.saveDerivedStat(DerivativeStats.gcm.cnrm_cm3, DerivativeStats.tempstat.cd18, DerivativeStats.scenario.a1b, DerivativeStats.stat_type.mean, DerivativeStats.temporal_aggregation.yearly, Math.random(), 1, 537, d);
//            long t1 = new Date().getTime();
//
//            if (count % 100 == 0) {
//                sum += (t1 - t0);
//                log.log(Level.INFO, "average insert time at  {0} is {1}", new Object[]{recordsCount, sum / recordsCount});
//            }
//
//            c.add(Calendar.DATE, 1);
//            recordsCount++;
//
//        }

    }
}
