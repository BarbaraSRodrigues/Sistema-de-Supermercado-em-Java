package com.example.ootrabalhopratico;

import com.example.ootrabalhopratico.controller.records.AddedProduct;
import com.example.ootrabalhopratico.pdv.dominio.CatalogoProdutos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.example.ootrabalhopratico.pdv.dominio.*;
import com.example.ootrabalhopratico.pdv.dominio.pagamento.Operadora;
import com.example.ootrabalhopratico.HelloController;
import javafx.scene.paint.Color;
import java.time.LocalDateTime;
import java.util.List;
import com.example.ootrabalhopratico.pdv.dominio.DescricaoProduto;

public class HelloController {
    private static final String REGISTRADORA_ID = "R01";
    private static final String SUPERMARKET_NAME = "Supermercado";
    private static final Endereco SUPERMARKET_ADDRESS = new Endereco("Rua X", "", 5, "Alfenas", "Aeroporto", "MG", "37130-000");
    private final Loja loja = new Loja(SUPERMARKET_NAME, SUPERMARKET_ADDRESS);
    private final Registradora registradora = loja.getRegistradora(REGISTRADORA_ID);
    private final CatalogoProdutos catalogo = registradora.getCatalogo();

    @FXML
    private ListView<AddedProduct> produtoListView;

    @FXML
    private ComboBox<String> calculadora;

    @FXML
    private ComboBox<String> pagamento;

    @FXML
    private ComboBox<String> operadora;

    @FXML
    private TextField produtoIdTextField;

    @FXML
    private TextField quantityTextField;

    @FXML
    private TextField amountTextField;

    @FXML
    private TextField bancoTextField;

    @FXML
    private TextField parcelasTextField;

    @FXML
    private Button pagamentoButton;

    @FXML
    private Button finalizarCompraButton;

    @FXML
    private Label totalPagamentoLabel;

    @FXML
    private Label reciboVendaLabel;

    @FXML
    private Label trocoVendaLabel;

    @FXML
    private HBox pagamentoCheque;

    @FXML
    private HBox pagamentoCartao;

    @FXML
    private HBox menuBox;

    @FXML
    private HBox fornecidoBox;

    @FXML
    private VBox pagamentoBox;

    @FXML
    private VBox reciboBox;

    private String pagamentoSelecionado;
    private String operadoraSelecionada;
    private String calculadoraSelecionada;

    @FXML
    public void initialize() {
        menuBox.setDisable(false);
        pagamentoButton.setDisable(true);
        pagamentoCartao.setDisable(true);
        pagamentoCheque.setDisable(true);
        menuBox.setDisable(true);
        pagamentoBox.setDisable(true);
        reciboBox.setVisible(false);

        pagamento.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Pagamento selecionado: " + newValue);
            pagamentoSelecionado = newValue;
            pagamentoButton.setDisable(false);

            switch (newValue) {
                case "Dinheiro":
                    pagamentoCartao.setDisable(true);
                    pagamentoCheque.setDisable(true);
                    fornecidoBox.setDisable(false);
                    amountTextField.setText("");
                    break;
                case "Cheque":
                    pagamentoCartao.setDisable(true);
                    pagamentoCheque.setDisable(false);
                    fornecidoBox.setDisable(true);
                    amountTextField.setText(String.format("%.2f", registradora.getVendaCorrente().calcularTotalVenda()));
                    break;
                case "Cartão":
                    pagamentoCartao.setDisable(false);
                    pagamentoCheque.setDisable(true);
                    fornecidoBox.setDisable(true);
                    amountTextField.setText(String.format("%.2f", registradora.getVendaCorrente().calcularTotalVenda()));
                    break;
            }
        });

        operadora.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Operadora selecionada: " + newValue);
            operadoraSelecionada = newValue;
        });

        calculadora.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Juros selecionado: " + newValue);
            calculadoraSelecionada = newValue;
        });
    }

    @FXML
    public void criarCompra() {
        log("Iniciando criação de nova venda");

        limparItensCarrinho();

        menuBox.setDisable(false);
        finalizarCompraButton.setDisable(false);
        reciboBox.setVisible(false);

        registradora.criarNovaVenda();
        totalAPagar(registradora.getVendaCorrente().calcularTotalVenda());

        log("Finalizando criação de nova venda");
    }

    @FXML
    public void adicionarProduto() {
        log("Iniciando adição de item ao carrinho");

        try {
            int quantidade = Integer.parseInt(quantityTextField.getText());
            registradora.entrarItem(produtoIdTextField.getText(), quantidade);

            DescricaoProduto produto = catalogo.getDescricaoProduto(produtoIdTextField.getText());

            produtoListView.getItems().add(new AddedProduct(produto.getDescricao(), produto.getPreco(), quantidade));
        } catch (Exception e) {
            log("Erro ao adicionar item ao carrinho");
            throw new RuntimeException("Erro ao adicionar item ao carrinho");
        } finally {
            totalAPagar(registradora.getVendaCorrente().calcularTotalVenda());
        }

        log("Finalizando adição de item ao carrinho");
    }

    @FXML
    public void finalizarCompra() {
        log("Iniciando finalização da venda");

        menuBox.setDisable(true);
        pagamentoBox.setDisable(false);
        finalizarCompraButton.setDisable(false);
        fornecidoBox.setDisable(true);

        produtoIdTextField.setText("");
        quantityTextField.setText("");

        registradora.finalizarVenda();

        log("Finalizando finalização da venda");
    }

    @FXML
    public void fazerPagamento() {
        log("Iniciando pagamento da venda");

        pagamentoBox.setDisable(true);

        try {
            double totalAPagar = registradora.getVendaCorrente().calcularTotalVenda();
            double troco = 0d;

            switch (pagamentoSelecionado) {
                case "Dinheiro":
                    double quantidadeFornecida = Double.parseDouble(amountTextField.getText());
                    troco = registradora.fazerPagamento(quantidadeFornecida);
                    break;
                case "Cheque":
                    registradora.fazerPagamento(totalAPagar, bancoTextField.getText());
                    break;
                case "Cartão":
                    int quantidadeParcelas = Integer.parseInt(parcelasTextField.getText());
                    registradora.fazerPagamento(totalAPagar, Operadora.valueOf(operadoraSelecionada), quantidadeParcelas, TipoCalculadora.valueOf(calculadoraSelecionada));
                    break;
            }

            gerarRecibo(troco);
            reciboBox.setVisible(true);

        } catch (Exception e) {
            log("Erro ao realizar o pagamento");
            pagamentoBox.setDisable(false);
            throw new RuntimeException("Erro ao realizar o pagamento");
        }

        reciboBox.setVisible(true);
        log("Finalizando pagamento da venda");
    }

    private void gerarRecibo(double troco) {
        log("Iniciando geração do recibo");

        System.out.println("Recibo: " + registradora.getId() + ", troco: " + troco);
        reciboVendaLabel.setText(registradora.getVendaCorrente().toString());
        trocoVendaLabel.setText("Troco.......: R$ " + troco);

        log("Finalizando geração do recibo");
    }

    private void totalAPagar(double valorTotal) {
        String valuePattern = "total: R$ %.2f";
        totalPagamentoLabel.setText(String.format(valuePattern, valorTotal));
    }

    private void log(String message) {
        String logPattern = "[%s] %s";
        System.out.println(String.format(logPattern, LocalDateTime.now().toString(), message));
    }

    private void limparItensCarrinho() {
        ObservableList<AddedProduct> observableList = produtoListView.getItems();
        observableList.clear();
    }
}