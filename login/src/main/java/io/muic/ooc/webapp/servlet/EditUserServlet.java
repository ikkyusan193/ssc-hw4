package io.muic.ooc.webapp.servlet;

import io.muic.ooc.webapp.Routable;
import io.muic.ooc.webapp.model.User;
import io.muic.ooc.webapp.service.SecurityService;
import io.muic.ooc.webapp.service.UserService;
import org.apache.commons.lang.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EditUserServlet extends HttpServlet implements Routable {

    private SecurityService securityService;

    @Override
    public String getMapping() {
        return "/user/edit";
    }

    @Override
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {

            String username = StringUtils.trim((String) request.getParameter("username"));
            UserService userService = UserService.getInstance();

            User user = userService.findByUsername(username);
            request.setAttribute("user",user);
            request.setAttribute("username",user.getUsername());
            request.setAttribute("displayName",user.getDisplayName());

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/edit.jsp");
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


            UserService userService = UserService.getInstance();
            User user = userService.findByUsername(username);

            String errorMessage = null;
            //check if user is valid
            if(user == null){
                errorMessage = String.format("Username %s does not exist.",username);
            }
            //check if displayName is valid
            else if(StringUtils.isBlank(displayName)){
                errorMessage = "Display Name cannot be blank/Display name is not valid";
            }

            if(errorMessage != null){
                request.getSession().setAttribute("hasError",true);
                request.getSession().setAttribute("message",errorMessage);
            }else{
                //edit a user if no error
                try{
                    userService.updateUserByUsername(username,displayName);
                    request.getSession().setAttribute("hasError",false);
                    request.getSession().setAttribute("message",String.format("user %s has been updated successfully.",username));
                    response.sendRedirect("/");
                }catch (Exception e){
                    request.getSession().setAttribute("hasError",true);
                    request.getSession().setAttribute("message",e.getMessage());
                }
            }
            //prefill form
            request.setAttribute("username",username);
            request.setAttribute("displayName",displayName);
            // if not success it will just come here again.
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/edit.jsp");
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

