package ch.demianh.xl2monitor.websync;

import java.net.URLEncoder;

public class Websync {

    private final String SYNC_URL = "http://holderegger.org/post.php";

    public void sendValue(String stationName, String value) throws Exception {
        stationName = URLEncoder.encode(stationName, "utf8");
        String data = "{\"station\":\""+ stationName + "\", \"value\":" + value + "}";
        Runnable r = new WebsyncPostThread(SYNC_URL, data);
        new Thread(r).start();
    }
}
