package org.delef.dialog;

import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.delef.Main;
import org.delef.model.Config;
import org.delef.model.TrackedProgram;
import org.delef.util.ProcessManager;

public class AddProgramDialogCreator implements DialogSetUp {

    public void show(Runnable afterConfirm) {
        List<DesktopWindow> openedWindows = new ArrayList<>();

        //dialog
        JDialog addProgramDialog = new JDialog(Main.getMainForm(), "Add app");
        Dimension dialogWindowSize = new Dimension(200, 100);
        addProgramDialog.setMinimumSize(dialogWindowSize);
        addProgramDialog.setMaximumSize(dialogWindowSize);
        JFrame mainFrame = Main.getMainForm();
        addProgramDialog.setLocation(
                new Point(
                        mainFrame.getLocation().x + mainFrame.getSize().width / 2,
                        mainFrame.getLocation().y + mainFrame.getSize().height / 2
                )
        );

        //panel container
        JPanel panel = new JPanel();
        panel.setMaximumSize(dialogWindowSize);
        panel.setBounds(
                0,
                0,
                dialogWindowSize.width,
                dialogWindowSize.height
        );

        //buttons container
        JPanel panelButtons = new JPanel();

        //process entry text
        JTextField addProgramTextField = new JTextField("Id");
        addProgramTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                addProgramTextField.setEditable(
                        ke.getKeyChar() >= '0' &&
                                ke.getKeyChar() <= '9' ||
                                ke.getKeyChar() == (char) 8
                );
            }
        });

        //track by title combo box
        JComboBox<TrackedProgram.TrackType> trackType = new JComboBox<>(
                new TrackedProgram.TrackType[]{
                        TrackedProgram.TrackType.ByProgramName,
                        TrackedProgram.TrackType.ByTitle
                }
        );

        //confirmation button
        JButton addProgramConfirmButton = new JButton("Confirm");
        addProgramConfirmButton.addActionListener((e1) -> {
            DesktopWindow dw =  openedWindows.get(Integer.parseInt(addProgramTextField.getText()));
            boolean byTitle = trackType.getSelectedItem() == TrackedProgram.TrackType.ByTitle;
            String name = byTitle ?
                    dw.getTitle():
                    ProcessManager.getNameFromPath(dw.getFilePath());
            if (name.contains("*")) {
                name = name.split("\\*")[0] + "*";
            }
            Config.getConfig().getSelectedPrograms()
                    .add(
                            new TrackedProgram(
                                    name,
                                    byTitle
                            )
                    );
            Config.Save();
            afterConfirm.run();
            addProgramDialog.dispose();
        });

        JTextArea processListText = new JTextArea();
        processListText.setEnabled(false);
        StringBuilder stringBuilder = new StringBuilder();
        WindowUtils.getAllWindows(true)
                .stream()
                .filter((dw) -> !dw.getTitle().isEmpty())
                .forEach(openedWindows::add);

        openedWindows
                .forEach((dw) -> stringBuilder
                        .append(openedWindows.indexOf(dw))
                        .append(" | ")
                        .append(ProcessManager.getNameFromPath(dw.getFilePath()))
                        .append(" | ")
                        .append(dw.getTitle())
                        .append("\n"));
        processListText.setText(stringBuilder.toString());

        //adding components to panel
        panelButtons.add(addProgramTextField);
        panelButtons.add(addProgramConfirmButton);
        panelButtons.add(trackType);
        panel.add(panelButtons);
        panel.add(processListText);

        addProgramDialog.add(panel);
        addProgramDialog.pack();
        addProgramDialog.setVisible(true);
    }
}
