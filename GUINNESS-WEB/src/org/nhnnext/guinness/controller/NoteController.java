package org.nhnnext.guinness.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.nhnnext.guinness.dao.GroupDao;
import org.nhnnext.guinness.dao.NoteDao;
import org.nhnnext.guinness.exception.MakingObjectListFromJdbcException;
import org.nhnnext.guinness.model.Note;
import org.nhnnext.guinness.util.ServletRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

@Controller
public class NoteController {
	private static final Logger logger = LoggerFactory.getLogger(NoteController.class);
	@Autowired
	private GroupDao groupDao;

	@Autowired
	private NoteDao noteDao;

	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}

	public void setNoteDao(NoteDao noteDao) {
		this.noteDao = noteDao;
	}

	@RequestMapping(value = "/g/{url}")
	protected ModelAndView notesRouter(@PathVariable String url, HttpSession session, Model model) throws IOException {
		if (!ServletRequestUtil.existedUserIdFromSession(session)) {
			return new ModelAndView("redirect:/");
		}

		String sessionUserId = ServletRequestUtil.getUserIdFromSession(session);
		if (!groupDao.checkJoinedGroup(sessionUserId, url)) {
			model.addAttribute("errorMessage", "비정상적 접근시도.");
			return new ModelAndView("illegal");
		}

		DateTime now = new DateTime();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

		DateTime targetDate = new DateTime(formatter.print(now)).plusDays(1).minusSeconds(1);

		List<Note> noteList = extractNoteListByMemberId(url, targetDate);
		model.addAttribute("noteList", new Gson().toJson(noteList));
		return new ModelAndView("notes");
	}

	@RequestMapping("/notelist/read")
	protected ModelAndView readNoteList(WebRequest req) throws IOException {
		String userIds = req.getParameter("checkedUserId");
		String groupId = req.getParameter("groupId");
		DateTime targetDate = new DateTime(req.getParameter("targetDate")).plusDays(1).minusSeconds(1);
		DateTime endDate = targetDate.minusYears(10);
		targetDate = targetDate.plusYears(10);
		List<Note> noteList = noteDao.readNoteList(groupId, endDate.toString(), targetDate.toString(), userIds);
		return new ModelAndView("jsonView").addObject("jsonData", noteList);
	}

	private List<Note> extractNoteListByMemberId(String groupId, DateTime targetDate) {
		DateTime endDate = targetDate.minusYears(10);
		targetDate = targetDate.plusYears(10);
		List<Note> noteList = noteDao.readNoteList(groupId, endDate.toString(), targetDate.toString());
		return noteList;
	}

	@RequestMapping("/note/read")
	protected ModelAndView show(WebRequest req) throws IOException {
		String noteId = req.getParameter("noteId");
		Note note = null;
		try {
			note = noteDao.readNote(noteId);
		} catch (MakingObjectListFromJdbcException e) {
			logger.error("Exception", e);
			return new ModelAndView("exception");
		}
		return new ModelAndView("jsonView").addObject("jsonData", note);
	}

	@RequestMapping(value = "/note/create/{groupId}/{targetDate}/{noteText}", method = RequestMethod.PUT)
	protected ModelAndView create(@PathVariable String groupId, @PathVariable String targetDate,
			@PathVariable String noteText, HttpSession session) throws IOException {
		if (!ServletRequestUtil.existedUserIdFromSession(session)) {
			//TODO 사실상 동작하지 않음 exception 에러남 ModelAndView return type 시 REDIRECT 동작 구 
			return new ModelAndView("redirect:/");
		}
		String sessionUserId = ServletRequestUtil.getUserIdFromSession(session);
		targetDate = targetDate + " " + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		;
		if (noteText.equals("")) {
			return new ModelAndView("jsonView","jsonData","create note fail");
		}
		noteDao.createNote(new Note(noteText, targetDate, sessionUserId, groupId));
		return new ModelAndView("jsonView","jsonData","create note success");
	}
}
