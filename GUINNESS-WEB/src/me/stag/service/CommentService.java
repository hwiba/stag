package me.stag.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import me.stag.dao.AlarmDao;
import me.stag.dao.CommentDao;
import me.stag.dao.GroupDao;
import me.stag.dao.NoteDao;
import me.stag.exception.group.UnpermittedAccessGroupException;
import me.stag.model.Alarm;
import me.stag.model.Comment;
import me.stag.model.Group;
import me.stag.model.Note;
import me.stag.model.SessionUser;
import me.stag.model.User;
import me.stag.util.RandomFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentService {
	@Resource
	private CommentDao commentDao;
	@Resource
	private NoteDao noteDao;
	@Resource
	private AlarmDao alarmDao;
	@Resource
	private GroupDao groupDao;

	public List<Map<String, Object>> create(SessionUser sessionUser, Note note, Comment comment) {
		Group group = groupDao.readGroupByNoteId(note.getNoteId());
		if (!groupDao.checkJoinedGroup(sessionUser.getUserId(), group.getGroupId())) {
			throw new UnpermittedAccessGroupException("권한이 없습니다. 그룹 가입을 요청하세요.");
		}
		comment = new Comment(comment.getCommentText(), sessionUser, note);
		comment.setCommentId(""+commentDao.createComment(comment));
		noteDao.increaseCommentCount(comment.getNote().getNoteId());
		createAlarm(comment);
		return commentDao.readCommentListByNoteId(comment.getNote().getNoteId());
	}

	private void createAlarm(Comment comment) {
		Note note = comment.getNote();
		User noteWriter = noteDao.readNote(note.getNoteId()).getUser();
		if (!comment.checkWriter(noteWriter)) {
			alarmDao.createNewComments(new Alarm(createAlarmId(), "C", comment.getUser(), noteWriter, note, comment));
		}
	}

	private String createAlarmId() {
		String alarmId = RandomFactory.getRandomId(10);
		if(alarmDao.isExistAlarmId(alarmId)) {
			return createAlarmId();
		}
		return alarmId;
	}

	public List<Map<String, Object>> list(String noteId) {
		return commentDao.readCommentListByNoteId(noteId);
	}

	public Comment update(String commentId, String commentText) {
		commentDao.updateComment(commentId, commentText);
		return commentDao.readCommentByCommentId(commentId);
	}

	public void delete(String commentId) {
		noteDao.decreaseCommentCountByComment(commentId);
		commentDao.deleteComment(commentId);
	}
}
