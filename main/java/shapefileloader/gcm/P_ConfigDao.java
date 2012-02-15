/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader.gcm;

/**
 *
 * @author wb385924
 */
public interface P_ConfigDao {

//    public  P_GcmConfigDao getInstance();

    public int insertConfig(P_Config config);


    public int getConfigId(P_Config config);
    


}
