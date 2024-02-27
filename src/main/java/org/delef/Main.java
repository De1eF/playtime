package org.delef;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.*;
import lombok.Getter;
import org.delef.dialog.ExitDialogCreator;
import org.delef.dialog.FirstLaunchDialogCreator;
import org.delef.model.Config;
import org.delef.model.TimeTrackListener;
import org.delef.model.TrackedProgram;
import org.delef.util.ProcessManager;
import org.delef.util.TimeManager;

public class Main {
    @Getter
    private static JFrame mainForm;
    private static TrayIcon trayIcon;
    @Getter
    private static TimeManager timer;
    private static final SystemTray tray = SystemTray.getSystemTray();
    private static final ExitDialogCreator exitDialogCreator = new ExitDialogCreator();
    private static final FirstLaunchDialogCreator configurationDialogCreator = new FirstLaunchDialogCreator();
    @Getter
    private static final LocalTime ZERO_TIME = LocalTime.of(0, 0, 0);

    public static void main(String[] args) {
        //FlatLaf
        FlatDarkLaf.setup();

        //Set up logic
        Config.Load();
        updateTimeBank();
        setUpTimeTracker();

        try {
            //retrieving images from resources
            Image icon = ImageIO.read(
                    Objects.requireNonNull(
                            Main.class.getClassLoader().getResource("icon.png")
                    )
            );

            //Only start base frame after configuration is complete
            if (Config.getConfig().getPassword().isEmpty()) {
                configurationDialogCreator.show(() -> {
                    Config.getConfig().setTimeBank(Config.getConfig().getRefillTime());
                    Config.Save();
                    createMainForm(icon);
                });
            } else {
                createMainForm(icon);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //On shutdown hook
        Thread onShutdownThread = new Thread(Config::Save);
        Runtime.getRuntime().addShutdownHook(onShutdownThread);
    }

    private static void setUpSystemTray(Image icon) {
        //setting up system tray icon
        trayIcon = creatTrayIcon(
                icon,
                //when open from tray
                (oE) -> {
                    terminateTrayIcon();
                    mainForm.setVisible(true);
                },
                //when close from tray
                (eE) -> exitDialogCreator.show(() -> {
                })
        );
        trayIcon.setImageAutoSize(true);

    }

    private static void setUpWindowCloseBehaviour() {
        //setting up on close behaviour
        mainForm.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    assert trayIcon != null;
                    tray.add(trayIcon);
                    mainForm.setVisible(false);
                } catch (AWTException ex) {
                    System.out.println("unable to add to tray");
                }
            }
        });
    }

    private static void createMainForm(Image icon) {
        try {
            //Base Form setup
            mainForm = new JFrame("Playtime");
            mainForm.setContentPane(new BaseForm().getPanel_main());
            mainForm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            Dimension windowSize = new Dimension(800, 350);
            mainForm.setMinimumSize(windowSize);
            mainForm.setMaximumSize(windowSize);
            mainForm.setLocation(new Point(0, 0));
            mainForm.setIconImage(icon);

            //set up minimization to the system tray
            setUpSystemTray(icon);
            setUpWindowCloseBehaviour();

            mainForm.pack();
            mainForm.setVisible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static void setUpTimeTracker() {
        //pull time form time bank
        LocalTime timeRemaining = LocalTime.parse(Config.getConfig().getTimeBank());

        //each second
        TimeTrackListener onTimeChanged = (time) -> {
            boolean timeIsUp = time.equals(ZERO_TIME);

            //close tracked games when time is up
            if (timeIsUp) {
                timer.setEnabled(false);
                for (TrackedProgram program : Config.getConfig().getSelectedPrograms()) {
                    ProcessManager.killGame(
                            program.getName(),
                            program.getTrackByTitle()
                    );
                }
                return;
            }

            //When one of selected programs is running, start counting time down
            Set<Boolean> runningPrograms = new HashSet<>();
            try {
                for (TrackedProgram program : Config.getConfig().getSelectedPrograms()) {
                    runningPrograms.add(
                            ProcessManager.isRunning(
                                    program.getName(),
                                    program.getTrackByTitle()
                            )
                    );
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            if (runningPrograms.contains(Boolean.TRUE)) {
                Config.getConfig().setTimeBank(time.toString());
                timer.setEnabled(true);
                return;
            }

            //when no tracked programs are running, save remaining time to the bank and disable timer
            Config.Save();
            timer.setEnabled(false);
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

    private static void updateTimeBank() {
        //when past refill time
        if (Config.getConfig().getNextTimeBankRefillOn() < System.currentTimeMillis()) {
            Config.getConfig().setNextTimeBankRefillOn(System.currentTimeMillis()
                    + Config.getConfig().getRefillFrequency().millis);
            Config.getConfig().setTimeBank(Config.getConfig().getRefillTime());
        }
    }

    private static void terminateTrayIcon() {
        assert trayIcon != null;
        tray.remove(trayIcon);
    }

    private static TrayIcon creatTrayIcon(Image trayImage,
                                          ActionListener onOpen,
                                          ActionListener onExit
    ) {
        PopupMenu popup = new PopupMenu();

        //tray exit button
        MenuItem popupMenuItem = new MenuItem("Exit");
        popupMenuItem.addActionListener(onExit);
        popup.add(popupMenuItem);

        //tray open button
        popupMenuItem = new MenuItem("Open");
        popupMenuItem.addActionListener(onOpen);
        popup.add(popupMenuItem);

        //setting up tray icon
        return new TrayIcon(trayImage, "", popup);
    }
}
