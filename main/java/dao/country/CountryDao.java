/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.country;

import dao.GeoDao;
import dao.RowMapper;
import database.DBUtils;
import domain.Country;
import domain.web.CountryShapeSvg;
import domain.web.ShapeSvg;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class CountryDao {

    private GeoDao<Country> geoDao = new GeoDao<Country>();
    private RowMapper rowMapper = new CountryRowMapper();
//    private RowMapper minRowMapper = new MinimalCountryRowMapper();
    private final String COUNTRY = "country";
    private static CountryDao dao = null;

    private String GET_SIMPLE_SVG_BOUNDARY = "select country_iso_3, country_name, st_asSVG(   st_simplify(boundary_shape,0.78),1  ,2) from "
            + "boundary inner join country on boundary_area_id = country_id and country_id = ?";

    private String GET_ALL_SIMPLE_SVG_BOUNDARY = "select country_iso_3, country_name, st_asSVG(   st_simplify(boundary_simple,1),1  ,2) from "
            + "boundary inner join country on boundary_area_id = country_id where country_region_id =9138";

//    private String GET_SIMPLE_SVG_BOUNDARY = "select country_name, st_asSVG(  st_scale(  st_simplify(boundary_simple,2 ) ,1.0,1.0),1  ,2) from "
//            + "boundary inner join country on boundary_area_id = ? AND  country_id = ?";
    private String SEARCH_COUNTRIES = "select country_name, country_iso_3 from country where lower(country_name) like '@%' ";
    private String POINT_TEMPLATE = "(  'POINT( @ # )', 4326))";
    private String GET_COUNTRY_FROM_POINT = "select country_name, country_iso_3, country_id, country_iso_2 from country inner join boundary on country_id = boundary_area_id and st_contains(boundary_simple,st_PointFromText";
    private String GET_COUNTRIES_FROM_REGION = "select * from country inner join region on country_region_id = region_id and region_name = ?";
    

    private final String NAME = "country_name";
    private final String ID  = "country_id";
    private final String ISO3 = "country_iso_3";
    private final String ISO2 = "country_iso_2";


    //    sb.append("ST_PointFromText('POINT(");
//        sb.append(longitude);
//        sb.append(" ");
//        sb.append(latitude);
//        sb.append(")', 4326))");

    private CountryDao() {
    }

    public static CountryDao get() {
        if (dao == null) {
            dao = new CountryDao();
        }
        return dao;
    }

//    public Country getCountry(){
//        geoDao.get
//    }
    public void assignCountryToRegion(int countryId, int regionId) {

        PreparedStatement ps = null;
        Connection c = null;
        ResultSet rs = null;

        try {
            c = DBUtils.getConnection();
            ps = c.prepareStatement("update country set country_region_id = ? where country_id = ?");
            ps.setInt(1, regionId);
            ps.setInt(2, countryId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CountryDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            DBUtils.close(c,ps, rs);
        }

    }

    public HashMap<String,Country> getCountriesAsMap(List<Country> countries){
        HashMap<String,Country> countryMap = new HashMap<String,Country>();

        for(Country c: countries){
            countryMap.put(c.getIso3(), c);
        }
        return countryMap;
    }



    public List<Country> getCountriesFromRegionName (String name){
        PreparedStatement ps = null;
        Connection c = null;
        Country country = null;
        ResultSet rs = null;
        List<Country> countries = new ArrayList<Country>();
        try {
            c = DBUtils.getConnection();
            ps = c.prepareStatement(GET_COUNTRIES_FROM_REGION);
            ps.setString(1, name);
            rs = ps.executeQuery();

            while(rs.next()){
                country = new Country(rs.getString(NAME), rs.getInt(ID), rs.getString(ISO3), rs.getString(ISO2));
                countries.add(country);
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            DBUtils.close(c, ps, rs);
            return countries;
        }
    }

    

    public Country getCountryFromPoint(double longitude, double latitude) {
        PreparedStatement ps = null;
        Connection c = null;
        Country country = null;
        try {


            c = DBUtils.getConnection();
            String suffix = POINT_TEMPLATE.replaceFirst("@", Double.toString(longitude));
            suffix = suffix.replaceFirst("#", Double.toString(latitude));

            ps = c.prepareStatement(GET_COUNTRY_FROM_POINT + suffix);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                country = new Country(rs.getString("country_name"), rs.getInt("country_id"), rs.getString("country_iso_3"), rs.getString("country_iso_2"));
            }


        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            DBUtils.close(c, ps, null);
            return country;
        }


    }

    public ShapeSvg getSVGBoundaryForCountry(int countryId) {
        PreparedStatement ps = null;
        Connection c = null;
        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement(GET_SIMPLE_SVG_BOUNDARY);
            ps.setInt(1, countryId);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new CountryShapeSvg(rs.getString("st_assvg"), rs.getString("country_name"), rs.getString("country_iso_3"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBUtils.close(c);
        }
        return null;

    }

    public List<ShapeSvg> getAllSVGBoundary() {
        PreparedStatement ps = null;
        Connection c = null;
        List<ShapeSvg> basinSvgs = new ArrayList<ShapeSvg>();
        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement(GET_ALL_SIMPLE_SVG_BOUNDARY);


            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                basinSvgs.add(new CountryShapeSvg(rs.getString("st_assvg"), rs.getString("country_name"), rs.getString("country_iso_3")));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBUtils.close(c);
        }
        return basinSvgs;

    }

    public List<HashMap<String, String>> searchCountries(String name) {
        PreparedStatement ps = null;
        Connection c = null;
        List<HashMap<String, String>> countries = new ArrayList<HashMap<String, String>>();
        if (name == null || name.trim().length() == 0) {
            return countries;
        }
        name = name.toLowerCase();
        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement(SEARCH_COUNTRIES.replaceFirst("\\@", name));
//            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HashMap<String, String> dataMap = new HashMap<String, String>();
                dataMap.put("label", rs.getString("country_name"));
                dataMap.put("value", rs.getString("country_iso_3"));

                countries.add(dataMap);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBUtils.close(c);
        }

        return countries;
    }

    public List<Country> getCountries() {
        Connection c =DBUtils.getConnection();
        List<Country> countries = null;
        countries = geoDao.getEntities(c, COUNTRY, rowMapper);
        DBUtils.close(c);

        return countries;
    }

    public List<domain.web.Country> getMinimalCountries() {
        PreparedStatement ps = null;
        Connection c = null;
        List<domain.web.Country> minCountries = new ArrayList<domain.web.Country>();
        try {

            c = DBUtils.getConnection();
            ps = c.prepareStatement("select country_name, country_iso_3 from country where country_iso_3 is not null");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                minCountries.add(new domain.web.Country(rs.getString("country_name"), rs.getString("country_iso_3")));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBUtils.close(c,ps, null);
        }

        return minCountries;
    }
}
