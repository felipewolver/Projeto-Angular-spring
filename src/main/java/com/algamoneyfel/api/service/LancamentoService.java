package com.algamoneyfel.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
	


}
