package com.algamoneyfel.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

@Configuration
@EnableResourceServer
//@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("oauth-security")
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetaisService;
	
	
	
	@Autowired
	public void configure(AuthenticationManagerBuilder oauth) throws Exception {
		
		oauth.userDetailsService(this.userDetaisService).passwordEncoder(passwordEnconder());
	}
	
	/*@Bean metodo antigo para atenticar senha pela memoria do Spring
	public UserDetailsService userDetailsService() {
		
		User.UserBuilder builder = User.withDefaultPasswordEncoder();
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(builder.username("admin").password("admin").roles("ROLE").build());
		
		return manager;
	}  */
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests() 
			.antMatchers("/categorias").permitAll() // Ao preencher autoriza a qualquer um entrar nas categorias, e o resto estah restrito soh para o usuario atenticado. Soh funciona sem o preAuthorize
	        .anyRequest().authenticated()
	        .and()
	    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
	    .csrf().disable();
	        
	}
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		
		resources.stateless(true);
	}
	
	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler() {
		
		return new OAuth2MethodSecurityExpressionHandler();
	}
	
	public PasswordEncoder passwordEnconder() {
		
		return new BCryptPasswordEncoder();
	} 
	

	
}
