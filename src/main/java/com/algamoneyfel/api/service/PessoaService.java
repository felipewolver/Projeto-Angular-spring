package com.algamoneyfel.api.service;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algamoneyfel.api.model.Pessoa;
import com.algamoneyfel.api.repository.PessoaRepository;

@Service
public class PessoaService {
	
	@Autowired
	private PessoaRepository pessoasRep;
	
	public Pessoa atualizar(Long codigo, Pessoa pessoa) {
		
		Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
		BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
		
		return this.pessoasRep.save(pessoaSalva);
    }

	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		
		//Refatorando para usar o codigo como metodo
		//Botao direito, Refactor, Extract Method
		Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo); // pessoaSalva recebeu o metodo buscar...
		pessoaSalva.setAtivo(ativo);
		this.pessoasRep.save(pessoaSalva);
	}
	
	private Pessoa buscarPessoaPeloCodigo(Long codigo) {
		Pessoa pessoaSalva = pessoasRep.findById(codigo)
				.orElseThrow(() -> new EmptyResultDataAccessException(1));
	
		return this.pessoasRep.save(pessoaSalva);
	}
	
}
