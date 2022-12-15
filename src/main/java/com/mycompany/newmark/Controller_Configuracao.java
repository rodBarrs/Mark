/**
 * @author Felipe Marques, João Paulo, Gabriel Ramos, Rafael Henrique
 * 
 * Responsável por controlar a Janela de configuração de triagem do Mark
 */
package com.mycompany.newmark;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.sun.glass.events.KeyEvent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Controller_Configuracao implements Initializable {

	private Chaves_Condicao chave = new Chaves_Condicao();
	private Chaves_Banco chaveBanco = new Chaves_Banco();

	public Controller_Configuracao() {
		this.chave = chave;
	}
	
	@FXML
	Spinner<Integer> spinnerDias = new Spinner<Integer>();;
	@FXML
	Tab nomeTeste;
	@FXML
	TableView<Chaves_Condicao> tabelaIdentificador;
	@FXML
	TableColumn<Chaves_Condicao, String> colunaIdentificador;
	@FXML
	TableView<Chaves_Condicao> tabelaProvidencia;
	@FXML
	TableColumn<Chaves_Condicao, String> colunaProvidencia;
	@FXML
	TableView<Chaves_Banco> tabelaMateria;
	@FXML
	TableColumn<Chaves_Banco, String> colunaPedido, colunaComplementoPedido, colunaNucleo, colunaPeso, colunaId;
	@FXML
	JFXTextField textoProv, textoPet, contTotal, contNao, contDoc, contSeq, pesquisaProvidencia, pesquisaIdentificador,
			pesquisaMateria, pesquisaMateriaId, pedido, complemento;
	@FXML
	JFXButton limparProv, inserirProv, excluirProv, voltarProv, atualizarProvidencia, salvarAvancada, voltarAvancada,
			salvarEspecifica, voltarEspecifica, voltarContador, botaoBuscaProvidencia;
	@FXML
	RadioButton verificaData, triarAntigo, tipoCOM, tipoDOC, tipoMOV, html, pdf, desativadoPericial, ativadoPericial,
			desativadoPeticaoInicial, ativadoPeticaoInicial, desativadoConcatenacao, ativadoConcatenacao, P1, P2, P3, P4;
	@FXML
	private JFXComboBox<String> comboBoxNucleo;
	final ToggleGroup grupoTriarAntigo = new ToggleGroup();
	final ToggleGroup grupoTipo = new ToggleGroup();
	final ToggleGroup grupoJuntada = new ToggleGroup();
	final ToggleGroup grupoPericial = new ToggleGroup();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		atualizar();
	}

	public void atualizar() {
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 30);
		spinnerDias.setValueFactory(valueFactory);

		Chaves_Configuracao configuracao = new Chaves_Configuracao();
		Banco banco = new Banco();
		configuracao = banco.pegarConfiguracao(configuracao);
		if (configuracao.getIntervaloDias() == -1) {
			spinnerDias.setDisable(true);
			triarAntigo.setSelected(true);
		} else {
			spinnerDias.setDisable(false);
			valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, configuracao.getIntervaloDias());
			spinnerDias.setValueFactory(valueFactory);
			verificaData.setSelected(true);
		}
		if (configuracao.isJuntManual()) {
			pdf.setSelected(true);
		} else {
			html.setSelected(true);
		}
		switch (configuracao.getTipoTriagem()) {
		case "COM":
			tipoCOM.setSelected(true);
			break;
		case "DOC":
			tipoDOC.setSelected(true);
			break;
		case "MOV":
			tipoMOV.setSelected(true);
			break;
		}
		if (configuracao.isLaudoPericial()) {
			ativadoPericial.setSelected(true);
		} else {
			desativadoPericial.setSelected(true);
		}
		if (configuracao.isPeticaoInicial()) {
			ativadoPeticaoInicial.setSelected(true);
		} else {
			desativadoPeticaoInicial.setSelected(true);
		}
		if ( configuracao.isConcatenacao()) {
			ativadoConcatenacao.setSelected(true);
		} else {
			desativadoConcatenacao.setSelected(true);
		}
		contadores();
	}

	@FXML
	public void validarSpinner() throws AWTException {
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_ENTER);
	}
	
	@FXML
	public void verificaDataSelecionado() {
		spinnerDias.setDisable(false);
	}

	@FXML
	public void ignorarDataSelecionado() {
		spinnerDias.setDisable(true);
	}

	public void salvarAvancada(ActionEvent event) {
		Aviso aviso = new Aviso();
		boolean juntadaTria;
		Integer periodoData;
		String tipoTria;
		Banco banco = new Banco();
		if (verificaData.isSelected()) {
			// Verificará as datas com o intervalo dado pelo usuário
			periodoData = spinnerDias.getValue();
			if(periodoData <= 0 || periodoData > 100) {
				aviso.aviso("O valor para o período de data não deve ser menor que 1 ou maior que 100");
			}
		} else {
			// NÃO irá verififcar data e considerará as ulitmas 10 movimentações
			periodoData = -1;
		}

		if (tipoCOM.isSelected()) {
			// Realizará a Triagem COMPLETA
			tipoTria = "COM";
		} else if (tipoDOC.isSelected()) {
			// Realizará a Triagem APENAS nos Documentos
			tipoTria = "DOC";
		} else {
			// Realizará a Triagem APENAS na Movimentação
			tipoTria = "MOV";
		}

		if (html.isSelected()) {
			// Irá ler somente movimentações presente no HTML
			juntadaTria = false;
		} else {
			// Irá ler movimentações que possuem PDF e HTML
			juntadaTria = true;
		}

		banco.salvarAvancadas(periodoData, tipoTria, juntadaTria);		
		String textoAviso = "Configuração salva com sucesso!";
		aviso.aviso(textoAviso);
	}

	public void salvarEspecificas(ActionEvent event) {
		boolean pericial;
		boolean peticao;
		boolean concatenacao;
		Banco banco = new Banco();

		if (ativadoPericial.isSelected()) {
			// Irá realiziar a busca pelo laudo pericial na movimentação
			pericial = true;
		} else {
			// Irá ignorar o laudo pericial na movimentação
			pericial = false;
		}

		if (ativadoPeticaoInicial.isSelected()) {
			peticao = true;
		} else {
			peticao = false;
		}

		if (ativadoConcatenacao.isSelected()) {
			concatenacao = true;
		} else {
			concatenacao = false;
		}

		Aviso aviso = new Aviso();
		String textoAviso = "";
		if (ativadoPericial.isSelected() && ativadoPeticaoInicial.isSelected()) {
			textoAviso = "Não é possivel realizar dois modos de pesquisa específicos simultaneamente, ative apenas uma opção!";
			aviso.aviso(textoAviso);
		} else {
			banco.salvarEspecificas(pericial, peticao, concatenacao);
			textoAviso = "Configuração salva com sucesso!";
			aviso.aviso(textoAviso);
		}
	}

	public void contadores() {
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM contador");
			ResultSet resultSet = stmt.executeQuery();
			contTotal.setText(resultSet.getString("ContTotal"));
			contNao.setText(resultSet.getString("ContNao"));
			contDoc.setText(resultSet.getString("ContDoc"));
			contSeq.setText(resultSet.getString("ContSeq"));
			connection.close();
		} catch (SQLException erro) {
			Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
			Aviso aviso = new Aviso();
			aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
		}
	}

	@FXML
	void RetornaMenu(ActionEvent event) {
		Node node = (Node) event.getSource();

		Stage stage = (Stage) node.getScene().getWindow();
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
		} catch (IOException erro) {
		}
		Scene scene = new Scene(root);
		stage.setMinWidth(900);
		stage.setMinHeight(500);
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.show();
		stage.setTitle("New Mark");
	}


}