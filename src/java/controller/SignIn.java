package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("Success", false);

        JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);
        String mobile = requestJson.get("mobile").getAsString();
        String password = requestJson.get("password").getAsString();

        if (mobile.isEmpty()) {
            responseJson.addProperty("message", "Please fill mobile number");
        } else if (password.isEmpty()) {
            responseJson.addProperty("message", "Please fill your Password");
        } else {

            Session session = HibernateUtil.getSessionFactory().openSession();

            //search mobile and Password
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("mobile", mobile));
            criteria.add(Restrictions.eq("password", password));

            if (!criteria.list().isEmpty()) {
                //User found
                User user=(User) criteria.uniqueResult();
                
                responseJson.addProperty("Success", true);
                responseJson.addProperty("message", "Sign InSuccess");
                responseJson.add("user",gson.toJsonTree(user));

            } else {
                //User not found
                responseJson.addProperty("message", "Invalid User");        
            }
            session.close();

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));
    }

}
