package io.openliberty.anki.web;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        System.out.println("LIB: "+System.getProperty("java.library.path"));
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			anki.test();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
