package guestbook;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: matt
 * Date: 2/8/13
 * Time: 11:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class SignGuestbookServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(SignGuestbookServlet.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		// We have one entity group per Guestbook with all Greetings residing
		// in the same entity group as the Guestbook to which they belong.
		// This lets us run a transactional ancestor query to retrieve all
		// Greetings for a given Guestbook.  However, the write rate to each
		// Guestbook should be limited to ~1/second.
		String guestbookName = req.getParameter("guestbookName");
		Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);
		String content = req.getParameter("content");
		Date date = new Date();
		Entity greeting = new Entity("Greeting", guestbookKey);
		greeting.setProperty("user", user);
		greeting.setProperty("date", date);
		greeting.setProperty("content", content);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(greeting);

		resp.sendRedirect("/guestbook.jsp?guestbookName=" + guestbookName);
	}
}
