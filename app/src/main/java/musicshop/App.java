package musicshop;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletException;
import musicshop.config.OrmConfig;
import musicshop.config.RootConfig;
import musicshop.config.SecurityConfig;
import musicshop.config.WebConfig;

public class App{

    public final static String RESOURCES_PATH = "app/src/main/resources";
    public static void main(String[] args) throws LifecycleException, ServletException, InterruptedException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        String docBase = new File(RESOURCES_PATH).getAbsolutePath();

        Context ctx = tomcat.addContext("", docBase);

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(WebConfig.class, OrmConfig.class, RootConfig.class, SecurityConfig.class);
        
        Wrapper wrapper = Tomcat.addServlet(ctx, "dispatcher", new DispatcherServlet(context));
        wrapper.setLoadOnStartup(1);
        wrapper.setMultipartConfigElement(new MultipartConfigElement(""));
        ctx.addServletMappingDecoded("/", "dispatcher");

        FilterDef filterDef = new FilterDef();
        filterDef.setFilter(new DelegatingFilterProxy("springSecurityFilterChain"));
        filterDef.setFilterName("springSecurityFilterChain");
        ctx.addFilterDef(filterDef);
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("springSecurityFilterChain");
        filterMap.addURLPattern("/*");
        ctx.addFilterMap(filterMap);

        tomcat.start();
        tomcat.getServer().await();
        context.close();
    }
}
