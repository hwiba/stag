package me.stag.service;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import me.stag.dao.ConfirmDao;
import me.stag.dao.GroupDao;
import me.stag.dao.UserDao;
import me.stag.exception.user.AlreadyExistedUserException;
import me.stag.exception.user.FailedLoginException;
import me.stag.exception.user.FailedUpdateUserException;
import me.stag.exception.user.NotExistedUserException;
import me.stag.model.User;
import me.stag.util.RandomFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class UserService {
	@Resource
	private UserDao userDao;
	@Resource
	private GroupDao groupDao;
	@Resource
	private ConfirmDao confirmDao;
	@Resource
	private MailService mailService;
	
	public void join(User user) {
		User existedUser = createUser(user);
		createConfirm(user, existedUser);
	}
	
	private User createUser(User user) {
		if(userDao.findUserByUserId(user.getUserId()) != null) {
			throw new AlreadyExistedUserException("이미 존재하는 계정입니다.");
		}
		userDao.createUser(user);
		//TODO 피드백 그룹으로 자동 가입을 위한 구문 
		try{
			groupDao.createGroupUser(user.getUserId(), "Dcdmp");
		} catch(Exception e) {
			// do nothing ...
		}
		return userDao.findUserByUserId(user.getUserId());
	}

	private void createConfirm(User user, User existedUser) {
		if("R".equals(existedUser.getUserStatus())) {
			confirmDao.deleteConfirmByUserId(user.getUserId());
		}
		String keyAddress = createKeyAddress();
		confirmDao.createConfirm(keyAddress, user.getUserId());
		mailService.sendMailforSignUp(keyAddress, user.getUserId());
	}
	
	private String createKeyAddress() {
		String keyAddress = RandomFactory.getRandomId(10);
		if(confirmDao.isExistKeyAddress(keyAddress)) {
			return createKeyAddress();
		}
		return keyAddress;
	}
	
	public User confirm(String keyAddress) {
		String userId = confirmDao.findUserIdByKeyAddress(keyAddress);
		userDao.updateUserState(userId, "E");
		confirmDao.deleteConfirmByKeyAddress(keyAddress);
		return userDao.findUserByUserId(userId);
	}
	
	public User login(String userId, String userPassword) {
		User user = userDao.findUserByUserId(userId);
		if (user == null || !user.isCorrectPassword(userPassword) || !user.checkUserStatus("E")) {
			throw new FailedLoginException();
		}
		return user;
	}
	
	public void update(User user, String rootPath, MultipartFile profileImage) {
		User dbUser = userDao.findUserByUserId(user.getUserId());
		
		boolean isDefaultImage = "avatar-default.png".equals(user.getUserImage());
		boolean isChangedImage = user.getUserId().equals(user.getUserImage());
		
		if(!isDefaultImage && !isChangedImage && !profileImage.isEmpty()) {
			try {
				String fileName = user.getUserId();
				profileImage.transferTo(new File(rootPath + "img/profile/" + fileName));
				user.setUserImage(fileName);
			} catch (IllegalStateException | IOException | DataIntegrityViolationException e) {
				throw new FailedUpdateUserException("잘못된 형식입니다.");
			}
		}
		dbUser.update(user);
		userDao.updateUser(dbUser);
	}

	public boolean checkUpdatePassword(String userId, String userPassword) {
		User user = userDao.findUserByUserId(userId);
		return user.isCorrectPassword(userPassword);
	}

	public void initPassword(String userId) {
		if(userDao.findUserByUserId(userId) == null) {
			throw new NotExistedUserException("사용자를 찾을 수 없습니다.");
		}
		String tempPassword = "temp_" + RandomFactory.getRandomId(4);
		User user = new User();
		user.setUserId(userId);
		user.setUserPassword(tempPassword);
		userDao.initPassword(user);
		mailService.sendMailforInitPassword(tempPassword, userId);
	}

	public String readUserImage(String userId) {
		return userDao.findUserByUserId(userId).getUserImage();
	}
}
