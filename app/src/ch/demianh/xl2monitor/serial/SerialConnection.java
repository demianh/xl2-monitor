package ch.demianh.xl2monitor.serial;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SerialConnection implements ISerialConnection {
    private static String DEVICE_NAME = "NTiAudio,XL2";
    private static String PORT_PREFIX = "tty.";

    private SerialPort serialPort = null;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;

    public SerialConnection(){
        this.connect();
    }

    public void connect(){
        System.out.println("Connecting...");
        if(this.isConnected()){
            this.disconnect();
        }
        this.searchAndConnectToPort();
    }

    public void disconnect(){
        try {
            System.out.println("Disconnecting...");
            inputStream.close();
            inputStream = null;
            outputStream.close();
            outputStream = null;
            serialPort.closePort();
            serialPort = null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // we don't care
        }
    }

    public boolean isConnected(){
        return (this.serialPort != null);
    }

    public String queryString(String query) throws SerialConnectionException {
        byte[] result = this.queryBytes(query);
        if(result == null){
            throw new SerialConnectionException("No data from device");
        }
        try {
            String outString = new String(result, "UTF-8");
            System.out.println("GOT String: "+ outString);
            return outString;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new SerialConnectionException("Invalid data from device: " + e.getMessage());
        }
    }

    public byte[] queryBytes(String query) throws SerialConnectionException {
        if(!this.isConnected()){
            throw new SerialConnectionException("Not connected!");
        }
        try {
            outputStream.write((query + "\n").getBytes());
            // wait until all bytes are written to the stream, this can take up to 50ms
            Thread.sleep(100);

            List<Byte> out = new ArrayList<Byte>();
            int b = inputStream.read();
            // read until we get -1 or no more bytes
            while (serialPort.bytesAvailable() > 0 && b >= 0){
                out.add((byte)b);

                b = inputStream.read();
            }
            System.out.println("Done reading " + out.size() + " bytes.");
            return toByteArray(out);

        } catch (Exception e) {
            System.out.println("Port Error: " + e.getMessage());
            return null;
        }
    }

    private static byte[] toByteArray(List<Byte> in) {
        final int n = in.size();
        byte ret[] = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = in.get(i);
        }
        return ret;
    }

    private void searchAndConnectToPort(){
        // check all available ports
        SerialPort[] ports = SerialPort.getCommPorts();
        for(SerialPort comPort : ports){
            System.out.println("Port found: " + comPort.getSystemPortName() + " -> " +  comPort.getDescriptivePortName());

            // if name starts with a known prefix, we try and connect to it
            if(comPort.getSystemPortName().startsWith(PORT_PREFIX)){
                try {
                    // Test if the port responds with the correct answer
                    System.out.println("Connecting to port: " + comPort.getSystemPortName());
                    comPort.openPort();
                    comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);

                    OutputStream testOutputStream = comPort.getOutputStream();

                    testOutputStream.write("*IDN?\n".getBytes());
                    InputStream testInputStream = comPort.getInputStream();
                    Thread.sleep(20);
                    if(testInputStream.available() > 0){
                        // read enough bytes to make sure the stream is empty
                        // sometimes there are some bytes left in the stream
                        byte buffer [] = new byte[30000];
                        int read = testInputStream.read(buffer, 0, 30000);
                        if (read > 0) {
                            String testResponse = new String(buffer, "UTF-8");
                            System.out.println("DEVICE TEST RESPONSE: " + testResponse);
                            if(testResponse.startsWith(DEVICE_NAME)){
                                System.out.println("We found a NTi XL2 Device on Port " + comPort.getSystemPortName());

                                // great, now make the current connection available to the instance for further use
                                serialPort = comPort;
                                outputStream = testOutputStream;
                                inputStream = testInputStream;
                                return;
                            }
                        }
                    }
                    comPort.closePort();
                } catch (Exception e) {
                    System.out.println("Cannot connect to " + comPort.getSystemPortName() + " Error: " + e.getMessage());
                    comPort.closePort();
                }
            }
        }
    }
}
