package com.algamoneyfel.api.service;

import java.util.Optional;

import org.bouncycastle.crypto.tls.FiniteFieldDHEGroup;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.algamoneyfel.api.model.Lancamento;
import com.algamoneyfel.api.model.Pessoa;
import com.algamoneyfel.api.repository.LancamentoRepository;
import com.algamoneyfel.api.repository.PessoaRepository;
import com.algamoneyfel.api.service.exception.pessoaInexistenteOuInativaException;

@Service
public class LancamentoService {
	
	@Autowired
	private PessoaRepository pessoasRep;
	
	@Autowired
	private LancamentoRepository lancamentosRep;
	
	
	public Lancamento salvarServico(Lancamento lanc) {
		
		Optional<Pessoa> pessoa = this.pessoasRep.findById(lanc.getPessoa().getCodigo());
		// Vai verificar se a pessoa nao existe ou se existe se ela não esta ativa
		if (!pessoa.isPresent() || pessoa.get().isInativo()) {
			
			throw new pessoaInexistenteOuInativaException();
		}
		
		return this.lancamentosRep.save(lanc);
	}
	
	public Lancamento atualizarServico(Long codigo, Lancamento lanc) {
		
		Lancamento lancSalvo = buscarLancamentoExistente(codigo); // metodo buscarLanc vind de private ResponseEntity... 
		if (!lanc.getPessoa().equals(lancSalvo.getPessoa())) {
			
		   validarPessoa(lanc);
		}
		
		BeanUtils.copyProperties(lanc, lancSalvo, "codigo");
		
		return this.lancamentosRep.save(lancSalvo);
	}
	
	private void validarPessoa(Lancamento lanc) {
		
		Optional<Pessoa> pessoa = null;
		if (lanc.getPessoa().getCodigo() != null) {
			
			pessoa =  this.pessoasRep.findById(lanc.getPessoa().getCodigo());
		}
		
		if (pessoa == null || pessoa.get().isInativo()) {
			
			throw new pessoaInexistenteOuInativaException();
		}
	}
	
	private Lancamento buscarLancamentoExistente(Long codigo) {
		
		Lancamento lancSalvo = this.lancamentosRep.findById(codigo)
				.orElseThrow(() -> new EmptyResultDataAccessException(1));
		//if (lancSalvo == null) {
			
	//		throw new IllegalArgumentException();
		//}
		
		return lancSalvo;
	}
	


}
