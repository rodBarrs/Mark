package com.mycompany.newmark.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.newmark.Aviso;
import com.mycompany.newmark.Banco;
import com.mycompany.newmark.Chaves_Banco;
import com.mycompany.newmark.connectionFactory.ConnectionFactory;

public class IdentificadorMateriaDAO {

	public List<Chaves_Banco> getTabelaIdentificadorMateria() {
		
		final String SQL = "SELECT * FROM identificador_materia ORDER BY id";
		List<Chaves_Banco> identificadoresMateria = new ArrayList<>();
		try (Connection connection = new ConnectionFactory().obterConexao();
				PreparedStatement stmt = connection.prepareStatement(SQL)) {
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				Chaves_Banco key = new Chaves_Banco();
				key.setID(resultSet.getInt("id"));
				key.setPALAVRACHAVE(resultSet.getString("palavrachave"));
				key.setCOMPLEMENTO(resultSet.getString("complemento"));
				key.setSubnucleo(resultSet.getString("subnucleo"));
				key.setETIQUETA(resultSet.getString("etiqueta"));
				key.setPRIORIDADE(resultSet.getString("prioridade"));
				identificadoresMateria.add(key);
			}
		} catch (Exception e) {
			new Aviso().aviso(e.getMessage() + "aaaaaa");
		} 

		
		return identificadoresMateria;
	}

	public void inserirIdentificadorMateria(String pedido, String complementoPedido,
			Integer pesoSelecionado, String etiqueta) {

		final String SQL = "INSERT INTO identificador_materia (palavrachave, complemento, subnucleo, prioridade, etiqueta) VALUES (?, ?, ?, ?, ?);";

		try (Connection connection = new ConnectionFactory().obterConexao();
				PreparedStatement stmt = connection.prepareStatement(SQL)) {
			stmt.setString(1, pedido);
			stmt.setString(2, complementoPedido);
		//	stmt.setString(3, subnucleo);
			stmt.setInt(4, pesoSelecionado);
			stmt.setString(5, etiqueta);

			stmt.execute();

			new Aviso().aviso("Etiqueta inserida com sucesso!");

		} catch (SQLException erro) {
			if (erro.getMessage().contains("UNIQUE constraint")) {
				new Aviso().aviso("Não foi possível inserir o registro:\n" + "O registro já esta cadastrado!");
				erro.printStackTrace();
			} else {
				Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, erro);
				new Aviso().aviso(erro.getMessage());
			}
		}
	}

	public void removerIdentificadorMateria(Integer idSelecionado) {
		final String SQL = "DELETE FROM identificador_materia WHERE id = ?";
		try (Connection connection = new ConnectionFactory().obterConexao();
				PreparedStatement stmt = connection.prepareStatement(SQL)) {
			stmt.setInt(1, idSelecionado);
			stmt.executeUpdate();
			new Aviso().aviso("Item removido");
		} catch (Exception e) {
			new Aviso().aviso("Item não removido\n" + e.getMessage());
		}

	}

	public List<Chaves_Banco> buscaIdentificadorPorID(String id) {

		List<Chaves_Banco> chaves = new ArrayList<>();

		final String SQL = "SELECT * FROM identificador_materia WHERE id = ?";

		try (Connection connection = new ConnectionFactory().obterConexao();
				PreparedStatement stmt = connection.prepareStatement(SQL)) {
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Chaves_Banco chave = new Chaves_Banco();
				chave.setID(Integer.valueOf(rs.getString("id")));
				chave.setPALAVRACHAVE(rs.getString("palavrachave"));
				chave.setCOMPLEMENTO(rs.getString("complemento"));
				chave.setETIQUETA(rs.getString("etiqueta"));
				chave.setSubnucleo(rs.getString("subnucleo"));
				chave.setPRIORIDADE(rs.getString("prioridade"));
				chaves.add(chave);
			}
		} catch (Exception e) {
			new Aviso().aviso(e.getMessage());
		}

		return chaves;

	}

	public List<Chaves_Banco> buscaIdentificador(String textoBusca) {
		final String SQL = "SELECT * FROM identificador_materia WHERE palavrachave LIKE ? OR complemento LIKE ? OR etiqueta LIKE ?";
		
		List<Chaves_Banco> chaves = new ArrayList<>();
		
		try (Connection connection = new ConnectionFactory().obterConexao();
				PreparedStatement stmt = connection.prepareStatement(SQL)){
				stmt.setString(1, '%' + textoBusca + '%');
				stmt.setString(2, '%' + textoBusca + '%');
				stmt.setString(3, '%' + textoBusca + '%');
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				Chaves_Banco chave = new Chaves_Banco();
				chave.setID(Integer.valueOf(rs.getString("id")));
				chave.setPALAVRACHAVE(rs.getString("palavrachave"));
				chave.setCOMPLEMENTO(rs.getString("complemento"));
				chave.setETIQUETA(rs.getString("etiqueta"));
				chave.setSubnucleo(rs.getString("subnucleo"));
				chave.setPRIORIDADE(rs.getString("prioridade"));
				chaves.add(chave);
			}
		} catch (Exception e) {
			new Aviso().aviso(e.getMessage());
		}
		
		return chaves;
		
	}
	
	public void atualizarIndetificadorMateria(Integer id, String palavrachave, String complemento, String etiqueta, String subnucleo, String prioridade) {
		
		final String SQL = "UPDATE identificador_materia SET palavrachave = ?, complemento = ?, etiqueta = ?, subnucleo = ?, prioridade = ? WHERE id = ?";
		
		try (Connection connection = new ConnectionFactory().obterConexao();
				PreparedStatement stmt = connection.prepareStatement(SQL)) {
			
			stmt.setString(1, palavrachave);
			stmt.setString(2, complemento);
			stmt.setString(3, etiqueta);
			stmt.setString(4, subnucleo);
			stmt.setString(5, prioridade);			
			stmt.setInt(6, id);
			stmt.executeUpdate();
			new Aviso().aviso("Item atualizado");
		} catch (Exception e) {
			new Aviso().aviso("Não foi possível atualizar o item\n" + e.getMessage());
		}
	}

}
