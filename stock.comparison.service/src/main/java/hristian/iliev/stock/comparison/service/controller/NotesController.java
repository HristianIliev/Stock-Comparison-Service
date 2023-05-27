package hristian.iliev.stock.comparison.service.controller;

import hristian.iliev.stock.comparison.service.comparison.ComparisonService;
import hristian.iliev.stock.comparison.service.comparison.NoteService;
import hristian.iliev.stock.comparison.service.comparison.entity.Note;
import hristian.iliev.stock.comparison.service.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class NotesController {

  @Autowired
  private UsersService usersService;

  @Autowired
  private ComparisonService comparisonService;

  @Autowired
  private NoteService noteService;

  @DeleteMapping("/api/users/{username}/notes/{noteId}")
  public ResponseEntity deleteNote(@PathVariable("username") String username, @PathVariable("noteId") int noteId) {
    noteService.deleteNote(noteId);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/api/users/{username}/notes")
  public ResponseEntity createNote(@PathVariable("username") String username, @RequestBody Note note) {
    noteService.createNote(note);

    return ResponseEntity.ok().build();
  }

}
