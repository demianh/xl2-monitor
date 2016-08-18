package ch.demianh.xl2monitor.serial.patterns;

public class LAEQ60_Narrow_Pattern implements IScreenPattern {
    /**
     * there is an offset before/after the screen pixels
     */
    public int OFFSET_BYTES_START() { return 117;}
    public int OFFSET_BYTES_END() { return 7; }

    /**
     * Which lines should be taken to recognize the numbers
     */
    public int LINE_NUMBER_1() { return 99; }
    public int LINE_NUMBER_2() { return 100; }

    /**
     * Indicates the rows where the numbers display start (LAeq60)
     */
    public int OFFSET_DIGIT_1() { return 76; }
    public int OFFSET_DIGIT_2() { return 89; }
    public int OFFSET_DIGIT_3() { return 102; }
    public int OFFSET_DIGIT_4() { return 120; }

    public int DIGIT_WIDTH() { return 11; }

    /**
     * Pattern of the cut-out of two lines for each number (0-9).
     * Patterns are a combination of line 100 + 101 of the screen
     * which make an unique identifier for each number
     */
    public String[] NUMBER_PATTERN() {
        return new String[]{
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
    }
}
