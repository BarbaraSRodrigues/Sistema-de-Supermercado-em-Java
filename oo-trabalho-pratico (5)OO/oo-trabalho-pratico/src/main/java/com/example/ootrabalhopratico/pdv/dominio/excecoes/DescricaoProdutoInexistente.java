package com.example.ootrabalhopratico.pdv.dominio.excecoes;

public class DescricaoProdutoInexistente extends Exception {
    private static final long serialVersionUID = 1L;
	private String id;
    
    public DescricaoProdutoInexistente(String mensagem, String id) {
        super(mensagem);
        this.id = id;       
    }

    public String toString() {
        return super.toString() + "\n" +
               "ID....: " + this.id;
    }
}
