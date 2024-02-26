package org.delef.dialog;

import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import java.awt.*;
import java.nio.file.Path;
import javax.swing.*;
import org.delef.Main;
import org.delef.model.Config;

public class AddProgramDialogCreator implements DialogSetUp {

    public void show(Runnable afterConfirm) {

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
        panel.setBounds(
                0,
                0,
                dialogWindowSize.width,
                dialogWindowSize.height
        );

        //buttons container
        JPanel panelButtons = new JPanel();

        //process entry text
        JTextField addProgramTextField = new JTextField("Process name");

        //confirmation button
        JButton addProgramConfirmButton = new JButton("Confirm");
        addProgramConfirmButton.addActionListener((e1) -> {
            Config.getConfig().getSelectedPrograms().add(addProgramTextField.getText());
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
                .map(DesktopWindow::getFilePath)
                .map((p) -> Path.of(p).getFileName().toString())
                .map((n) -> n.split("\\.")[0])
                .forEach((t) -> stringBuilder.append("- ").append(t).append("\n"));
        processListText.setText(stringBuilder.toString());

        //adding components to panel
        panelButtons.add(addProgramTextField);
        panelButtons.add(addProgramConfirmButton);
        panel.add(panelButtons);
        panel.add(processListText);

        addProgramDialog.add(panel);
        addProgramDialog.pack();
        addProgramDialog.setVisible(true);
    }
}
