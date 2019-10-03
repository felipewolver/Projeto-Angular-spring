package com.algamoneyfel.api.repository.lancamento;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algamoneyfel.api.model.Lancamento;
import com.algamoneyfel.api.repository.filter.LancamentoFilter;
import com.algamoneyfel.api.repository.projection.ResumoLancamento;

public interface LancamentoRepositoryQuery {
    
	public Page<Lancamento> filtrar(LancamentoFilter lancFilter, Pageable pageable);
	public Page<ResumoLancamento> resumir(LancamentoFilter lancFilter, Pageable pageable);
	
}
