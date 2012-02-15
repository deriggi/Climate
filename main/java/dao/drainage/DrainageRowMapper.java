/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao.drainage;

import dao.RowMapper;
import domain.Drainage;
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
public class DrainageRowMapper implements RowMapper<Drainage>{

    public List<Drainage> results(ResultSet rs) {
        List<Drainage> countries = new ArrayList<Drainage>();
        try {

            while (rs.next()) {
                countries.add(new Drainage(rs.getString("drainage_name")));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DrainageRowMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return countries;
    }

}
