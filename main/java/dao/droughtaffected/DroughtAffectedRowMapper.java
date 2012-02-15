/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao.droughtaffected;

import dao.RowMapper;
import domain.Country;
import domain.DroughtAffected;
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
public class DroughtAffectedRowMapper implements RowMapper<DroughtAffected>{

    public List<DroughtAffected> results(ResultSet rs) {
        List<DroughtAffected> droughtAffecteds = new ArrayList<DroughtAffected>();
        try {

            while (rs.next()) {

                DroughtAffected da = new DroughtAffected();
                da.setCountry(new Country(rs.getString("country_name"),rs.getInt("country_id")));
                da.setNumberAffected(rs.getInt("drought_affected_data"));
                da.setStartYear(rs.getInt("drought_affected_start_year"));
                da.setEndYear(rs.getInt("drought_affected_end_year"));
                droughtAffecteds.add(da);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DroughtAffectedRowMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return droughtAffecteds;
    }

}
