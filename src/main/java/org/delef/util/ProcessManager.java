package org.delef.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class ProcessManager {
    public static void killGame(String name, boolean byTitle) {
        try {
            String command = "taskkill /F " + name + ".exe";
            if (byTitle) {
                command = "/FI \"WINDOWTITLE eq  " + name +"\"";
            }
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isRunning(String name, boolean byTitle) throws IOException {
        String filenameFilter = "/NH /FI \"Imagename eq " + name + ".exe\"";
        if (byTitle) {
            filenameFilter = "/NH /FI \"WINDOWTITLE eq " + name + "\"";
        }
        String tasksCmd = System.getenv("windir") + "/system32/tasklist.exe " + filenameFilter;

        Process p = Runtime.getRuntime().exec(tasksCmd);
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

        return !input.readLine().contains("No tasks");
    }

    public static String getNameFromPath(String path) {
        return Path.of(path).getFileName().toString().split("\\.")[0];
    }
}
