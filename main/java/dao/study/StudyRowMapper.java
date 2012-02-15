/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao.study;

import dao.RowMapper;
import domain.Study;
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
public class StudyRowMapper implements RowMapper<Study>{

    public List<Study> results(ResultSet rs) {
        List<Study> studies = new ArrayList<Study>();
        try {

            while (rs.next()) {

                studies.add(new Study(rs.getString("study_code"), rs.getInt("data")));

            }

        } catch (SQLException ex) {
            Logger.getLogger(StudyRowMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return studies;

    }

}
