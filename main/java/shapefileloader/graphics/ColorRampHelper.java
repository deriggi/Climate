/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.graphics;

import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Johnny
 */
public class ColorRampHelper {

    
    private static ArrayList<Color> greenSequence = new ArrayList<Color>();
    private static ArrayList<Color> blueToRed = new ArrayList<Color>();

    public static enum ColoredVariable {

        precip, temp;
        private ArrayList<Color> colorRamp = null;

        public ArrayList<Color> getColorRamp() {
            return colorRamp;
        }

        private void setRamp(ArrayList<Color> colors) {
            this.colorRamp = colors;
        }
    }
    
    public static ArrayList<Color> getPrecipRamp(){
      return greenSequence;
    }
    
    public static ArrayList<Color> getTemperatureRamp(){
        return blueToRed;
    }
    
    static{
        createColors();
    }

    private static void createColors() {
        greenSequence.add(new Color(0xFFFF80));
        greenSequence.add(new Color(0xE8FC72));
        greenSequence.add(new Color(0xD0FA66));
        greenSequence.add(new Color(0xB6F558));
        greenSequence.add(new Color(0xA1F24B));
        greenSequence.add(new Color(0x87ED3E));
        greenSequence.add(new Color(0x71EB2F));
        greenSequence.add(new Color(0x55E620));
        greenSequence.add(new Color(0x38E009));
        greenSequence.add(new Color(0x3BD62D));
        ColoredVariable.precip.setRamp(greenSequence);

        blueToRed.add(new Color(5, 48, 97));
        blueToRed.add(new Color(33, 102, 172));
        blueToRed.add(new Color(67, 147, 195));
        blueToRed.add(new Color(146, 197, 222));
        blueToRed.add(new Color(209, 229, 240));
        blueToRed.add(new Color(247, 247, 247));
        blueToRed.add(new Color(253, 219, 199));
        blueToRed.add(new Color(244, 165, 130));
        blueToRed.add(new Color(214, 96, 77));
        blueToRed.add(new Color(178, 24, 43));
        ColoredVariable.temp.setRamp(blueToRed);
    }
}
