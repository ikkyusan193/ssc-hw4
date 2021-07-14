package io.muic.ooc.webapp;

import java.io.File;
import javax.servlet.ServletException;

import io.muic.ooc.webapp.service.DatabaseConnectionService;
import io.muic.ooc.webapp.service.SecurityService;
import io.muic.ooc.webapp.service.UserService;
import io.muic.ooc.webapp.servlet.HomeServlet;
import io.muic.ooc.webapp.servlet.LoginServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ErrorPage;

public class Webapp {

    public static void main(String[] args) {

        File docBase = new File("src/main/webapp/");
        docBase.mkdirs();
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(80);

        SecurityService securityService = new SecurityService();
        securityService.setUserService(UserService.getInstance());

        ServletRouter servletRouter = new ServletRouter();
        servletRouter.setSecurityService(securityService);

        Context ctx;
        try {
            ctx = tomcat.addWebapp("", docBase.getAbsolutePath());
            servletRouter.init(ctx);


            ErrorPage error404page = new ErrorPage();
            error404page.setErrorCode(404);
            error404page.setLocation("/WEB-INF/error404.jsp");
            ctx.addErrorPage(error404page);

            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException ex) {
            ex.printStackTrace();
        }

    }
}
