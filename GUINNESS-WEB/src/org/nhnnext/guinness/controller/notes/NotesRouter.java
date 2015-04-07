package org.nhnnext.guinness.controller.notes;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nhnnext.guinness.common.Forwarding;
import org.nhnnext.guinness.common.WebServletUrl;
import org.nhnnext.guinness.model.GroupDao;

@WebServlet(WebServletUrl.NOTELIST)
public class NotesRouter extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// session을 통해 userid를 받고 해당 그룹의 권한이 있는지 확인 후 있으면 notes.jsp로 이동
		HttpSession session = req.getSession();
		String userId = (String) session.getAttribute("sessionUserId");
		if (userId == null) {
			resp.sendRedirect("/");
			return;
		}
		try {
			String url = req.getRequestURI().split("/")[2];
			if (!new GroupDao().checkJoinedGroup(userId, url)) {
				Forwarding.forwardForError(req, resp, "errorMessage", "비정상적 접근시도.", "/illegal.jsp");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Forwarding.forwardForError(req, resp, "errorMessage", "비정상적 접근시도.", "/exception.jsp");
			return;
		}
		Forwarding.forwardForError(req, resp, null, null, "/notes.jsp");
	}
}
