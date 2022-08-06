package com.cloudstorage.controllers;

import com.cloudstorage.forms.ButtonForm;
import com.cloudstorage.forms.CredentialForm;
import com.cloudstorage.forms.FileForm;
import com.cloudstorage.forms.NoteForm;
import com.cloudstorage.models.Credential;
import com.cloudstorage.models.File;
import com.cloudstorage.models.Note;
import com.cloudstorage.models.User;
import com.cloudstorage.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Controller
public class HomeController {
    private Logger logger;
    private HomeService homeService;
    private NoteService noteService;
    private UserService userService;
    private FileService fileService;
    

    public HomeController(HomeService homeService, NoteService noteService, UserService userService, FileService fileService) {
        this.homeService = homeService;
        this.noteService = noteService;
        this.userService = userService;
        this.fileService = fileService;
    }

    @PostConstruct
    public void postConstruct() {
        this.logger = LoggerFactory.getLogger(HomeController.class);
    }

    public String defaultModelData(Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("notes", noteService.getNotesFor(userService.getUser(currentUsername).getUserId()));
        model.addAttribute("credentials", homeService.getCredentialsFor(currentUsername));
        model.addAttribute("files", fileService.getFilesFor(userService.getUser(currentUsername).getUserId()));
        return currentUsername;
    }


    @GetMapping(value = {"/home", "/credential", "/deleteCredential", "/updateCredential", "/note", "/deleteNote", "/addFile", "/deleteFile"})
    public String homeView(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model) {
        String currentUsername = defaultModelData(model);
        return "home";
    }

    @PostMapping("/credential")
    public String addCredential(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if(credentialForm.getCredentialId() != null)
            homeService.updateCredential(currentUsername,credentialForm);
        else
            homeService.addCredential(credentialForm, currentUsername);

        defaultModelData(model);
        return "home";
    }

    @PostMapping("/deleteCredential")
    public String deleteCredential(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        homeService.deleteCredential(currentUsername, buttonForm.getButtonId());
        defaultModelData(model);
        return "home";
    }

    @PostMapping("/updateCredential")
    public String updateCredential(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        homeService.updateCredential(currentUsername,credentialForm);
        defaultModelData(model);
        return "home";
    }

    @GetMapping("getCredential")
    @ResponseBody
    public Optional<Credential> getCredential(Integer credentialId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return Optional.of(homeService.getCredentialOf(currentUsername, credentialId));
    }

    // NOTE CONTROLLER //

    @PostMapping("/addNote")
    public String addNote(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userService.getUser(currentUsername).getUserId();
        if(noteForm.getNoteId() == null) noteService.createNote(noteForm, userId); else noteService.updateNote(noteForm);
        defaultModelData(model);
        return "home";
    }

    @PostMapping("/deleteNote")
    public String deleteNote(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model) {
        noteService.deleteNote(buttonForm.getButtonId());
        defaultModelData(model);
        return "home";
    }

    @GetMapping("getNote")
    @ResponseBody
    public Optional<Note> getNote(Integer noteId) {
        return Optional.of(noteService.getNoteOf(noteId));
    }

    // FILE CONTROLLER //

    @PostMapping("/addFile")
    public String addFile(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userService.getUser(currentUsername).getUserId();
        if(fileService.isFilenameUsed(userId, fileForm.getFileObj().getOriginalFilename())) {
            model.addAttribute("filenameExistsError", "Filename already exists. Upload a file with unique name.");
        } else {
            model.addAttribute("filenameExistsError", "");
            fileService.createFile(fileForm, userId);
        }
        defaultModelData(model);
        return "home";
    }

    @PostMapping("/deleteFile")
    public String deleteFile(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model) {
        fileService.deleteFile(buttonForm.getButtonId());
        defaultModelData(model);
        return "home";
    }
    @GetMapping("fileExists")
    @ResponseBody
    public Optional<Boolean> isFilenameUsed(String filename) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userService.getUser(currentUsername).getUserId();
        return Optional.of(fileService.isFilenameUsed(userId, filename));
    }
}
