package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.Chat_Status;
import entity.User;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;

@WebServlet(name = "SendChat", urlPatterns = {"/SendChat"})
public class SendChat extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);

        Session session = HibernateUtil.getSessionFactory().openSession();

        //get parameter
        String logged_user_id = request.getParameter("logged_user_id");
        String other_user_id = request.getParameter("other_user_id");
        String message = request.getParameter("message");

        //log User
        User logged_user = (User) session.get(User.class, Integer.parseInt(logged_user_id));

        //get Other User
        User other_user = (User) session.get(User.class, Integer.parseInt(other_user_id));

        //save chat
        Chat chat = new Chat();

        Chat_Status chat_status = (Chat_Status) session.get(Chat_Status.class, 2);
        chat.setChat_Status(chat_status);

        chat.setDate_time(new Date());
        chat.setFrom_user(logged_user);
        chat.setTo_user(other_user);
        chat.setMessage(message);

        session.save(chat);
        try {
            session.beginTransaction().commit();
            responseJson.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));
    }

}
