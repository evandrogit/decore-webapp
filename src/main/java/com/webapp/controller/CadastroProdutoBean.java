package com.webapp.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.webapp.model.CategoriaProduto;
import com.webapp.model.Fornecedor;
import com.webapp.model.ItemCompra;
import com.webapp.model.ItemVenda;
import com.webapp.model.Produto;
import com.webapp.repository.CategoriasProdutos;
import com.webapp.repository.Fornecedores;
import com.webapp.repository.ItensCompras;
import com.webapp.repository.ItensVendas;
import com.webapp.repository.Produtos;
import com.webapp.util.jsf.FacesUtil;

@Named
@ViewScoped
public class CadastroProdutoBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<CategoriaProduto> todasCategoriasProdutos;
	
	private List<Fornecedor> todosFornecedores;
	
	@Inject
	private CategoriasProdutos categoriasProdutos;
	
	@Inject
	private Fornecedores fornecedores;
	
	@Inject
	private Produtos produtos;
	
	@Inject
	private Produto produto;
	
	private UploadedFile file;
	
	private byte[] fileContent;
	
	@Inject
	private ItensCompras itensCompras;
	
	@Inject
	private ItensVendas itensVendas;
	
	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			todasCategoriasProdutos();
			todosFornecedores();
		}
	}
	
	public void buscar() {
		produto = produtos.porId(produto.getId());
		fileContent = produto.getFoto();
		
		Double quantidadeItensComprados = 0D;
		Long totalItensComprados = 0L;
		List<ItemCompra> itensCompra = itensCompras.porProduto(produto);
		for (ItemCompra itemCompra : itensCompra) {
			quantidadeItensComprados += itemCompra.getValorUnitario().doubleValue() * itemCompra.getQuantidade();
			totalItensComprados += itemCompra.getQuantidade();
		}
		
		produto.setTotalCompras(BigDecimal.valueOf(quantidadeItensComprados));
			
		Long totalItensVendidos = 0L;
		Double quantidadeItensVendidos = 0D;
		List<ItemVenda> itensVenda = itensVendas.porProduto(produto);
		for (ItemVenda itemVenda : itensVenda) {
			quantidadeItensVendidos += itemVenda.getValorUnitario().doubleValue() * itemVenda.getQuantidade();
			totalItensVendidos += itemVenda.getQuantidade();
		}
		
		produto.setTotalVendas(BigDecimal.valueOf(quantidadeItensVendidos));
		
		produto.setTotalAcumulado(BigDecimal.valueOf(itensCompras.aVender(produto).doubleValue() * (1 + (produto.getMargemLucro().doubleValue()/100))));
		//produto.setTotalAcumulado(BigDecimal.valueOf(produto.getQuantidadeAtual() * produto.getLocalizacao().doubleValue()));
		
		if(totalItensVendidos > 0) {
			produto.setPrecoMedioVenda(BigDecimal.valueOf(produto.getTotalVendas().doubleValue() / BigDecimal.valueOf(totalItensVendidos).intValue()));
		} else {
			produto.setPrecoMedioVenda(BigDecimal.ZERO);
		}
		
		produto.setQuantidadeItensComprados(totalItensComprados);
		
		produto.setQuantidadeItensVendidos(totalItensVendidos);
	}
	
	public void calculaAvender() {
		produto.setTotalAcumulado(BigDecimal.valueOf(itensCompras.aVender(produto).doubleValue() * (1 + (produto.getMargemLucro().doubleValue()/100))));
		System.out.println(produto.getTotalAcumulado());
	}
	
	public void salvar() {

		if(fileContent != null) {
			produto.setFoto(fileContent);
		}

		if (produto.getId() == null) {
			
			Produto produtoTemp = produtos.porCodigo(produto.getCodigo());
			
			if(produtoTemp == null) {
				
				produto = produtos.save(produto);
				
				produto = new Produto();
				fileContent = null;
				file = null;
				
				PrimeFaces.current().executeScript(
						"swal({ type: 'success', title: 'Concluído!', text: 'Produto cadastrado com sucesso!' });");			
			} else {
				
				PrimeFaces.current().executeScript(
						"swal({ type: 'error', title: 'Erro!', text: 'Já existe um produto com o código informado!' });");	
			}
						
	
		} else {
			
			Produto produtoTemp = produtos.porCodigoCadastrado(produto);
			if(produtoTemp == null) {
				produto = produtos.save(produto);			
				PrimeFaces.current().executeScript(
						"swal({ type: 'success', title: 'Concluído!', text: 'Produto atualizado com sucesso!' });");
				
			} else {
				PrimeFaces.current().executeScript(
						"swal({ type: 'error', title: 'Erro!', text: 'Já existe um produto com o código informado!' });");	
			}		
		}

	}
	
	private void todasCategoriasProdutos() {
		todasCategoriasProdutos = categoriasProdutos.todos();
	}
	
	private void todosFornecedores() {
		todosFornecedores = fornecedores.todos();
	}
	
	public UploadedFile getFile() {
        return file;
    }
	
	public void setFile(UploadedFile file) {
        this.file = file;
    }
	
	public List<CategoriaProduto> getTodasCategoriasProdutos() {
		return todasCategoriasProdutos;
	}
	
	public List<Fornecedor> getTodosFornecedores() {
		return todosFornecedores;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}
	
	public String getImageContentsAsBase64() {
	    return Base64.getEncoder().encodeToString(fileContent);
	}
		
	public StreamedContent getImage() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        }
        else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            //String id = context.getExternalContext().getRequestParameterMap().get("id");
            //Image image = service.find(Long.valueOf(id));
            return new DefaultStreamedContent(new ByteArrayInputStream(fileContent));
        }
    }
	
	public void upload() {
		if(file != null && file.getFileName() != null) {
			fileContent = file.getContents();						
			
		} else {
			PrimeFaces.current().executeScript(
					"swal({ type: 'error', title: 'Erro!', text: 'Selecione uma imagem com até 200KB!' });");
		}
	}

	public byte[] getFileContent() {
		return fileContent;
	}
		
}
