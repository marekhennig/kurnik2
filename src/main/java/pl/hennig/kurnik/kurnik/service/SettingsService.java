package pl.hennig.kurnik.kurnik.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service
public class SettingsService {

    private Image avatar(String username) {
        try {
            BufferedImage bImage = ImageIO.read(new File("src\\main\\resources\\avatars\\" + username + ".jpg"));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "jpg", bos);
            byte[] data = bos.toByteArray();
            StreamResource resource = new StreamResource("fakeImageName.jpg", () -> new ByteArrayInputStream(data));
            Image image = new Image(resource, "alternative image text");
            return image;
        } catch (IOException e) {
            RestTemplate restTemplate = new RestTemplate();
            JsonNode jsonNode = restTemplate.getForObject("https://random.dog/woof.json", JsonNode.class);
            Image image = new Image(jsonNode.get("url").asText(), "alternative image text");
            image.setWidth("200px");
            image.setHeight("200px");
            return image;
        }

    }

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
