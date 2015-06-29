package me.stag.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import me.stag.dao.GroupDao;
import me.stag.dao.UserDao;
import me.stag.model.Group;
import me.stag.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/applicationContext.xml")
public class GroupDaoTest {

	@Autowired
	private GroupDao groupDao;
	
	@Autowired
	private UserDao userDao;
	
	private User user = new User("das@das.com", "다스", "1q2w3e", "I", "avatar-default.png");
	private Group group = new Group("group", "groupName", "das@das.com", "F", null);

	@Test
	public void createGroup() throws ClassNotFoundException {
		if (groupDao.readGroup(group.getGroupId()) != null)
			groupDao.deleteGroup(group.getGroupId());
		groupDao.createGroup(group);
		assertNotNull(group);
	}

	@Test
	public void readExistedGroup() {
		assertNotNull(groupDao.readGroup("eNoQv"));
	}

	@Test
	public void readNotExistedGroup() {
		assertNull(groupDao.readGroup("aaaaa"));
	}
	
	@Test
	public void createGroupUser() throws Exception {
		if(userDao.findUserByUserId(user.getUserId()) != null)
			userDao.createUser(user);
		if (groupDao.readGroup(group.getGroupId()) != null)
			groupDao.createGroup(group);
		
		groupDao.createGroupUser(user.getUserId(), group.getGroupId());
		
	}

}
