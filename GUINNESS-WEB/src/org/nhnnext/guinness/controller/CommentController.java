package org.nhnnext.guinness.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nhnnext.guinness.model.Comment;
import org.nhnnext.guinness.model.CommentDao;
import org.nhnnext.guinness.util.ServletRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/comment")
public class CommentController {
	private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

	@Autowired
	private CommentDao commentDao;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	protected ModelAndView create(HttpServletRequest req, HttpSession session) throws IOException {
		if (!ServletRequestUtil.existedUserIdFromSession(session)) {
			return new ModelAndView("redirect:/");
		}
		String sessionUserId = ServletRequestUtil.getUserIdFromSession(session);
		Map<String, String> paramsList = ServletRequestUtil.getRequestParameters(req, "commentText", "commentType",
				"noteId");
		try {
			if (!paramsList.get("commentText").equals("")) {
				Comment comment = new Comment(paramsList.get("commentText"), paramsList.get("commentType"),
						sessionUserId, paramsList.get("noteId"));
				commentDao.createComment(comment);
			}
			List<Comment> commentList = commentDao.readCommentListByNoteId(paramsList.get("noteId"));
			ModelAndView mav = new ModelAndView("jsonView");
			mav.addObject("jsonData", commentList);
			return mav;
		} catch (ClassNotFoundException e) {
			logger.error("Exception", e);
			return new ModelAndView("/WEB-INF/jsp/exception.jsp");
		}
	}

	@RequestMapping("")
	protected ModelAndView list(HttpServletRequest req) {
		Map<String, String> paramsList = ServletRequestUtil.getRequestParameters(req, "noteId");

		List<Comment> commentList = null;
		try {
			commentList = commentDao.readCommentListByNoteId(paramsList.get("noteId"));
			ModelAndView mav = new ModelAndView("jsonView");
			mav.addObject("jsonData", commentList);
			return mav;
		} catch (ClassNotFoundException e) {
			logger.error("Exception", e);
			return new ModelAndView("/WEB-INF/jsp/exception.jsp");
		}
	}
}