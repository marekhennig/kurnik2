package pl.hennig.kurnik.kurnik.service;

import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@Service
public class SettingsService {
    public void setAvatar(MemoryBuffer buffer, String username) {

        try {
            File file = new File("registration-vaadin-master\\src\\main\\resources\\avatars", File.separator + username + ".jpg");

            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedImage image = ImageIO.read(buffer.getInputStream());
            if (image.getHeight() < image.getWidth()) {
                int to_crop = image.getWidth() - image.getHeight();
                image = image.getSubimage(to_crop, 0, image.getWidth() - to_crop, image.getHeight());
            }

            BufferedImage outputImage = new BufferedImage(200, 200, image.getType());
            Graphics2D graphics2D = outputImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.drawImage(image, 0, 0, 200, 200, null);
            graphics2D.dispose();

            ImageIO.write(outputImage, "jpg", file);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
