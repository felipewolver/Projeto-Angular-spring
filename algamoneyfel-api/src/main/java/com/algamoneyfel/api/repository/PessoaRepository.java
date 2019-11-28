package com.algamoneyfel.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.algamoneyfel.api.model.Pessoa;
import com.algamoneyfel.api.repository.pessoa.PessoaRepositoryQuery;

public interface PessoaRepository extends JpaRepository<Pessoa, Long>, PessoaRepositoryQuery {
	
	//public Page<Pessoa> findByNomeContainingAndAtivo(String nome, boolean ativo,Pageable pageable);
	
}
