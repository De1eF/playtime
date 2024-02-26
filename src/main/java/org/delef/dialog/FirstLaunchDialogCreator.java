package org.delef.dialog;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.util.Objects;
import javax.swing.*;
import org.delef.Main;
import org.delef.model.Config;
import org.delef.util.Toast;

public class FirstLaunchDialogCreator implements DialogSetUp {
    public void show(Runnable afterConfirm)  {
        //dialog
        JDialog addProgramDialog = new JDialog(Main.getMainForm(), "Configuration");
        Dimension dialogWindowSize = new Dimension(250, 150);
        addProgramDialog.setMinimumSize(dialogWindowSize);
        addProgramDialog.setMaximumSize(dialogWindowSize);
        JFrame mainFrame = Main.getMainForm();
        addProgramDialog.setLocation(
                new Point(
                        mainFrame.getLocation().x + mainFrame.getSize().width / 2,
                        mainFrame.getLocation().y + mainFrame.getSize().height / 2
                )
        );

        //close program if not configured
        addProgramDialog.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        //panel container
        JPanel panel = new JPanel();
        panel.setBounds(
                0,
                0,
                dialogWindowSize.width,
                dialogWindowSize.height);

        //refill frequency entry text
        JComboBox<Config.RefillFrequency> refillFrequencyComboBox = new JComboBox<>(
                new Config.RefillFrequency[]{
                        Config.RefillFrequency.Daily,
                        Config.RefillFrequency.Weekly
                }
        );

        //password entry text
        JLabel addPasswordLabel = new JLabel("Password:");
        JTextField addPasswordTextField = new JTextField("Password here");

        //refill time entry text
        JLabel refillTimeLabel = new JLabel("Hours per period");
        JTextField refillTimeTextField = new JTextField("1");
        refillTimeTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                refillTimeTextField.setEditable(
                        ke.getKeyChar() >= '0' &&
                        ke.getKeyChar() <= '9' ||
                                ke.getKeyChar() == (char)8
                );
            }
        });

        //confirmation button
        JButton addProgramConfirmButton = new JButton("Confirm");
        addProgramConfirmButton.addActionListener((e1) -> {
            if (Objects.equals(addPasswordTextField.getText(), "Password here") ||
                    addPasswordTextField.getText().isEmpty()
            ) {
                new Toast("Please, set a password").showToast();
                return;
            }
            if (addPasswordTextField.getText().length() < 4) {
                new Toast("Password must be at least 4 symbols").showToast();
                return;
            }
            if (refillTimeTextField.getText().length() < 1) {
                new Toast("Please, set time bank refill amount").showToast();
                return;
            }

            Config.getConfig().setPassword(addPasswordTextField.getText());
            Config.getConfig().setRefillFrequency(
                    (Config.RefillFrequency)refillFrequencyComboBox.getSelectedItem());
            Config.getConfig().setRefillTime(
                    LocalTime.of(Integer.parseInt(refillTimeTextField.getText()), 0, 0).toString()
            );
            Config.Save();
            afterConfirm.run();
            addProgramDialog.dispose();
        });

        //adding components to panel
        panel.add(refillFrequencyComboBox);
        panel.add(refillTimeLabel);
        panel.add(refillTimeTextField);
        panel.add(addPasswordLabel);
        panel.add(addPasswordTextField);
        panel.add(addProgramConfirmButton);

        addProgramDialog.add(panel);
        addProgramDialog.pack();
        addProgramDialog.setVisible(true);
    }
}
