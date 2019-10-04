package com.algamoneyfel.api.event.listener;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.algamoneyfel.api.event.RecursoCriadoEvent;

@Component
public class RecursoCriadoListener implements ApplicationListener<RecursoCriadoEvent> {
	
	@Override
	public void onApplicationEvent(RecursoCriadoEvent recursoCriadoEvent) {
		
		HttpServletResponse response = recursoCriadoEvent.getResponse();
		Long codigo = recursoCriadoEvent.getCodigo();
		
		adicionarHeaderLocation(response, codigo);
	}
    
	// metodo que pega o novo codigo do registro no header do postman
	// O .path(/{codigo}) vai apresentar o codigo da nova categoria,pessoa, etc no endereço url 
    // Com essa url na parte header /categorias/novoIdInserido
	private void adicionarHeaderLocation(HttpServletResponse response, Long codigo) {
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
				.path("/{codigo}").buildAndExpand(codigo).toUri(); 
			response.setHeader("Location", uri.toASCIIString());
	}
}
