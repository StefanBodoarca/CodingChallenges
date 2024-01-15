package com.ro.googlekeep.controller;

import com.ro.googlekeep.model.entities.NoteEntity;
import com.ro.googlekeep.repository.NotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/notes")
public class NotesController {

    private NotesRepository notesRepository;

    @Autowired
    public NotesController(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<NoteEntity> findById(@PathVariable Long requestedId) {
        Optional<NoteEntity> note = notesRepository.findById(requestedId);
        return note.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody NoteEntity noteRequest,
                                                UriComponentsBuilder ucb) {
        NoteEntity note = new NoteEntity(noteRequest.getTitle(), noteRequest.getContent());
        NoteEntity savedNote = notesRepository.save(note);
        URI locationOfNewNote = ucb
                .path("notes/{id}")
                .buildAndExpand(savedNote.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewNote).build();
    }
}
