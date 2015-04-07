package org.nhnnext.guinness.controller.notes;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nhnnext.guinness.common.Forwarding;
import org.nhnnext.guinness.common.WebServletUrl;
import org.nhnnext.guinness.exception.MakingObjectListFromJdbcException;
import org.nhnnext.guinness.model.Note;
import org.nhnnext.guinness.model.NoteDao;

import com.google.gson.Gson;

@WebServlet(WebServletUrl.NOTE_READ)
public class DetailNoteRouter extends HttpServlet {
	private static final long serialVersionUID = 1810055739085682471L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String noteId = req.getParameter("noteId");
		List<Note> note = null;
		try {
			note = new NoteDao().readNote(noteId);
		} catch (MakingObjectListFromJdbcException | SQLException e) {
			e.printStackTrace();
			Forwarding.forwardForError(req, resp, "errorMessage", "데이터베이스 접근이 잘못되었습니다.", "/exception.jsp");
		}
		PrintWriter out = resp.getWriter();
		StringBuffer sb = new StringBuffer();
		sb.append(new Gson().toJson(note));
		out.write(sb.toString());
		out.close();
	}
}
