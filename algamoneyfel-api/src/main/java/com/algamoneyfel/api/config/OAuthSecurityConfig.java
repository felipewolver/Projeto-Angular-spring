package com.algamoneyfel.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder; 

/* Deixar as anotações comentada para basic-security */
@Configuration
@EnableWebSecurity
public class OAuthSecurityConfig extends WebSecurityConfigurerAdapter {
	/* Classe utilazada para produçaõ com OAuth. Deixar comentada para basic-security */
	@Bean 
	@Override
	public AuthenticationManager authenticationManager() throws Exception {
	    return super.authenticationManager();
	}
	 /*Este Bean password  não estah mais sendo utilizada neste projeto */
	@Bean
	public PasswordEncoder passwordEnconder() {
		
		return new BCryptPasswordEncoder();
	} 
	
} 
