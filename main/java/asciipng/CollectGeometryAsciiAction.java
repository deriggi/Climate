/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package asciipng;

import ascii.AsciiAction;
import com.vividsolutions.jts.geom.Polygon;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import sdnis.wb.util.BasicAverager;


/**
 *
 * @author wb385924
 */
public class CollectGeometryAsciiAction implements AsciiAction{
    private BasicAverager ba = new BasicAverager();

    public BasicAverager getBa() {
        return ba;
    }

    private Set<GridCell> gridCells = new HashSet<GridCell>();

    public Set<GridCell> getGridCells() {
        return gridCells;
    }
    public void handleNonNullData(double y, double x,  Date date, double data) {
        ba.update(data);
        Polygon gridCell = GeometryBuilder.createGridCellFromLowerLeftPoint(x, y, 0.5);
        gridCells.add(new GridCell(gridCell, data));
        
    }
    public int getSize(){
        return gridCells.size();
    }


}
