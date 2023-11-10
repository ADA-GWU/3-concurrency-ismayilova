package srv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Timer;

public class PixelationApp extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage pixelatedImage;
    private int pixelSize = 10; // Adjust this value for different levels of pixelation
    private int currentPixelSize = 100;
    private Timer timer;

    public PixelationApp() {
        setTitle("Pixelation App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load the original image
        try {
            originalImage = ImageIO.read(new File("src/main/resources/monalisa.jpg")); // Replace with your image URL
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create a timer for step-by-step pixelation
        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPixelSize <= pixelSize) {
                    pixelateImage();
                    repaint();
                    currentPixelSize++;
                } else {
                    timer.stop();
                }
            }
        });

        JButton startButton = new JButton("Start Pixelation");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPixelSize = 1;
                timer.start();
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(startButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JLabel(new ImageIcon(originalImage)), BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void changeColor(int x, int y ){
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int rgb = originalImage.getRGB(x, y);
        for (int i = 0; i < currentPixelSize; i++) {
            for (int j = 0; j < currentPixelSize; j++) {
                if (x + i < width && y + j < height) {
                    originalImage.setRGB(x + i, y + j, rgb);

                }
            }
        }
//        pixelatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);



    }
    private void pixelateImage() {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        pixelatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y += currentPixelSize) {
            for (int x = 0; x < width; x += currentPixelSize) {
                System.out.println(" x = "+x +  " y =  "+ y );
                int rgb = originalImage.getRGB(x, y);
                for (int i = 0; i < currentPixelSize; i++) {
                    for (int j = 0; j < currentPixelSize; j++) {
                        if (x + i < width && y + j < height) {
                            pixelatedImage.setRGB(x + i, y + j, rgb);

                        }
                    }
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (pixelatedImage != null) {
            g.drawImage(pixelatedImage, originalImage.getWidth() + 10, 0, null);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PixelationApp().setVisible(true);
            }
        });
    }
}

