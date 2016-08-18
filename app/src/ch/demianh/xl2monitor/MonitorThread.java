package ch.demianh.xl2monitor;

import ch.demianh.xl2monitor.serial.SerialConnection;
import ch.demianh.xl2monitor.serial.SerialConnectionException;
import ch.demianh.xl2monitor.serial.SerialScreenReader;
import ch.demianh.xl2monitor.serial.patterns.*;
import ch.demianh.xl2monitor.websync.Websync;

class MonitorThread implements Runnable {

    private App app;
    private boolean TESTING_MODE = false;
    private String SYNC_URL;

    public MonitorThread(App app, Boolean testing, String syncUrl){
        this.app = app;
        this.TESTING_MODE = testing;
        this.SYNC_URL = syncUrl;
    }

    public void run() {
        System.out.println("Thread started..." + app.getName());

        Websync websync = new Websync(this.SYNC_URL);

        SerialConnection connection = new SerialConnection();

        if(!connection.isConnected() && !TESTING_MODE){
            app.setErrorMessage("No NTi XL2 Device found");
            connection.disconnect();
            app.setStateStopped();
            return;
        }

        try {

            while(!Thread.currentThread().isInterrupted() && (connection.isConnected() || TESTING_MODE)){

                try {
                    Double decibel;
                    if(TESTING_MODE){
                        // Generate random number for testing
                        decibel = Math.round(Math.random()*400 + 600)/10.0;
                        Thread.sleep(100);
                    } else {
                        // Read from serial port
                        byte[] data = connection.queryBytes("RXL2S");
                        SerialScreenReader serialScreenReader = new SerialScreenReader(data, new LAEQ60_Wide_Pattern());
                        decibel = serialScreenReader.parseLAeq60Value();
                    }

                    app.setValue(decibel);
                    app.setErrorMessage("");

                    System.out.println("dB: " + decibel);

                    websync.sendValue(app.getName(), Double.toString(decibel));

                } catch (SerialConnectionException e){
                    app.setErrorMessage(e.getMessage());
                    break;
                }

                // Pause for 0.9 second, serial connection sleeps another 100ms
                Thread.sleep(900);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.disconnect();
        app.setStateStopped();
    }

}