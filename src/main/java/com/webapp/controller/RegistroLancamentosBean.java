package com.webapp.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import org.primefaces.PrimeFaces;

import com.webapp.model.CategoriaLancamento;
import com.webapp.model.Conta;
import com.webapp.model.DestinoLancamento;
import com.webapp.model.Lancamento;
import com.webapp.model.OrigemLancamento;
import com.webapp.model.PeriodoPagamento;
import com.webapp.model.TipoConta;
import com.webapp.model.TipoLancamento;
import com.webapp.model.TipoPagamento;
import com.webapp.repository.CategoriasLancamentos;
import com.webapp.repository.Contas;
import com.webapp.repository.DestinosLancamentos;
import com.webapp.repository.Lancamentos;
import com.webapp.util.jsf.FacesUtil;

@Named
@ViewScoped
public class RegistroLancamentosBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Lancamento lancamento;

	@Inject
	private Lancamento despesa;

	@Inject
	private Lancamento receita;

	@Inject
	private Lancamentos lancamentos;

	@Inject
	private CategoriasLancamentos categoriasLancamentos;

	private List<TipoLancamento> todosTiposDespesas;

	private List<CategoriaLancamento> todasCategoriasDespesas;

	private List<CategoriaLancamento> todasCategoriasReceitas;

	@Inject
	private CategoriaLancamento categoriaLancamentoDespesa;

	@Inject
	private CategoriaLancamento categoriaLancamentoReceita;

	@Inject
	private DestinosLancamentos destinosLancamentos;

	private List<DestinoLancamento> todosDestinosLancamentos;

	private static final Locale BRAZIL = new Locale("pt", "BR");

	private TipoConta tipoConta;

	private Integer activeIndex = 0;

	@Inject
	private Contas contas;

	private List<Conta> todasContas = new ArrayList<>();

	private List<Conta> todasContas_ = new ArrayList<>();

	private TipoPagamento tipoPagamento = TipoPagamento.AVISTA;

	@NotNull
	private Long parcelas = 3L;

	@NotNull
	private Long parcelas_ = 3L;

	private PeriodoPagamento periodoPagamento = PeriodoPagamento.MESES;

	private boolean contaAPagar = true;

	private boolean lancamentoPago = true;

	private boolean repetirLancamento;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			todasCategoriasDespesas = categoriasLancamentos.todasDespesas();
			todasCategoriasReceitas = categoriasLancamentos.todasReceitas();

			todosDestinosLancamentos = destinosLancamentos.todos();

			Calendar calendar = Calendar.getInstance(BRAZIL);
			despesa.setDataLancamento(calendar.getTime());
			receita.setDataLancamento(calendar.getTime());
		}
	}

	public void buscar() {
		lancamento = lancamentos.porId(lancamento.getId());

		if (lancamento.getId() != null) {
			if (lancamento.getCategoriaLancamento().getTipoLancamento().getOrigem() == OrigemLancamento.DEBITO) {
				categoriaLancamentoDespesa = lancamento.getCategoriaLancamento();
				despesa = lancamento;
				activeIndex = 0;
			} else {
				categoriaLancamentoReceita = lancamento.getCategoriaLancamento();
				receita = lancamento;
				activeIndex = 1;
			}
		}

	}

	public void changePagamento() {
		if (tipoPagamento == TipoPagamento.AVISTA) {
			PrimeFaces.current().executeScript("ocultar();");
		} else {
			PrimeFaces.current().executeScript("mostrar();");
		}
	}

	public void changeRepetir() {
		if (repetirLancamento != true) {
			PrimeFaces.current().executeScript("ocultarRepetir();");
		} else {
			PrimeFaces.current().executeScript("mostrarRepetir();");
		}
	}

	public void gerarParcelas() {

		todasContas = new ArrayList<>();

		Calendar vencimento = Calendar.getInstance();
		vencimento.setTime(lancamento.getDataLancamento());

		Double valorParcela = lancamento.getValor().doubleValue() / parcelas;
		for (int i = 0; i < parcelas; i++) {

			long dias = parcelas;
			if (periodoPagamento == PeriodoPagamento.MESES) {
				// dias = 30;
				// vencimento.add(Calendar.DAY_OF_MONTH, (int) dias);
				vencimento.add(Calendar.MONTH, 1);
			}

			if (periodoPagamento == PeriodoPagamento.QUINZENAS) {
				dias = 15;
				vencimento.add(Calendar.DAY_OF_MONTH, (int) dias);
			}

			if (periodoPagamento == PeriodoPagamento.SEMANAS) {
				dias = 7;
				vencimento.add(Calendar.DAY_OF_MONTH, (int) dias);
			}

			if (periodoPagamento == PeriodoPagamento.DIAS) {
				dias = i + 1;
				vencimento.add(Calendar.DAY_OF_MONTH, (int) dias);
			}

			Conta conta = new Conta();
			conta.setParcela((i + 1) + "/" + parcelas);
			conta.setValor(new BigDecimal(valorParcela));
			conta.setVencimento(vencimento.getTime());

			todasContas.add(conta);
		}
	}

	public void gerarContas() {

		todasContas_ = new ArrayList<>();

		Calendar vencimento = Calendar.getInstance();
		vencimento.setTime(lancamento.getDataLancamento());

		Double valorParcela = lancamento.getValor().doubleValue();
		for (int i = 0; i < parcelas_; i++) {

			long dias = parcelas;
			if (periodoPagamento == PeriodoPagamento.MESES) {
				// dias = 30;
				// vencimento.add(Calendar.DAY_OF_MONTH, (int) dias);
				vencimento.add(Calendar.MONTH, 1);
			}

			if (periodoPagamento == PeriodoPagamento.QUINZENAS) {
				dias = 15;
				vencimento.add(Calendar.DAY_OF_MONTH, (int) dias);
			}

			if (periodoPagamento == PeriodoPagamento.SEMANAS) {
				dias = 7;
				vencimento.add(Calendar.DAY_OF_MONTH, (int) dias);
			}

			if (periodoPagamento == PeriodoPagamento.DIAS) {
				dias = i + 1;
				vencimento.add(Calendar.DAY_OF_MONTH, (int) dias);
			}

			Conta conta = new Conta();
			conta.setParcela((i + 1) + "/" + parcelas_);
			conta.setValor(new BigDecimal(valorParcela));
			conta.setVencimento(vencimento.getTime());

			todasContas_.add(conta);
		}
	}

	public void changeCategoriaDespesa() {
		despesa.setCategoriaLancamento(new CategoriaLancamento());
		despesa.setDestinoLancamento(new DestinoLancamento());

		if (categoriaLancamentoDespesa != null) {
			despesa.setCategoriaLancamento(categoriaLancamentoDespesa);
			despesa.setDestinoLancamento(categoriaLancamentoDespesa.getDestinoLancamento());
		}
	}

	public void changeCategoriaReceita() {
		receita.setCategoriaLancamento(new CategoriaLancamento());
		receita.setDestinoLancamento(new DestinoLancamento());

		if (categoriaLancamentoReceita != null) {
			receita.setCategoriaLancamento(categoriaLancamentoReceita);
			receita.setDestinoLancamento(categoriaLancamentoReceita.getDestinoLancamento());
		}
	}

	public void salvarDespesa() {

		if (categoriaLancamentoDespesa != null) {
			lancamento = despesa;
			PrimeFaces.current().executeScript("PF('confirmDialog').show();");
			todasContas = new ArrayList<>();
			todasContas_ = new ArrayList<>();

		} else {
			PrimeFaces.current().executeScript(
					"swal({ type: 'warning', title: 'Atenção!', text: 'Informe uma categoria de despesa!' });");
		}
	}

	public void salvarReceita() {

		if (categoriaLancamentoReceita != null) {
			lancamento = receita;
			PrimeFaces.current().executeScript("PF('confirmDialog').show();");
			todasContas = new ArrayList<>();
			todasContas_ = new ArrayList<>();

		} else {
			PrimeFaces.current().executeScript(
					"swal({ type: 'warning', title: 'Atenção!', text: 'Informe uma categoria de receita!' });");
		}
	}

	public void confirmarLancamento() {

		boolean contasPagas = false;

		if (lancamento.getId() != null) {
			//List<Conta> contasTemp = contas.porContasPagas(lancamento.getNumeroLancamento(), "LANCAMENTO");

			//if (contasTemp.size() == 0) {

			List<Conta> contasTemp = contas.porCodigoOperacao(lancamento.getNumeroLancamento(), "LANCAMENTO");
			for (Conta conta : contasTemp) {
				contas.remove(conta);
			}

			/*} else {

				String tipoConta = "";
				if (contasTemp.get(0).getTipo().equals("DEBITO")) {
					tipoConta = "contas à pagar";
				} else {
					tipoConta = "contas à receber";
				}

				contasPagas = true;
				PrimeFaces.current().executeScript("stop();swal({ type: 'error', title: 'Erro!', text: 'Existe "
						+ tipoConta + ", pagos já registrados para esse lançamento!' });");
			}*/
		}

		if (contasPagas != true) {

			Lancamento lancamentoTemp = lancamentos.ultimoLancamento();

			if (lancamentoTemp == null) {
				lancamento.setNumeroLancamento(1L);
			} else {
				if (lancamento.getId() == null) {
					lancamento.setNumeroLancamento(lancamentoTemp.getNumeroLancamento() + 1);
				}
			}

			Calendar calendario = Calendar.getInstance();
			Calendar calendarioTemp = Calendar.getInstance();
			calendarioTemp.setTime(lancamento.getDataLancamento());
			calendarioTemp.set(Calendar.HOUR, calendario.get(Calendar.HOUR));
			calendarioTemp.set(Calendar.MINUTE, calendario.get(Calendar.MINUTE));
			calendarioTemp.set(Calendar.SECOND, calendario.get(Calendar.SECOND));
			lancamento.setDataLancamento(calendarioTemp.getTime());

			lancamento.setDia(Long.valueOf((calendarioTemp.get(Calendar.DAY_OF_MONTH))));
			lancamento.setNomeDia(Long.valueOf((calendarioTemp.get(Calendar.DAY_OF_WEEK))));
			lancamento.setSemana(Long.valueOf((calendarioTemp.get(Calendar.WEEK_OF_YEAR))));
			lancamento.setMes(Long.valueOf((calendarioTemp.get(Calendar.MONTH))) + 1);
			lancamento.setAno(Long.valueOf((calendarioTemp.get(Calendar.YEAR))));

			lancamentos.save(lancamento);

			if (tipoPagamento == TipoPagamento.AVISTA) {

				Conta conta = new Conta();
				conta.setParcela(tipoPagamento.name());
				conta.setCodigoOperacao(lancamento.getNumeroLancamento());
				conta.setOperacao("LANCAMENTO");
				conta.setTipo(lancamento.getCategoriaLancamento().getTipoLancamento().getOrigem().name());
				conta.setStatus(lancamentoPago != true ? false : true);
				conta.setValor(lancamento.getValor());
				conta.setVencimento(lancamento.getDataLancamento());

				contas.save(conta);

				if (repetirLancamento) {
					for (Conta contaTemp : todasContas_) {

						contaTemp.setCodigoOperacao(lancamento.getNumeroLancamento());
						contaTemp.setOperacao("LANCAMENTO");
						contaTemp.setTipo(lancamento.getCategoriaLancamento().getTipoLancamento().getOrigem().name());
						contaTemp.setStatus(false);

						contas.save(contaTemp);
					}
				}

			} else {
				for (Conta conta : todasContas) {

					conta.setCodigoOperacao(lancamento.getNumeroLancamento());
					conta.setOperacao("LANCAMENTO");
					conta.setTipo(lancamento.getCategoriaLancamento().getTipoLancamento().getOrigem().name());
					conta.setStatus(false);

					contas.save(conta);
				}
			}

			if (lancamento.getId() == null) {

				if (lancamento.getCategoriaLancamento().getTipoLancamento().getOrigem() == OrigemLancamento.DEBITO) {

					PrimeFaces.current()
							.executeScript("swal({ type: 'success', title: 'Concluído!', text: 'Lançamento N."
									+ lancamento.getNumeroLancamento() + " registrado com sucesso!' });");
					PrimeFaces.current().ajax().update("form:tabView:panel-despesa");

					despesa = new Lancamento();
					categoriaLancamentoDespesa = new CategoriaLancamento();

				} else {
					PrimeFaces.current()
							.executeScript("swal({ type: 'success', title: 'Concluído!', text: 'Lançamento N."
									+ lancamento.getNumeroLancamento() + " registrado com sucesso!' });");
					PrimeFaces.current().ajax().update("form:tabView:panel-receita");

					receita = new Lancamento();
					categoriaLancamentoReceita = new CategoriaLancamento();
				}

				tipoPagamento = TipoPagamento.AVISTA;
				lancamentoPago = true;
				repetirLancamento = false;

			} else {

				PrimeFaces.current().executeScript("swal({ type: 'success', title: 'Concluído!', text: 'Lançamento N."
						+ lancamento.getNumeroLancamento() + " atualizado com sucesso!' });");
			}
		}

	}

	public Lancamento getDespesa() {
		return despesa;
	}

	public void setDespesa(Lancamento despesa) {
		this.despesa = despesa;
	}

	public Lancamento getReceita() {
		return receita;
	}

	public void setReceita(Lancamento receita) {
		this.receita = receita;
	}

	public List<DestinoLancamento> getTodosDestinosLancamentos() {
		return todosDestinosLancamentos;
	}

	public List<TipoLancamento> getTodosTiposDespesas() {
		return todosTiposDespesas;
	}

	public List<CategoriaLancamento> getTodasCategoriasDespesas() {
		return todasCategoriasDespesas;
	}

	public List<CategoriaLancamento> getTodasCategoriasReceitas() {
		return todasCategoriasReceitas;
	}

	public CategoriaLancamento getCategoriaLancamentoDespesa() {
		return categoriaLancamentoDespesa;
	}

	public void setCategoriaLancamentoDespesa(CategoriaLancamento categoriaLancamentoDespesa) {
		this.categoriaLancamentoDespesa = categoriaLancamentoDespesa;
	}

	public CategoriaLancamento getCategoriaLancamentoReceita() {
		return categoriaLancamentoReceita;
	}

	public void setCategoriaLancamentoReceita(CategoriaLancamento categoriaLancamentoReceita) {
		this.categoriaLancamentoReceita = categoriaLancamentoReceita;
	}

	public OrigemLancamento[] getOrigensLancamentos() {
		return OrigemLancamento.values();
	}

	public TipoConta[] getTiposContas() {
		return TipoConta.values();
	}

	public TipoConta getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(TipoConta tipoConta) {
		this.tipoConta = tipoConta;
	}

	public Lancamento getLancamento() {
		return lancamento;
	}

	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}

	public Integer getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(Integer activeIndex) {
		this.activeIndex = activeIndex;
	}

	public TipoPagamento[] getTiposPagamentos() {
		return TipoPagamento.values();
	}

	public List<Conta> getTodasContas() {
		return todasContas;
	}

	public TipoPagamento getTipoPagamento() {
		return tipoPagamento;
	}

	public void setTipoPagamento(TipoPagamento tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

	public PeriodoPagamento getPeriodoPagamento() {
		return periodoPagamento;
	}

	public void setPeriodoPagamento(PeriodoPagamento periodoPagamento) {
		this.periodoPagamento = periodoPagamento;
	}

	public boolean isContaAPagar() {
		return contaAPagar;
	}

	public void setContaAPagar(boolean contaAPagar) {
		this.contaAPagar = contaAPagar;
	}

	public Long getParcelas() {
		return parcelas;
	}

	public void setParcelas(Long parcelas) {
		this.parcelas = parcelas;
	}

	public PeriodoPagamento[] getPeriodosPagamentos() {
		return PeriodoPagamento.values();
	}

	public Integer getTodasContasSize() {
		return todasContas.size();
	}

	public Integer getTodasContasSize_() {
		return todasContas_.size();
	}

	public boolean isLancamentoPago() {
		return lancamentoPago;
	}

	public void setLancamentoPago(boolean lancamentoPago) {
		this.lancamentoPago = lancamentoPago;
	}

	public boolean isRepetirLancamento() {
		return repetirLancamento;
	}

	public void setRepetirLancamento(boolean repetirLancamento) {
		this.repetirLancamento = repetirLancamento;
	}

	public List<Conta> getTodasContas_() {
		return todasContas_;
	}

	public Long getParcelas_() {
		return parcelas_;
	}

	public void setParcelas_(Long parcelas_) {
		this.parcelas_ = parcelas_;
	}
}
