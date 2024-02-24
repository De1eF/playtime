import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import org.example.BaseForm;
import org.example.exception.GlobalExceptionHandler;

public class Main {
    public static void main(String[] args) {
        //ExceptionHandler
        //GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        //Thread.setDefaultUncaughtExceptionHandler(globalExceptionHandler);

        //FlatLaf
        FlatDarkLaf.setup();

        //Base Form setup
        JFrame frame = new JFrame("Playtime");
        frame.setContentPane(new BaseForm().getPanel_main());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
