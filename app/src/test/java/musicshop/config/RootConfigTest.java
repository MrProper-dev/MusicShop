package musicshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import musicshop.mappers.PictureMapper;
import musicshop.mappers.ProductMapper;
import musicshop.services.ProductService;

@Configuration
@ComponentScan(basePackageClasses = {ProductService.class, ProductMapper.class, PictureMapper.class})
public class RootConfigTest {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
