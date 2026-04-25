package musicshop.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"musicshop.services", "musicshop.mappers"})
public class RootConfig {

}
