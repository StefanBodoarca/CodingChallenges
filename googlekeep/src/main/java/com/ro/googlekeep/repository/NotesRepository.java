package com.ro.googlekeep.repository;

import com.ro.googlekeep.model.entities.NoteEntity;
import org.springframework.data.repository.CrudRepository;

public interface NotesRepository extends CrudRepository<NoteEntity, Long> {
}
