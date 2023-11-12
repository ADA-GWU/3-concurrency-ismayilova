package srv;

import util.ImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ImageProcessingParallelWithTimer extends JFrame {
    private int pixelSize = 100; // Adjust this value for different levels of pixelation
    private String path ;
    private BufferedImage originalImage;

    private JLabel label;
    private int numThreads = 4;// this one is ac
    private int regionWidth;
    private int regionHeight;
    private int currentRegionX = 0;
    private int currentRegionY = 0;

    public ImageProcessingParallelWithTimer(String name, int pixelSize) {
        super("Multi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        this.pixelSize = pixelSize;
        this.path  = String.format("src/main/resources/%s", name);
        try {
            originalImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        label = new JLabel(new ImageIcon(originalImage));
        JScrollPane scrollPane = new JScrollPane(label);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> startParallelPixelation());
        JButton saveButton = new JButton("Save Result");
        saveButton.addActionListener(e -> ImageUtils.savePixelatedImage(originalImage));

        JPanel controlPanel = new JPanel();
        controlPanel.add(startButton);
        controlPanel.add(saveButton);

        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }


    private void startParallelPixelation() {

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(numThreads);

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int divisor = 2;// numThreads/2;
        regionWidth = width / divisor;
        regionHeight = height / divisor;

        for (int i = 0; i < divisor; i++) {
            int startX = i * regionWidth;
            int endX = (i + 1) * regionWidth;

            for (int j = 0; j < divisor; j++) {
                int startY = j * regionHeight;
                int endY = (j + 1) * regionHeight;

                executorService.schedule(() -> pixelateRegion(startX, startY, endX, endY), 9, TimeUnit.MILLISECONDS);
            }
        }
//        BufferedImage pixelatedImage = copyImage(originalImage);
//
//
//        executorService.schedule(() -> {
            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


    }

    private void pixelateRegion(int startX, int startY, int endX, int endY) {
        Timer timer = new Timer(400, new ActionListener() {
            private int currentX = startX;
            private int currentY = startY;

            @Override
            public void actionPerformed(ActionEvent e) {
//                int rgb = originalImage.getRGB(currentX, currentY);
                int rgb = ImageUtils.calcAvrRGB( originalImage,currentX , currentY , pixelSize);
                for (int i = 0; i < pixelSize; i++) {
                    for (int j = 0; j < pixelSize; j++) {
                        int x = currentX + i;
                        int y = currentY + j;

                        if (x < endX && y < endY) {
                            originalImage.setRGB(x, y, rgb);
                        }
                    }
                }

                currentX += pixelSize;

                if (currentX >= endX) {
                    currentX = startX;
                    currentY += pixelSize;

                    if (currentY >= endY) {
                        ((Timer) e.getSource()).stop();
                        SwingUtilities.invokeLater(() -> label.repaint());
                    }
                }
            }
        });

        timer.start();
    }



    private BufferedImage copyImage(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        BufferedImage copy = new BufferedImage(width, height, original.getType());
        Graphics g = copy.getGraphics();
        g.drawImage(original, 0, 0, null);
        g.dispose();
        return copy;
    }
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new ImageProcessingParallelWithTimer("city.jpg",100).setVisible(true));
//    }
}

