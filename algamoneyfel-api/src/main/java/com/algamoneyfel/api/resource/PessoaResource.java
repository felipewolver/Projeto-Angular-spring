package com.algamoneyfel.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algamoneyfel.api.event.RecursoCriadoEvent;
import com.algamoneyfel.api.model.Pessoa;
import com.algamoneyfel.api.repository.PessoaRepository;
import com.algamoneyfel.api.repository.filter.PessoaFilter;
import com.algamoneyfel.api.service.PessoaService;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {
	
	@Autowired
	private PessoaRepository pessoasRep;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private PessoaService pessoaServ;
	
	// Metodo listar antigo sem uso do filtro de pesquisa
	/*@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')") 
	public List<Pessoa> listar() {
		
		return this.pessoasRep.findAll();
	} */
	
	//Antes: public Page<Pessoa> pesquisar(@RequestParam(required = false, defaultValue = "%") String nome, boolean ativo, Pageable pageable) {
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
	public Page<Pessoa> pesquisar(PessoaFilter pessoaFilter, Pageable pageable) {
		
		//return this.pessoasRep.findByNomeContainingAndAtivo(nome, ativo, pageable);
		return this.pessoasRep.filtrar(pessoaFilter, pageable);
	} 
	
	@GetMapping("/{cod}")
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')") 
	public ResponseEntity buscarCodigo(@PathVariable Long cod) {
		
		Optional pessoaExist = this.pessoasRep.findById(cod);
		return pessoaExist.isPresent() ? // foi usado operador ternário para esta busca
				ResponseEntity.ok(pessoaExist.get()) : 
					ResponseEntity.notFound().build(); 
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')") 
	public ResponseEntity adicionar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {
		
		Pessoa pessoaSalva = pessoasRep.save(pessoa); // Salva o novo recurso
		this.publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoa.getCodigo())); // pega o codigo e poem no location
		
		return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
	}
	
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_REMOVER_PESSOA') and #oauth2.hasScope('write')") 
	public void remover(@PathVariable Long codigo) {
		
		this.pessoasRep.deleteById(codigo);
	}
	
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
	@PutMapping("/{codigo}")
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa) {
	    
		Pessoa pessoaSalva = pessoaServ.atualizar(codigo, pessoa);
		return ResponseEntity.ok(pessoaSalva);
		
	}
	
	//Metodo de atualização parcial de uma propriedade do registro
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarParcial(@PathVariable Long codigo, @RequestBody Boolean ativo) {
		
		pessoaServ.atualizarPropriedadeAtivo(codigo, ativo); 
			
	}
	
}
