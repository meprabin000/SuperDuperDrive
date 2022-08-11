package com.cloudstorage.services;

import com.cloudstorage.forms.FileForm;
import com.cloudstorage.forms.NoteForm;
import com.cloudstorage.mappers.FileMapper;
import com.cloudstorage.models.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.http.HttpHeaders;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class FileService {
    private FileMapper fileMapper;
    private Logger logger;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    @PostConstruct
    public void postConstruct() {
        logger = LoggerFactory.getLogger(FileService.class);
    }

    public List<File> getFilesFor(Integer userId) {
        return fileMapper.getFilesFor(userId);
    }

    public File getFileOf(Integer fileId) {
        return fileMapper.getFileOf(fileId);
    }

    public Boolean isFilenameUsed(Integer userId, String filename) {
        return getFilesFor(userId).stream()
                .filter((file) -> file.getFilename().equals(filename))
                .count() != 0;
    }

    public Integer createFile(FileForm fileForm, Integer userId){
        MultipartFile multipartFile = fileForm.getFileObj();
        if(multipartFile.getSize() > 1000000)
            return -1;
        String fileName = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();
        String fileSize = String.valueOf(multipartFile.getSize());

        byte[] fileData = null;
        try {
            fileData = multipartFile.getBytes();
        } catch(IOException e) {
            logger.error(e.getMessage());
            return -2;
        }

        return fileMapper.insert(new File(null,  fileName, contentType, fileSize, userId, fileData));
    }

    public void deleteFile(Integer fileId) {
        fileMapper.delete(fileId);
    }

}
