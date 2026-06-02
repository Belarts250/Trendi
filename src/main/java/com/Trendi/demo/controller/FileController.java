package com.Trendi.demo.controller;

import com.Trendi.demo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    public ResponseEntity<Resource> getFile(@PathVariable String filename){
        try {
            Path filepath = fileService.loadFile(filename);

            Resource resource = new UrlResource(filepath.toUri());

            if(resource.exists() && resource.isReadable()){
                return ResponseEntity.ok()
                        .contentType(MediaType.ALL)
                        .body(resource);
            }else{
                return ResponseEntity.notFound().build();
            }
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

}
