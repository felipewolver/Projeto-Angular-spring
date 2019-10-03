package com.algamoneyfel.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algamoneyfel.api.model.Lancamento;
import com.algamoneyfel.api.repository.lancamento.LancamentoRepositoryQuery;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery {

	
}
