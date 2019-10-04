package com.algamoneyfel.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algamoneyfel.api.event.RecursoCriadoEvent;
import com.algamoneyfel.api.model.Categoria;
import com.algamoneyfel.api.repository.CategoriaRepository;

import net.bytebuddy.implementation.bytecode.Throw;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {
	
	@Autowired
	private CategoriaRepository catRepositories;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping                                              // escopo de autorização somente leitura
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')") 
	public List<Categoria> listar() {
		
		return catRepositories.findAll();
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA') and #oauth2.hasScope('write')")
	public ResponseEntity adicionar(@Valid @RequestBody Categoria cat, HttpServletResponse response) {
		
		Categoria catSalva = catRepositories.save(cat);
		// O .path(/{codigo}) vai apresentar o codigo da nova categoria no endereço url 
		// Com essa url na parte header /categorias/novoIdInserido
		this.publisher.publishEvent(new RecursoCriadoEvent(this, response, cat.getCodigo()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(catSalva);
	}
	
	//Vai buscar pelo id no browser com essa uri /categorias/idBuscado
	@GetMapping("/{cod}")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read') ")
	public ResponseEntity buscarCodigo(@PathVariable Long cod) {
		
		Optional catExiste = this.catRepositories.findById(cod);
		if (catExiste.isPresent()) {
		
			return ResponseEntity.ok(catExiste.get());
		}
		
		return ResponseEntity.notFound().build();
		
	}
	
	@DeleteMapping("/{cod}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long cod) {
		
		this.catRepositories.deleteById(cod);
	}
	
}
