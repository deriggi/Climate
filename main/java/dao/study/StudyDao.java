/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao.study;

import dao.GeoDao;
import database.DBUtils;
import domain.Study;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wb385924
 */
public class StudyDao {

    private static StudyDao studyDao = null;
    private GeoDao geoDao = new GeoDao<StudyDao>();
    private StudyRowMapper rowMapper = null;
    public static StudyDao get(){
        if(studyDao == null){
            studyDao = new StudyDao();
            studyDao.rowMapper = new StudyRowMapper();
        }
        return studyDao;
    }

    public List<Study> getStudies(int basinId){
        Connection c = DBUtils.getConnection();
        List<Study> studies = new ArrayList<Study>();
        studies.addAll(geoDao.getJoinedChildEntitiesByParentId(c, "basin", "study", rowMapper, basinId));
        DBUtils.close(c);

        return studies;
    }

}
