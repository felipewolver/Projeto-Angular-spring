package com.algamoneyfel.api.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algamoneyfel.api.repository.LancamentoRepository;
import com.algamoneyfel.api.repository.filter.LancamentoFilter;
import com.algamoneyfel.api.repository.projection.ResumoLancamento;
import com.algamoneyfel.api.service.LancamentoService;
import com.algamoneyfel.api.service.exception.pessoaInexistenteOuInativaException;
import com.algamoneyfel.api.event.RecursoCriadoEvent;
import com.algamoneyfel.api.exceptionhandler.AlgamoneyfelExceptionHandler.Erro;
import com.algamoneyfel.api.model.Lancamento;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {
	
	@Autowired
	private LancamentoRepository lancamentosRep;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private LancamentoService lancaServ;
	
	@Autowired
	private MessageSource msgSource;
	
	//Pageable ira fazer a paginação ou seja varias paginas de lançamentos
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')") 
	public Page<Lancamento> pesquisar(LancamentoFilter lancFilter, Pageable pageable) {
		
		return this.lancamentosRep.filtrar(lancFilter, pageable);
	}
	
	//Pageable ira fazer a paginação ou seja varias paginas de lançamentos
	@GetMapping(params = "resumo")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')") 
	public Page<ResumoLancamento> resumir(LancamentoFilter lancFilter, Pageable pageable) {
		
		return this.lancamentosRep.resumir(lancFilter, pageable);
	}
	
	@GetMapping("/{codigo}")
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity buscarCodigo(@PathVariable Long codigo) {
		
		Optional lancaExiste = this.lancamentosRep.findById(codigo);
		// Outra forma de verificar se o codigo existe
		if (lancaExiste.isPresent()) {
			
			return ResponseEntity.ok(lancaExiste);
		}
		
		return ResponseEntity.notFound().build();
	
	}
	
	@PostMapping
	public ResponseEntity adicionar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
		
		// Antigo: Lancamento lancaSalva = this.lancamentosRep.save(lancamento);
		Lancamento lancaSalva = lancaServ.salvarServico(lancamento);
		
		this.publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamento.getCodigo()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(lancaSalva);
	
	}
	
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT) 
	public void remover(@PathVariable Long codigo) {
		
		this.lancamentosRep.deleteById(codigo);
	}
	
	// tratamento exception feito dentro do classe LancamentoResource
	@ExceptionHandler({ pessoaInexistenteOuInativaException.class })
	public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(pessoaInexistenteOuInativaException ex) {
		
		// Primeriro parametro de getMessage() pessoa.inexistente-ou-inativa vinda do
		//  arquivo messages.properties que contém a mensagem de exceção.
		String msgUsuario = msgSource.getMessage("pessoa.inexistente-ou-inativa", null, LocaleContextHolder.getLocale());
		String msgDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(msgUsuario, msgDesenvolvedor));
	    return ResponseEntity.badRequest().body(erros);
	}

}
