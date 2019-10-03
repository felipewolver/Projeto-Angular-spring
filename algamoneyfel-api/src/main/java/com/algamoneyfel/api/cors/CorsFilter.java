package com.algamoneyfel.api.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.algamoneyfel.api.config.property.AlgamoneyFelApiProperty;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {
    
	@Autowired
	private AlgamoneyFelApiProperty algamoneyProperties; // Criando o objeto algamoney para config. externas
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request =  (HttpServletRequest) req;
		HttpServletResponse response   =  (HttpServletResponse) resp;
		
		response.setHeader("Access-Control-Allow-Origin", this.algamoneyProperties.getOriginPermitida());
		response.setHeader("Access-Control-Allow-Credentials", "true");
		if ("OPTIONS".equals(request.getMethod()) && this.algamoneyProperties.getOriginPermitida().equals(request.getHeader("Origin"))) {
			
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
			response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
			response.setHeader("Access-Control-Max-Age", "3600"); // 3600 segundos equivale a 1 hora q ira fazer outra requisição
			
			response.setStatus(HttpServletResponse.SC_OK);
			
		} else {
			
			chain.doFilter(req, resp);
		}
		
	}
	
	

}
