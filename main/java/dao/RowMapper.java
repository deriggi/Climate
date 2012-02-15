/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import java.sql.ResultSet;
import java.util.List;

/**
 *
 * @author wb385924
 */
public interface RowMapper<E> {

    public List<E> results(ResultSet rs);

}
