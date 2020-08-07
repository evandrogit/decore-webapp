package com.webapp.controller;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.webapp.mail.configuration.AppConfig;
import com.webapp.mail.model.CustomerInfo;
import com.webapp.mail.model.ProductOrder;
import com.webapp.mail.service.OrderService;
import com.webapp.model.Bairro;
import com.webapp.model.Pedido;
import com.webapp.model.Produto;
import com.webapp.repository.Bairros;
import com.webapp.repository.filter.BairroFilter;
import com.webapp.util.jsf.FacesUtil;

@Named
@SessionScoped
public class CarrinhoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Produto> listaDeProdutos = new ArrayList<Produto>();

	private static final Locale BRAZIL = new Locale("pt", "BR");

	private static final DecimalFormatSymbols REAL = new DecimalFormatSymbols(BRAZIL);

	private NumberFormat nf = new DecimalFormat("###,##0.00", REAL);
	
	private String totalGeralEmString = "R$ 0,00";
	
	private Long totalDeItens = 0L;
	
	@Inject
	private Pedido pedido;
	
	@Inject
	private Bairros bairros;
	
	
	public void inicializar() {
		if (FacesUtil.isNotPostback()) {			
		}
	}
		
	public List<Bairro> completeText(String query) {
		
        BairroFilter filtro = new BairroFilter();
        filtro.setNome(query);
        
        List<Bairro> listaDeBairros = bairros.filtrados(filtro);       
        
        return listaDeBairros;
    }	
	
	public void enviarPedido() throws IOException {
		
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(
				AppConfig.class);

		OrderService orderService = (OrderService) context.getBean("orderService");
		
		orderService.sendOrderConfirmation(getDummyOrder());
		
		((AbstractApplicationContext) context).close();
		
		
		pedido = new Pedido();
		
		listaDeProdutos = new ArrayList<Produto>();
		
		atualizarCarrinho();
		
		PrimeFaces.current().executeScript("pedidoEnviado();");		
	}
	
	public ProductOrder getDummyOrder()
	{
		ProductOrder order = new ProductOrder();
		order.setOrderId("5123");
		//order.setProductName("Thinkpad Laptop");
		//order.setStatus("Confirmed");
	
		CustomerInfo customerInfo = new CustomerInfo();
		customerInfo.setName(convertToTitleCaseIteratingChars(pedido.getNome()));
		//customerInfo.setAddress("25, West Street, Bangalore");
		customerInfo.setEmail(pedido.getEmail().toLowerCase());
		order.setCustomerInfo(customerInfo);
		
		System.out.println(customerInfo.getName());
		System.out.println(customerInfo.getEmail());
		
		return order;
	}
	
	public void finalizarPedido() throws IOException {
		
		atualizarCarrinho();

		if(totalDeItens > 0) {
			FacesContext.getCurrentInstance().getExternalContext().redirect("/webstore/decore/checkout.xhtml");
		} else {
			FacesContext.getCurrentInstance().getExternalContext().redirect("/webstore/decore/cart.xhtml");
		}
				
	}
	
	public void atualizarCarrinho() {
		
		List<Produto> produtos = new ArrayList<>();	
		
		totalDeItens = 0L;
		Double totalGeral = 0D;
		for (Produto produtoTemp : listaDeProdutos) {
	
			if(produtoTemp.getQuantidadePedido() == null || produtoTemp.getQuantidadePedido().intValue() == 0) {
				produtos.add(produtoTemp);
			} else {
				produtoTemp.setDescricao(convertToTitleCaseIteratingChars(produtoTemp.getDescricao()));
				totalGeral += produtoTemp.getPrecoDeVenda().doubleValue() * produtoTemp.getQuantidadePedido().intValue();
				totalDeItens += produtoTemp.getQuantidadePedido().intValue();
			}
		}
		
		for (Produto produto : produtos) {
			listaDeProdutos.remove(produto);
		}
		
		totalGeralEmString = nf.format(totalGeral.doubleValue());
		
		try {
			Thread.sleep(2000);
			
		} catch (InterruptedException e) {
		}
	}
	
	public void adicionarNoCarrinho(Produto produto) throws IOException {
		
		if(!listaDeProdutos.contains(produto)) {
			produto.setQuantidadePedido(produto.getQuantidadePedido());
			listaDeProdutos.add(produto);
		} else {
			Produto produtoTemp = listaDeProdutos.get(listaDeProdutos.indexOf(produto));
			produtoTemp.setQuantidadePedido(produtoTemp.getQuantidadePedido() + produto.getQuantidadePedido());
		}
		
		try {
			Thread.sleep(2000);
			FacesContext.getCurrentInstance().getExternalContext().redirect("/webstore/decore/cart.xhtml");
			
		} catch (InterruptedException e) {
		}
		
		totalDeItens = 0L;
		Double totalGeral = 0D;
		for (Produto produtoTemp : listaDeProdutos) {
			produtoTemp.setDescricao(convertToTitleCaseIteratingChars(produtoTemp.getDescricao()));
			totalGeral += produtoTemp.getPrecoDeVenda().doubleValue() * produtoTemp.getQuantidadePedido().intValue();
			totalDeItens += produtoTemp.getQuantidadePedido().intValue();
		}
		
		totalGeralEmString = nf.format(totalGeral.doubleValue());
	}
	
	public void removerDoCarrinho(Produto produto) throws IOException {
		Produto produtoTemp = listaDeProdutos.get(listaDeProdutos.indexOf(produto));
		listaDeProdutos.remove(produtoTemp);
		
		totalDeItens = 0L;
		Double totalGeral = 0D;
		for (Produto produtoTemp_ : listaDeProdutos) {
			produtoTemp_.setDescricao(convertToTitleCaseIteratingChars(produtoTemp_.getDescricao()));
			totalGeral += produtoTemp_.getPrecoDeVenda().doubleValue() * produtoTemp_.getQuantidadePedido().intValue();
			totalDeItens += produtoTemp_.getQuantidadePedido().intValue();
		}
		
		totalGeralEmString = nf.format(totalGeral.doubleValue());
		
		try {
			Thread.sleep(2000);
			
		} catch (InterruptedException e) {
		}
	}
	
	public String convertToTitleCaseIteratingChars(String text) {
	    if (text == null || text.isEmpty()) {
	        return text;
	    }
	 
	    StringBuilder converted = new StringBuilder();
	 
	    boolean convertNext = true;
	    for (char ch : text.toCharArray()) {
	        if (Character.isSpaceChar(ch)) {
	            convertNext = true;
	        } else if (convertNext) {
	            ch = Character.toTitleCase(ch);
	            convertNext = false;
	        } else {
	            ch = Character.toLowerCase(ch);
	        }
	        converted.append(ch);
	    }
	 
	    return converted.toString();
	}

	public List<Produto> getListaDeProdutos() {
		return listaDeProdutos;
	}

	public String getTotalGeralEmString() {
		return totalGeralEmString;
	}

	public Long getTotalDeItens() {
		return totalDeItens;
	}


	public Pedido getPedido() {
		return pedido;
	}


	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}
	
}
