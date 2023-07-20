package com.example.ootrabalhopratico.pdv.dominio;

import com.example.ootrabalhopratico.pdv.dominio.pagamento.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venda {
	private List<ItemVenda> itensVenda;
    private boolean estaCompleta;
    private LocalDateTime data;
    private Pagamento pagamento;

    public Venda(LocalDateTime data) {
    	this.itensVenda = new ArrayList<>();
        this.data = data;
    }

    public void criarItemVenda(DescricaoProduto desc, int quantidade) {
        ItemVenda iv = new ItemVenda(desc, quantidade);
        itensVenda.add(iv);
    }

    public double fazerPagamento(double quantiaFornecida) {
        pagamento = new PagamentoDinheiro(quantiaFornecida);
        return calcularTroco();
    }

    public void fazerPagamento(double quantiaFornecida, String banco) {
        pagamento = new PagamentoCheque(quantiaFornecida, banco);
    }

    public void fazerPagamento(double quantiaFornecida, Operadora operadora, int quantidadeParcelas, TipoCalculadora tipoCalculadora) {
             pagamento = new PagamentoCartao(quantiaFornecida, operadora, quantidadeParcelas, tipoCalculadora);
        }


    private double calcularTroco() {
        return pagamento.getQuantiaFornecida() - calcularTotalVenda();
    }

    public double calcularTotalVenda() {
        double totalVenda = 0.0;
        for (ItemVenda itemVenda : itensVenda) {
        	   if(itemVenda != null) { 
        		   totalVenda += itemVenda.getDescricaoProduto().getPreco() * itemVenda.getQuantidade();
        	   }
        }
        return totalVenda;
    }

    public void setEstaCompleta(boolean estaCompleta) {
        this.estaCompleta = estaCompleta;
    }

    @Override
    public String toString() {
        String status = estaCompleta ? "completa" : "incompleta";
        String dataTemp = data.getDayOfMonth() + "/" + data.getMonthValue() + "/" + data.getYear();
        String horaTemp = data.getHour() + ":" + data.getMinute()  + ":" + data.getSecond();
        String cabecalho = "Data: " + dataTemp + " hora: " + horaTemp+ "\n" +
                                         "\t\t\t\t\tStatus da venda: " + status + "\n\n" +
                                          " Descrição\t\tPreço Unitário(R$)\t\tQuantidade\t\tSubtotal(R$) \n";
        String corpo = "";

        for (ItemVenda iv : itensVenda)
        	   if (iv != null) {
            corpo += iv.toString();
        	   }

        String rodape = "Total à vista (R$)\t\t\t\t\t\t\t" + calcularTotalVenda() + "\n\n" +
            this.pagamento.toString();
        return cabecalho + corpo + rodape;
    }
}
