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
    private boolean running = false;

    public MonitorThread(App app, Boolean testing, String syncUrl){
        this.app = app;
        this.TESTING_MODE = testing;
        this.SYNC_URL = syncUrl;
    }

    public void run() {
        System.out.println("Thread started..." + app.getName());

        this.running = true;

        Websync websync = new Websync(this.SYNC_URL);

        SerialConnection connection = new SerialConnection();

        // Different Patterns for different Device Software Versions / Builds
        //IScreenPattern pattern = new LAEQ60_Narrow_Pattern();
        IScreenPattern pattern = new LAEQ60_Wide_Pattern();

        if(!connection.isConnected() && !TESTING_MODE){
            app.setErrorMessage("No NTi XL2 Device found");
        }

        try {

            while(!Thread.currentThread().isInterrupted() && this.running) {
                try {
                    Double decibel;
                    if(TESTING_MODE){
                        // Generate random number for testing
                        decibel = Math.round(Math.random()*400 + 600)/10.0;
                        Thread.sleep(100);
                    } else {
                        // Read from serial port
                        byte[] data = connection.queryBytes("RXL2S");
                        SerialScreenReader serialScreenReader = new SerialScreenReader(data, pattern);
                        decibel = serialScreenReader.parseLAeq60Value();
                    }

                    app.setValue(decibel);
                    app.setErrorMessage("");

                    System.out.println("dB: " + decibel);

                    websync.sendValue(app.getName(), Double.toString(decibel));

                } catch (SerialConnectionException e){
                    app.setErrorMessage(e.getMessage());
                    e.printStackTrace();

                    // retry if port has errors or is not connected
                    if(connection.isConnected()){
                        connection.disconnect();
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    connection.connect();
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

    public void stopMonitoring() {
        this.running = false;
    }

}