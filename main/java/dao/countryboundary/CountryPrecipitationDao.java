/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.countryboundary;

import dao.GeoDao;
import database.DBUtils;
import domain.Country;
import domain.web.ClimateDatum;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import domain.web.CountryRain;
import domain.web.PrecipitationData;
import java.util.Calendar;
import java.util.Collection;
import sdnis.wb.util.BasicAverager;

/**
 *
 * @author wb385924
 */
public class CountryPrecipitationDao {

    private static Logger log = Logger.getLogger(CountryPrecipitationDao.class.getName());
    private static CountryPrecipitationDao dao = null;
    private GeoDao geoDao = null;
    private final String COUNTRY_RAIN = "select precipitation_date,precipitation_area_id,precipitation_sum/precipitation_count "
            + " from precipitation where precipitation_area_id = ? "
            + "order by precipitation_date";
    private String COUNTRY_RAIN_ISO3 = "select scenario_code, @_date, @_area_id, @_sum/@_count "
            + " as avg from @ inner join country  on @_area_id = country_id inner join scenario on @_scenario_id = scenario_id and country_iso_3 = ? "
            + " order by @_date";
    private String COUNTRY_RAIN_ISO3_DATE_RANGE = "select scenario_code, @_date, @_area_id, @_sum/@_count "
            + " as avg from @ inner join country  on @_area_id = country_id inner join scenario on @_scenario_id = scenario_id and country_iso_3 = ? "
            + " where @_date >= ? and @_date <= ? order by @_date";
    private String COUNTRY_RAIN_COUNTRY_ID = "select scenario_code, @_date, @_area_id, @_sum/@_count "
            + " as avg from @ inner join country  on @_area_id = country_id inner join scenario on @_scenario_id = scenario_id and country_id = ? "
            + " order by @_date";
    private String COUNTRY_RAIN_COUNTRY_ID_DATE_RANGE = "select scenario_code, @_date, @_area_id, @_sum/@_count "
            + " as avg from @ inner join country  on @_area_id = country_id inner join scenario on @_scenario_id = scenario_id and country_id = ? "
            + " where @_date >= ? and @_date <= ? order by @_date";
//    private final String COUNTRY_RAIN_ISO3 = "select scenario_code,precipitation_date,precipitation_area_id,precipitation_sum/precipitation_count "
//            + " as avg from precipitation inner join country  on precipitation_area_id = country_id inner join scenario on precipitation_scenario_id = scenario_id and country_iso_3 = ? "
//            + " order by precipitation_date";
//    private final String regionsRain1 = "select country_name,st_astext(ST_Centroid(country_boundary_shape)) as centroid,a.precipitation_date,a.precipitation_area_id,a.sum from ( select precipitation_date,precipitation_area_id,sum(precipitation_sum/precipitation_count + 0) from precipitation p   group by precipitation_date, precipitation_area_id  ) a inner join country_boundary cb on a.precipitation_area_id = country_boundary_country_id and ST_Intersects( ST_GeomFromText('POLYGON((";
    private final String regionsRain1 = "select country_iso_3,country_name,st_astext(ST_Centroid(country_boundary_shape)) as centroid,a.precipitation_date,a.precipitation_area_id,a.sum from ( select precipitation_date,precipitation_area_id, precipitation_sum/precipitation_count as sum from precipitation p     ) a inner join country_boundary cb on a.precipitation_area_id = country_boundary_country_id and ST_Intersects( ST_GeomFromText('POLYGON((";
    private final String regionsRain2 = "))', 4326),cb.country_boundary_shape) inner join country on cb.country_boundary_country_id = country.country_id where precipitation_date >= ? and precipitation_date <= ? order by a.precipitation_area_id,precipitation_date";

    public static CountryPrecipitationDao get() {
        if (dao == null) {
            dao = new CountryPrecipitationDao();
            dao.geoDao = new GeoDao<Country>();
        }
        return dao;
    }

    public List<HashMap<String, String>> getRainDataAsMap(int countryId) {
        PreparedStatement ps = null;


        Connection c = null;
        ResultSet rs = null;
        List<HashMap<String, String>> countryRains = new ArrayList<HashMap<String, String>>();

        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement(COUNTRY_RAIN);
            ps.setInt(1, countryId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Date pdate = rs.getDate("precipitation_date");
                float avg = rs.getFloat("sum");
                HashMap<String, String> propertymap = new HashMap<String, String>(2);
                propertymap.put("sum", Float.toString(avg));
                propertymap.put("rainDate", pdate.toString());
                //System.out.println("have sum of  " + propertymap.get("sum"));

                countryRains.add(propertymap);
            }

        } catch (SQLException ex) {
            Logger.getLogger(CountryPrecipitationDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, null);
        }
        return countryRains;

    }

    public HashMap<String, BasicAverager> averageByMonth(List<HashMap<String, Object>> data, String dateProperty, String dataProperty) {
        HashMap<String, BasicAverager> averages = new HashMap<String, BasicAverager>();
        String prefix = "m_";
        for (HashMap<String, Object> map : data) {
            BasicAverager ba = null;
            Date d = (Date) map.get(dateProperty);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            int month = c.get(Calendar.MONTH);
            
            if (averages.containsKey(prefix+month)) {
                ba = averages.get(prefix+month);

            }
            else{
                ba = new BasicAverager();
                averages.put(prefix+month, ba);
            }
            ba.update(((Float)map.get(dataProperty)).doubleValue());
        }
        return averages;
    }

    public List<HashMap<String, Object>> getRainDataAsMap(String iso3, String statPrefix, Date fromDate, Date toDate) {
        PreparedStatement ps = null;


        Connection c = null;
        ResultSet rs = null;
        List<HashMap<String, Object>> countryRains = new ArrayList<HashMap<String, Object>>();

        try {

            c = DBUtils.getConnection();
            String query = COUNTRY_RAIN_ISO3_DATE_RANGE.replaceAll("\\@", statPrefix);
            System.out.println("query is " + query);
            ps = c.prepareStatement(query);
            ps.setString(1, iso3);
            ps.setDate(2, new java.sql.Date(fromDate.getTime()));
            ps.setDate(3, new java.sql.Date(toDate.getTime()));
            rs = ps.executeQuery();
            while (rs.next()) {
                Date pdate = rs.getDate(statPrefix + "_date");
                float avg = rs.getFloat("avg");
                String scenarioCode = rs.getString("scenario_code");

                HashMap<String, Object> propertymap = new HashMap<String, Object>(3);
                propertymap.put("sum", avg);
                propertymap.put("rainDate", pdate);
                propertymap.put("scenarioId", scenarioCode);


                //System.out.println("have sum of  " + propertymap.get("sum"));

                countryRains.add(propertymap);
            }
            System.out.println(countryRains.size() + " is the size");
        } catch (SQLException ex) {
            Logger.getLogger(CountryPrecipitationDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, null);
        }
        return countryRains;

    }

    public List<HashMap<String, String>> getRainDataAsMap(int countryId, String statPrefix, Date fromDate, Date toDate) {
        PreparedStatement ps = null;


        Connection c = null;
        ResultSet rs = null;
        List<HashMap<String, String>> countryRains = new ArrayList<HashMap<String, String>>();

        try {

            c = DBUtils.getConnection();
            String query = COUNTRY_RAIN_COUNTRY_ID_DATE_RANGE.replaceAll("\\@", statPrefix);
            System.out.println("query is " + query);
            ps = c.prepareStatement(query);
            ps.setInt(1, countryId);
            ps.setDate(2, new java.sql.Date(fromDate.getTime()));
            ps.setDate(3, new java.sql.Date(toDate.getTime()));
            rs = ps.executeQuery();
            while (rs.next()) {
                Date pdate = rs.getDate(statPrefix + "_date");
                float avg = rs.getFloat("avg");
                String scenarioCode = rs.getString("scenario_code");

                HashMap<String, String> propertymap = new HashMap<String, String>(3);
                propertymap.put("sum", Float.toString(avg));
                propertymap.put("rainDate", pdate.toString());
                propertymap.put("scenarioId", scenarioCode);


                //System.out.println("have sum of  " + propertymap.get("sum"));

                countryRains.add(propertymap);
            }
            System.out.println(countryRains.size() + " is the size");
        } catch (SQLException ex) {
            Logger.getLogger(CountryPrecipitationDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, null);
        }
        return countryRains;

    }

    public List<PrecipitationData> getPrecipitationData(String iso3) {
        PreparedStatement ps = null;


        Connection c = null;
        ResultSet rs = null;
        List<PrecipitationData> countryRains = new ArrayList<PrecipitationData>();

        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement(COUNTRY_RAIN_ISO3);
            ps.setString(1, iso3);
            rs = ps.executeQuery();
            while (rs.next()) {
                Date pdate = rs.getDate("precipitation_date");
                float avg = rs.getFloat("avg");
                String scenarioCode = rs.getString("scenario_code");




                PrecipitationData precipitationData = new PrecipitationData();
                precipitationData.setSum(avg);
                precipitationData.setIso3(iso3);

                precipitationData.setRainDate(pdate);
//                precipitationData.setScenarioId(scenarioCode);

                countryRains.add(precipitationData);
            }

        } catch (SQLException ex) {
            Logger.getLogger(CountryPrecipitationDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, null);
        }
        return countryRains;

    }

    public List<ClimateDatum> getRainDataWitinRegion(String boundingPolygon, Date fromDate, Date toDate) {
        System.out.println("polygon is " + boundingPolygon);
        PreparedStatement ps = null;
        Connection c = null;
        ResultSet rs = null;
        HashMap<Integer, ClimateDatum> climateDatumMap = new HashMap<Integer, ClimateDatum>();
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(regionsRain1);
            sb.append(boundingPolygon);
            sb.append(regionsRain2);

            c = DBUtils.getConnection();
            ps = c.prepareStatement(sb.toString());
            ps.setDate(1, new java.sql.Date(fromDate.getTime()));
            ps.setDate(2, new java.sql.Date(toDate.getTime()));
            rs = ps.executeQuery();
            ClimateDatum datum = null;
            while (rs.next()) {

                Date pdate = rs.getDate("precipitation_date");
                float avg = rs.getFloat("sum");
                int countryId = rs.getInt("precipitation_area_id");
                String name = rs.getString("country_name");
                String iso3 = rs.getString("country_iso_3");

                if (climateDatumMap.containsKey(countryId)) {
                    datum = climateDatumMap.get(countryId);
                } else {
                    datum = new ClimateDatum();
                    String centroid = rs.getString("centroid");
                    HashMap<String, String> metadata = new HashMap<String, String>();
                    metadata.put("centroid", centroid);
                    metadata.put("cid", Integer.toString(countryId));
                    metadata.put("cn", name);
                    metadata.put("iso3", iso3);
                    datum.setMetadata(metadata);
                    climateDatumMap.put(countryId, datum);
                }


                datum.addData(new CountryRain(pdate, avg));
            }

        } catch (SQLException ex) {
            Logger.getLogger(CountryPrecipitationDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, null);
        }
        return convertCollectionToList(climateDatumMap.values());
    }

    private List<ClimateDatum> convertCollectionToList(Collection<ClimateDatum> datums) {
        List<ClimateDatum> cd = new ArrayList<ClimateDatum>();
        for (ClimateDatum c : datums) {
            cd.add(c);
        }
        return cd;

    }

    public List<CountryRain> getRainData(int countryId) {
        PreparedStatement ps = null;


        Connection c = null;
        ResultSet rs = null;
        List<CountryRain> countryRains = new ArrayList<CountryRain>();

        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement(COUNTRY_RAIN);
            ps.setInt(1, countryId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Date pdate = rs.getDate("precipitation_date");
                float avg = rs.getFloat("sum");

                countryRains.add(new CountryRain(pdate, avg, countryId));
            }

        } catch (SQLException ex) {
            Logger.getLogger(CountryPrecipitationDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DBUtils.close(c, ps, null);
        }
        return countryRains;

    }

    public HashMap<String, String> getRegionPropertiesContainingPoint(double latitude, double longitude) {
        Connection c = DBUtils.getConnection();




//        countries.addAll(geoDao.getEntitiesContainingPoint(c,latitude,longitude,new CountryRowMapper(),"country",null, null));
        HashMap<String, String> props = GeoDao.getPropertiesOfRegionContainingPoint(c, latitude, longitude, "boundary", "shape", "area_id");


        log.log(Level.INFO, "country id is {0}", props.get("id"));
        log.log(Level.INFO, "country centroid is {0}", props.get("centroid"));


        DBUtils.close(c);

        return props;
    }
}
