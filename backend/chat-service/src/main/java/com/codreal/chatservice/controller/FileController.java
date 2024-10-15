package com.codreal.chatservice.controller;

import com.codreal.chatservice.dto.FileResponseDto;
import com.codreal.chatservice.dto.MessageDto;
import com.codreal.chatservice.services.FileCleanupService;
import com.codreal.chatservice.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping ("/file")
public class FileController {

    @Autowired
    FileCleanupService fileCleanupService;
    @Autowired
    MessageService messageService;
    @PostMapping ("/upload")
    public ResponseEntity<FileResponseDto> uploadFile(@RequestParam ("file") MultipartFile file) throws IOException {
        FileResponseDto fileResponseDto = new FileResponseDto();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        Files.createDirectories(fileStorageLocation);
        Path targetLocation = fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // LÊN LỊCH XÓA FILE SAU 10 GIỜ
        fileCleanupService.scheduleFileDeletion(targetLocation);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/").path(fileName)
                .toUriString();
        fileResponseDto.setFileName(fileName);
        fileResponseDto.setFileUrl(fileDownloadUri);
        return ResponseEntity.ok().body(fileResponseDto);
    }
    @GetMapping ("dowload/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable String id) throws MalformedURLException {
        MessageDto messageDto = messageService.getMessById(id);
        String fileName = messageDto.getFileName();
        Path filePath = Paths.get("uploads").resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
