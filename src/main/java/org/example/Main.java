package org.example;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.*;
import java.time.LocalTime;
import javax.swing.*;
import org.example.exception.GlobalExceptionHandler;
import org.example.model.Config;

public class Main {
    private static final long TIME_BANK_REFILL_FREQUENCY = 604800000;
    private static final LocalTime TIME_BANK_REFILL_AMOUNT = LocalTime.of(10,0,0);

    public static void main(String[] args) {
        //ExceptionHandler
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(globalExceptionHandler);

        //FlatLaf
        FlatDarkLaf.setup();

        //Load Config every time on start
        Config.Load();
        updateTimeBank();

        //Base Form setup
        JFrame frame = new JFrame("Playtime");
        frame.setContentPane(new BaseForm().getPanel_main());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600, 300));
        frame.pack();
        frame.setVisible(true);

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
