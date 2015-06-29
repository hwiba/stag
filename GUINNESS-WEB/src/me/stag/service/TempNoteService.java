package me.stag.service;

import java.util.List;

import javax.annotation.Resource;

import me.stag.dao.TempNoteDao;
import me.stag.model.TempNote;
import me.stag.model.User;

import org.springframework.stereotype.Service;

@Service
public class TempNoteService {
	@Resource
	TempNoteDao tempNoteDao;
	
	public long create(String noteText, String createDate, String sessionUserId) {
		return tempNoteDao.create(new TempNote(noteText, createDate, new User(sessionUserId)));
	}

	public List<TempNote> read(String userId) {
		return tempNoteDao.read(userId);
	}

	public Object readByNoteId(long noteId) {
		return tempNoteDao.readByNoteId(noteId);
	}

	public boolean delete(long noteId) {
		if(tempNoteDao.delete(noteId) == 1) {
			return true;
		}
		return false;
	}

	public boolean update(long noteId, String noteText, String createDate) {
		if(tempNoteDao.update(new TempNote(noteId, noteText, createDate)) == 1) {
			return true;
		}
		return false;
	}
}
