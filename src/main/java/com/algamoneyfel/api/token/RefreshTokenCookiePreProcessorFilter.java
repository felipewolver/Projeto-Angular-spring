package com.algamoneyfel.api.token;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.util.ParameterMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RefreshTokenCookiePreProcessorFilter implements Filter {
    
	// O metodo public destroy() e init() não serão usados
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		
		if ("/oauth/token".equalsIgnoreCase(req.getRequestURI())
				&& "refresh_token".equals(req.getParameter("grant_type"))
				&& req.getCookies() != null) {
			
			// cookie atribuido como parametro antes da ->
			// -> semelhante ao return pois vai retorna com o getName() o novo Cookie
			// refreshToken vindo da classe RefreshTokenPos.. que criado em new Cookie
			String refreshToken = Stream.of(req.getCookies()) // Transformar o array de cookies em um Strem, com o comando Stream.of(...)
					.filter(cookie -> "refreshToken".equals(cookie.getName())) 
					.findFirst() // Obter o primeiro objeto do Stream (caso exista)
					.map(cookie -> cookie.getValue()) // Transformá-lo de cookie em uma String com o seu valor.
					.orElse(null); // senao encontrar um cookie com refresh token retorna null 
			
			req = new MyServletRequestWrapper(req, refreshToken);
		}
		
		chain.doFilter(req, response);
	}
	

    static class MyServletRequestWrapper extends HttpServletRequestWrapper {
        
    	private String refreshToken;
    	
		public MyServletRequestWrapper(HttpServletRequest request, String refreshToken) {
			super(request);
			
			this.refreshToken = refreshToken;
			
		}
    	
		// Somente digitar o getParameterMap<> e ira cria o metodo 
    	@Override
    	public Map<String, String[]> getParameterMap() {
    		
    		ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
    		map.put("refresh_token", new String[] { this.refreshToken });
    		map.setLocked(true); // Esse map.setLocked Ele vai travar o refresh_token
    		
    		return map;
    	}
    
    }
	
}
