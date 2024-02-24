package org.example;

import java.io.File;
import java.time.LocalTime;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import lombok.Getter;
import org.example.model.Config;
import org.example.model.TimeOutListener;
import org.example.model.TimeTrackListener;
import org.example.service.ProcessManager;
import org.example.service.TimeManager;

public class BaseForm {
    @Getter
    private JPanel panel_main;
    private JButton button_start;
    private JLabel label_time;
    private JButton button_select_file;
    private JLabel label_selected_game;

    private TimeManager timer = null;

    public BaseForm() {
        label_time.setText(Config.getConfig().getTimeBank());
        setSelectedGame();

        button_start.addActionListener(e -> {
            ProcessManager.launchGame(Config.getConfig().getSelectedProgram());

            if (timer == null) {
                setUpTimer();
            } else {
                timer.setEnabled(true);
            }
        });
        button_select_file.addActionListener(e -> {
            String pathToSelectedFile;
            //setting up file chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Application","exe"));
            int response = fileChooser.showOpenDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) {
                //getting file path from chooser
                pathToSelectedFile = fileChooser.getSelectedFile().getAbsolutePath();
                //saving the file path to config
                Config.getConfig().setSelectedProgram(pathToSelectedFile);
                setSelectedGame();
            }
        });
    }

    private void setUpTimer() {
        //pull time form time bank
        LocalTime timeRemaining = LocalTime.parse(Config.getConfig().getTimeBank());
        //define "on time out" listener
        TimeOutListener onTimeOut = () -> ProcessManager.killGame(label_selected_game.getText() + ".exe");
        //define "each second track" listener
        TimeTrackListener onTimeChanged = (time) -> {
            label_time.setText(time.toString());
            //stop timer and save remaining time to bank when the targeted program is closed
            try {
                if (!ProcessManager.isRunning(label_selected_game.getText() + ".exe")) {
                    Config.getConfig().setTimeBank(time.toString());
                    Config.Save();
                    timer.setEnabled(false);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };

        //assemble and start the time thread
        timer = new TimeManager(
                timeRemaining,
                onTimeOut,
                onTimeChanged);
        Thread timerThread = new Thread(
                timer,
                TimeManager.class.getName()
        );
        timerThread.start();
    }

    private void setSelectedGame() {
        File f = new File(Config.getConfig().getSelectedProgram());
        label_selected_game.setText(f.getName().split("\\.")[0]);
    }
}
