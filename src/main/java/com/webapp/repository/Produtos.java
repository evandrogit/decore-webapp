package com.webapp.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

import com.webapp.model.CategoriaProduto;
import com.webapp.model.Produto;
import com.webapp.repository.filter.ProdutoFilter;
import com.webapp.util.jpa.Transacional;

public class Produtos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Produto porId(Long id) {
		return this.manager.find(Produto.class, id);
	}
	
	public Produto porCodigo(String codigo) {
		try {
			return this.manager
					.createQuery("from Produto e where e.codigo = :codigo", Produto.class)
					.setParameter("codigo", codigo).getSingleResult();
		} catch(NoResultException e) {
			return null;
		}
	}
	
	public Produto porCodigoCadastrado(Produto produto) {
		try {
			return this.manager
					.createQuery("from Produto e where e.codigo = :codigo and e.id != :id", Produto.class)
					.setParameter("codigo", produto.getCodigo()).setParameter("id", produto.getId()).getSingleResult();
		} catch(NoResultException e) {
			return null;
		}
	}

	@Transacional
	public Produto save(Produto produto) {
		return this.manager.merge(produto);
	}

	@Transacional
	public void remove(Produto produto) {
		Produto produtoTemp = new Produto();
		produtoTemp = this.manager.merge(produto);

		this.manager.remove(produtoTemp);
	}
	

	public List<Produto> porCategoria(CategoriaProduto categoriaProduto) {
		return this.manager
				.createQuery("from Produto e where e.categoriaProduto.nome = :nome order by e.categoriaProduto.nome asc", Produto.class)
				.setParameter("nome", categoriaProduto.getNome()).getResultList();
	}


	public List<Produto> filtrados(ProdutoFilter filter) {

		TypedQuery<Produto> typedQuery;
		
		String condition = "";
		
		if (StringUtils.isNotBlank(filter.getDescricao())) {
			
			if(filter.getCategoriaProduto() != null) {
				condition = "AND e.categoriaProduto.id = :id";
			}
			
			typedQuery = manager.createQuery(
					"select e from Produto e where " + condition + " e.descricao like :descricao or e.codigo = :codigo order by e.codigo",
					Produto.class).setParameter("descricao", "%" + filter.getDescricao() + "%").setParameter("codigo", filter.getDescricao());
			
		} else {
			
			if(filter.getCategoriaProduto() != null) {
				condition = "WHERE e.categoriaProduto.id = :id";
			}
			
			typedQuery = manager.createQuery("select e from Produto e " + condition, Produto.class);
		}
		
		if(filter.getCategoriaProduto() != null) {
			typedQuery.setParameter("id", filter.getCategoriaProduto().getId());
		}

		return typedQuery.getResultList();

	}
	
	
	public Number totalAVender() {

                Number count = 0;
		
                try {
		    count = (Number) this.manager
				.createQuery("SELECT sum((e.valorUnitario * e.quantidadeDisponivel) * (1 + (e.produto.margemLucro/100))) from ItemCompra e where e.quantidadeDisponivel > 0")
				.getSingleResult();

		} catch(NoResultException e) {
			
		}
                
		if(count == null) {
			count = 0;
		}
		
		return count;
	}

}
