package com.ro.googlekeep.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class NoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;

    public NoteEntity(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public NoteEntity(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
