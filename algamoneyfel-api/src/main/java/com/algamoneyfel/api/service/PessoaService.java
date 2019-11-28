package com.algamoneyfel.api.service;

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
		 // o parametro pessoa no beanUtils vai copiar as propriedades pessoa vindo da atualização para pessoaSalva do banco de dados
		 // pessoaSalvo busca o codigo da pessoa selecionada como alvo(target)
		 // o parametro codigo no BeanUtils vai ser ignorado na hora da atualização e ele eh o mesmo passado na url no Postman pessoa/codigo(ex. 3)
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
