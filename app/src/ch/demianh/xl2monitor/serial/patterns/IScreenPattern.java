package ch.demianh.xl2monitor.serial.patterns;

public interface IScreenPattern {
    /**
     * there is an offset before/after the screen pixels
     */
    int OFFSET_BYTES_START();
    int OFFSET_BYTES_END();

    /**
     * Which lines should be taken to recognize the numbers
     */
    int LINE_NUMBER_1();
    int LINE_NUMBER_2();

    /**
     * Indicates the rows where the numbers display start (LAeq60)
     */
    int OFFSET_DIGIT_1();
    int OFFSET_DIGIT_2();
    int OFFSET_DIGIT_3();
    int OFFSET_DIGIT_4();

    int DIGIT_WIDTH();

    /**
     * Pattern of the cut-out of two lines for each number (0-9).
     * Patterns are a combination of line 100 + 101 of the screen
     * which make an unique identifier for each number
     */
    String[] NUMBER_PATTERN();
}
