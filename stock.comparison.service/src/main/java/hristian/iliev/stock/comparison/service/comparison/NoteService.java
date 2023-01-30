package hristian.iliev.stock.comparison.service.comparison;

import hristian.iliev.stock.comparison.service.comparison.entity.Note;

public interface NoteService {

  void deleteNote(int noteId);

  void createNote(Note note);

}
