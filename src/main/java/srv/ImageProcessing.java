package srv;



import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageProcessing extends JFrame {
    private int pixelSize = 100; // Adjust this value for different levels of pixelation
    private String path ;
    private BufferedImage originalImage;
    private JLabel label;
    private int currentX = 0;
    private int currentY = 0;

    public ImageProcessing(String name , int pixelSize) {
        super("Pixelation Demo");
        this.pixelSize = pixelSize;
        this.path  = String.format("src/main/resources/%s", name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLayout(new FlowLayout());
        setLayout(new BorderLayout());


        try {
            originalImage = ImageIO.read(new File(path)); // Replace with your image URL
        } catch (IOException e) {
            e.printStackTrace();
        }

        label = new JLabel(new ImageIcon(originalImage));
        JScrollPane scrollPane = new JScrollPane(label);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);


        JButton startButton = new JButton("Start Pixelation");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startPixelation();
            }
        });
        JButton saveButton = new JButton("Save Result");
        saveButton.addActionListener(e -> savePixelatedImage(originalImage));
        JPanel controlPanel = new JPanel();
        controlPanel.add(startButton);
        controlPanel.add(saveButton);
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

    }

    private void startPixelation() {
        Timer timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pixelateNextRegion();
                repaint();

                if (currentY >= originalImage.getHeight()) {
                    ((Timer) e.getSource()).stop();
                }
            }
        });

        timer.start();
    }

    private void pixelateNextRegion() {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int rgb = originalImage.getRGB(currentX, currentY);

        for (int i = 0; i < pixelSize; i++) {
            for (int j = 0; j < pixelSize; j++) {
                if (currentX + i < width && currentY + j < height) {
                    originalImage.setRGB(currentX + i, currentY + j, rgb);
                }
            }
        }

        currentX += pixelSize;

        if (currentX >= width) {
            currentX = 0;
            currentY += pixelSize;
        }
    }
    private void savePixelatedImage(BufferedImage imageToSave) {
        try {
            File output = new File("src/main/resources/result.jpg");
            ImageIO.write(imageToSave, "jpg", output);
            System.out.println("Pixelated image saved to: " + output.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new ImageProcessing("HA.jpeg",100).setVisible(true);
//            }
//        });
//    }
}
