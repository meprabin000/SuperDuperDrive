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
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.Optional;

@Controller
public class HomeController implements HandlerExceptionResolver {
    private Logger logger;
    private CredentialService credentialService;
    private NoteService noteService;
    private UserService userService;
    private FileService fileService;
    

    public HomeController(CredentialService credentialService, NoteService noteService, UserService userService, FileService fileService) {
        this.credentialService = credentialService;
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
        model.addAttribute("credentials", credentialService.getCredentialsFor(userService.getUser(currentUsername).getUserId()));
        model.addAttribute("files", fileService.getFilesFor(userService.getUser(currentUsername).getUserId()));
        return currentUsername;
    }


    @GetMapping(value = {"/", "/home", "/addCredential", "/deleteCredential", "/note", "/deleteNote", "/addFile", "/deleteFile"})
    public String homeView(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model, @ModelAttribute(name = "success") String success) {
        defaultModelData(model);
        return "home";
    }

    @PostMapping("/addCredential")
    public String addCredential(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model, RedirectAttributes redirectAttributes) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userService.getUser(currentUsername).getUserId();
        if(credentialForm.getCredentialId() == null) {
            credentialService.createCredential(credentialForm, userId);
            redirectAttributes.addFlashAttribute("success", "Successfully added a new credential");
        } else {
            credentialService.updateCredential(credentialForm);
            redirectAttributes.addFlashAttribute("success", "Successfully updated the credential");
        }
        defaultModelData(model);

        return "redirect:/home";
    }

    @PostMapping("/deleteCredential")
    public String deleteCredential(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model, RedirectAttributes redirectAttributes) {
        credentialService.deleteCredential(buttonForm.getButtonId());
        redirectAttributes.addFlashAttribute("success", "Successfully deleted the credential");
        defaultModelData(model);
        return "redirect:/home";
    }

    @GetMapping("getCredential")
    @ResponseBody
    public Optional<Credential> getCredential(Integer credentialId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userService.getUser(currentUsername).getUserId();
        Credential credential = credentialService.getCrediantialOf(credentialId);
        if(credential.getUserId() != userId)
            return Optional.of(null);
        return Optional.of(credential);
    }

    // NOTE CONTROLLER //

    @PostMapping("/addNote")
    public String addNote(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model, RedirectAttributes redirectAttributes) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userService.getUser(currentUsername).getUserId();
        if(noteForm.getNoteId() == null) {
            noteService.createNote(noteForm, userId);
            redirectAttributes.addFlashAttribute("success", "Successfully added a new note");

        } else {
            noteService.updateNote(noteForm);
            redirectAttributes.addFlashAttribute("success", "Successfully updated the note");
        }
        defaultModelData(model);
        return "redirect:/home";
    }

    @PostMapping("/deleteNote")
    public String deleteNote(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model, RedirectAttributes redirectAttributes) {
        noteService.deleteNote(buttonForm.getButtonId());
        redirectAttributes.addFlashAttribute("success", "Successfully deleted the note");
        defaultModelData(model);
        return "redirect:/home";
    }

    @GetMapping("getNote")
    @ResponseBody
    public Optional<Note> getNote(Integer noteId) {
        return Optional.of(noteService.getNoteOf(noteId));
    }

    // FILE CONTROLLER //

    @PostMapping("/addFile")
    public String addFile(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model, RedirectAttributes redirectAttributes) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userService.getUser(currentUsername).getUserId();
        if(fileService.isFilenameUsed(userId, fileForm.getFileObj().getOriginalFilename())) {
            model.addAttribute("filenameExistsError", "Filename already exists. Upload a file with unique name.");
        }
        else if(fileForm.getFileObj().getOriginalFilename().isEmpty()) {
            model.addAttribute("filenameExistsError", "Please add some file to upload.");
        }
        else {
            Integer errorId = fileService.createFile(fileForm, userId);
            if(errorId == -1)
                model.addAttribute("filenameExistsError", "File Size Exceeded. Upload smaller file.");
            else
                model.addAttribute("filenameExistsError", "");
        }
        defaultModelData(model);
        redirectAttributes.addFlashAttribute("success", "Successfully added the file");
        return "redirect:/home";
    }

    @PostMapping("/deleteFile")
    public String deleteFile(FileForm fileForm, NoteForm noteForm, CredentialForm credentialForm, ButtonForm buttonForm, Model model, RedirectAttributes redirectAttributes) {
        fileService.deleteFile(buttonForm.getButtonId());
        redirectAttributes.addFlashAttribute("success", "Successfully deleted the file");
        defaultModelData(model);
        return "redirect:/home";
    }

    @GetMapping("download")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(Integer fileId) {
        File file = fileService.getFileOf(fileId);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(file.getFileData()));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", file.getContentType());
        headers.set("Content-Disposition", String.format("inline; filename="+file.getFilename()));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.getFileData().length)
                .body(resource);
    }


    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        if(ex instanceof MaxUploadSizeExceededException) {
            modelAndView.getModel().put("errorMessage", "File size exceeds limit!");
        } else {
            modelAndView.getModel().put("errorMessage", ex.getMessage());
        }
        return modelAndView;
    }
}
