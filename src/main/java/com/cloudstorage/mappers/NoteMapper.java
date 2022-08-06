package com.cloudstorage.mappers;

import com.cloudstorage.models.Credential;
import com.cloudstorage.models.Note;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoteMapper {
    @Insert("INSERT INTO NOTES (noteTitle, noteDescription, userId) VALUES (#{noteTitle}, #{noteDescription}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "noteId")
    public int insert(Note note);

    @Select("SELECT * FROM NOTES WHERE userId = #{userId}")
    public List<Note> getNotesFor(Integer userId);

    @Select("SELECT * FROM NOTES WHERE noteId = #{noteId}")
    public Note getNoteOf(Integer noteId);

    @Delete("DELETE FROM NOTES WHERE noteId = #{noteId}")
    public void delete(Integer noteId);

    @Update("UPDATE NOTES SET noteTitle = #{noteTitle}, noteDescription = #{noteDescription} WHERE noteId = #{noteId}")
    public void update(Note note);
}
