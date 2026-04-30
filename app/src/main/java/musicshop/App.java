package musicshop;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
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

        FilterDef filterDefEncoding = new FilterDef();
        filterDefEncoding.setFilter(new CharacterEncodingFilter("UTF-8"));
        filterDefEncoding.setFilterName("characterEncodingFilter");
        ctx.addFilterDef(filterDefEncoding);

        FilterMap filterMapEncoding = new FilterMap();
        filterMapEncoding.setFilterName("characterEncodingFilter");
        filterMapEncoding.addURLPattern("/*");
        ctx.addFilterMap(filterMapEncoding);
        

        FilterDef filterDefSecurity = new FilterDef();
        filterDefSecurity.setFilter(new DelegatingFilterProxy("springSecurityFilterChain"));
        filterDefSecurity.setFilterName("springSecurityFilterChain");
        ctx.addFilterDef(filterDefSecurity);
        FilterMap filterMapSecurity = new FilterMap();
        filterMapSecurity.setFilterName("springSecurityFilterChain");
        filterMapSecurity.addURLPattern("/*");
        ctx.addFilterMap(filterMapSecurity);

        tomcat.start();
        tomcat.getServer().await();
        context.close();
    }
}
