package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import entity.User_Status;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("Success", false);

//        JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);
        String mobile = request.getParameter("mobile");
        String fisrtName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String password = request.getParameter("password");
        Part avatarImage = request.getPart("avatarImage");

        if (mobile.isEmpty()) {
            responseJson.addProperty("message", "Please fill mobile number");
        } else if (fisrtName.isEmpty()) {
            responseJson.addProperty("message", "Please fill your First Name");
        } else if (lastName.isEmpty()) {
            responseJson.addProperty("message", "Please fill your Last Name");
        } else if (password.isEmpty()) {
            responseJson.addProperty("message", "Please fill your Password");
        } else {

            Session session = HibernateUtil.getSessionFactory().openSession();

            //search mobile no
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("mobile", mobile));

            if (!criteria.list().isEmpty()) {
                //mobile no already usd
                responseJson.addProperty("message", "This mobile number already used");

            } else {
                //new user add
                User user = new User();
                user.setMobile(mobile);
                user.setFirst_name(fisrtName);
                user.setLast_name(lastName);
                user.setPassword(password);
                user.setRegistered_date(new Date());

                User_Status user_status = (User_Status) session.get(User_Status.class, 2);
                user.setUser_Status(user_status);

                session.save(user);
                session.beginTransaction().commit();

                if (avatarImage.getName() != null) {

                    String applicationPath = request.getServletContext().getRealPath("");
                    String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");
                    File folder = new File(newApplicationPath + "//AvatarImages");
                    folder.mkdir();

                    File imageFile = new File(folder, mobile + "avatar.png");
                    InputStream inputStreamImage = avatarImage.getInputStream();
                    Files.copy(inputStreamImage, imageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                }

                responseJson.addProperty("Success", true);
                responseJson.addProperty("Message", "Registration Complete");

            }
            session.close();

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));
    }

}
