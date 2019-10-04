package com.algamoneyfel.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algamoneyfel.api.model.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
	
}
