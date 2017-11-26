package com.example.serviceupload.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
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
            file.transferTo(Paths.get(atomicInteger.getAndIncrement()+"_"+file.getOriginalFilename()).toFile());
            return "ok";
        }
        return "empty";
    }


}
