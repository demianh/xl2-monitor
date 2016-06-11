package ch.demianh.xl2monitor;

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

    /**
     * there is an offset before/after the screen pixels
     */
    public static int OFFSET_BYTES_START = 117;
    public static int OFFSET_BYTES_END = 7;

    /**
     * Which lines should be taken to recognize the numbers
     */
    public static int LINE_NUMBER_1 = 99;
    public static int LINE_NUMBER_2 = 100;

    /**
     * Indicates the rows where the numbers display start (LAeq60)
     */
    public static int OFFSET_DIGIT_1 = 76;
    public static int OFFSET_DIGIT_2 = 89;
    public static int OFFSET_DIGIT_3 = 102;
    public static int OFFSET_DIGIT_4 = 120;

    public static int DIGIT_WIDTH = 11;

    /**
     * Pattern of the cut-out of two lines for each number (0-9).
     * Patterns are a combination of line 100 + 101 of the screen
     * which make an unique identifier for each number
     */
    public static String[] NUMBER_PATTERN = {
            "###     ######     ###",
            "    ###        ###    ",
            "      ####      ####  ",
            "    #####      ###### ",
            " ###  ###   ###  ###  ",
            "##############     ###",
            "########## ###########",
            "     ###        ###   ",
            "  #######   ######### ",
            "########### ##########"};


    public SerialScreenReader(byte[] data){
        this.data = data;
    }

    public double parseLAeq60Value(){

        String LAeq60value = "";

        List<String> lines = this.getScreenLines();
        if(lines.size() > LINE_NUMBER_2){
            String line1 = lines.get(LINE_NUMBER_1);
            String line2 = lines.get(LINE_NUMBER_2);

            String number1 = line1.substring(OFFSET_DIGIT_1, OFFSET_DIGIT_1+DIGIT_WIDTH) + line2.substring(OFFSET_DIGIT_1, OFFSET_DIGIT_1+DIGIT_WIDTH);
            LAeq60value += guessNumber(number1);

            String number2 = line1.substring(OFFSET_DIGIT_2, OFFSET_DIGIT_2+DIGIT_WIDTH) + line2.substring(OFFSET_DIGIT_2, OFFSET_DIGIT_2+DIGIT_WIDTH);
            LAeq60value += guessNumber(number2);

            String number3 = line1.substring(OFFSET_DIGIT_3, OFFSET_DIGIT_3+DIGIT_WIDTH) + line2.substring(OFFSET_DIGIT_3, OFFSET_DIGIT_3+DIGIT_WIDTH);
            LAeq60value += guessNumber(number3);

            LAeq60value += ".";

            String number4 = line1.substring(OFFSET_DIGIT_4, OFFSET_DIGIT_4+DIGIT_WIDTH) + line2.substring(OFFSET_DIGIT_4, OFFSET_DIGIT_4+DIGIT_WIDTH);
            LAeq60value += guessNumber(number4);

            return Double.parseDouble(LAeq60value);
        } else {
            return 0.0;
        }

    }

    public void printScreenToLog(){
        int linecount = 0;
        for (String line : this.getScreenLines()) {
            System.out.println(String.format("%3d", linecount) + ": " + line);
            linecount++;
        }
    }

    private int guessNumber(String pixels){
        for(int i = 0; i < 10; i++){
            if(NUMBER_PATTERN[i].equals(pixels))
                return i;
        }
        return 0;
    }

    private List<String> getScreenLines(){
        String tmpLine = "";
        List<String> lines = new ArrayList<String>();

        // only read the screen bytes, skip chunks
        for(int i = OFFSET_BYTES_START; i < data.length - OFFSET_BYTES_END; i++){

            // convert byte into two 4 bit parts (each represents a pixel)
            byte pixel1 = (byte) (data[i] & 0xF);
            byte pixel2 = (byte) ((data[i] >> 4) & 0xF);
            tmpLine += bitToPixel(pixel2) + bitToPixel(pixel1);

            if((i + OFFSET_BYTES_START) % 80 == 0){
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
