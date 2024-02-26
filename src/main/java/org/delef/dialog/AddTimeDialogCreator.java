package org.delef.dialog;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.util.Arrays;
import javax.swing.*;
import lombok.AllArgsConstructor;
import org.delef.Main;
import org.delef.model.Config;
import org.delef.util.TimeManager;
import org.delef.util.Toast;

@AllArgsConstructor
public class AddTimeDialogCreator implements DialogSetUp {
    private TimeManager timeManager;

    public void show(Runnable afterConfirm) {
        //dialog
        JDialog addTimeDialog = new JDialog(Main.getMainForm(), "Add time");
        Dimension dialogWindowSize = new Dimension(200, 100);
        addTimeDialog.setMinimumSize(dialogWindowSize);
        addTimeDialog.setMaximumSize(dialogWindowSize);
        JFrame mainFrame = Main.getMainForm();
        addTimeDialog.setLocation(
                new Point(
                        mainFrame.getLocation().x + mainFrame.getSize().width / 2,
                        mainFrame.getLocation().y + mainFrame.getSize().height / 2
                )
        );

        //panel container
        JPanel panel = new JPanel();

        //minutes to add entry text
        JTextField addMinutesTextField = new JTextField("15");
        addMinutesTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                addMinutesTextField.setEditable(
                        ke.getKeyChar() >= '0' &&
                                ke.getKeyChar() <= '9' ||
                                ke.getKeyChar() == (char) 8
                );
            }
        });

        //password text
        JPasswordField passwordTextField = new JPasswordField();

        //confirmation button
        JButton addProgramConfirmButton = new JButton("Confirm");
        addProgramConfirmButton.addActionListener((e1) -> {
            int toastPosX = addTimeDialog.getX() + dialogWindowSize.width / 2;
            int toastPosY = addTimeDialog.getY() + dialogWindowSize.height / 2;

            //check if the password is correct
            if (Arrays.equals(
                    passwordTextField.getPassword(),
                    Config.getConfig().getPassword().toCharArray()
            )) {
                //add time to bank
                LocalTime timeLeft = LocalTime.parse(Config.getConfig().getTimeBank());
                timeLeft = timeLeft.plusMinutes(Integer.parseInt(addMinutesTextField.getText()));
                timeManager.setTime(timeLeft);
                afterConfirm.run();
                addTimeDialog.dispose();
                return;
            }

            //throw a toast that the password is incorrect
            new Toast(
                    "Wrong password",
                    toastPosX,
                    toastPosY
            ).showToast();
        });

        //adding components to panel
        panel.add(addMinutesTextField);
        panel.add(passwordTextField);
        panel.add(addProgramConfirmButton);

        addTimeDialog.add(panel);
        addTimeDialog.pack();
        addTimeDialog.setVisible(true);
    }
}
