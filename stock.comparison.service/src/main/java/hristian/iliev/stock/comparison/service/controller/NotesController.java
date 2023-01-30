package hristian.iliev.stock.comparison.service.controller;

import hristian.iliev.stock.comparison.service.Application;
import hristian.iliev.stock.comparison.service.comparison.ComparisonService;
import hristian.iliev.stock.comparison.service.comparison.NoteService;
import hristian.iliev.stock.comparison.service.comparison.entity.Comparison;
import hristian.iliev.stock.comparison.service.comparison.entity.Note;
import hristian.iliev.stock.comparison.service.events.Event;
import hristian.iliev.stock.comparison.service.users.UsersService;
import hristian.iliev.stock.comparison.service.users.entity.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NotesController {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private UsersService usersService;

  @Autowired
  private ComparisonService comparisonService;

  @Autowired
  private NoteService noteService;

  @GetMapping("/users/{username}/notes")
  public String notes(@PathVariable("username") String username, Model model) {
    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return "error";
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());

    Map<String, List<Note>> notesGroupedByComparison = new HashMap<>();

    for (Comparison comparison : comparisons) {
      if (comparison.getNotes().isEmpty()) {
        continue;
      }

      String comparisonKey = comparison.getFirstStockName() + "-" + comparison.getSecondStockName();

      notesGroupedByComparison.put(comparisonKey, comparison.getNotes());
    }

    model.addAttribute("groupedNotes", notesGroupedByComparison);

    return "notes";
  }

  @DeleteMapping("/api/users/{username}/notes/{noteId}")
  public ResponseEntity deleteNote(@PathVariable("username") String username, @PathVariable("noteId") int noteId) {
    noteService.deleteNote(noteId);

    Event event = new Event.Builder()
        .withAction(Event.EventAction.DELETE)
        .withEntityClass(Note.class.getName())
        .withUsername(username)
        .withMessage("User with " + username + " deleted note with id " + noteId)
        .build();

    rabbitTemplate.convertAndSend(Application.topicExchangeName, "analytics.notes", event.toJson());

    return ResponseEntity.ok().build();
  }

  @PostMapping("/api/users/{username}/notes")
  public ResponseEntity createNote(@PathVariable("username") String username, @RequestBody Note note) {
    noteService.createNote(note);

    Event event = new Event.Builder()
        .withAction(Event.EventAction.CREATE)
        .withEntityClass(Note.class.getName())
        .withUsername(username)
        .withMessage("User with " + username + " created a new note with title " + note.getTitle())
        .build();

    rabbitTemplate.convertAndSend(Application.topicExchangeName, "analytics.notes", event.toJson());

    return ResponseEntity.ok().build();
  }

}
