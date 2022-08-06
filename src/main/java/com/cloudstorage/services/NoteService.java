package com.cloudstorage.services;

import com.cloudstorage.forms.CredentialForm;
import com.cloudstorage.forms.NoteForm;
import com.cloudstorage.mappers.NoteMapper;
import com.cloudstorage.models.Credential;
import com.cloudstorage.models.Note;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class NoteService {
    private NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public List<Note> getNotesFor(Integer userId) {
        return noteMapper.getNotesFor(userId);
    }

    public Note getNoteOf(Integer noteId) {
        return noteMapper.getNoteOf(noteId);
    }

    public Integer createNote(NoteForm noteForm, Integer userId) {
        return noteMapper.insert(new Note(null, noteForm.getNoteTitle(), noteForm.getNoteDescription(), userId));
    }

    public void deleteNote(Integer noteId) {
        noteMapper.delete(noteId);
    }

    public void updateNote(NoteForm noteForm) {
        Note note = getNoteOf(noteForm.getNoteId());
        note.setNoteTitle(noteForm.getNoteTitle());
        note.setNoteDescription(noteForm.getNoteDescription());
        noteMapper.update(note);
    }
}
