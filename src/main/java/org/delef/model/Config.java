package org.delef.model;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.delef.util.FileManager;

@AllArgsConstructor
public class Config extends FileManager {
    @Getter
    private static Config config;

    @Getter
    @Setter
    private String timeBank;

    @Getter
    @Setter
    private Set<String> selectedPrograms;

    @Getter
    @Setter
    private long nextTimeBankRefillOn;

    @Getter
    @Setter
    private String refillTime;

    @Getter
    @Setter
    private RefillFrequency refillFrequency;

    @Getter
    @Setter
    private String password;

    private static void setConfig(Config config){
        Config.config = config;
    }

    public static final String CONFIG_URI =  System.getProperty("user.home") + "\\AppData\\Local\\config.json";

    private static final Config DEFAULT_CONFIG = new Config(
            "01:00:00",
            new HashSet<>(),
            0,
            "01:00:00",
            RefillFrequency.Weekly,
            ""
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

    public enum RefillFrequency {
        Daily(86400000),
        Weekly(604800000);

        public final long millis;
        RefillFrequency(long millis){
            this.millis = millis;
        }
    }
}

