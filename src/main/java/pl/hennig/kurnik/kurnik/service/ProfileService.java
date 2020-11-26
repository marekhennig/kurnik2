package pl.hennig.kurnik.kurnik.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service
public class ProfileService {

    public Image avatar(String username) {
        try {
            BufferedImage bImage = ImageIO.read(new File("registration-vaadin-master\\src\\main\\resources\\avatars\\" + username + ".jpg"));
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
}
