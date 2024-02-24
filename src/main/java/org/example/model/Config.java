package org.example.model;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.service.FileManager;

@AllArgsConstructor
public class Config extends FileManager {
    @Getter
    @Setter
    private static Config config;

    @Getter
    @Setter
    private String timeBank;

    @Getter
    @Setter
    private String selectedProgram;

    @Getter
    @Setter
    private long nextTimeBankRefillOn;

    public static final String CONFIG_URI = "./src/main/resources/config.json";

    private static final Config DEFAULT_CONFIG = new Config(
            "01:00:00",
            "",
            0
    );

    public static Gson gson = new Gson();

    public static void Save() {
        try {
            String json = gson.toJson(Config.getConfig());
            Write(json, CONFIG_URI);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Load() {
        try {
            String json = Read(CONFIG_URI);
            Config config = gson.fromJson(json, Config.class);
            Config.setConfig(config);
        } catch (NoSuchFileException e) {
            Config.setConfig(DEFAULT_CONFIG);
            Save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

