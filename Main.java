// tonna imports >_<

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Main extends vga13 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Path for the images: ");
        String imagesFolder = sc.nextLine();

        System.out.print("Path to save the files: ");
        String outputsFolder = sc.nextLine();

        System.out.println("Maximum bytes allowed: ");
        int maximumBytes = sc.nextInt();

        if (maximumBytes <= 8192) {
            System.out.println("Defaulted 64000, reason, inputted less than 8192");
            maximumBytes = 64000;
        }

        System.out.print("New width (Set 0 for auto): ");
        int newWidth = sc.nextInt();

        System.out.print("New height (Set 0 for auto): ");
        int newHeight = sc.nextInt();

        File folder = new File(imagesFolder);
        File[] listOfFiles = folder.listFiles();

        if (newWidth == 0 && newHeight == 0) {
            // figure out the most optimal resolution assuming that our limit is going to be
            // 64000 bytes of full memory (for maximum quality)
            int xres = 320, yres = 200;
            boolean best = false;
            while (!best) {
                // mantain the best ratio
                if (xres < 0 || yres < 0) {
                    System.out.println("It is impossible to compress such an amount of files, try reducing the number of frames.");
                    System.exit(1);
                    break;
                }
                if ((xres * yres) * listOfFiles.length > maximumBytes) {
                    xres--;
                    yres--;
                } else {
                    newWidth = xres;
                    newHeight = yres;
                    best = true;
                }
            }
        }



        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                try {
                    BufferedImage originalImage = resizeImage(ImageIO.read(listOfFiles[i]), newWidth, newHeight);
                    byte[] imageData = convertToVgaPalette(originalImage);
                    String outputFileName = outputsFolder + File.separator + (i + 1) + ".txl";
                    writeBinaryFile(imageData, outputFileName);
                    System.out.println("Image converted and saved: " + outputFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        int r = (newWidth * newHeight) * listOfFiles.length;
        System.out.println("Total size in bytes = " + r + "\nApplied resolution = " + newWidth + "x" + newHeight);
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
        Image tmp = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }

    private static byte[] convertToVgaPalette(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] result = new byte[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = new Color(image.getRGB(x, y));
                // Find the closest color in the VGA palette
                int closestIndex = findClosestColorIndex(pixelColor);
                result[y * width + x] = (byte) closestIndex;
            }
        }

        return result;
    }

    private static int findClosestColorIndex(Color color) {
        int closestIndex = 0;
        double closestDistance = Double.MAX_VALUE;

        for (int i = 0; i < vgaPalette.length; i++) {
            double distance = colorDistance(color, vgaPalette[i]);
            if (distance < closestDistance) {
                closestIndex = i;
                closestDistance = distance;
            }
        }

        return closestIndex;
    }

    private static double colorDistance(Color c1, Color c2) {
        double rDiff = c1.getRed() - c2.getRed();
        double gDiff = c1.getGreen() - c2.getGreen();
        double bDiff = c1.getBlue() - c2.getBlue();
        return Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
    }

    private static void writeBinaryFile(byte[] data, String fileName) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName))) {
            bos.write(data);
        }
    }
}
