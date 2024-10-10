/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

/**
 *
 * @author User
 */
@MultipartConfig
@WebServlet(name = "Profile", urlPatterns = {"/Profile"})
public class Profile extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false);

        String mobile = request.getParameter("mobile");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        Part image = request.getPart("image");

        if (firstName.isEmpty()) {
            responseJson.addProperty("message", "Please fill your First Name");
        } else if (lastName.isEmpty()) {
            responseJson.addProperty("message", "Please fill your Last Name");
        } else {

            Session session = HibernateUtil.getSessionFactory().openSession();

            //search mobile no
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("mobile", mobile));

            if (!criteria.list().isEmpty()) {

                User user = (User) criteria.uniqueResult();
                user.setFirst_name(firstName);
                user.setLast_name(lastName);

                session.update(user);
                session.beginTransaction().commit();

                if (image.getName() != null) {

                    String applicationPath = request.getServletContext().getRealPath("");
                    String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");
                    File folder = new File(newApplicationPath + "//AvatarImages");
                    folder.mkdir();

                    File imageFile = new File(folder, mobile + "avatar.png");
                    InputStream inputStreamImage = image.getInputStream();
                    Files.copy(inputStreamImage, imageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                }
                
                responseJson.addProperty("success", true);
                responseJson.addProperty("message", "Update Complete");
                responseJson.add("user", gson.toJsonTree(user));

            } else {
                responseJson.addProperty("error", "Please try again later...");
            }
            session.close();

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));
    }

}
