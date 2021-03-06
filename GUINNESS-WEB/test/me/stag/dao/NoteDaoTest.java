package me.stag.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import me.stag.dao.NoteDao;
import me.stag.model.Group;
import me.stag.model.Note;
import me.stag.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/applicationContext.xml")
public class NoteDaoTest {

	@Autowired
	private NoteDao noteDao;
	
	@Test
	public void CreateNote() throws ClassNotFoundException {
		Note note = new Note("test", "2015-03-19 17:56:24", new User("jyb0823@naver.com"), new Group("Ogsho"));
		noteDao.createNote(note);
		Note newNote = noteDao.readNote(note.getNoteId());
		assertNotNull(newNote);
	}

	@Test
	public void readSingleNote() throws Exception {
		assertNotNull(noteDao.readNote("3"));
	}
	
	@Test
	public void checkGroupNotesCount_노트가없을때() throws Exception {
		assertEquals(0, noteDao.checkGroupNotesCount("oQLZi"));
	}
	
	@Test
	public void checkGroupNotesCount_노트가있을때() throws Exception {
		assertNotEquals(0, noteDao.checkGroupNotesCount("WZDaW"));
	}
	
	@Test
	public void deleteNote_권한이있을때() throws Exception {
		
		String noteId = "21";
		
		assertEquals(1, noteDao.deleteNote(noteId));
	}
	
	@Test
	public void update() throws Exception {
		String noteId = "3";
		String text = "수정된 내용";
		String targetDate = "20150315";
		noteDao.updateNote(text, noteId, targetDate);
		assertEquals(text, noteDao.readNote(noteId).getNoteText());
	}
}
