package org.example;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.*;
import java.io.File;
import java.time.LocalTime;
import javax.imageio.ImageIO;
import javax.swing.*;
import lombok.Getter;
import org.example.model.Config;

public class Main {
    private static final long TIME_BANK_REFILL_FREQUENCY = 604800000;
    private static final LocalTime TIME_BANK_REFILL_AMOUNT = LocalTime.of(10,0,0);
    @Getter
    private static JFrame mainFrame;

    public static void main(String[] args) {
        //FlatLaf
        FlatDarkLaf.setup();

        //Load Config every time on start
        Config.Load();
        updateTimeBank();

        try {
            //Base Form setup
            mainFrame = new JFrame("Playtime");
            mainFrame.setContentPane(new BaseForm().getPanel_main());
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Dimension windowSize = new Dimension(750, 300);
            mainFrame.setMinimumSize(windowSize);
            mainFrame.setMaximumSize(windowSize);
            mainFrame.setIconImage(ImageIO.read(new File("src\\main\\resources\\icon.png")));
            mainFrame.pack();
            mainFrame.setVisible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //On shutdown hook
        Thread onShutdownThread = new Thread(Config::Save);
        Runtime.getRuntime().addShutdownHook(onShutdownThread);
    }

    private static void updateTimeBank() {
        //when past refill time
        if (Config.getConfig().getNextTimeBankRefillOn() < System.currentTimeMillis()) {
            Config.getConfig().setNextTimeBankRefillOn(System.currentTimeMillis() + TIME_BANK_REFILL_FREQUENCY);
            Config.getConfig().setTimeBank(TIME_BANK_REFILL_AMOUNT.toString());
        }
    }
}
