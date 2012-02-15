/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao.totalco2;

import dao.RowMapper;
import domain.Country;
import domain.TotalCo2;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class TotalCo2RowMapper implements RowMapper<TotalCo2>{

    public List<TotalCo2> results(ResultSet rs) {
        List<TotalCo2> totalCo2s = new ArrayList<TotalCo2>();
        try {

            while (rs.next()) {

                TotalCo2 da = new TotalCo2();
                da.setCountry(new Country(rs.getString("country_name"),rs.getInt("country_id")));
                da.setCo2(rs.getInt("total_Co2_data"));
                da.setYear(rs.getInt("total_co2_year"));
                
                totalCo2s.add(da);
            }

        } catch (SQLException ex) {
            Logger.getLogger(TotalCo2RowMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return totalCo2s;
    }

}
