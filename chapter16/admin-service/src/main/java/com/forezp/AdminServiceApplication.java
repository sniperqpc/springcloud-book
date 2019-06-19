package com.forezp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.config.EnableAdminServer;


@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
public class AdminServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminServiceApplication.class, args);
	}

	@Configuration
	public static class SecuritySecureConfig extends WebSecurityConfigurerAdapter {
	    private final String adminContextPath;

	    public SecuritySecureConfig(AdminServerProperties adminServerProperties) {
	        this.adminContextPath = adminServerProperties.getContextPath();
	    }

	    @Override
	    protected void configure(HttpSecurity http) throws Exception {
	        // @formatter:off
	        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
	        successHandler.setTargetUrlParameter("redirectTo");
	        successHandler.setDefaultTargetUrl(adminContextPath + "/");

	        http.authorizeRequests()
	            .antMatchers(adminContextPath + "/assets/**").permitAll() 
	            .antMatchers(adminContextPath + "/login").permitAll()
	            .anyRequest().authenticated() 
	            .and()
	        .formLogin().loginPage(adminContextPath + "/login").successHandler(successHandler).and() 
	        .logout().logoutUrl(adminContextPath + "/logout").and()
	        .httpBasic().and() 
	        .csrf()
	            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())  
	            .ignoringAntMatchers(
	                adminContextPath + "/instances",   
	                adminContextPath + "/actuator/**"  
	            );
	        // @formatter:on
	    }
	    
	    @Autowired
		public void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth
			.inMemoryAuthentication()
			.withUser("admin").password("$2a$10$wSn/YF9qt6.khpFLCHqoJu2yRcaRWnZKG6vYv4N/Ib61x6ykYFsa.").roles("USER", "ADMIN");
		}
	}

	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
