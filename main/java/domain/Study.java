/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package domain;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author wb385924
 */
@XmlRootElement
public class Study {

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private int data;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public Study(){
        
    }
    public Study(String code, int data){
        this.code = code; this.data = data;
    }

}
