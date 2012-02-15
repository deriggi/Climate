/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.derivedmapdata;

import dao.GeoDao;
import database.DBUtils;
import domain.DerivativeStats;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author wb385924
 */
public class DerivedMapDataDao {

    private static DerivedMapDataDao dao = null;

    public static DerivedMapDataDao get() {
        if (dao == null) {
            dao = new DerivedMapDataDao();
        }
        return dao;
    }
    private static String SAVE_SPATIAL_DATA = "insert into derived_map_stat (derived_map_stat_value, derived_map_stat_area_id, derived_map_stat_time_period_id, derived_map_stat_statistic_type_id, derived_map_stat_climate_statistic_type_id, derived_map_stat_temporal_aggregation_id, derived_map_stat_scenario_id, derived_map_stat_run, derived_map_stat_month, derived_map_stat_year, derived_map_stat_gcm_id, derived_map_stat_cell_id) values(?,?,?,?,?,?,?,?,?,?,?,?);";
    private static String GET_SPATIAL_DATA =
            "select * from derived_map_stat where  derived_map_stat_area_id = ? AND derived_map_stat_time_period_id = ? AND "
            + "derived_map_stat_statistic_type_id = ? AND derived_map_stat_climate_statistic_type_id = ? AND "
            + "derived_map_stat_temporal_aggregation_id = ? AND derived_map_stat_scenario_id = ? AND "
            + "derived_map_stat_run = ?  AND derived_map_stat_month = ? AND derived_map_stat_year = ? AND "
            + "derived_map_stat_gcm_id = ? AND derived_map_stat_cell_id = ?";


   


    private final static Logger log = Logger.getLogger(DerivedMapDataDao.class.getName());

    private String getPointString(double latitude, double longitude) {
        StringBuilder sb = new StringBuilder();
        sb.append("POINT(");
        sb.append(longitude);
        sb.append(" ");
        sb.append(latitude);
        sb.append(")");

        return sb.toString();
    }

    private String constructGridSquareFromLLPoint(double latitude, double longitude) {
        StringBuilder sb = new StringBuilder();
        String space = " ";
        String comma = ",";

        sb.append("POLYGON((");
        sb.append(longitude);
        sb.append(space);
        sb.append(latitude);
        sb.append(comma);

        sb.append(longitude);
        sb.append(space);
        sb.append(latitude + 0.5);
        sb.append(comma);

        sb.append(longitude + 0.5);
        sb.append(space);
        sb.append(latitude + 0.5);
        sb.append(comma);

        sb.append(longitude + 0.5);
        sb.append(space);
        sb.append(latitude);
        sb.append(comma);

        sb.append(longitude);
        sb.append(space);
        sb.append(latitude);


        sb.append("))");

        return sb.toString();
    }

    public int savePoint(double latitude, double longitude) {
        int id = getPointIdIfExists(latitude, longitude);
        if (id == -1) {

            Connection c = DBUtils.getConnection();
            String pointString = constructGridSquareFromLLPoint(latitude, longitude);
            log.log(Level.INFO, "saving point ", pointString);

            id = GeoDao.storeGeometry(c, "derived_data_cell", "geom", pointString);
            DBUtils.close(c);
        }
        return id;

    }

    private int getPointIdIfExists(double latitude, double longitude) {
        Connection c = DBUtils.getConnection();
        String pointString = constructGridSquareFromLLPoint(latitude, longitude);
        int id = GeoDao.getGeometryBoundsId(c, pointString, "derived_data_cell", "geom");
        DBUtils.close(c);

        return id;
    }

    public void saveSpatialData(double value, int areaId,
            DerivativeStats.time_period timePeriod,
            DerivativeStats.stat_type statType,
            DerivativeStats.climatestat climateStat,
            DerivativeStats.temporal_aggregation temporalAggregation,
            DerivativeStats.scenario scenario,
            DerivativeStats.gcm gcm,
            int run,
            int month,
            int year,
            int cellId) {

        if(doesSpatialDataExist( areaId, timePeriod, statType, climateStat, temporalAggregation, scenario, gcm, run, month, year, cellId)){
            log.log(Level.FINE, "skipping duplicate for area id {0}", areaId);
            return;
        }
        
        Connection c = null;
        PreparedStatement ps = null;
        try {

            c = DBUtils.getConnection();
            ps = c.prepareCall(SAVE_SPATIAL_DATA);
//           derived_map_stat_climate_statistic_type_id, derived_map_stat_temporal_aggregation_id, derived_map_stat_scenario_id, derived_map_stat_run, derived_map_stat_month, derived_map_stat_year, derived_map_stat_gcm_id, derived_map_stat_point_id) values(?,?,?,?,?,?,?,?,?,?,?,?);";


            ps.setDouble(1, value);
            ps.setInt(2, areaId);
            ps.setInt(3, timePeriod.getId());
            ps.setInt(4, statType.getId());
            ps.setInt(5, climateStat.getId());
            ps.setInt(6, temporalAggregation.getId());
            ps.setInt(7, scenario.getId());
            ps.setInt(8, run);
            ps.setInt(9, month);
            ps.setInt(10, year);
            ps.setInt(11, gcm.getGcmId());
            ps.setInt(12, cellId);

            ps.executeUpdate();

            //        DerivativeStats
        } catch (SQLException ex) {
            if (ex.getLocalizedMessage().indexOf("duplicate key value violates unique constraint \"derived_map_stat_index\"") != -1) {
                log.warning(ex.getLocalizedMessage());
            } else {
                log.info(ex.getLocalizedMessage());
            }
        } finally {
            DBUtils.close(c, ps, null);
        }
    }

    private boolean doesSpatialDataExist( int areaId,
            DerivativeStats.time_period timePeriod,
            DerivativeStats.stat_type statType,
            DerivativeStats.climatestat climateStat,
            DerivativeStats.temporal_aggregation temporalAggregation,
            DerivativeStats.scenario scenario,
            DerivativeStats.gcm gcm,
            int run,
            int month,
            int year,
            int cellId) {

        Connection c = null;
        PreparedStatement ps = null;
        try {

            c = DBUtils.getConnection();
            ps = c.prepareCall(GET_SPATIAL_DATA);
//           derived_map_stat_climate_statistic_type_id, derived_map_stat_temporal_aggregation_id, derived_map_stat_scenario_id, derived_map_stat_run, derived_map_stat_month, derived_map_stat_year, derived_map_stat_gcm_id, derived_map_stat_point_id) values(?,?,?,?,?,?,?,?,?,?,?,?);";

            ps.setInt(1, areaId);
            ps.setInt(2, timePeriod.getId());
            ps.setInt(3, statType.getId());
            ps.setInt(4, climateStat.getId());
            ps.setInt(5, temporalAggregation.getId());
            ps.setInt(6, scenario.getId());
            ps.setInt(7, run);
            ps.setInt(8, month);
            ps.setInt(9, year);
            ps.setInt(10, gcm.getGcmId());
            ps.setInt(11, cellId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }

            //        DerivativeStats
        } catch (SQLException ex) {

            ex.printStackTrace();
            

        } finally {
            DBUtils.close(c, ps, null);
        }
        return false;
    }




     private static String GET_MAX_MIN_AVG_WITHIN_GCM =
            " select max(derived_map_stat_value), min(derived_map_stat_value), avg(derived_map_stat_value) from derived_map_stat inner join derived_data_cell on "
            + "derived_map_stat_cell_id = derived_data_cell_id  and derived_map_stat_gcm_id = ? "
            + " and derived_map_stat_scenario_id = ? and derived_map_stat_run = ? and derived_map_stat_climate_statistic_type_id = ? "
            + " and derived_map_stat_statistic_type_id = ? and derived_map_stat_time_period_id= ? and derived_map_stat_Area_id = ?  "
            + " and derived_map_stat_temporal_aggregation_id = ? ";


     /*
      * Returns max, min, avg in that order
      *
      */
    public ArrayList<Double> getMaxMinAvgWithinGCM(
            int areaId,
            DerivativeStats.time_period timePeriod,
            DerivativeStats.stat_type statType,
            DerivativeStats.climatestat climateStat,
            DerivativeStats.temporal_aggregation temporalAggregation,
            DerivativeStats.scenario scenario,
            DerivativeStats.gcm gcm,
            int run) {

        Connection c = null;
        PreparedStatement ps = null;
        ArrayList<Double> returnList = new ArrayList<Double>();

        try {

            c = DBUtils.getConnection();
            ps = c.prepareCall(GET_MAX_MIN_AVG_WITHIN_GCM);
//           derived_map_stat_climate_statistic_type_id, derived_map_stat_temporal_aggregation_id, derived_map_stat_scenario_id, derived_map_stat_run, derived_map_stat_month, derived_map_stat_year, derived_map_stat_gcm_id, derived_map_stat_point_id) values(?,?,?,?,?,?,?,?,?,?,?,?);";

            ps.setInt(1, gcm.getGcmId());
            ps.setInt(2, scenario.getId());
            ps.setInt(3, run);
            ps.setInt(4, climateStat.getId());
            ps.setInt(5, statType.getId());
            ps.setInt(6, timePeriod.getId());
            ps.setInt(7, areaId);
            ps.setInt(8, temporalAggregation.getId());

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                returnList.add(rs.getDouble("max"));
                returnList.add(rs.getDouble("min"));
                returnList.add(rs.getDouble("avg"));
            }

            //        DerivativeStats
        } catch (SQLException ex) {

            ex.printStackTrace();


        } finally {
            DBUtils.close(c, ps, null);
        }
        return returnList;
    }

     private static String GET_MAX_MIN_AVG_ALL_GCMS =
            " select max(derived_map_stat_value), min(derived_map_stat_value), avg(derived_map_stat_value) from derived_map_stat inner join derived_data_cell on "
            + "derived_map_stat_cell_id = derived_data_cell_id   "
            + " and derived_map_stat_scenario_id = ? and derived_map_stat_run = ? and derived_map_stat_climate_statistic_type_id = ? "
            + " and derived_map_stat_statistic_type_id = ? and derived_map_stat_time_period_id= ? and derived_map_stat_Area_id = ?  "
            + " and derived_map_stat_temporal_aggregation_id = ? ";


     /*
      * Returns max, min, avg in that order
      *
      */
    public ArrayList<Double> getMaxMinAvgAllGCMs(
            int areaId,
            DerivativeStats.time_period timePeriod,
            DerivativeStats.stat_type statType,
            DerivativeStats.climatestat climateStat,
            DerivativeStats.temporal_aggregation temporalAggregation,
            DerivativeStats.scenario scenario,
            DerivativeStats.gcm gcm,
            int run) {

        Connection c = null;
        PreparedStatement ps = null;
        ArrayList<Double> returnList = new ArrayList<Double>();

        try {

            c = DBUtils.getConnection();
            ps = c.prepareCall(GET_MAX_MIN_AVG_ALL_GCMS);
//           derived_map_stat_climate_statistic_type_id, derived_map_stat_temporal_aggregation_id, derived_map_stat_scenario_id, derived_map_stat_run, derived_map_stat_month, derived_map_stat_year, derived_map_stat_gcm_id, derived_map_stat_point_id) values(?,?,?,?,?,?,?,?,?,?,?,?);";

            
            ps.setInt(1, scenario.getId());
            ps.setInt(2, run);
            ps.setInt(3, climateStat.getId());
            ps.setInt(4, statType.getId());
            ps.setInt(5, timePeriod.getId());
            ps.setInt(6, areaId);
            ps.setInt(7, temporalAggregation.getId());

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                returnList.add(rs.getDouble("max"));
                returnList.add(rs.getDouble("min"));
                returnList.add(rs.getDouble("avg"));
            }

            //        DerivativeStats
        } catch (SQLException ex) {

            ex.printStackTrace();


        } finally {
            DBUtils.close(c, ps, null);
        }
        return returnList;
    }

    public String getMonthSVg(
            int areaId,
            double max,
            double min,
            int month,
            DerivativeStats.time_period timePeriod,
            DerivativeStats.stat_type statType,
            DerivativeStats.climatestat climateStat,
            DerivativeStats.temporal_aggregation temporalAggregation,
            DerivativeStats.scenario scenario,
            DerivativeStats.gcm gcm,
            int run) {

        Connection c = null;
        PreparedStatement ps = null;
        String svg = null;

        try {

            c = DBUtils.getConnection();
            ps = c.prepareCall(GET_MONTH_SVG);
//           derived_map_stat_climate_statistic_type_id, derived_map_stat_temporal_aggregation_id, derived_map_stat_scenario_id, derived_map_stat_run, derived_map_stat_month, derived_map_stat_year, derived_map_stat_gcm_id, derived_map_stat_point_id) values(?,?,?,?,?,?,?,?,?,?,?,?);";

            ps.setInt(1, gcm.getGcmId());
            ps.setInt(2, scenario.getId());
            ps.setInt(3, run);
            ps.setInt(4, climateStat.getId());
            ps.setInt(5, statType.getId());
            ps.setInt(6, timePeriod.getId());
            ps.setInt(7, areaId);
            ps.setInt(8, temporalAggregation.getId());
            ps.setInt(9, month);
            ps.setDouble(10, min);
            ps.setDouble(11, max);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                svg = (rs.getString(1));
            }

            //        DerivativeStats
        } catch (SQLException ex) {

            ex.printStackTrace();


        } finally {
            DBUtils.close(c, ps, null);
        }
        return svg;
    }



     private static String GET_MONTH_SVG =
            " select st_assvg(st_union(st_scale(derived_data_cell_geom,5,5))) from derived_map_stat inner join derived_data_cell on "
            + "derived_map_stat_cell_id = derived_data_cell_id and derived_map_stat_gcm_id = ? "
            + "and derived_map_stat_scenario_id = ? and derived_map_stat_run = ? and derived_map_stat_climate_statistic_type_id = ? "
            + "and derived_map_stat_statistic_type_id = ? and derived_map_stat_time_period_id= ? and derived_map_stat_Area_id = ?  "
            + "and derived_map_stat_temporal_aggregation_id = ? and derived_map_stat_month = ? and derived_map_stat_value >= ? and derived_map_stat_value < ?";
}
