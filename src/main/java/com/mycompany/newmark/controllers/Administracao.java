package com.mycompany.newmark.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.mycompany.newmark.*;
import com.mycompany.newmark.DAO.BancosDAO;
import com.mycompany.newmark.DAO.EtiquetaDAO;
import com.mycompany.newmark.DAO.IdentificadorMateriaDAO;
import com.mycompany.newmark.DAO.IdentificadorPeticaoInicialDAO;
import com.mycompany.newmark.DAO.TipoMovimentacaoDAO;
import com.mycompany.newmark.DAO.UsuarioLocalDAO;
import com.mycompany.newmark.entities.UsuarioLocal;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Administracao implements Initializable {

	@FXML
	private JFXTextField identificadorEtiqueta, numeroEtiquetas, numeroTipoMovimentacao, numeroIdentificadorMateria,
			numeroPeticaoInicial, pedido, complementoPedido, buscaIdentificadorMateriaID, buscaIdentificadorMateria,
			identificadorPeticaoInicial, pesquisaIdentificador, pesquisaEtiqueta, pesquisaEtiquetaId, palavraChave,
			complemento, etiqueta, buscaTipoMovimentacao, buscaPasta, nomePasta, tipoMovimentacao, textoSigla, textoBanco, textoNomeUsuario,
			textoSenhaUsuarioVisivel;
	@FXML
	private JFXPasswordField textoSenhaUsuario;
	@FXML
	private JFXComboBox<String> comboBoxBancos, comboBoxNucleo;
	@FXML
	private TableView<Chaves_GrupoEtiquetas> tabelaBancos;
	@FXML
	private TableView<Chaves_Banco> tabelaIdentificadorMateria, tabelaEtiquetas;
	@FXML
	private TableView<Chaves_Condicao> tabelaTipoMovimento;
	@FXML
	private TableView<Chaves_Condicao> tabelaIdentificadorPeticao;
	@FXML
	private TableView<Chaves_Pasta_Condicao> tabelaPasta;
	@FXML
	private TableView<UsuarioLocal> tabelaUsuarios;
	@FXML
	private TableColumn<UsuarioLocal, String> nomeUsuario;

	@FXML
	private TableColumn<Chaves_Banco, String> identificadorMateriaID, identificadorMateriaPedido,
			identificadorMateriaComplemento, identificadorMateriaSubnucleo, identificadorMateriaPeso, etiquetaID,
			etiquetaFraseChave, etiquetaComplemento, etiquetaEtiqueta, etiquetaPeso, etiquetaTipo,
			identificadorMateriaEtiqueta;
	@FXML
	private TableColumn<Chaves_Condicao, String> colunaTipoMovimento;
	@FXML
	private TableColumn<Chaves_Pasta_Condicao, String> colunaPasta;
	@FXML
	private TableColumn<Chaves_Condicao, String> colunaIdentificadorPeticao;
	@FXML
	private TableColumn<Chaves_Condicao, String> colunaIdentificadorPeticaoInicial;
	@FXML
	private TableColumn<Chaves_GrupoEtiquetas, String> bancoSigla, bancoNome, bancoNumeroDeEtiquetas;
	@FXML
	private RadioButton etiquetaP1, etiquetaP2, etiquetaP3, etiquetaP4, identificadorMateriaP1, identificadorMateriaP2,
			identificadorMateriaP3, identificadorMateriaP4, documento, movimentacao;
	@FXML
	private JFXButton inserirIdentificadorMateria, btnAtualizarUsuario;
	@FXML
	private JFXCheckBox exibirSenha;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		inicializarMenuPeticaoInicial();
		inicializarMenuTriagemPadrao();
		inicializarBancoDeDados();
		inicializarUsuarios();
	}

	/* Inicializações */

	public void inicializarMenuPeticaoInicial() {
		/* Inicializa a tabela Identificador de Matérias */
		ObservableList<String> subnucleos = FXCollections.observableArrayList("ER-SEAS", "ETR-BI", "ER-TRU");
		comboBoxNucleo.setItems(subnucleos);

		List<Chaves_Banco> listaIdentificadoresMateria = new IdentificadorMateriaDAO().getTabelaIdentificadorMateria();

		identificadorMateriaID.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("ID"));
		identificadorMateriaPedido.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("PALAVRACHAVE"));
		identificadorMateriaComplemento
				.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("COMPLEMENTO"));
		identificadorMateriaPeso.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("PRIORIDADE"));
		identificadorMateriaSubnucleo.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("subnucleo"));
		identificadorMateriaEtiqueta.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("ETIQUETA"));
		ObservableList<Chaves_Banco> identificadoresDeMateria = FXCollections
				.observableArrayList(listaIdentificadoresMateria);
		tabelaIdentificadorMateria.setItems(identificadoresDeMateria);
		numeroIdentificadorMateria.setText(String.valueOf(tabelaIdentificadorMateria.getItems().size()));

		/* Inicializa a tabela Identificador de Petição Inicial */
		List<Chaves_Condicao> listaIdentificadorPeticao = new IdentificadorPeticaoInicialDAO()
				.getTabelaIdentificadorPeticaoInicial();

		colunaIdentificadorPeticaoInicial
				.setCellValueFactory(new PropertyValueFactory<Chaves_Condicao, String>("TEXTO"));
		ObservableList<Chaves_Condicao> identPeticao = FXCollections.observableArrayList(listaIdentificadorPeticao);
		tabelaIdentificadorPeticao.setItems(identPeticao);
		numeroPeticaoInicial.setText(String.valueOf(tabelaIdentificadorPeticao.getItems().size()));
	}

	public void inicializarMenuTriagemPadrao() {
		/* Inicializa os campos do comboBox */
		comboBoxBancos.setItems(new Banco().setarBanco());
		try {
			comboBoxBancos.getItems().remove(0);
			comboBoxBancos.getSelectionModel().selectFirst();
		} catch (Exception e) {
		}

		/* Inicializa a tabela Etiquetas */
		String bancoSelecionado = "";
		try {
			bancoSelecionado = comboBoxBancos.getSelectionModel().getSelectedItem().substring(0, 3);
		} catch (Exception e) {

		}
		List<Chaves_Banco> listaTabelaEtiquetas = new EtiquetaDAO().getTabelaEtiqueta(bancoSelecionado);

		etiquetaID.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("ID"));
		etiquetaFraseChave.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("PALAVRACHAVE"));
		etiquetaComplemento.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("COMPLEMENTO"));
		etiquetaEtiqueta.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("ETIQUETA"));
		etiquetaPeso.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("PRIORIDADE"));
		etiquetaTipo.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("TIPO"));
		ObservableList<Chaves_Banco> genericos = FXCollections.observableArrayList(listaTabelaEtiquetas);

		tabelaEtiquetas.setItems(genericos);
		numeroEtiquetas.setText(String.valueOf(tabelaEtiquetas.getItems().size()));

		/* Inicializa tabela Tipos de Movimentação */
		List<Chaves_Condicao> listaTiposMovimentacao = new TipoMovimentacaoDAO().getTabelaTipoMovimentacao();
		colunaTipoMovimento.setCellValueFactory(new PropertyValueFactory<Chaves_Condicao, String>("TEXTO"));
		ObservableList<Chaves_Condicao> tiposMovimentacao = FXCollections.observableArrayList(listaTiposMovimentacao);
		tabelaTipoMovimento.setItems(tiposMovimentacao);
		numeroTipoMovimentacao.setText(String.valueOf(tabelaTipoMovimento.getItems().size()));

		// Inicializa a Tabela de Pastas
		List<Chaves_Pasta_Condicao> listaPasta = new TipoMovimentacaoDAO().getTabelaPastas();
		colunaPasta.setCellValueFactory(new PropertyValueFactory<Chaves_Pasta_Condicao, String>("nome"));
		ObservableList<Chaves_Pasta_Condicao> tabelaPastas = FXCollections.observableArrayList(listaPasta);
		tabelaPasta.setItems(tabelaPastas);
		numeroTipoMovimentacao.setText(String.valueOf(tabelaTipoMovimento.getItems().size()));
	}

	public void inicializarBancoDeDados() {
		/* Inicializa a tabela Banco de Dados */
		List<Chaves_GrupoEtiquetas> chaves = new BancosDAO().getTabelaBancoDeDados();
		bancoSigla.setCellValueFactory(new PropertyValueFactory<Chaves_GrupoEtiquetas, String>("sigla"));
		bancoNome.setCellValueFactory(new PropertyValueFactory<Chaves_GrupoEtiquetas, String>("nome"));
		bancoNumeroDeEtiquetas
				.setCellValueFactory(new PropertyValueFactory<Chaves_GrupoEtiquetas, String>("qntEtiquetas"));
		ObservableList<Chaves_GrupoEtiquetas> genericos = FXCollections.observableArrayList(chaves);
		tabelaBancos.setItems(genericos);
	}

	public void inicializarUsuarios() {
		List<UsuarioLocal> usuarios = new UsuarioLocalDAO().getTabelaUsuarios();
		nomeUsuario.setCellValueFactory(new PropertyValueFactory<UsuarioLocal, String>("nome"));
		ObservableList<UsuarioLocal> users = FXCollections.observableArrayList(usuarios);
		tabelaUsuarios.setItems(users);
	}

	/* Global */
	@FXML
	public void retornaMenu(ActionEvent event) {
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		stage.close();
	}

	/* Identificador de Matéria */

	@FXML
	public void inserirIdentificadorMateria() {
		Boolean pedidoNaoEstaVazio = !pedido.getText().trim().isEmpty() || pedido != null;
		Boolean subnucleoFoiSelecionado = !comboBoxNucleo.getSelectionModel().isEmpty();
		// Boolean etiquetaNaoEstaVazia = !identificadorEtiqueta.getText().isEmpty();
		Integer pesoSelecionado;

		if (identificadorMateriaP1.isSelected()) {
			pesoSelecionado = 1;
		} else if (identificadorMateriaP2.isSelected()) {
			pesoSelecionado = 2;
		} else if (identificadorMateriaP3.isSelected()) {
			pesoSelecionado = 3;
		} else {
			pesoSelecionado = 4;
		}


		if (pedidoNaoEstaVazio && subnucleoFoiSelecionado) {
			new IdentificadorMateriaDAO().inserirIdentificadorMateria(pedido.getText().toUpperCase().replace("'", "").replace("´", ""), complementoPedido.getText().toUpperCase().replace("'", "").replace("´", ""),
					comboBoxNucleo.getSelectionModel().getSelectedItem(), pesoSelecionado,
					identificadorEtiqueta.getText().toUpperCase().replace("'", "").replace("´", ""));
			inicializarMenuPeticaoInicial();
		} else {
			new Aviso().aviso("Revise os campos preenchidos");
		}

		inicializarMenuTriagemPadrao();

	}

	@FXML
	public void alterarIdentificadorMateria() throws IOException {
		Chaves_Banco chave = new Chaves_Banco();
		chave.setID(tabelaIdentificadorMateria.getSelectionModel().getSelectedItem().getID());
		chave.setPALAVRACHAVE(tabelaIdentificadorMateria.getSelectionModel().getSelectedItem().getPALAVRACHAVE());
		chave.setCOMPLEMENTO(tabelaIdentificadorMateria.getSelectionModel().getSelectedItem().getCOMPLEMENTO());
		chave.setETIQUETA(tabelaIdentificadorMateria.getSelectionModel().getSelectedItem().getETIQUETA());
		chave.setTIPO(tabelaIdentificadorMateria.getSelectionModel().getSelectedItem().getTIPO());
		chave.setPRIORIDADE(tabelaIdentificadorMateria.getSelectionModel().getSelectedItem().getPRIORIDADE());
		chave.setBANCO("PET");

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TagEdicaoMateria.fxml"));
		loader.setController(new Controller_TagEdicaoMateria(chave));
		Parent root = loader.load();
		Stage stage = new Stage();
		stage.setTitle("Editar Etiqueta");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setScene(new Scene(root));
		stage.show();

	}

	@FXML
	public void excluirIdentificadorMateria() {
		Integer idSelecionado = tabelaIdentificadorMateria.getSelectionModel().getSelectedItem().getID();
		new IdentificadorMateriaDAO().removerIdentificadorMateria(idSelecionado);
		inicializarMenuPeticaoInicial();
	}

	public void limparIdentificadorMateria() {
		buscaIdentificadorMateriaID.clear();
		pedido.clear();
		complementoPedido.clear();
		comboBoxNucleo.getSelectionModel().clearSelection();
	}

	public void selecionarIdentificadorMateria() {
		pedido.setText(tabelaIdentificadorMateria.getSelectionModel().getSelectedItem().getPALAVRACHAVE());
		complementoPedido.setText(tabelaIdentificadorMateria.getSelectionModel().getSelectedItem().getCOMPLEMENTO());
		comboBoxNucleo.getSelectionModel()
				.select(tabelaIdentificadorMateria.getSelectionModel().getSelectedItem().getSubnucleo());
	}

	public void buscaIDIdentificadorMateria() {
		String id = buscaIdentificadorMateriaID.getText();
		if (id.isEmpty()) {
			inicializarMenuPeticaoInicial();
		} else {
			List<Chaves_Banco> listaIdentificadoresMateria = new IdentificadorMateriaDAO().buscaIdentificadorPorID(id);
			identificadorMateriaID.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("ID"));
			identificadorMateriaPedido
					.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("PALAVRACHAVE"));
			identificadorMateriaComplemento
					.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("COMPLEMENTO"));
			identificadorMateriaSubnucleo
					.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("subnucleo"));
			identificadorMateriaEtiqueta
					.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("ETIQUETA"));
			identificadorMateriaPeso.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("PRIORIDADE"));
			ObservableList<Chaves_Banco> identificadoresDeMateria = FXCollections
					.observableArrayList(listaIdentificadoresMateria);
			tabelaIdentificadorMateria.setItems(identificadoresDeMateria);
			numeroIdentificadorMateria.setText(String.valueOf(tabelaIdentificadorMateria.getItems().size()));
		}
	}

	public void buscaIdentificadorMateria() {
		String textoBusca = buscaIdentificadorMateria.getText().toUpperCase();
		if (textoBusca.isEmpty()) {
			inicializarMenuPeticaoInicial();
		} else {
			List<Chaves_Banco> listaIdentificadoresMateria = new IdentificadorMateriaDAO()
					.buscaIdentificador(textoBusca);

			identificadorMateriaID.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("ID"));
			identificadorMateriaPedido
					.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("PALAVRACHAVE"));
			identificadorMateriaComplemento
					.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("COMPLEMENTO"));
			identificadorMateriaPeso.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("PRIORIDADE"));
			identificadorMateriaSubnucleo
					.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("subnucleo"));
			identificadorMateriaEtiqueta
					.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("ETIQUETA"));
			ObservableList<Chaves_Banco> identificadoresDeMateria = FXCollections
					.observableArrayList(listaIdentificadoresMateria);
			tabelaIdentificadorMateria.setItems(identificadoresDeMateria);
			numeroIdentificadorMateria.setText(String.valueOf(tabelaIdentificadorMateria.getItems().size()));
		}
	}

	/* Identificador de Petição Inicial */

	public void inserirIdentificadorPeticao() {
		String identificadorPeticao = identificadorPeticaoInicial.getText().toUpperCase();
		if (identificadorPeticao.isEmpty()) {
			new Aviso().aviso("Revise os campos");
		} else {
			new IdentificadorPeticaoInicialDAO().inserirIdentificadorPeticaoInicial(identificadorPeticao);
		}
		inicializarMenuPeticaoInicial();

	}

	public void excluirIdentificadorPeticao() {
		String texto = tabelaIdentificadorPeticao.getSelectionModel().getSelectedItem().getTEXTO();
		new IdentificadorPeticaoInicialDAO().removerIdentificadorPeticaoInicial(texto);
		inicializarMenuPeticaoInicial();
	}

	@FXML
	public void buscaIdentificadorPeticao() {
		String textoBusca = pesquisaIdentificador.getText().toUpperCase();
		System.out.println(textoBusca);
		if (textoBusca.isEmpty()) {
			inicializarMenuPeticaoInicial();
		} else {
			List<Chaves_Condicao> itensIdentificadoresPeticao = new IdentificadorPeticaoInicialDAO()
					.buscarIdentificadorPeticao(textoBusca);
			colunaIdentificadorPeticaoInicial
					.setCellValueFactory(new PropertyValueFactory<Chaves_Condicao, String>("TEXTO"));
			ObservableList<Chaves_Condicao> identPeticao = FXCollections
					.observableArrayList(itensIdentificadoresPeticao);
			tabelaIdentificadorPeticao.setItems(identPeticao);
			numeroPeticaoInicial.setText(String.valueOf(tabelaIdentificadorPeticao.getItems().size()));
		}
	}

	public void alterarIdentificadorPeticao() throws IOException {
		Chaves_Condicao chave = new Chaves_Condicao();
		chave.setTEXTO(tabelaIdentificadorPeticao.getSelectionModel().getSelectedItem().getTEXTO());

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TagEdicaoConfiguracao.fxml"));
		loader.setController(new Controller_TagEdicaoCondicao(chave));
		Parent root = loader.load();
		Stage stage = new Stage();

		stage.setTitle("Editar Condição");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setScene(new Scene(root));
		stage.show();
	}

	public void selecionarIdentificadorPeticao() {
		identificadorPeticaoInicial
				.setText(tabelaIdentificadorPeticao.getSelectionModel().getSelectedItem().getTEXTO());
	}

	/* Etiquetas */

	public void selecionarBancoEtiqueta() {
		String bancoSelecionado = comboBoxBancos.getSelectionModel().getSelectedItem().substring(0, 3);

		List<Chaves_Banco> listaTabelaEtiquetas = new EtiquetaDAO().getTabelaEtiqueta(bancoSelecionado);

		etiquetaID.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("ID"));
		etiquetaFraseChave.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("PALAVRACHAVE"));
		etiquetaComplemento.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("COMPLEMENTO"));
		etiquetaEtiqueta.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("ETIQUETA"));
		etiquetaPeso.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("PRIORIDADE"));
		etiquetaTipo.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("TIPO"));
		ObservableList<Chaves_Banco> genericos = FXCollections.observableArrayList(listaTabelaEtiquetas);

		tabelaEtiquetas.setItems(genericos);
		numeroEtiquetas.setText(String.valueOf(tabelaEtiquetas.getItems().size()));
	}

	public void buscaEtiqueta() {
		String textoEtiqueta = pesquisaEtiqueta.getText().toUpperCase();
		if (textoEtiqueta.isEmpty()) {
			inicializarMenuTriagemPadrao();
		} else {
			List<Chaves_Banco> etiquetas = new EtiquetaDAO().buscaEtiqueta(textoEtiqueta);
			etiquetaID.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("ID"));
			etiquetaFraseChave.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("PALAVRACHAVE"));
			etiquetaComplemento.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("COMPLEMENTO"));
			etiquetaEtiqueta.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("ETIQUETA"));
			etiquetaPeso.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("PRIORIDADE"));
			etiquetaTipo.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("TIPO"));
			ObservableList<Chaves_Banco> genericos = FXCollections.observableArrayList(etiquetas);

			tabelaEtiquetas.setItems(genericos);
			numeroEtiquetas.setText(String.valueOf(tabelaEtiquetas.getItems().size()));
		}
	}

	public void buscaEtiquetaID() {
		String id = pesquisaEtiquetaId.getText().toUpperCase();
		if (id.isEmpty()) {
			inicializarMenuTriagemPadrao();
		} else {
			List<Chaves_Banco> etiquetas = new EtiquetaDAO().buscaEtiquetaPorID(id);
			etiquetaID.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("ID"));
			etiquetaFraseChave.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("PALAVRACHAVE"));
			etiquetaComplemento.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("COMPLEMENTO"));
			etiquetaEtiqueta.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("ETIQUETA"));
			etiquetaPeso.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("PRIORIDADE"));
			etiquetaTipo.setCellValueFactory(new PropertyValueFactory<Chaves_Banco, String>("TIPO"));
			ObservableList<Chaves_Banco> genericos = FXCollections.observableArrayList(etiquetas);

			tabelaEtiquetas.setItems(genericos);
			numeroEtiquetas.setText(String.valueOf(tabelaEtiquetas.getItems().size()));
		}
	}

	public void limparEtiqueta() {
		pesquisaEtiquetaId.clear();
		palavraChave.clear();
		complemento.clear();
		etiqueta.clear();
		documento.setSelected(true);
	}

	public void atualizarTabelaEtiqueta() {
		inicializarMenuTriagemPadrao();
	}

	public void inserirEtiqueta() {
		String palavra = this.palavraChave.getText().toUpperCase();
		String comp = this.complemento.getText().toUpperCase();
		String etiq = this.etiqueta.getText().toUpperCase();
		String banco = comboBoxBancos.getSelectionModel().getSelectedItem().substring(0, 3);

		String tipo;
		if (documento.isSelected()) {
			tipo = "DOC";
		} else {
			tipo = "MOV";
		}

		String peso = "1";
		if (etiquetaP1.isSelected())
			peso = "1";
		if (etiquetaP2.isSelected())
			peso = "2";
		if (etiquetaP3.isSelected())
			peso = "3";
		if (etiquetaP4.isSelected())
			peso = "4";

		if (palavra.isEmpty() || etiq.isEmpty()) {
			new Aviso().aviso("Revise os campos");
		} else {
			new EtiquetaDAO().inserirEtiqueta(palavra, comp, etiq, tipo, peso, banco);
			inicializarMenuTriagemPadrao();
		}
	}

	public void excluirEtiqueta() {
		Integer id = tabelaEtiquetas.getSelectionModel().getSelectedItem().getID();
		new EtiquetaDAO().removerEtiqueta(id);
		inicializarMenuTriagemPadrao();
	}

	public void selecionarEtiqueta() {
		palavraChave.setText(tabelaEtiquetas.getSelectionModel().getSelectedItem().getPALAVRACHAVE());
		complemento.setText(tabelaEtiquetas.getSelectionModel().getSelectedItem().getCOMPLEMENTO());
		etiqueta.setText(tabelaEtiquetas.getSelectionModel().getSelectedItem().getETIQUETA());
		String peso = tabelaEtiquetas.getSelectionModel().getSelectedItem().getPRIORIDADE();
		String leitura = tabelaEtiquetas.getSelectionModel().getSelectedItem().getTIPO();

		switch (peso) {
		case "1":
			etiquetaP1.setSelected(true);
			break;
		case "2":
			etiquetaP2.setSelected(true);
			break;
		case "3":
			etiquetaP3.setSelected(true);
			break;
		case "4":
			etiquetaP4.setSelected(true);
			break;
		default:
			break;
		}

		switch (leitura) {
		case "MOV":
			movimentacao.setSelected(true);
			break;
		case "DOC":
			documento.setSelected(true);
			break;
		default:
			break;
		}
	}

	public void alterarEtiqueta() throws IOException {
		Chaves_Banco chave = new Chaves_Banco();
		String bancoSelecionado = comboBoxBancos.getSelectionModel().getSelectedItem().substring(0, 3);
		chave.setID(tabelaEtiquetas.getSelectionModel().getSelectedItem().getID());
		chave.setPALAVRACHAVE(tabelaEtiquetas.getSelectionModel().getSelectedItem().getPALAVRACHAVE().replace("'", "")
				.replace("´", ""));
		chave.setCOMPLEMENTO(tabelaEtiquetas.getSelectionModel().getSelectedItem().getCOMPLEMENTO().replace("'", "")
				.replace("´", ""));
		chave.setETIQUETA(
				tabelaEtiquetas.getSelectionModel().getSelectedItem().getETIQUETA().replace("'", "").replace("´", ""));
		chave.setTIPO(tabelaEtiquetas.getSelectionModel().getSelectedItem().getTIPO());
		chave.setPRIORIDADE(tabelaEtiquetas.getSelectionModel().getSelectedItem().getPRIORIDADE());
		chave.setBANCO(bancoSelecionado);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TagEdicaoEtiqueta.fxml"));
		loader.setController(new Controller_TagEdicaoEtiqueta(chave));
		Parent root = loader.load();
		Stage stage = new Stage();
		stage.setTitle("Editar Etiqueta");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setScene(new Scene(root));
		stage.show();
	}

	/* Tipos de Movimentação */

	public void buscaTipoMovimentacao() {
		String textoBusca = buscaTipoMovimentacao.getText().toUpperCase();
		if (textoBusca.isEmpty()) {
			inicializarMenuTriagemPadrao();
		} else {
			List<Chaves_Condicao> tipos = new TipoMovimentacaoDAO().buscarTipoMovimentacao(textoBusca);
			colunaTipoMovimento.setCellValueFactory(new PropertyValueFactory<Chaves_Condicao, String>("TEXTO"));
			ObservableList<Chaves_Condicao> tiposMovimentacao = FXCollections.observableArrayList(tipos);
			tabelaTipoMovimento.setItems(tiposMovimentacao);
			numeroTipoMovimentacao.setText(String.valueOf(tabelaTipoMovimento.getItems().size()));
		}
	}

	public void excluirTipoMovimentacao() {
		String texto = tabelaTipoMovimento.getSelectionModel().getSelectedItem().getTEXTO();
		new TipoMovimentacaoDAO().removerTipoMovimentacao(texto);
		inicializarMenuTriagemPadrao();
	}

	public void inserirTipoMovimentacao() {
		String texto = tipoMovimentacao.getText().toUpperCase();
		new TipoMovimentacaoDAO().inserirTipoMovimentacao(texto);
		inicializarMenuTriagemPadrao();
	}

	public void selecionarTipoMovimentacao() {
		tipoMovimentacao.setText(tabelaTipoMovimento.getSelectionModel().getSelectedItem().getTEXTO());
	}

	public void alterarTipoMovimentacao() throws IOException {
		Chaves_Condicao chave = new Chaves_Condicao();
		chave.setTIPO("PRO");
		chave.setTEXTO(
				tabelaTipoMovimento.getSelectionModel().getSelectedItem().getTEXTO().replace("'", "").replace("´", ""));

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TagEdicaoConfiguracao.fxml"));
		loader.setController(new Controller_TagEdicaoCondicao(chave));
		Parent root = loader.load();
		Stage stage = new Stage();

		stage.setTitle("Editar Condição");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setScene(new Scene(root));
		stage.show();
	}

	/* Pasta */

	public void buscaPasta() {
		String textoBusca = buscaPasta.getText().toUpperCase();
		if (textoBusca.isEmpty()) {
			inicializarMenuTriagemPadrao();
		} else {
			List<Chaves_Pasta_Condicao> pastas = new TipoMovimentacaoDAO().buscarPasta(textoBusca);
			colunaPasta.setCellValueFactory(new PropertyValueFactory<Chaves_Pasta_Condicao, String>("nome"));
			ObservableList<Chaves_Pasta_Condicao> tabelaPastas = FXCollections.observableArrayList(pastas);
			tabelaPasta.setItems(tabelaPastas);
			numeroTipoMovimentacao.setText(String.valueOf(tabelaTipoMovimento.getItems().size()));
		}
	}

	public void selecionarPasta() {
		nomePasta.setText(tabelaPasta.getSelectionModel().getSelectedItem().getNome());
	}

	public void excluirPasta() {
		String texto = tabelaPasta.getSelectionModel().getSelectedItem().getNome();
		new TipoMovimentacaoDAO().removerPasta(texto);
		inicializarMenuTriagemPadrao();
	}

	public void inserirPasta() {
		String texto = nomePasta.getText().toUpperCase();
		new TipoMovimentacaoDAO().inserirPasta(texto);
		inicializarMenuTriagemPadrao();
	}

	/* Bancos de Dados */

	public void inserirBanco() {
		String sigla = textoSigla.getText();
		String banco = textoBanco.getText();
		if (sigla.isEmpty() || banco.isEmpty()) {
			new Aviso().aviso("Revise os campos!");
		} else {
			if (sigla.length() > 3) {
				new Aviso().aviso("Sigla não pode conter mais que 3 caracteres");
				return;
			}
			new BancosDAO().inserirBanco(sigla, banco);
			inicializarBancoDeDados();
			inicializarMenuTriagemPadrao();
			inicializarMenuPeticaoInicial();
		}
	}

	public void excluirBanco() {
		String sigla = tabelaBancos.getSelectionModel().getSelectedItem().getSigla();
		new BancosDAO().removerBanco(sigla);
		inicializarBancoDeDados();
		inicializarMenuTriagemPadrao();
		inicializarMenuPeticaoInicial();
	}

	public void selecionarBanco() {
		textoSigla.setText(tabelaBancos.getSelectionModel().getSelectedItem().getSigla());
		textoBanco.setText(tabelaBancos.getSelectionModel().getSelectedItem().getNome());
	}

	/* Usuários */

	public void atualizarUsuario() {
		String antigoNome = tabelaUsuarios.getSelectionModel().getSelectedItem().getNome();
		String nome = textoNomeUsuario.getText();
		String senha = "";

		if (exibirSenha.isSelected()) {
			senha = textoSenhaUsuarioVisivel.getText();
		} else {
			senha = textoSenhaUsuario.getText();
		}

		if (antigoNome.isEmpty() || nome.isEmpty() || senha.isEmpty()) {
			new Aviso().aviso("Selecione um usuário e não deixe nenhum campo vazio");
		} else {
			new UsuarioLocalDAO().atualizarUsuario(nome, senha, antigoNome);
		}
		inicializarUsuarios();
	}

	public void selecionarUsuario() {
		btnAtualizarUsuario.setDisable(false);
		textoNomeUsuario.setText(tabelaUsuarios.getSelectionModel().getSelectedItem().getNome());
		textoSenhaUsuario.setText(tabelaUsuarios.getSelectionModel().getSelectedItem().getSenha());
	}

	public void toggleSenha() {
		if (textoSenhaUsuario.isVisible()) {
			textoSenhaUsuarioVisivel.setVisible(true);
			textoSenhaUsuarioVisivel.setText(textoSenhaUsuario.getText());
			textoSenhaUsuario.setVisible(false);
		} else {
			textoSenhaUsuarioVisivel.setVisible(false);
			textoSenhaUsuario.setText(textoSenhaUsuarioVisivel.getText());
			textoSenhaUsuario.setVisible(true);
		}
	}

}
