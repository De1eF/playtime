package org.example.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ProcessManager {
    public static void launchGame(String path) {
        try {
            Runtime.getRuntime().exec(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void killGame(String name) {
        try {
            Runtime.getRuntime().exec("taskkill /F /IM " + name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isRunning(String name) throws IOException {
        String filenameFilter = "/nh /fi \"Imagename eq "+ name +"\"";
        String tasksCmd = System.getenv("windir") +"/system32/tasklist.exe "+filenameFilter;

        Process p = Runtime.getRuntime().exec(tasksCmd);
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

        ArrayList<String> procs = new ArrayList<>();
        String line;
        while ((line = input.readLine()) != null)
            procs.add(line);

        input.close();

        return procs.stream().anyMatch(row -> row.contains(name));
    }
}
