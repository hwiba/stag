package me.stag.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import me.stag.dao.UserDao;
import me.stag.exception.user.AlreadyExistedUserException;
import me.stag.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/applicationContext.xml")
public class UserDaoTest {
	
    @Autowired
    private UserDao userDao;
	
	@Test
	public void ExistCreateUserTest() throws ClassNotFoundException {
		assertEquals(null, userDao.findUserByUserId("h@s.com"));
	}
	
	@Test
	public void CRUDTest() throws ClassNotFoundException, AlreadyExistedUserException {
		userDao.createUser(new User("daoTest@guinness.com","Name","password", "I"));
		User user = userDao.findUserByUserId("daoTest@guinness.com");
		assertNotNull(user);
	}
}
