/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao.carbonintensitywri;

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
public class CarbonIntensityWriRowMapper implements RowMapper<TotalCo2>{

    public List<TotalCo2> results(ResultSet rs) {
        List<TotalCo2> totalCo2s = new ArrayList<TotalCo2>();
        try {

            while (rs.next()) {

                TotalCo2 da = new TotalCo2();
                da.setCountry(new Country(rs.getString("country_name"),rs.getInt("country_id")));
                da.setCo2(rs.getInt("carbon_intensity_wri_data"));
                da.setYear(rs.getInt("carbon_intensity_wri_year"));
                
                totalCo2s.add(da);
            }

        } catch (SQLException ex) {
            Logger.getLogger(CarbonIntensityWriRowMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return totalCo2s;
    }

}
