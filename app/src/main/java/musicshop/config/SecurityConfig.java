package musicshop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import musicshop.entities.Admin;
import musicshop.entities.Client;
import musicshop.entities.Seller;
import musicshop.repositories.AdminRepository;
import musicshop.repositories.ClientRepository;
import musicshop.repositories.SellerRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain filterChainSeller(HttpSecurity http) throws Exception{
        return http
        .securityMatcher("/seller/**")
        .authorizeHttpRequests(auth -> auth
            .anyRequest().hasRole("SELLER")
        )
        .formLogin(form -> form
            .loginPage("/seller/login")
            .loginProcessingUrl("/seller/login")
            .usernameParameter("login")
            .permitAll()
        )
        .userDetailsService(sellerDetailsService())
        .csrf(csrf -> csrf.disable())
        .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChainAdmin(HttpSecurity http) throws Exception{
        return http
        .securityMatcher("/admin/**")
        .authorizeHttpRequests(auth -> auth
            .anyRequest().hasRole("ADMIN")
        )
        .formLogin(form -> form
            .loginPage("/admin/login")
            .loginProcessingUrl("/admin/login")
            .usernameParameter("login")
            .permitAll()
        )
        .userDetailsService(adminDetailsService())
        .csrf(csrf -> csrf.disable())
        .build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain filterChainClient(HttpSecurity http) throws Exception{
        return http
        .securityMatcher("/**")
        .authorizeHttpRequests(auth -> auth 
            .requestMatchers("/products/**", 
                "/client/css/products_list.css", 
                "/client/js/products_list.js", 
                "/client/css/product_card.css",
                "/client/js/product_card.js").permitAll()
            .anyRequest().hasRole("CLIENT")
        )
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .usernameParameter("phone")
            .permitAll()
        )
        .userDetailsService(clientDetailsService())
        .csrf(csrf -> csrf.disable())
        .build();
    }

    @Bean
    public UserDetailsService clientDetailsService(){
        return new ClientDetailsService();
    }

    @Bean
    public UserDetailsService sellerDetailsService(){
        return new SellerDetailsService();
    }

    @Bean
    public UserDetailsService adminDetailsService(){
        return new AdminDetailsService();
    }

    @Component
    public static class ClientDetailsService implements UserDetailsService{

        @Autowired
        private ClientRepository clientRepository;

        @Override
        public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
            Client client = clientRepository.findByPhone(phone);
            if (client == null) {
                throw new UsernameNotFoundException("Client not found: " + phone);
            }
            return client;
        }

    }

    @Component
    public static class SellerDetailsService implements UserDetailsService{

        @Autowired
        private SellerRepository sellerRepository;

        @Override
        public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
            Seller seller = sellerRepository.findByLogin(login);
            if (seller == null) {
                throw new UsernameNotFoundException("Seller not found: " + login);
            }
            return seller;
        }

    }

    @Component
    public static class AdminDetailsService implements UserDetailsService{

        @Autowired
        private AdminRepository adminRepository;

        @Override
        public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
            Admin admin = adminRepository.findByLogin(login);
            if (admin == null) {
                throw new UsernameNotFoundException("Admin not found: " + login);
            }
            return admin;
        }

    }

    public static enum Roles implements GrantedAuthority{
        CLIENT,
        SELLER,
        ADMIN;

        @Override
        public String getAuthority() {
            return "ROLE_" + name();
        }

    } 

}
