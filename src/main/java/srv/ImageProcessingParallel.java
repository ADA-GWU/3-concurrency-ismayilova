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
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ImageProcessingParallel extends JFrame {
    private int pixelSize = 50; // Adjust this value for different levels of pixelation
    private BufferedImage originalImage;
    private JLabel label;
    private int numThreads = 4; // Adjust the number of threads based on your requirements

    public ImageProcessingParallel() {
        super("Parallel Pixelation Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        try {
            originalImage = ImageIO.read(new File("src/main/resources/HA.jpeg")); // Replace with your image path
        } catch (IOException e) {
            e.printStackTrace();
        }

        label = new JLabel(new ImageIcon(originalImage));

        JButton startButton = new JButton("Start Parallel Pixelation");
        startButton.addActionListener(e -> startSequentialPixelation());

        JPanel controlPanel = new JPanel();
        controlPanel.add(startButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(label, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void startParallelPixelation() {
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Pair<Integer, Integer>> regions = getImagesRegions();

        for (Pair<Integer, Integer> region : regions) {
            int startX = region.getValue0();
            int startY = region.getValue1();

            executorService.submit(() -> pixelateRegion(startX, startY));
        }

        executorService.shutdown();
    }


    private void startSequentialPixelation() {
        Timer sequentialTimer = new Timer(50, new ActionListener() {
            private int currentRegion = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentRegion < numThreads) {
                    Pair<Integer, Integer> region = getImagesRegions().get(currentRegion);
                    int startX = region.getValue0();
                    int startY = region.getValue1();

                    pixelateRegion(startX, startY);

                    currentRegion++;
                    repaint();
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });

        sequentialTimer.start();
    }



    private List<Pair<Integer, Integer>> getImagesRegions() {
        List<Pair<Integer, Integer>> regions = new ArrayList<>();
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        int subImageWidth = width / 2;
        int subImageHeight = height / 2;

        // Generate starting positions for each sub-image
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int startX = i * subImageWidth;
                int startY = j * subImageHeight;
                regions.add(new Pair<>(startX, startY));
            }
        }

        return regions;
    }

    private void pixelateRegion(int startX, int startY) {
        int endX = startX + originalImage.getWidth() / 2;
        int endY = startY + originalImage.getHeight() / 2;

        for (int y = startY; y < endY; y += pixelSize) {
            for (int x = startX; x < endX; x += pixelSize) {
                int rgb = originalImage.getRGB(x, y);
                for (int i = 0; i < pixelSize; i++) {
                    for (int j = 0; j < pixelSize; j++) {
                        if (x + i < endX && y + j < endY) {
                            originalImage.setRGB(x + i, y + j, rgb);
                        }
                    }
                }
            }
        }

        SwingUtilities.invokeLater(() -> label.repaint()); // Repaint the label after pixelating the region
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageProcessingParallel().setVisible(true));
    }
}