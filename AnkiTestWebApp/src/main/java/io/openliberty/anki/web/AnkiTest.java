package io.openliberty.anki.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.adesso.anki.messages.Message;
import de.adesso.anki.messages.MessageMap;
import de.adesso.anki.messages.SetSpeedMessage;
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
		if(action.equals("Send")) {
			String messageType = request.getParameter("messageType");
			Class<? extends Message> messageClass = MessageMap.getMessageClass(Integer.parseInt(messageType));
			Message message = null;
			if(messageClass == SetSpeedMessage.class) {
				String speed = request.getParameter("SetSpeedMessage_speed");
				String acceleration = request.getParameter("SetSpeedMessage_acceleration");
				message = new SetSpeedMessage(Integer.parseInt(speed), Integer.parseInt(acceleration));
			}
			
			anki.sendMessage(address, message);
		}
		else {
			anki.disconnect(address);
		}
		doGet(request, response);
	}
	
	private String getHTML() {
		try {
			Map<Integer, Class<? extends Message>> messageClasses = MessageMap.MESSAGES;
			List<String> vehicles = anki.listVehicles();

			StringBuilder builder = new StringBuilder();
			builder.append("<html><head><title>Anki</title></head><body>");
			builder.append("<form action=\"\" method=\"post\">");
			for (String v : vehicles) {
				builder.append("<input type=\"radio\" name=\"address\" value=\"");
				builder.append(v);
				builder.append("\"> ");
				builder.append(v);
				builder.append("<br>");
			}
			
			for (Map.Entry<Integer, Class<? extends Message>> clazz : messageClasses.entrySet()) {
				builder.append("<input type=\"radio\" name=\"messageType\" value=\"");
				builder.append(clazz.getKey());
				builder.append("\"> ");
				builder.append(clazz.getValue().getSimpleName());
				builder.append("<br>");
				if(clazz.getValue() == SetSpeedMessage.class) {
					builder.append("Speed: <input type=\"text\" name=\"SetSpeedMessage_speed\" value=\"\">m/s<br>");
					builder.append("Speed: <input type=\"text\" name=\"SetSpeedMessage_acceleration\" value=\"\">m/s/s<br>");
				}
			}
			
			builder.append("<input type=\"submit\" name=\"action\" value=\"Send\">");
			builder.append("<input type=\"submit\" name=\"action\" value=\"Disconnect\">");
			
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
