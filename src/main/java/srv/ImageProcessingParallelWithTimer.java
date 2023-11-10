package srv;

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
    private int pixelSize = 100;
    private BufferedImage originalImage;

    private JLabel label;
    private int numThreads = 2;// this one is ac
    private int regionWidth;
    private int regionHeight;
    private int currentRegionX = 0;
    private int currentRegionY = 0;

    public ImageProcessingParallelWithTimer() {
        super("Parallel Pixelation with Timer Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
            originalImage = ImageIO.read(new File("src/main/resources/city.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        label = new JLabel(new ImageIcon(originalImage));
        JScrollPane scrollPane = new JScrollPane(label);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JButton startButton = new JButton("Start Parallel Pixelation");
        startButton.addActionListener(e -> startParallelPixelation());
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


    private void startParallelPixelation() {

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(numThreads*2);

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        regionWidth = width / numThreads;
        regionHeight = height / numThreads;

        for (int i = 0; i < numThreads; i++) {
            int startX = i * regionWidth;
            int endX = (i + 1) * regionWidth;

            for (int j = 0; j < numThreads; j++) {
                int startY = j * regionHeight;
                int endY = (j + 1) * regionHeight;

                executorService.schedule(() -> pixelateRegion(startX, startY, endX, endY), 9, TimeUnit.MILLISECONDS);
            }
        }
//        BufferedImage pixelatedImage = copyImage(originalImage);
//
//
//        executorService.schedule(() -> {
//            executorService.shutdown();
//            try {
//                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//                savePixelatedImage(pixelatedImage);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }, numThreads * numThreads * 10, TimeUnit.MILLISECONDS);

    }

    private void pixelateRegion(int startX, int startY, int endX, int endY) {
        Timer timer = new Timer(200, new ActionListener() {
            private int currentX = startX;
            private int currentY = startY;

            @Override
            public void actionPerformed(ActionEvent e) {
                int rgb = originalImage.getRGB(currentX, currentY);

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


    private void savePixelatedImage(BufferedImage imageToSave) {
        try {
            File output = new File("src/main/resources/result.jpg");
            ImageIO.write(imageToSave, "jpg", output);
            System.out.println("Pixelated image saved to: " + output.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageProcessingParallelWithTimer().setVisible(true));
    }
}

