package com.iplan.watermark;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by user on 4/3/2018.
 */

public class Watermark {

    public static BufferedImage resize(BufferedImage img, int height, int width) {
        // getScaledInstance : Creates a scaled version of this image
        // SCALE_SMOOTH : Choose an image-scaling algorithm that gives higher priority to image smoothness than scaling speed
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        // BufferedImage : describes an Image with an accessible buffer of image data
        // TYPE_INT_ARGB : Represents an image with 8-bit RGBA color components packed into integer pixels
        // In case of PNG images we set the image type to BufferedImage.TYPE_INT_ARGB
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // createGraphics : Creates a Graphics2D, which can be used to draw into this BufferedImage.
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }


    public static void addTextWatermark(String watermarkText, String imagePath) throws IOException {

        int HEIGHT=500 , WIDTH=800;
        int watermarkMaxHeightPercentage = 20; // 10% of the original image height
        int watermarkMaxWidthPercentage = 90; // 25% of the original image height
        String CwatermarkText = "\u00a9 " + watermarkText;

        File input = new File(imagePath);
        BufferedImage image = ImageIO.read(input);

        // resize BufferedImage
        BufferedImage resizedImage = resize(image, HEIGHT, WIDTH);

        ////////////////////////////////////////???/////////////////////////////////////

        BufferedImage watermarked = new BufferedImage(resizedImage.getWidth(), resizedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // initializes necessary graphic properties
        Graphics2D w = (Graphics2D) watermarked.getGraphics();
        w.drawImage(resizedImage, 0, 0, null);
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        w.setComposite(alphaChannel);
        w.setColor(Color.black);

        int fontSize = 1; // variable font size
        double watermarkMaxHeight = (resizedImage.getHeight() * watermarkMaxHeightPercentage) / 100;
        double watermarkMaxWidth = (resizedImage.getWidth() * watermarkMaxWidthPercentage) / 100;

        w.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        FontMetrics fontMetrics = w.getFontMetrics();
        Rectangle2D rect = fontMetrics.getStringBounds(CwatermarkText, w);

        while((rect.getWidth() <= watermarkMaxWidth) && (rect.getHeight() <= watermarkMaxHeight)){
            fontSize++;
            w.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
            fontMetrics = w.getFontMetrics();
            rect = fontMetrics.getStringBounds(CwatermarkText, w);
        }

        // add text overlay to the image
        w.drawString(CwatermarkText, 0,  resizedImage.getHeight() - (int) rect.getHeight());
        ImageIO.write(watermarked, "png", input);
        w.dispose();
    }
}
