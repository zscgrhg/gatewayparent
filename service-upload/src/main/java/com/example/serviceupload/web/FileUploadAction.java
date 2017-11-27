package com.example.serviceupload.web;

import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class FileUploadAction {

    private static final AtomicInteger atomicInteger = new AtomicInteger(1);

    @PostMapping("/upload")
    @ResponseBody
    public String uploadFile(
            @RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            //Files.copy(file.getInputStream(), Paths.get(atomicInteger.getAndIncrement()+"_"+file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
            file.transferTo(Paths.get(getClass().getClassLoader().getResource(".")
                    .getPath().substring(1) +
                    atomicInteger.getAndIncrement() +
                    "_" + file.getOriginalFilename()).toFile());
            return "ok";
        }
        return "empty";
    }

    @RequestMapping("image")
    public void image(HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "public, max-age=604800000");
        response.setHeader("Last-Modified",
                "Sun, 26 Nov 2017 05:52:09 GMT");
        FileCopyUtils.copy(getClass().getClassLoader().getResourceAsStream("static\\image\\girls.jpg"), response.getOutputStream());
    }

}
