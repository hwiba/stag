package me.stag.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import me.stag.dao.GroupDao;
import me.stag.dao.PreviewDao;
import me.stag.exception.group.UnpermittedAccessGroupException;
import me.stag.model.Group;
import me.stag.model.Note;
import me.stag.model.Preview;

import org.springframework.stereotype.Service;

@Service
public class PreviewService {
	@Resource
	private PreviewDao previewDao;
	@Resource
	private GroupDao groupDao;
	
	public List<Preview> initNotes(String sessionUserId, String groupId) {
		Group group = groupDao.readGroup(groupId);
		if (!group.checkStatus() && !groupDao.checkJoinedGroup(sessionUserId, groupId)) {
			throw new UnpermittedAccessGroupException("비공개 된 그룹입니다");
		}
		return previewDao.initReadPreviews(groupId);
	}
	
	public List<Preview> reloadPreviews(String groupId, String noteTargetDate) {
		return previewDao.reloadPreviews(groupId, noteTargetDate);
	}
	
	public void createPreview(String noteId, String groupId, String noteText) {
		previewDao.create(new Note(noteId), new Group(groupId), match(noteText, "!{3}[^\n]{1,}!{3}", "!{3}"), 
				match(noteText, "\\?{3}[^\n]{1,}\\?{3}", "\\?{3}"));
	}
	
	public void updatePreview(String noteId, String noteText) {
		previewDao.update(noteId, match(noteText, "!{3}[^\n]{1,}!{3}", "!{3}"), match(noteText, "\\?{3}[^\n]{1,}\\?{3}", "\\?{3}"));
	}
	
	public ArrayList<String> match(String givenText, String regex, String regexToBeRemoved) {
		givenText = givenText.trim();
		ArrayList<String> list = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(givenText);
		
		while(matcher.find()) {
			list.add(matcher.group().replaceAll(regexToBeRemoved, " "));
		}
		
		return list;
	}
}
