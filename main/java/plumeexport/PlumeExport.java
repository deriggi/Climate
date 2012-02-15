/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plumeexport;

import database.DBUtils;
import export.util.FileExportHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class PlumeExport {

    public static void main(String[] args){
        new PlumeExport().exportDepartment();
    }

    public void export(){
        try {
            Connection con = null;//DBUtils.get("city_risk").getConnection();
            PreparedStatement ps = con.prepareStatement("select plume_sat_source,plume_rank,plume_date,ST_asSVG(  st_translate(st_scale(plume_shape,20,20),1700,500)   ) as svg,plume_rank from plume order by plume_date,plume_sat_source");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String svg = rs.getString("svg");
                String rank = rs.getString("plume_rank");
                String date = rs.getString("plume_date");
                String source = rs.getString("plume_sat_source");
                FileExportHelper.appendToFile("plume.txt","plumes.push(['" + svg + "','"+ date + "',"+ rank+",'"+source+"']);" );
                System.out.println(svg);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlumeExport.class.getName()).log(Level.SEVERE, null, ex);
        }
        DBUtils.closeAll();


    }

     public void exportProvince(){
        try {
            Connection con = null;//DBUtils.get("city_risk").getConnection();
            PreparedStatement ps = con.prepareStatement("select province_name,ST_asSVG(  st_translate(st_scale(st_simplify(province_shape,0.01),20,20),1700,500)   ) as svg from province");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String svg = rs.getString("svg");
                String name = rs.getString("province_name");
                
                
                FileExportHelper.appendToFile("province.txt","provinces.push(['" + svg + "','"+ name + "']);" );
                System.out.println(svg);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlumeExport.class.getName()).log(Level.SEVERE, null, ex);
        }
        DBUtils.closeAll();


    }



     public void exportDepartment(){
        try {
            Connection con = null;//DBUtils.get("city_risk").getConnection();
            PreparedStatement ps = con.prepareStatement("select department_name,ST_asSVG(  st_translate(st_scale(st_simplify(department_shape,0.01),20,20),1700,500)   ) as svg from department");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String svg = rs.getString("svg");
                String name = rs.getString("department_name");


                FileExportHelper.appendToFile("department.txt","departments.push(['" + svg + "','"+ name + "']);" );
                System.out.println(svg);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlumeExport.class.getName()).log(Level.SEVERE, null, ex);
        }
        DBUtils.closeAll();


    }
}
