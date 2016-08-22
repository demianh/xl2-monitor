package ch.demianh.xl2monitor.serial;

import ch.demianh.xl2monitor.serial.patterns.IScreenPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen width is 160 x 160 pixels, each pixel is represented by 4 bits
 *
 * Convert bit to color legend:
 * 0000 white
 * 0001 grey
 * 0010 black
 * 0011 black (some rare cases)
 *
 */
public class SerialScreenReader {

    private byte[] data = null;

    private IScreenPattern P;


    public SerialScreenReader(byte[] data, IScreenPattern pattern){
        this.data = data;
        this.P = pattern;
    }

    public double parseLAeq60Value() throws SerialConnectionException {


        String LAeq60value = "";

        List<String> lines = this.getScreenLines();
        if(lines == null){
            throw new SerialConnectionException("No data from device");
        }
        if(lines.size() > P.LINE_NUMBER_2()){
            String line1 = lines.get(P.LINE_NUMBER_1());
            String line2 = lines.get(P.LINE_NUMBER_2());

            String number1 = line1.substring(P.OFFSET_DIGIT_1(), P.OFFSET_DIGIT_1()+P.DIGIT_WIDTH()) + line2.substring(P.OFFSET_DIGIT_1(), P.OFFSET_DIGIT_1()+P.DIGIT_WIDTH());
            LAeq60value += guessNumber(number1);

            String number2 = line1.substring(P.OFFSET_DIGIT_2(), P.OFFSET_DIGIT_2()+P.DIGIT_WIDTH()) + line2.substring(P.OFFSET_DIGIT_2(), P.OFFSET_DIGIT_2()+P.DIGIT_WIDTH());
            LAeq60value += guessNumber(number2);

            String number3 = line1.substring(P.OFFSET_DIGIT_3(), P.OFFSET_DIGIT_3()+P.DIGIT_WIDTH()) + line2.substring(P.OFFSET_DIGIT_3(), P.OFFSET_DIGIT_3()+P.DIGIT_WIDTH());
            LAeq60value += guessNumber(number3);

            LAeq60value += ".";

            String number4 = line1.substring(P.OFFSET_DIGIT_4(), P.OFFSET_DIGIT_4()+P.DIGIT_WIDTH()) + line2.substring(P.OFFSET_DIGIT_4(), P.OFFSET_DIGIT_4()+P.DIGIT_WIDTH());
            LAeq60value += guessNumber(number4);

            double val = Double.parseDouble(LAeq60value);
            if(val == 0.0){
                throw new SerialConnectionException("Parsed value is '0.0'. Wrong pattern?");
            }
            return val;
        } else {
            throw new SerialConnectionException("Invalid data from device");
        }

    }

    public void printScreenToLog(){
        int linecount = 0;
        List<String> lines = this.getScreenLines();
        if(lines == null){
            return;
        }
        for (String line : lines) {
            System.out.println(String.format("%3d", linecount) + ": " + line);
            linecount++;
        }
    }

    private int guessNumber(String pixels){
        for(int i = 0; i < 10; i++){
            if(P.NUMBER_PATTERN()[i].equals(pixels))
                return i;
        }
        return 0;
    }

    private List<String> getScreenLines(){
        if(data == null){
            return null;
        }
        String tmpLine = "";
        List<String> lines = new ArrayList<String>();

        // only read the screen bytes, skip chunks
        for(int i = P.OFFSET_BYTES_START(); i < data.length - P.OFFSET_BYTES_END(); i++){

            // convert byte into two 4 bit parts (each represents a pixel)
            byte pixel1 = (byte) (data[i] & 0xF);
            byte pixel2 = (byte) ((data[i] >> 4) & 0xF);
            tmpLine += bitToPixel(pixel2) + bitToPixel(pixel1);

            if((i + P.OFFSET_BYTES_START()) % 80 == 0){
                // screen lines are sent from bottom to top
                // add screen line to the beginning of lines array for easier processing later
                lines.add(0, tmpLine);
                tmpLine = "";
            }
        }
        return lines;
    }

    private String bitToPixel(byte bytes){
        switch(bytes){
            case 0:
                return " ";
            case 1:
                return ".";
            case 2:
            case 3:
                return "#";
            default:
                return "?";
        }
    }
}
