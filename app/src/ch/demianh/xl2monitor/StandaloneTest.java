package ch.demianh.xl2monitor;


import ch.demianh.xl2monitor.serial.SerialConnection;
import ch.demianh.xl2monitor.serial.SerialScreenReader;

public class StandaloneTest {

    public static void main(String[] args) throws Exception {
        SerialConnection connection = new SerialConnection();

        connection.queryString("*IDN?");

        byte[] data = connection.queryBytes("RXL2S");
        SerialScreenReader reader = new SerialScreenReader(data);

        reader.printScreenToLog();

        System.out.println("******* DB VALUE "+reader.parseLAeq60Value()+" ******");

        connection.disconnect();

        System.out.println("Done");
    }
}
