/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao.basin;

import dao.RowMapper;
import domain.Basin;
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
public class BasinRowMapper implements RowMapper<Basin>{

    public List<Basin> results(ResultSet rs) {
        List<Basin> basins = new ArrayList<Basin>();
        try {

            while (rs.next()) {
                basins.add(new Basin(rs.getInt("basin_id"),rs.getInt("basin_code")));
            }

        } catch (SQLException ex) {
            Logger.getLogger(BasinRowMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return basins;
    }

}
