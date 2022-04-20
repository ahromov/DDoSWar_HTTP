package ua.cc.lajdev.ddoshttp.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class FileParser {

    public static List<String> getUrls(String fileName) {
        List<String> hostNames = new ArrayList<>();
        try (Scanner reader = new Scanner(new FileReader(fileName))) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.startsWith("http")) {
                    hostNames.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return hostNames;
    }
}
