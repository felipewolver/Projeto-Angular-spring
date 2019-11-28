package com.algamoneyfel.api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.algamoneyfel.api.config.token.CustomTokenEnhancer;

@Configuration
@EnableAuthorizationServer
@Profile("oauth-security") // Acesso de segurança para produção.
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
    @Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception{
		
		clients.inMemory()
				.withClient("angular")
				.secret("$2a$10$G1j5Rf8aEEiGc/AET9BA..xRR.qCpOUzBZoJd8ygbGy6tb3jsMT9G") // senha encodada @ngul@r0 - $2a$10$G1j5Rf8aEEiGc/AET9BA..xRR.qCpOUzBZoJd8ygbGy6tb3jsMT9G 
				.scopes("read", "write") // Um Array de Strings de permissões(leitura, alterar) para conseguir limitar o acesso do cliente q neste caso eh o app Angular que vai poder ler ou alterar
		        .authorizedGrantTypes("password", "refresh_token") //refresh_token vai renovar 
		        .accessTokenValiditySeconds(60) // Tempo de acesso ateh expirar 20 segundos  
		        .refreshTokenValiditySeconds(60) // 3600 * 24 validade de 1 dia para ser renovado
	        .and()
	        .withClient("mobile") // obter a senha Bcrypt no site https://www.browserling.com/tools/bcrypt 
			.secret("$2a$10$BZZ2fn7O.lrMaBayuoCcfeL1sOQalaKf//3G0EzxIzGT6pnPpIFpy") // senha encodada m0b1l30
			.scopes("read") // Um Array de Strings de permissões(leitura) para conseguir limitar o acesso do cliente q neste caso eh o app Mobile que somente vai poder ler e nem os usuarios q tiverem permissao poderam alterar. 
	        .authorizedGrantTypes("password", "refresh_token") //refresh_token vai renovar a cada 20 segundos
	        .accessTokenValiditySeconds(120)
	        .refreshTokenValiditySeconds(3600 * 24); 
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		
		endpoints
			.tokenStore(tokenStore())
			.tokenEnhancer(tokenEnhancerChain)
			.reuseRefreshTokens(false) // false para o refresh_token nao reaproveita-lo
			.userDetailsService(this.userDetailsService)
			.authenticationManager(this.authenticationManager);
			
	}
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		
		JwtAccessTokenConverter accessTokenConveter = new JwtAccessTokenConverter();
		accessTokenConveter.setSigningKey("algaworks"); //senha colocada para validar a assinatura
		
		return accessTokenConveter;
	}

	@Bean
	public TokenStore tokenStore() {
		
		return new JwtTokenStore(accessTokenConverter());
	}
	
	@Bean
	public TokenEnhancer tokenEnhancer() {
		
		return new CustomTokenEnhancer();
	}
	

}
