package org.example.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {
    public static void Write(String content, String toPath) throws IOException {
        Path path = Path.of(toPath);
        byte[] bytes = content.getBytes();
        Files.write(path, bytes);
    }

    public static String Read(String fromPath) throws IOException {
        Path path = Path.of(fromPath);
        return Files.readString(path);
    }
}
