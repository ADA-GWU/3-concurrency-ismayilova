
//import main.java.util.ThreadColors;

import srv.ImageProcessing;
import srv.ImageProcessingParallelWithTimer;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Check if there are exactly three command-line arguments
        if (args.length != 3) {
            System.out.println("Please provide three input parameters.");
            return;
        }

        // Retrieve and print the three input parameters
        String param1 = args[0];
        int param2 = Integer.parseInt(args[1]);
        char param3 = args[2].charAt(0);  // Assuming the third parameter is a single character

        switch (param3) {
            case 'S':
                SwingUtilities.invokeLater(() -> new ImageProcessing(param1,param2).setVisible(true));

                System.out.println("Pattern matched: 'S'");

                break;
            case 'M':
                SwingUtilities.invokeLater(() -> new ImageProcessingParallelWithTimer(param1,param2).setVisible(true));

                System.out.println("Pattern matched: 'M'");
                break;
            default:
                System.out.println("No pattern matched");
        }


        // You can perform further processing with these parameters here
    }

}
