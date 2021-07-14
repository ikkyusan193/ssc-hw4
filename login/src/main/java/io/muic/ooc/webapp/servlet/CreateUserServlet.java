package io.muic.ooc.webapp.servlet;

import io.muic.ooc.webapp.Routable;
import io.muic.ooc.webapp.service.SecurityService;
import io.muic.ooc.webapp.service.UserService;
import org.apache.commons.lang.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CreateUserServlet extends HttpServlet implements Routable {

    private SecurityService securityService;

    @Override
    public String getMapping() {
        return "/user/create";
    }

    @Override
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            // do MVC in here

            String username = (String) request.getSession().getAttribute("username");
            request.setAttribute("user", username);

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/create.jsp");
            rd.include(request, response);


            request.getSession().removeAttribute("hasError");
            request.getSession().removeAttribute("message");
        } else {
            request.removeAttribute("hasError");
            request.removeAttribute("message");
            response.sendRedirect("/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {

            String username = StringUtils.trim((String) request.getParameter("username"));
            String displayName = StringUtils.trim((String)  request.getParameter("displayName"));
            String password = (String)  request.getParameter("password");
            String cpassword = (String)  request.getParameter("cpassword");
            String errorMessage = null;

            UserService userService = UserService.getInstance();
            //check if user is valid
            if(userService.findByUsername(username) != null){
                errorMessage = String.format("Username %s has already been taken.",username);
            }

            //check if displayName is valid
            if(StringUtils.isBlank(displayName)){
                errorMessage = "Display Name cannot be blank/Display name is not valid";
            }

            //check if confirm password is correct
            if(!StringUtils.equals(password,cpassword)){
                errorMessage = "Password does not match";
            }

            if(errorMessage != null){
                request.getSession().setAttribute("hasError",true);
                request.getSession().setAttribute("message",errorMessage);
            }else{
                //create user if no errors
                try{
                    userService.createUser(username,password,displayName);
                    request.getSession().setAttribute("hasError",false);
                    request.getSession().setAttribute("message",String.format("Username %s has been created Sucessfully.",username));
                    response.sendRedirect("/");
                }catch (Exception e){
                    request.getSession().setAttribute("hasError",true);
                    request.getSession().setAttribute("message",e.getMessage());
                }
            }

            //prefill form
            request.setAttribute("username",username);
            request.setAttribute("displayName",displayName);
            request.setAttribute("password",password);
            request.setAttribute("cpassword",cpassword);
            // if not success it will just come here again.
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/create.jsp");
            rd.include(request, response);
//            request.getSession().removeAttribute("hasError");
//            request.getSession().removeAttribute("message");
        } else {
            request.removeAttribute("hasError");
            request.removeAttribute("message");
            response.sendRedirect("/login");
        }
    }
}

