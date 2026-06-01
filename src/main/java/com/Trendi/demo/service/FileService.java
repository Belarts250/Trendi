package com.Trendi.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {
    private final Path uploadDir;

    public FileService(@Value("${file.upload-dir}") String uploadDirPath){
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();

        try{
            Files.createDirectories(this.uploadDir);
        }catch(IOException e){
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String saveFile(MultipartFile file){
        if(file.isEmpty()){
            return null;
        }

        String originalFilename  = file.getOriginalFilename();

        String extension = "";

        if(originalFilename != null && originalFilename.contains(".")){
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFilename = UUID.randomUUID().toString() + extension;

        try {
            Path targetPath = uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "uploads/" + uniqueFilename;
        } catch (IOException e){
            throw new RuntimeException("Failed to save file:"+  originalFilename, e);
        }
    }

    public  Path loadFile(String filename){
        return uploadDir.resolve(filename).normalize();
    }


}
