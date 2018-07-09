package io.openliberty.anki.web;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.adesso.anki.Vehicle;
import de.adesso.anki.messages.Message;
import de.adesso.anki.messages.MessageMap;
import io.openliberty.anki.cdi.Anki;

/**
 * Servlet implementation class AnkiTest
 */
@WebServlet("/AnkiTest")
public class AnkiTest extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	Anki anki;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AnkiTest() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			response.getWriter().println(getHTML());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String address = (String) request.getParameter("address");
		String action = request.getParameter("action");
		if(action.equals("Connect")) {
			anki.connect(address);
		}
		else {
			anki.disconnect(address);
		}
		doGet(request, response);
	}
	
	private String getHTML() {
		try {
			Map<Integer, Class<? extends Message>> messageClasses = MessageMap.MESSAGES;
			List<Vehicle> vehicles = anki.listVehicles();

			StringBuilder builder = new StringBuilder();
			builder.append("<html><head><title>Anki</title></head><body>");
			builder.append("<form action=\"\" method=\"post\">");
			for (Vehicle v : vehicles) {
				builder.append("<input type=\"radio\" name=\"address\" value=\"");
				builder.append(v.getAddress());
				builder.append("\"> ");
				builder.append(v.getAddress());
				builder.append("<br>");
			}
			builder.append("<input type=\"submit\" name=\"action\" value=\"Connect\">");
			builder.append("<input type=\"submit\" name=\"action\" value=\"Disconnect\">");
			
			for (Map.Entry<Integer, Class<? extends Message>> clazz : messageClasses.entrySet()) {
				builder.append("<input type=\"radio\" name=\"messageType\" value=\"");
				builder.append(clazz.getKey());
				builder.append("\"> ");
				builder.append(clazz.getValue().getSimpleName());
				builder.append("<br>");
			}
			
			builder.append("</form>");
			builder.append("</body></html>");
			
			return builder.toString();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.toString();
		}
	}
}
