/**
 * @author Felipe Marques, Gabriel Ramos, Rafael Henrique e Adriano Vilhena
 *
 */
package com.mycompany.newmark;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

public class Triagem_Etiquetas {

	public Chaves_Resultado triarBanco(String processo, String banco, String localtriagem, String tipoTriagem,
									   Boolean identificadorDePeticao) {
		processo.replace("\n", "");
		processo.replace(" ","");
		Chaves_Resultado resultado = new Chaves_Resultado();
		Tratamento tratamento = new Tratamento();
		processo = tratamento.tratamento(processo);

		boolean bancoBool = false;
		while (bancoBool == false) {
			try {
				Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");

				PreparedStatement stmt;
				ResultSet resultSet;

				if (identificadorDePeticao) {
					stmt = connection.prepareStatement("SELECT * FROM identificador_materia ORDER BY prioridade DESC");
					//public boolean peticaoTrue = true;
				} else {

					stmt = connection.prepareStatement(
							"SELECT * FROM etiquetas WHERE tipo = '" + localtriagem + "' ORDER BY prioridade DESC");

				}

				resultSet = stmt.executeQuery();

				while (resultSet.next()) {
					String PALAVRACHAVE = resultSet.getString("palavrachave");
					PALAVRACHAVE = tratamento.tratamento(PALAVRACHAVE);
					String COMPLEMENTO = resultSet.getString("complemento");
					COMPLEMENTO = tratamento.tratamento(COMPLEMENTO);
					String BANCO = "";

					if (identificadorDePeticao){

					}else {
						 BANCO = resultSet.getString("banco");
					}

					System.out.println(BANCO);
					System.out.println(banco);
					if (identificadorDePeticao){
						if (processo.contains(PALAVRACHAVE) && processo.contains(COMPLEMENTO)) {
							//resultado.setId(resultSet.getInt("id"));
							resultado.setPalavraChave(resultSet.getString("id"));
							if (identificadorDePeticao) {
								resultado.setSubnucleo(resultSet.getString("subnucleo"));
							}
							resultado.setEtiqueta(resultSet.getString("etiqueta"));
					resultSet.getString("etiqueta");
					if (resultSet.wasNull()) {
							resultado.setEtiqueta(resultSet.getString("etiqueta"));
						} else {
						resultado.setEtiqueta(resultSet.getString("etiqueta"));
						}

							connection.close();
							return resultado;
						}
					} else if (processo.contains(PALAVRACHAVE) && processo.contains(COMPLEMENTO) && (BANCO.contains(banco) || banco.contains("TODOS OS BANCOS"))) {
						//resultado.setId(resultSet.getInt("id"));
						resultado.setPalavraChave(resultSet.getString("id"));
						if (identificadorDePeticao) {
							resultado.setSubnucleo(resultSet.getString("subnucleo"));
						}
						resultado.setEtiqueta(resultSet.getString("etiqueta"));
//						resultSet.getString("etiqueta");
//						if (resultSet.wasNull()) {
//							resultado.setEtiqueta(resultSet.getString("subnucleo"));
//						} else {
//							resultado.setEtiqueta(resultSet.getString("subnucleo") + "/" + resultSet.getString("etiqueta"));
//						}

						connection.close();
						return resultado;
					}

				}
				connection.close();
				bancoBool = true;

			} catch (SQLException ex) {
				bancoBool = false;
				Logger.getLogger(Triagem_Etiquetas.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		resultado.setEtiqueta("NÃO FOI POSSÍVEL LOCALIZAR FRASE CHAVE ATUALIZADA (" + banco + ", " + tipoTriagem + ")");
		return resultado;
	}
}


