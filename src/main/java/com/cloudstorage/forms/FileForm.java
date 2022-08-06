package com.cloudstorage.forms;

import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;

public class FileForm {
    private MultipartFile fileObj;

    public MultipartFile getFileObj() {
        return fileObj;
    }

    public void setFileObj(MultipartFile fileObj) {
        this.fileObj = fileObj;
    }
}
