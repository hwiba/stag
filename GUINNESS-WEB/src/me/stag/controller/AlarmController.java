package me.stag.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import me.stag.dao.AlarmDao;
import me.stag.model.SessionUser;
import me.stag.util.JsonResult;
import me.stag.util.ServletRequestUtil;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alarms")
public class AlarmController {
	@Resource
	private AlarmDao alarmDao;

	@RequestMapping("")
	protected @ResponseBody Map<String, JsonResult> list(HttpSession session) {
		String userId = ((SessionUser)session.getAttribute("sessionUser")).getUserId();
		Map<String, JsonResult> result = new HashMap<String, JsonResult>();
		result.put("note", new JsonResult().setSuccess(true).setMapValues(alarmDao.listNotes(userId)));
		result.put("group", new JsonResult().setSuccess(true).setMapValues(alarmDao.listGroups(userId)));
		return result;
	}

	@RequestMapping(value = "/note/{alarmId}", method = RequestMethod.DELETE)
	protected @ResponseBody JsonResult deleteNote(@PathVariable String alarmId) {
		alarmDao.deleteNote(alarmId);
		return new JsonResult().setSuccess(true);
	}
	
	@RequestMapping(value = "/group/{alarmId}", method = RequestMethod.DELETE)
	protected @ResponseBody JsonResult deleteGroup(@PathVariable String alarmId) {
		alarmDao.deleteGroup(alarmId);
		return new JsonResult().setSuccess(true);
	}

	@RequestMapping("/count")
	protected @ResponseBody JsonResult alarmCounts(HttpSession session) throws IOException {
		String sessionUserId = ServletRequestUtil.getUserIdFromSession(session);
		return new JsonResult().setSuccess(true).setMapValues(alarmDao.readNoteAlarm(sessionUserId));
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.DELETE)
	protected @ResponseBody JsonResult deleteAlarmAll(HttpSession session) throws IOException {
		String sessionUserId = ServletRequestUtil.getUserIdFromSession(session);
		alarmDao.deleteNoteAlarm(sessionUserId);
		alarmDao.deleteGroupAlarm(sessionUserId);
		return new JsonResult().setSuccess(true);
	}
}
