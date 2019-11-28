package com.algamoneyfel.api.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.algamoneyfel.api.config.property.AlgamoneyFelApiProperty;

// Esta classe irar lançar o token refresh para o Cookie
@Profile("oauth-security")
@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {
    

	@Autowired
	private AlgamoneyFelApiProperty algamoneyProperties;
	
	// Passar o mouse em cima da classe e clicar na opção implement methods para implementar os metodos supports e beforeBody..
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
	
		return returnType.getMethod().getName().equals("postAccessToken");
	}

    // Metodo que recupera o conteudo(body) da requisição
	@Override
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		   // Foi feito um cast de httpServletRequest para ServletServerHttpRequest para nao ocorre o erro Type mismatch
		HttpServletRequest req =  ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
		
		// Foi feito um cast de OAuthAccessToken para DefaultOAuth2AccessToken para nao ter o erro Type mismatch
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;
		
		String refreshToken = body.getRefreshToken().getValue();
		adicionarRefreshTokenNoCookie(refreshToken, req, resp);
		removerRefreshTokenNoBody(token);
		
		return body;
	}

	private void removerRefreshTokenNoBody(DefaultOAuth2AccessToken token) {

		token.setRefreshToken(null); // O null remove o refresh token do body
		
	}

	private void adicionarRefreshTokenNoCookie(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
                                   // refreshToken entre aspas "" eh o nome atribuido para esse Cookie
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setSecure(this.algamoneyProperties.getSeguranca().isEnableHttps()); // Mudar para true quando for usado para produção
		refreshTokenCookie.setPath(req.getContextPath() + "/oauth/token");
		
		refreshTokenCookie.setMaxAge(2598000); // 2.598.000 segundos equivale a 30 dias para o cookie expirar
		resp.addCookie(refreshTokenCookie);
		
	}
	
	

}
