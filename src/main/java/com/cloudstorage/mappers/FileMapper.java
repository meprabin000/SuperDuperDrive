package com.cloudstorage.mappers;

import com.cloudstorage.models.File;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileMapper {
    @Insert("INSERT INTO FILES (filename, contentType, fileSize, userid, fileData) VALUES (#{filename}, #{contentType}, #{fileSize}, #{userId}, #{fileData, jdbcType=BLOB})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    public int insert(File file);

    @Select("SELECT * FROM FILES WHERE userId = #{userId}")
    public List<File> getFilesFor(Integer userId);

    @Select("SELECT * FROM FILES WHERE fileId = #{fileId}")
    public File getFileOf(Integer fileId);

    @Delete("DELETE FROM FILES WHERE fileId = #{fileId}")
    public void delete(Integer fileId);

}
