package org.example;

import java.time.LocalTime;
import javax.swing.*;
import lombok.Getter;
import org.example.dialog.AddProgramDialogCreator;
import org.example.dialog.AddTimeDialogCreator;
import org.example.dialog.PasswordSetUpDialogCreator;
import org.example.dialog.RemoveProgramDialogCreator;
import org.example.model.Config;
import org.example.model.TimeTrackListener;
import org.example.service.ProcessManager;
import org.example.service.TimeManager;

public class BaseForm {
    @Getter
    private JPanel panel_main;
    private JLabel label_time;
    private JButton button_addProgram;
    private JPanel panel_time;
    private JPanel panel_ProgramListWindow;
    private JTextArea textArea_selectedApps;
    private JButton button_remvoeProgram;
    private JButton button_addTime;
    private TimeManager timer = null;
    private static final AddProgramDialogCreator addProgramDialogCreator = new AddProgramDialogCreator();
    private static final RemoveProgramDialogCreator removeProgramDialogCreator = new RemoveProgramDialogCreator();
    private static final PasswordSetUpDialogCreator passwordSetupDialogCreator = new PasswordSetUpDialogCreator();
    private static final AddTimeDialogCreator addTimeDialogCreator = new AddTimeDialogCreator();

    private static final LocalTime ZERO_TIME = LocalTime.of(0,0,0);

    public BaseForm() {
        panel_main.setMaximumSize(Main.getMainFrame().getMaximumSize());
        if (Config.getConfig().getPassword().isEmpty()) {
            passwordSetupDialogCreator.show(()-> {});
        }

        label_time.setText(Config.getConfig().getTimeBank());
        setUpTimer();
        refreshTrackedPrograms();

        button_addProgram.addActionListener((e) -> addProgramDialogCreator.show(this::refreshTrackedPrograms));
        button_remvoeProgram.addActionListener((e) -> removeProgramDialogCreator.show(this::refreshTrackedPrograms));
        button_addTime.addActionListener((e) -> addTimeDialogCreator.show(()-> {}));
    }

    private void refreshTrackedPrograms() {
        textArea_selectedApps.setText("...");

        StringBuilder stringBuilder = new StringBuilder();
        for (String programName : Config.getConfig().getSelectedPrograms()) {
            stringBuilder.append("-").append(programName).append("\n");
        }
        textArea_selectedApps.setText(stringBuilder.toString());
    }

    private void setUpTimer() {
        //pull time form time bank
        LocalTime timeRemaining = LocalTime.parse(Config.getConfig().getTimeBank());
        //each second
        TimeTrackListener onTimeChanged = (time) -> {
            boolean timeIsUp = time.equals(ZERO_TIME);

            //set the remaining time as text
            String onTimeOutText =
                    "Next bank refill in "
                            + (int)((
                                    Config.getConfig().getNextTimeBankRefillOn() - System.currentTimeMillis()
                                    ) / 86400000)
                    + " days";
            label_time.setText(timeIsUp ? onTimeOutText : time.toString());

            try {
                for (String processName : Config.getConfig().getSelectedPrograms()) {

                    //When one of selected programs is running, start counting time down
                    if (ProcessManager.isRunning(processName + ".exe")) {

                        System.out.println(processName + ": active");
                        //Close the game if there is no time left
                        if (timeIsUp) {
                            ProcessManager.killGame(processName + ".exe");
                            timer.setEnabled(false);
                            return;
                        }

                        timer.setEnabled(true);
                        return;
                    }

                    //when the program is stopped, save remaining time to the bank and disable timer
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
                onTimeChanged);
        Thread timerThread = new Thread(
                timer,
                TimeManager.class.getName()
        );
        timerThread.start();
    }
}
