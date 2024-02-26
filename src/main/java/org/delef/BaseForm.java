package org.delef;

import javax.swing.*;
import lombok.Getter;
import org.delef.dialog.AddProgramDialogCreator;
import org.delef.dialog.AddTimeDialogCreator;
import org.delef.dialog.RemoveProgramDialogCreator;
import org.delef.model.Config;

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
    private static final AddProgramDialogCreator addProgramDialogCreator = new AddProgramDialogCreator();
    private static final RemoveProgramDialogCreator removeProgramDialogCreator = new RemoveProgramDialogCreator();
    private static AddTimeDialogCreator addTimeDialogCreator;

    public BaseForm() {
        panel_main.setMaximumSize(Main.getMainForm().getMaximumSize());

        //insert all text data
        addTimeDialogCreator = new AddTimeDialogCreator(Main.getTimer());
        Main.getTimer().getOnTimeChangedListener().add((t) -> {

            //set the remaining time as text
            String onTimeOutText =
                    "Next bank refill in "
                            + (int)((
                            Config.getConfig().getNextTimeBankRefillOn()
                                    - System.currentTimeMillis()
                    ) / 86400000)
                            + " days";
            label_time.setText(t.equals(Main.getZERO_TIME()) ? onTimeOutText : t.toString());
        });
        refreshTrackedPrograms();

        //button listeners
        button_addProgram.addActionListener(
                (e) -> addProgramDialogCreator.show(this::refreshTrackedPrograms)
        );
        button_remvoeProgram.addActionListener(
                (e) -> removeProgramDialogCreator.show(this::refreshTrackedPrograms)
        );
        button_addTime.addActionListener(
                (e) -> addTimeDialogCreator.show(
                        ()-> label_time.setText(Config.getConfig().getTimeBank())
                )
        );
    }

    private void refreshTrackedPrograms() {
        textArea_selectedApps.setText("...");

        StringBuilder stringBuilder = new StringBuilder();
        for (String programName : Config.getConfig().getSelectedPrograms()) {
            stringBuilder.append("-").append(programName).append("\n");
        }
        textArea_selectedApps.setText(stringBuilder.toString());
    }
}
