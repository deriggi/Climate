/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.country;

import dao.RowMapper;
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
public class CountryHashMapper implements RowMapper{

    public List<HashMap<String, String>> results(ResultSet rs) {
        List<HashMap<String, String>> countries = new ArrayList<HashMap<String, String>>();
        try {
            
            while (rs.next()) {
                HashMap<String, String> dataMap = new HashMap<String, String>();
                dataMap.put("label", rs.getString("country_name"));
                dataMap.put("value", rs.getString("country_iso_3"));
                countries.add(dataMap);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(CountryHashMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return countries;
    }
}
