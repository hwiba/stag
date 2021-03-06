package me.stag.dao;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

import me.stag.model.Alarm;
import me.stag.model.Group;
import me.stag.model.SessionUser;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class AlarmDao extends JdbcDaoSupport {

	@Resource
	private DataSource dataSource;

	@PostConstruct
	private void initialize() {
		setDataSource(dataSource);
	}

	public void createNewNotes(Alarm alarm) {
		String sql = "insert into NOTE_ALARMS (alarmId, calleeId, callerId, noteId, alarmStatus, alarmCreateDate) values(?, ?, ?, ?, ?, default)";
		getJdbcTemplate().update(sql, alarm.getAlarmId(), alarm.getReader().getUserId(), alarm.getWriter().getUserId(),
				alarm.getNote().getNoteId(), alarm.getAlarmStatus());
	}

	public void createNewComments(Alarm alarm) {
		String sql = "insert into NOTE_ALARMS (alarmId, calleeId, callerId, noteId, commentId, alarmStatus, alarmCreateDate) values(?, ?, ?, ?, ?, ?, default)";
		getJdbcTemplate().update(sql, alarm.getAlarmId(), alarm.getReader().getUserId(), alarm.getWriter().getUserId(),
				alarm.getNote().getNoteId(), alarm.getComment().getCommentId(), alarm.getAlarmStatus());
	}

	public void createGroupInvitation(Alarm alarm) {
		String sql = "insert into GROUP_ALARMS (alarmId, calleeId, callerId, groupId, alarmStatus, alarmCreateDate) values(?, ?, ?, ?, ?, default)";
		getJdbcTemplate().update(sql, alarm.getAlarmId(), alarm.getReader().getUserId(), alarm.getWriter().getUserId(),
				alarm.getGroup().getGroupId(), alarm.getAlarmStatus());
	}

	public boolean isExistAlarmId(String alarmId) {
		String sql = "select count(1) from NOTE_ALARMS where alarmId = ?";
		if (getJdbcTemplate().queryForObject(sql, Integer.class, alarmId) == 0) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public List<Map<String, Object>> listNotes(String calleeId) {
		String sql = "select A.*, U.userName, G.groupName, G.groupId from NOTE_ALARMS as A, USERS as U, NOTES as N, GROUPS as G where A.calleeId=? and A.callerId=U.userId and A.noteId = N.noteId and N.groupId = G.groupId order by A.alarmCreateDate desc;";
		return getJdbcTemplate().queryForList(sql, calleeId);
	}

	public List<Object> listGroups(String calleeId) {
		String sql = "select A.*, U.userId, U.userName, U.userImage, G.groupId, G.groupName from GROUP_ALARMS as A, USERS as U, GROUPS as G where A.calleeId=? and A.callerId=U.userId and A.groupId = G.groupId order by A.alarmCreateDate desc;";
		return getJdbcTemplate().query(sql, (rs, rowNum) -> new Alarm(rs.getString("alarmId"),
				rs.getString("alarmStatus"), rs.getString("alarmCreateDate"),
				new SessionUser(rs.getString("userId"), rs.getString("userName"), rs.getString("userImage")), null,
				new Group(rs.getString("groupId"), rs.getString("groupName"))
				), calleeId);
	}

	public void deleteNote(String alarmId) {
		String sql = "delete from NOTE_ALARMS where alarmId = ?";
		getJdbcTemplate().update(sql, alarmId);
	}

	public void deleteGroup(String alarmId) {
		String sql = "delete from GROUP_ALARMS where alarmId = ?";
		getJdbcTemplate().update(sql, alarmId);
	}

	public List<Map<String, Object>> readNoteAlarm(String sessionUserId) {
		String sql = "select groupId, count(*) as groupAlarmCount from NOTE_ALARMS as A, NOTES as N where A.alarmStatus = 'N' and A.calleeId =? and N.noteId = A.noteId GROUP BY groupId order by groupId;";
		return getJdbcTemplate().queryForList(sql, sessionUserId);
	}

	public boolean checkGroupAlarms(String userId, String groupId) {
		String sql = "select count(*) from GROUP_ALARMS where calleeId = ? and groupId = ?";
		if (getJdbcTemplate().queryForObject(sql, Integer.class, new Object[] { userId, groupId }) > 0)
			return true;
		return false;
	}

	public boolean checkJoinedGroupAlarms(String userId, String groupId) {
		String sql = "select count(*) from GROUP_ALARMS where callerId = ? and groupId = ?";
		if (getJdbcTemplate().queryForObject(sql, Integer.class, new Object[] { userId, groupId }) > 0)
			return true;
		return false;
	}

	public void deleteGroupByGroupId(String groupId) {
		String sql = "delete from GROUP_ALARMS where groupId = ?";
		getJdbcTemplate().update(sql, groupId);
	}

	public void deleteGroupByNoteId(String noteId) {
		String sql = "delete from NOTE_ALARMS where noteId = ?";
		getJdbcTemplate().update(sql, noteId);
	}

	public void deleteNoteAlarm(String sessionUserId) {
		String sql = "delete from NOTE_ALARMS where calleeId = ?";
		getJdbcTemplate().update(sql, sessionUserId);
	}

	public void deleteGroupAlarm(String sessionUserId) {
		String sql = "delete from GROUP_ALARMS where calleeId = ?";
		getJdbcTemplate().update(sql, sessionUserId);
	}
}
