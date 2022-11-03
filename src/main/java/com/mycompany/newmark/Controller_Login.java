/**
 * @author Felipe Marques, Gabriel Ramos, Rafael Henrique e Adriano Vilhena
 *
 * Classe controladora da Janela de login
 */
package com.mycompany.newmark;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.mycompany.newmark.DAO.UsuarioLocalDAO;
import com.mycompany.newmark.controllers.LoginLocal;
import com.mycompany.newmark.entities.UsuarioLocal;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Controller_Login implements Initializable {
	private boolean triagemIniciada = false;
	private Thread backgroundThread;
	public WebDriver driver;
	public WebDriverWait wait;
	public Usuario usuario = new Usuario();

	@FXML
	private static AnchorPane anchor;
	@FXML
	public Label labelDebug;
	@FXML
	public MenuItem popupDebug;
	@FXML
	public JFXButton btInformacao, btAdministracao, btLogin, btTriar, btParar;
	@FXML
	public JFXTextField LoginTxt, SenhaTxtMostar;
	@FXML
	public JFXPasswordField SenhaTxt;

	@FXML
	public ComboBox<String> Bancos;
	public ObservableList<String> Lista;

	@FXML
	public JFXTextArea Saida, SaidaTriagem;

	@FXML
	public JFXProgressBar BarraDeProgresso;

	@FXML
	public CheckBox exibirSenha, salvarSenha;
	private Chaves_Configuracao debugPi;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		debugPi = new Chaves_Configuracao();
		saida();
		exibirSenha.setSelected(false);
		salvarSenha.setSelected(true);
		SenhaTxtMostar.setVisible(false);
		pegarSenha();
		Banco banco = new Banco();
		Bancos.setItems(banco.setarBanco());
		Bancos.setValue("Selecione um banco:");
	}

	@FXML
	public void exibirDebugPI() {
		if (debugPi.isDebugpi()) {
			debugPi.setDebugpi(false);
			popupDebug.setText("Exibir Debug de Petição Inicial");
			labelDebug.setVisible(false);
		} else {
			debugPi.setDebugpi(true);
			popupDebug.setText("Ocultar Debug de Petição Inicial");
			labelDebug.setVisible(true);
		}
	}

	public void saida() {
		try {
			Chaves_Configuracao configuracao = new Chaves_Configuracao();
			Banco banco = new Banco();
			configuracao = banco.pegarConfiguracao(configuracao);
			Saida.setText("CONFIGURAÇÕES:");
			if (configuracao.getIntervaloDias() == -1) {
				Saida.setText(Saida.getText() + "\nConfiguração de Data: Ignorar datas");
			} else {
				Saida.setText(Saida.getText() + "\\nConfiguração de Data: Considerando datas");
			}
			switch (configuracao.getTipoTriagem()) {
				case "COM":
					Saida.setText(Saida.getText() + "\nTriando: MOVIMENTAÇÃO e DOCUMENTO");
					break;
				case "MOV":
					Saida.setText(Saida.getText() + "\nTriando: MOVIMENTAÇÃO");
					break;
				case "DOC":
					Saida.setText(Saida.getText() + "\nTriando: DOCUMENTO");
					break;
			}
			if (configuracao.isJuntManual() == false) {
				Saida.setText(Saida.getText() + "\nConsiderando movimentações: HTML");
			} else {
				Saida.setText(Saida.getText() + "\nConsiderando movimentações: HTML e PDF");
			}

			if (configuracao.isLaudoPericial() == true) {
				Saida.setText("Triando: LAUDO PERICIAL"
						+ "\nO Sistema está buscando EXCLUSIVAMENTE pela configuração de Laudo Pericial dentro de pastas na plataforma Sapiens.");
			}

			if (configuracao.isPeticaoInicial() == true) {
				Saida.setText("Triando: PETIÇÃO INICIAL" + "\nO Sistema está triando as petições iniciais.");
				if (configuracao.getIntervaloDias() == -1) {
					Saida.setText(Saida.getText() + "\nConfiguração de Data: Ignorar datas");
				} else {
					Saida.setText(Saida.getText() + "\nConfiguração de Data: Considerando datas");
				}
				switch (configuracao.getTipoTriagem()) {
					case "COM":
						Saida.setText(Saida.getText() + "\nTriando: MOVIMENTAÇÃO e DOCUMENTO");
						break;
					case "MOV":
						Saida.setText(Saida.getText() + "\nTriando: MOVIMENTAÇÃO");
						break;
					case "DOC":
						Saida.setText(Saida.getText() + "\nTriando: DOCUMENTO");
						break;
				}
			}

		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}

	public void exibirSenha() {
		if (exibirSenha.isSelected()) {
			// Exibir senha
			SenhaTxtMostar.setText(SenhaTxt.getText());
			SenhaTxt.clear();
			SenhaTxtMostar.setVisible(true);
			SenhaTxt.setVisible(false);
		} else {
			// Esconde senha
			SenhaTxt.setText(SenhaTxtMostar.getText());
			SenhaTxtMostar.clear();
			SenhaTxt.setVisible(true);
			SenhaTxtMostar.setVisible(false);
		}
	}

	public void pegarSenha() {
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM configuracao WHERE id = 1997");
			ResultSet resultadoBanco = stmt.executeQuery();
			while (resultadoBanco.next()) {
				LoginTxt.setText(resultadoBanco.getString("Login"));
				String senha = descriptografar(resultadoBanco.getString("Senha"));
				if (exibirSenha.isSelected()) {
					// Exibir senha
					SenhaTxtMostar.clear();
					SenhaTxtMostar.setText(senha);
				} else {
					// Esconder senha
					SenhaTxt.clear();
					SenhaTxt.setText(senha);
				}
			}
			connection.close();
		} catch (Exception erro) {
			Aviso aviso = new Aviso();
			aviso.aviso(erro.getMessage());
			erro.printStackTrace();
			Logger.getLogger(Chaves_Configuracao.class.getName()).log(Level.SEVERE, null, erro);
		}
	}

	@FXML
	public void informacao(ActionEvent event) {
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/fxml/Informacao.fxml"));
		} catch (Exception erro) {
			Aviso aviso = new Aviso();
			aviso.aviso(erro.getMessage());
			erro.printStackTrace();
			Logger.getLogger(Chaves_Configuracao.class.getName()).log(Level.SEVERE, null, erro);
		}

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.setTitle("Sistema de Triagem Mark - Informações");
		stage.show();
	}

	@FXML
	public void abrirPopupLogin() throws IOException {
		abrirJanelaAdministracao();

	}


	@FXML
	public void abrirJanelaAdministracao() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Administracao.fxml"));
		Parent root = loader.load();
		Stage stage = new Stage();
		stage.setTitle("Sistema de Triagem Mark - Administração");
		stage.setScene(new Scene(root));
		stage.getIcons().add(new Image("/fxml/Imagens/iconeMark.png"));
		stage.setResizable(false);
		stage.show();
	}


	@FXML
	public void editarBancos(ActionEvent event) {
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/fxml/BancosEdicao.fxml"));
		} catch (Exception erro) {
			Aviso aviso = new Aviso();
			aviso.aviso(erro.getMessage());
			erro.printStackTrace();
			Logger.getLogger(Chaves_Configuracao.class.getName()).log(Level.SEVERE, null, erro);
		}

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.setMinWidth(900);
		stage.setMinHeight(500);
		stage.setTitle("Sistema de Triagem Mark - Edição de Bancos");
		stage.show();
	}

	@FXML
	public void configuracao(ActionEvent event) {
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/fxml/Configuracao.fxml"));
		} catch (Exception erro) {
			Aviso aviso = new Aviso();
			aviso.aviso(erro.getMessage());
			erro.printStackTrace();
			Logger.getLogger(Chaves_Configuracao.class.getName()).log(Level.SEVERE, null, erro);
		}

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.setTitle("Sistema de Triagem Mark - Configurações");
		stage.show();
	}

	@FXML
	public void triar(ActionEvent event) {
		som();
		Runnable task = new Runnable() {
			@Override
			public void run() {
				triagemIniciada();
			}
		};
		// Run the task in a background thread
		backgroundThread = new Thread(task);
		// Terminate the running thread if the application exits
		backgroundThread.setDaemon(true);
		// Start the thread
		backgroundThread.start();
	}

	public void triagemIniciada() {
		try {
			if (!driver.getTitle().contains("SAPIENS")) {
				som();
				SaidaTriagem.setText("- Erro: Login Não Realizado!");
				BarraDeProgresso.setVisible(false);
			} else {
				if (Bancos.getValue().contains("Selecione")) {
					SaidaTriagem.setText("- Selecione um banco para a triagem!");
				} else {
					Banco banco = new Banco();
					String bancoSelecionado = banco.selecionarBanco(Bancos.getSelectionModel().getSelectedItem());
					if (!bancoSelecionado.equals("")) {
						SaidaTriagem.setText("- Triagem Iniciada!");
						BarraDeProgresso.setVisible(true);
						Processo_Triagem triagem = new Processo_Triagem(driver, bancoSelecionado);
						boolean triar = triagem.iniciarTriagem(driver, wait, bancoSelecionado, triagemIniciada,
								debugPi);
						if (triar == false) {
							som();
							SaidaTriagem.setText("- Erro de comunicação com plataforma Sapiens!");
							BarraDeProgresso.setVisible(false);
						} else {
							JOptionPane.showMessageDialog(null, "Não há tarefas à serem triadas");
							SaidaTriagem.setText("- Triagem finalizada");
							BarraDeProgresso.setVisible(false);
						}
					}
				}
			}
			triagemIniciada = true;
		} catch (Exception erro) {
			som();
			SaidaTriagem.setText("- Erro: Login Não Realizado!");
			BarraDeProgresso.setVisible(false);
			erro.printStackTrace();
			Logger.getLogger(Chaves_Configuracao.class.getName()).log(Level.SEVERE, null, erro);
		}
	}

	@FXML
	public void parar(ActionEvent event) {
		triagemIniciada = false;
		BarraDeProgresso.setVisible(false);
		SaidaTriagem.setText("- Triagem Parada!");
		som();
		try {

			driver.quit();
			backgroundThread.stop();
		} catch (Exception e) {
		}

	}

	public void apertouEnter(KeyEvent event) throws InterruptedException, IOException {
		if (event.getCode() == KeyCode.ENTER) {
			som();
			login();
		}
	}

	@FXML
	public void login() throws InterruptedException, IOException {
		som();
		Banco banco = new Banco();
		Usuario usuario = new Usuario();
		usuario.setLogin(LoginTxt.getText());
		if (exibirSenha.isSelected()) {
			usuario.setSenha(SenhaTxtMostar.getText());
		} else {
			usuario.setSenha(SenhaTxt.getText());
		}
		if (salvarSenha.isSelected()) {
			Usuario usuarioSalvar = new Usuario();
			usuarioSalvar.setLogin(usuario.getLogin());
			usuarioSalvar.setSenha(criptografar(usuario.getSenha()));
			banco.salvarSenha(usuarioSalvar);
		} else {
			Usuario zerar = new Usuario();
			zerar.setLogin("");
			zerar.setSenha("");
			banco.salvarSenha(zerar);
		}
		try {
			if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
				System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
			} else {
				System.setProperty("webdriver.gecko.driver", "geckodriver.tar");
			}
		} catch (Exception erro) {
			Aviso aviso = new Aviso();
			String avisoTexto = "Driver de comunicação não encontrado, favor contatar o desenvolvedor.";
			aviso.aviso(avisoTexto);
		}
		// Cria a pasta para receber os pdfs
		Files.createDirectories(Paths.get("C:\\MarkPDF"));
		// Configurações de perfil para o Firefox
		// Seta a pasta de downloads para a mencionada e faz o download automático de
		// pdfs
		FirefoxProfile profile = new FirefoxProfile();
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		profile.setPreference("browser.download.dir", "C:\\MarkPDF"); // folder
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf"); // MIME type
		profile.setPreference("pdfjs.disabled", true); // disable the built-in viewer
		profile.setPreference("browser.download.folderList", 2);
		// profile.setPreference("browser.download.panel.shown", false);

		capabilities.setCapability(FirefoxDriver.PROFILE, profile);
		capabilities.setCapability(CapabilityType.ELEMENT_SCROLL_BEHAVIOR, 1);
		final WebDriver driver = new FirefoxDriver(capabilities);

		wait = new WebDriverWait(driver, 15);
		try {
			String url = "https://sapiens.agu.gov.br/login";
			driver.get(url);
			driver.manage().window().maximize();

			usuario.logar(driver, wait, usuario);
			this.driver = driver;
			SaidaTriagem.setText("- Login Realizado!");
		} catch (Exception erro) {
			SaidaTriagem.setText("- Erro de comunicação!");
			erro.printStackTrace();
			Logger.getLogger(Chaves_Configuracao.class.getName()).log(Level.SEVERE, null, erro);
			som();
		}
	}

	public static String criptografar(String msg) {
		String senhaCrip = "";
		for (int i = 0; i < msg.length(); i++) {
			senhaCrip += (char) (msg.charAt(i) + 12);
		}
		return senhaCrip;
	}

	public static String descriptografar(String msgCript) {
		String senhaCrip = "";
		for (int i = 0; i < msgCript.length(); i++) {
			senhaCrip += (char) (msgCript.charAt(i) - 12);
		}
		return senhaCrip;
	}

	public void som() {
		URL url = getClass().getResource("/SOUNDS/have_this.wav"); // Som duvidoso
		AudioClip clip = Applet.newAudioClip(url);
		clip.play();
	}

}