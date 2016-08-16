package ch.demianh.xl2monitor.websync;

import java.net.URLEncoder;

public class Websync {

    private String SYNC_URL;

    public Websync(String syncUrl){
        this.SYNC_URL = syncUrl;
    }

    public void sendValue(String stationName, String value) throws Exception {
        stationName = URLEncoder.encode(stationName, "utf8");
        String data = "{\"station\":\""+ stationName + "\", \"value\":" + value + "}";
        Runnable r = new WebsyncPostThread(SYNC_URL, data);
        new Thread(r).start();
    }
}
