/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.muic.ooc.webapp.servlet;

import io.muic.ooc.webapp.Routable;
import io.muic.ooc.webapp.model.User;
import io.muic.ooc.webapp.service.SecurityService;
import io.muic.ooc.webapp.service.UserService;
import org.apache.commons.lang.StringUtils;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author gigadot
 */
public class DeleteUserServlet extends HttpServlet implements Routable {

    private SecurityService securityService;
    private UserService userService;

    @Override
    public String getMapping() {
        return "/user/delete";
    }

    @Override
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean authorized = securityService.isAuthorized(request);
        if (authorized) {
            String username = (String) request.getSession().getAttribute("username");
            UserService userService = UserService.getInstance();
            try{
                User currentUser = userService.findByUsername(username);
                User deletingUser = userService.findByUsername(request.getParameter("username"));
                if(StringUtils.equals(currentUser.getUsername(),deletingUser.getUsername())){
                    request.getSession().setAttribute("hasError",true);
                    request.getSession().setAttribute("message","You cannot delete your own account!");
                }else{
                    if(userService.deleteUserByUsername(deletingUser.getUsername())){
                        request.getSession().setAttribute("hasError",false);
                        request.getSession().setAttribute("message",String.format("User %s is successfully deleted.",deletingUser.getUsername()));
                    }else{
                        request.setAttribute("hasError",true);
                        request.getSession().setAttribute("message",String.format("Unable to delete user %s.",deletingUser.getUsername()));
                    }
                }
            } catch (Exception e){
                request.getSession().setAttribute("hasError",true);
                request.getSession().setAttribute("message",String.format("Unable to delete user %s.",request.getParameter("username")));
             }
            response.sendRedirect("/");
        } else {
            response.sendRedirect("/login");
        }
    }
}
