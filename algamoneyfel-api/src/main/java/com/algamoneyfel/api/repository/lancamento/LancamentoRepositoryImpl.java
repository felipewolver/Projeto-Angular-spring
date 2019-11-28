package com.algamoneyfel.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

// Usar ctrl+shift+o para limpar imports nao usados
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils; // para uso isEmpty

import com.algamoneyfel.api.model.Lancamento;
import com.algamoneyfel.api.repository.filter.LancamentoFilter;
import com.algamoneyfel.api.repository.projection.ResumoLancamento;

// Classe responsável em cria uma lista de paginas para o lançamentos
public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery{
	
	/* Usando MetaModel: Serve para evitar erros ao digitando manualmente os campos de consulta e vai
	 * gerar classes models com _. no final da classe ex Pessoa_. 
	 * Botao direito em cima do projeto e clicar em properties,
	 * javacompiler, Annotation Processing, habilitar as caixinhas e digitar
	 * no campo generated source src/main/java para gerar, depois clicar em path factory
	 * e habilitar as caixinhas, depois nesta janela ir em Add External Jars
	 * no home do usuario STS tem uma pasta .m2 clicar nela, depois repository,
	 * org, hibernate, hibernateJpaModelgen, clicar na versao mais recente, e selecionar o
	 * Jar mais recente e clicar em apply e yes e aguardar rebuild. 
	 * Se não tiver, procurar a dependencia dele e colocar no pom.xml e repeti o processo  */
	
	@PersistenceContext
	private EntityManager manager;
	
	@Override
	public Page<Lancamento> filtrar(LancamentoFilter lancFilter, Pageable pageable) {
		CriteriaBuilder builder = this.manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		//Criar as restrições
		Predicate[] predicates = criarRestricoes(lancFilter, builder, root);
		criteria.where(predicates);
		
		TypedQuery<Lancamento> query = this.manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageable);
		
		//Antigo sem Pageable: return query.getResultList();
		return new PageImpl<>(query.getResultList(), pageable, total(lancFilter));
	}
	
	// Criando a pagina Resumo e pegando os dados do metodo contrutor da Classe ResumoLancamento
	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter lancFilter, Pageable pageable) {
		
		CriteriaBuilder builder = this.manager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		criteria.select(builder.construct(ResumoLancamento.class, 
				root.get("codigo"), root.get("descricao"), // com metaModel: root.get(Lancamento_.DESCRICAO))
				root.get("dataVencimento"), root.get("dataPagamento"), 
				root.get("valor"), root.get("tipo"), 
				root.get("categoria").get("nome"), 
				root.get("pessoa").get("nome") ));
		
		//Criar as restrições
		Predicate[] predicates = criarRestricoes(lancFilter, builder, root);
		criteria.where(predicates);
				
		TypedQuery<ResumoLancamento> query = this.manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(lancFilter));
	}

	
	private Predicate[] criarRestricoes(LancamentoFilter lancFilter, CriteriaBuilder builder, Root<Lancamento> root) {
        
		List<Predicate> predicates = new ArrayList<Predicate>();
		
		
        if (!StringUtils.isEmpty(lancFilter.getDescricao()) ) {
        	//Vai fazer uma consulta igual a o SQL procurando a descriação usando
        	// o lower para letras minusculas
        	predicates.add(builder.like(builder.lower(root.get("descricao")) // com metaModel: root.get(Lancamento_.DESCRICAO))
        			, "%" + lancFilter.getDescricao().toLowerCase() + "%"));
        	
        }
        
        if (lancFilter.getDataVencimentoDe() != null) {
        	predicates.add(
        			builder.greaterThanOrEqualTo(root.get("dataVencimento"), // com metaModel: root.get(Lancamento_.DATA_VENCIMENTO)
        					lancFilter.getDataVencimentoDe()));
        	
        }
        
        if (lancFilter.getDataVencimentoAte() != null) {
        	predicates.add(
        			builder.lessThanOrEqualTo(root.get("dataVencimento"), // com metaModel: root.get(Lancamento_.DATA_VENCIMENTO)
        					lancFilter.getDataVencimentoAte()));
        	
        }
		
        return predicates.toArray(new Predicate[predicates.size()]);
	
	}
	                                         // O ? no TypeQuery generaliza para qualquer tipo
	private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
		
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		// Vai calcular o total de registro por pagina
		int primeiroRegistroPorPagina = paginaAtual * totalRegistrosPorPagina;
		
		query.setFirstResult(primeiroRegistroPorPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}
	
	private Long total(LancamentoFilter lancFilter) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		
		Predicate[] predicates = criarRestricoes(lancFilter, builder, root);
		criteria.where(predicates);
		
		//Esse comando semelhante ao SQL que seria select count* from lancamento where..
		criteria.select(builder.count(root));
		return manager.createQuery(criteria).getSingleResult();
	}


}
