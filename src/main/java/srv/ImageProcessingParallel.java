package srv;



import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.javatuples.Pair;
import util.ImageUtils;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ImageProcessingParallel extends JFrame {
    private int pixelSize = 50; // Adjust this value for different levels of pixelation
    private BufferedImage originalImage;
    private String path;
    private JLabel label;
    private int numThreads = Runtime.getRuntime().availableProcessors(); // Adjust the number of threads based on your requirements

    public ImageProcessingParallel(String name , int pixelSize) {
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
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
//        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(numThreads);
        int divisor = 2;
        int widthOfRegion = originalImage.getWidth()/divisor;
        int heightOfRegion = originalImage.getHeight()/divisor;
        List<Pair<Integer, Integer>> regions = getImagesRegions(divisor);

        for (Pair<Integer, Integer> region : regions) {
            int startX = region.getValue0();
            int startY = region.getValue1();
            int endX =startX+widthOfRegion;
            int endY = startY + heightOfRegion;

            executorService.submit(() -> pixelateRegion(startX, startY, endX, endY));
        }

        executorService.shutdown();

        try {
            // Wait for all tasks to complete or timeout after a specified time
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                // Timeout occurred (optional: you can take additional action here)
                System.out.println("Some tasks did not complete within the specified timeout.");
            }
        } catch (InterruptedException e) {
            // Handle interruption (optional: you can take additional action here)
            e.printStackTrace();
        }
    }



    private List<Pair<Integer, Integer>> getImagesRegions(int divisor) {
        List<Pair<Integer, Integer>> regions = new ArrayList<>();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        Runtime.getRuntime().availableProcessors();
        int subImageWidth = width / divisor;
        int subImageHeight = height / divisor;

        // Generate starting positions for each sub-image
        for (int i = 0; i < divisor; i++) {
            for (int j = 0; j < divisor; j++) {
                int startX = i * subImageWidth;
                int startY = j * subImageHeight;
                regions.add(new Pair<>(startX, startY));
            }
        }

        return regions;
    }

//    private void pixelateRegion(int startX, int startY) {
//        int endX = startX + originalImage.getWidth() / 2;
//        int endY = startY + originalImage.getHeight() / 2;
//
//        for (int y = startY; y < endY; y += pixelSize) {
//            for (int x = startX; x < endX; x += pixelSize) {
//                int rgb = originalImage.getRGB(x, y);
//                for (int i = 0; i < pixelSize; i++) {
//                    for (int j = 0; j < pixelSize; j++) {
//                        if (x + i < endX && y + j < endY) {
//                            originalImage.setRGB(x + i, y + j, rgb);
//                        }
//                    }
//                }
//            }
//        }
//
//        SwingUtilities.invokeLater(() -> label.repaint()); // Repaint the label after pixelating the region
//    }

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



//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new ImageProcessingParallel("monalisa.jpg", 10).setVisible(true));
//    }
}