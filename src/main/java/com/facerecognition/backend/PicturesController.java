package com.facerecognition.backend;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class PicturesController {

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public  Map<String,String>  receivePhoto(@RequestParam MultipartFile file){

        Map<String,String> response=new HashMap<>();
        try {
            response= PictureService.processPicture(file);
        }
        catch (IOException e){
            System.out.println(e);
            response.put("message","Произошул внцтренний сбой сервера. Пожалуйста, попробуйте загрузить фото еще раз");
        }
        return response;
    }

}
