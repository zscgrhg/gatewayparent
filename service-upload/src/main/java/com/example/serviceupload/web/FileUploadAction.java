package com.example.serviceupload.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class FileUploadAction {

   private static final AtomicInteger atomicInteger=new AtomicInteger(1);

    @PostMapping("/upload")
    @ResponseBody
    public String uploadFile(
            @RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            //Files.copy(file.getInputStream(), Paths.get(atomicInteger.getAndIncrement()+"_"+file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
            file.transferTo(Paths.get("D:\\github\\gatewayparent\\service-upload\\" + atomicInteger.getAndIncrement() + "_" + file.getOriginalFilename()).toFile());
            return "ok";
        }
        return "empty";
    }


}
