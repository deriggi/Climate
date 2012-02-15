/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao.basin;

import dao.RowMapper;
import domain.Basin;
import domain.Country;
import domain.Drainage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wb385924
 */
@Deprecated
public class SpatialBasinRowMapper implements RowMapper<Basin>{

    public List<Basin> results(ResultSet rs) {
        List<Basin> basins = new ArrayList<Basin>();
//        try {
            
//            while (rs.next()) {
//                Basin b = new Basin(
//                        rs.getString("basin_name"),
//                        new Country(rs.getString("country_name")),
//                        new Drainage(rs.getString("drainage_name")));
//                b.setId(rs.getInt("basin_id"));
//
//                basins.add(b);
//            }
            
//        } catch (SQLException ex) {
//           ex.printStackTrace();
//        }

        return basins;
    }

}
