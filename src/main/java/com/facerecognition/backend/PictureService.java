package com.facerecognition.backend;

import com.facerecognition.deepLearning.FaceRecognizer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Service
public class PictureService {

    public static Map<String,String> processPicture(MultipartFile multipartFile) throws IOException {

        File file = new File("src/main/java/com/facerecognition/pictures"+multipartFile.getOriginalFilename());

        InputStream initialStream = multipartFile.getInputStream();
        byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);

        try (OutputStream outStream = new FileOutputStream(file)) {
            outStream.write(buffer);
        }
        String filepath=file.getPath();
        String message=FaceRecognizer.detectFaces(filepath);

        BufferedImage bImage = ImageIO.read(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos );
        byte [] result = bos.toByteArray();
        String resultEncoded = Base64.getEncoder().encodeToString(result);

        file.delete();

        Map<String,String> responce=new HashMap<>();
        responce.put("message",message);
        responce.put("picture",resultEncoded);

        return responce;
        }

}


