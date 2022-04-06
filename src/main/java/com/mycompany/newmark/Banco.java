/**
 * @author Felipe Marques, João Paulo, Gabriel Ramos, Rafael Henrique
 * 
 * Classe responsável por identificar e, caso não exista, criar, o Banco de dados além de inserir, alterar e excluir os dados lá armazenados 
 */

package com.mycompany.newmark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Banco {
	public void conectar() {
		try {
			//Estabelece a conecção com o banco de dados
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();
			//Cria o banco de dados, com todas as tabelas caso o mesmo não seja localizado no diretorio especificado
			comandoSql.execute("CREATE TABLE IF NOT EXISTS bancos (                 \n"
					+ "sigla        VARCHAR (3)         NOT NULL,                       \n"
					+ "nome         STRING              NOT NULL,                       \n"
					+ "PRIMARY KEY (sigla),                                             \n"
					+ " UNIQUE (sigla, nome)                                            \n" + ");");

			comandoSql.execute("CREATE TABLE IF NOT EXISTS condicao (               \n"
					+ "texto        VARCHAR (100)       NOT NULL,                       \n"
					+ "tipo         VARCHAR (3)         NOT NULL,                       \n"
					+ "PRIMARY KEY (texto,tipo)                                         \n" + ");");

			comandoSql.execute("CREATE TABLE IF NOT EXISTS configuracao (           \n"
					+ "id           INT                 NOT NULL    DEFAULT (1997),     \n"
					+ "TriarAntigo  INTEGER             NOT NULL    DEFAULT (30),    \n"
					+ "TipoTriagem  VARCHAR (3)         NOT NULL    DEFAULT ('COM'),    \n"
					+ "JuntManual   BOOLEAN             NOT NULL    DEFAULT (false),     \n"
					+ "LaudoPericial BOOLEAN            NOT NULL    DEFAULT (false),    \n"
					+ "PeticaoInicial BOOLEAN           NOT NULL    DEFAULT (true),     \n"
					+ "Login        STRING                          DEFAULT (''),       \n"
					+ "Senha        STRING                          DEFAULT (''),       \n"
					+ "PRIMARY KEY (id)                                                 \n" + ");");

			comandoSql.execute("CREATE TABLE IF NOT EXISTS contador (               \n"
					+ "id           INT                 NOT NULL    DEFAULT (1997),     \n"
					+ "ContTotal    INT                 NOT NULL    DEFAULT (0),        \n"
					+ "ContNao      INT                 NOT NULL    DEFAULT (0),        \n"
					+ "ContDoc      INT                 NOT NULL    DEFAULT (0),        \n"
					+ "ContSeq      INT                 NOT NULL    DEFAULT (0),        \n"
					+ "ContErro     INT                 NOT NULL    DEFAULT (0),        \n"
					+ "PRIMARY KEY (id)                                                 \n" + ");");

			comandoSql.execute("CREATE TABLE IF NOT EXISTS etiquetas (\n"
					+ "    id           INTEGER       PRIMARY KEY AUTOINCREMENT,\n"
					+ "    palavrachave VARCHAR (45)  NOT NULL,\n"
					+ "    complemento  VARCHAR (100) NOT NULL,\n"
					+ "    etiqueta     VARCHAR (100) NOT NULL,\n"
					+ "    tipo         STRING        NOT NULL\n"
					+ "                               DEFAULT ('MOV'),\n"
					+ "    prioridade   VARCHAR (100) NOT NULL\n"
					+ "                               DEFAULT (0),\n"
					+ "    banco        VARCHAR (3)   NOT NULL\n"
					+ ");\n"
					+ "");
			comandoSql.execute("CREATE TABLE IF NOT EXISTS identificador_materia (\n"
					+ "    id           INTEGER       PRIMARY KEY AUTOINCREMENT\n"
					+ "                               NOT NULL,\n"
					+ "    palavrachave VARCHAR (255) NOT NULL,\n"
					+ "    complemento  VARCHAR (255),\n"
					+ "    etiqueta     VARCHAR (255) NOT NULL,\n"
					+ "    prioridade   VARCHAR (1)   NOT NULL\n"
					+ "                               DEFAULT ('1') \n"
					+ ");\n"
					+ "");
			comandoSql.execute("CREATE TABLE IF NOT EXISTS usuarios (\n"
					+ "    id    INTEGER       PRIMARY KEY AUTOINCREMENT,\n"
					+ "    nome  VARCHAR (255),\n"
					+ "    senha VARCHAR (255) \n"
					+ ");\n"
					+ "");

			//Verifica se o banco foi criado pelo codigo acima, caso tenha sido, o codigo a baixo implementará as linhas de configuração e contador para o correto funcionamento das demais classes
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM configuracao");
			ResultSet resultSet = stmt.executeQuery();
			int linhasBanco = 0;
			while (resultSet.next()) {
				linhasBanco++;
			}
			if (linhasBanco == 0) {
				comandoSql.execute("INSERT INTO contador (id,ContTotal, ContNao, ContDoc, ContSeq, ContErro)       \n"
						+ "VALUES (1997,0,0,0,0,0);");
				comandoSql.execute(
						"INSERT INTO configuracao (id,TriarAntigo, TipoTriagem, JuntManual, LaudoPericial, PeticaoInicial, Login, Senha)  \n"
								+ "VALUES (1997,'30','COM','true','false','true','','');");
			}
			
			stmt = connection.prepareStatement("SELECT * FROM usuarios");
			ResultSet rs = stmt.executeQuery();
			Integer teste = 0;
			while(rs.next()) {
				teste++;
			}
			
			if(teste == 0) {
				comandoSql.execute("INSERT INTO usuarios (nome, senha) VALUES ('admin-mark', 'tonystark')");
			}
			//Desconecta com o banco de dados, garantindo assim a integridade do dados
			connection.close();
		} catch (SQLException erro) {
			erro.printStackTrace();
		}
	}

	//Insere etiquetas dentro do banco na tabela ETIQUETAS
	public void inserirEtiquetas(Chaves_Banco chave) throws SQLException {
		Aviso aviso = new Aviso();
		String textoAviso = "";
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();
			comandoSql
					.execute("INSERT INTO etiquetas (palavrachave ,complemento,etiqueta,tipo,prioridade,banco) VALUES ("
							+ "'" + chave.getPALAVRACHAVE() + "'," + "'" + chave.getCOMPLEMENTO() + "'," + "'"
							+ chave.getETIQUETA() + "'," + "'" + chave.getTIPO() + "'," + "'" + chave.getPRIORIDADE()
							+ "'," + "'" + chave.getBANCO() + "');");
			//Desconecta com o banco de dados, garantindo assim a integridade do dados
			connection.close();
			textoAviso = "Etiqueta inserida com sucesso!";
			aviso.aviso(textoAviso);
		} catch (SQLException erro) {
			connection.close();
			if (erro.getMessage().contains("UNIQUE constraint")) {
				erro.printStackTrace();
				textoAviso = "Não foi possível inserir o registro:\n" + "O registro já esta cadastrado!";
				aviso.aviso(textoAviso);
			} else {
				Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
				aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
			}
		}
	}

	//Altera etiquetas dentro do banco na tabela ETIQUETAS
	public void alterarEtiquetas(Chaves_Banco chave, String PalavraChave, String Complemento, String Etiqueta,
			String Tipo, String Prioridade) {
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();
			comandoSql.execute("UPDATE etiquetas SET " + "palavrachave = '" + PalavraChave + "', " + "complemento = '"
					+ Complemento + "', " + "etiqueta = '" + Etiqueta + "', " + "tipo = '" + Tipo + "', "
					+ "prioridade = '" + Prioridade + "' " + "WHERE palavrachave = '" + chave.getPALAVRACHAVE() + "' "
					+ "AND complemento = '" + chave.getCOMPLEMENTO() + "' " + "AND banco = '" + chave.getBANCO()
					+ "';");
			//Desconecta com o banco de dados, garantindo assim a integridade do dados
			connection.close();
		} catch (SQLException erro) {
			Aviso aviso = new Aviso();
			if (erro.getMessage().contains("UNIQUE constraint")) {
				String textoAviso = "Não foi possível inserir o registro:\n" + "O registro já esta cadastrado!";
				aviso.aviso(textoAviso);
			} else {
				Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
				aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
			}
		}
	}

	//Exclui etiquetas dentro do banco na tabela ETIQUETAS
	public void excluirEtiquetas(Chaves_Banco chave) {
		Aviso aviso = new Aviso();
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();
			comandoSql.execute("DELETE FROM etiquetas WHERE" + "(palavrachave = '" + chave.getPALAVRACHAVE() + "') AND "
					+ "(complemento = '" + chave.getCOMPLEMENTO() + "') AND " + "(banco = '" + chave.getBANCO()
					+ "');");
			connection.close();
			String textoAviso = "Etiqueta deletada!";
			aviso.aviso(textoAviso);
		} catch (SQLException erro) {
			Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
			aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
		}
	}

	//Insere dados dentro da tabela PROVJURI e CABECALHO
	public void inserirCondicao(Chaves_Condicao chave) {
		Aviso aviso = new Aviso();
		String textoAviso = "";
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();
			//Inserindo
			comandoSql.execute("INSERT INTO condicao VALUES(" + "'"
					+ chave.getTEXTO().toUpperCase().replaceAll("'", "").replaceAll("\"", "").trim() + "','"
					+ chave.getTIPO() + "');");
			//Desconecta com o banco de dados, garantindo assim a integridade do dados
			connection.close();
			if (chave.getTIPO().equals("PRO")) {
				textoAviso = "Providência adicionada!";
				aviso.aviso(textoAviso);
			} else {
				textoAviso = "Cabeçalho adicionado!";
				aviso.aviso(textoAviso);
			}
		} catch (SQLException erro) {
			if (erro.getMessage().contains("UNIQUE constraint")) {
				textoAviso = "Não foi possível inserir o registro:\n" + "O registro já esta cadastrado!";
				aviso.aviso(textoAviso);
			} else {
				Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
				aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
			}
		}
	}

	//Altera dados dentro da tabela PROVJURI e CABECALHO
	public void alterarCondicao(Chaves_Condicao chave, String texto) {
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();
			comandoSql.execute("UPDATE condicao SET texto = '" + texto + "'" + " WHERE texto = '" + chave.getTEXTO()
					+ "'" + " AND tipo = '" + chave.getTIPO() + "';");
			//Desconecta com o banco de dados, garantindo assim a integridade do dados
			connection.close();
		} catch (SQLException erro) {
			Aviso aviso = new Aviso();
			if (erro.getMessage().contains("UNIQUE constraint")) {
				String textoAviso = "Não foi possível inserir o registro:\n" + "O registro já esta cadastrado!";
				aviso.aviso(textoAviso);
			} else {
				Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
				aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
			}
		}
	}

	//Exclui dados dentro da tabela PROVJURI e CABECALHO
	public void excluirCondicao(Chaves_Condicao chave) {
		Aviso aviso = new Aviso();
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();
			//Excluir
			comandoSql.execute("DELETE FROM condicao WHERE tipo ='" + chave.getTIPO() + "' AND texto = '"
					+ chave.getTEXTO() + "';");
			//Desconecta com o banco de dados, garantindo assim a integridade do dados
			connection.close();

			String textoAviso = "Condição deletada!";
			aviso.aviso(textoAviso);
		} catch (SQLException erro) {
			Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
			aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
		}
	}

	public void salvarAvancadas(Integer periodoData, String triagem, boolean juntadamanual) {
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();
			comandoSql.execute("UPDATE configuracao SET" + " TriarAntigo = '" + periodoData + "'          \n"
					+ ", TipoTriagem = '" + triagem + "'        \n" + ", JuntManual = '" + juntadamanual + "'   \n"
					+ " WHERE id = 1997;");
			//Desconecta com o banco de dados, garantindo assim a integridade do dados
			connection.close();
		} catch (SQLException erro) {
			Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
			Aviso aviso = new Aviso();
			aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
		}
	}

	//Altera o valor da unica linha da coluna LaudoPericial na tabela CONFIG para alterar o processo de Triagem
	public void salvarEspecificas(boolean laudoPericial, boolean peticaoInicial, boolean triagemPadrao, boolean triagemPastas) {
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();
			comandoSql.execute("UPDATE configuracao SET" + " LaudoPericial = '" + laudoPericial + "'   \n"
					+ ", PeticaoInicial = '" + peticaoInicial +  "'   \n" + ", triagemPadrao = '" + triagemPadrao +  "'   \n" + ", triagemPastas = '" + triagemPastas + "'   \n" + " WHERE id = 1997;");
			//Desconecta com o banco de dados, garantindo assim a integridade do dados
			connection.close();
		} catch (SQLException erro) {
			Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
			Aviso aviso = new Aviso();
			aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
		}
	}

	public void salvarSenha(Usuario usuario) {
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();
			comandoSql.execute("UPDATE configuracao SET Login = '" + usuario.getLogin() + "'   \n" + ", Senha = '"
					+ usuario.getSenha() + "' " + "WHERE id = 1997;");
			//Desconecta com o banco de dados, garantindo assim a integridade do dados
			connection.close();
		} catch (SQLException erro) {
			Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
			Aviso aviso = new Aviso();
			aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
		}
	}

	//Metodos para realizar a incrementação dos contadores
	public void contarNao() throws SQLException {
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();

			comandoSql.execute("UPDATE contador SET" + " ContNao = ContNao + 1" + ", ContTotal = ContTotal + 1"
					+ " WHERE id = 1997;");
			connection.close();
		} catch (SQLException erro) {
			Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
			Aviso aviso = new Aviso();
			aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
		}
	}

	public void contarDoc() {
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();
			comandoSql.execute("UPDATE contador SET" + " ContDoc = ContDoc + 1" + ", ContTotal = ContTotal + 1"
					+ " WHERE id = 1997;");
			//Desconecta com o banco de dados, garantindo assim a integridade do dados
			comandoSql.close();
			connection.close();
		} catch (SQLException erro) {
			Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
			Aviso aviso = new Aviso();
			aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
		}
	}

	public void contarMov() {
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();
			comandoSql.execute("UPDATE contador SET" + " ContSeq = ContSeq + 1" + ", ContTotal = ContTotal + 1"
					+ " WHERE id = 1997;");
			//Desconecta com o banco de dados, garantindo assim a integridade do dados
			comandoSql.close();
			connection.close();
		} catch (SQLException erro) {
			Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
			Aviso aviso = new Aviso();
			aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
		}
	}

	public Chaves_Configuracao pegarConfiguracao(Chaves_Configuracao config) {
		try {
			Integer TriarAntigo;
			boolean JuntManual;
			boolean LaudoPericial;
			boolean PeticaoInicial;
			boolean triagemPadrao;
			boolean triagemPastas;

			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM configuracao WHERE id = 1997");
			ResultSet resultadoBanco = stmt.executeQuery();
			while (resultadoBanco.next()) {
				TriarAntigo = resultadoBanco.getInt("TriarAntigo");

				String TipoTriagemLocal = resultadoBanco.getString("TipoTriagem");

				String JuntManualString = resultadoBanco.getString("JuntManual");
				if (JuntManualString.contains("false")) {
					JuntManual = false;
				} else {
					JuntManual = true;
				}

				String LaudoPericialString = resultadoBanco.getString("LaudoPericial");
				if (LaudoPericialString.contains("false")) {
					LaudoPericial = false;
				} else {
					LaudoPericial = true;
				}

				String PeticaoInicialString = resultadoBanco.getString("PeticaoInicial");
				if (PeticaoInicialString.contains("false")) {
					PeticaoInicial = false;
				} else {
					PeticaoInicial = true;
				}
				String triagemPadraoString = resultadoBanco.getString("triagemPadrao");
				if (triagemPadraoString.contains("false")) {
					triagemPadrao = false;
				} else {
					triagemPadrao = true;
				}

				String triagemPastasString = resultadoBanco.getString("triagemPastas");
				if (triagemPastasString.contains("false")) {
					triagemPastas = false;
				} else {
					triagemPastas = true;
				}

				config.setIntervaloDias(TriarAntigo);
				config.setTipoTriagem(TipoTriagemLocal);
				config.setJuntManual(JuntManual);
				config.setLaudoPericial(LaudoPericial);
				config.setPeticaoInicial(PeticaoInicial);
				config.setTriagemPadrao(triagemPadrao);
				config.setTriagemPastas(triagemPastas);
			}
			connection.close();
		} catch (Exception erro) {
			Aviso aviso = new Aviso();
			aviso.aviso(erro.getMessage());
			erro.printStackTrace();
			Logger.getLogger(Chaves_Configuracao.class.getName()).log(Level.SEVERE, null, erro);
		}
		return config;
	}

	public void inserirBancos(Chaves_Condicao chave) {
		Aviso aviso = new Aviso();
		String textoAviso = "";
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();
			//Inserindo
			comandoSql.execute("INSERT INTO bancos VALUES(" + "'"
					+ chave.getTIPO().toUpperCase().replaceAll("'", "").replaceAll("\"", "").trim() + "','"
					+ chave.getTEXTO().toUpperCase().replaceAll("'", "").replaceAll("\"", "").trim() + "');");
			//Desconecta com o banco de dados, garantindo assim a integridade do dados
			connection.close();
			textoAviso = "Banco inserido com sucesso!";
			aviso.aviso(textoAviso);
		} catch (SQLException erro) {
			if (erro.getMessage().contains("UNIQUE constraint")) {
				textoAviso = "Não foi possível inserir o registro:\n" + "O registro já esta cadastrado!";
				aviso.aviso(textoAviso);
			} else {
				Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
				aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
			}
		}
	}

	public void excluirBancos(Chaves_Condicao chave) {
		Aviso aviso = new Aviso();
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			Statement comandoSql = connection.createStatement();
			//Excluir
			comandoSql.execute(
					"DELETE FROM bancos WHERE sigla ='" + chave.getTIPO() + "' AND nome = '" + chave.getTEXTO() + "';");
			//Desconecta com o banco de dados, garantindo assim a integridade do dados
			connection.close();

			String textoAviso = "Banco deletado!";
			aviso.aviso(textoAviso);
		} catch (SQLException erro) {
			Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
			aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
		}
	}

	public ObservableList<String> setarBanco() {
		ObservableList<String> Lista = null;
		try {
			List<String> lista = new ArrayList<>();
			lista.add("TODOS OS BANCOS");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM bancos ORDER BY nome");
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				lista.add(resultSet.getString("sigla") + " - " + resultSet.getString("nome"));
			}
			Lista = FXCollections.observableArrayList(lista);
			connection.close();
		} catch (SQLException erro) {
			Aviso aviso = new Aviso();
			aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
		}
		return Lista;
	}

	public String selecionarBanco(String bancoSelecionadoLocal) throws SQLException {
		String bancoSelecionado = bancoSelecionadoLocal;

		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM bancos ORDER BY nome");
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				if (!bancoSelecionado.contains("TODOS OS BANCOS")) {
					if (bancoSelecionadoLocal.contains(resultSet.getString("sigla"))
							&& bancoSelecionadoLocal.contains(resultSet.getString("nome"))) {
						bancoSelecionado = resultSet.getString("sigla");
						connection.close();
						return bancoSelecionado;
					}
				} else {
					connection.close();
					return bancoSelecionado;
				}
			}
			connection.close();
		} catch (SQLException erro) {
			Aviso aviso = new Aviso();
			aviso.aviso(erro.getMessage() + "\nCódigo do Erro: " + erro.getErrorCode());
		}

		return bancoSelecionado;
	}

	public List<String> selecionarIdentificadoresDePeticao() {
		return null;
	}
}