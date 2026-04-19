package musicshop;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.ServletException;
import musicshop.config.OrmConfig;
import musicshop.config.RootConfig;
import musicshop.config.WebConfig;

public class App{
    public static void main(String[] args) throws LifecycleException, ServletException, InterruptedException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        String docBase = new File("app/src/main/resources").getAbsolutePath();

        Context ctx = tomcat.addContext("", docBase);

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(WebConfig.class, OrmConfig.class, RootConfig.class);
        
        Tomcat.addServlet(ctx, "dispatcher", new DispatcherServlet(context)).setLoadOnStartup(1);
        ctx.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();
        tomcat.getServer().await();
        context.close();
    }
}
