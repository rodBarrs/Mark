package com.mycompany.newmark;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerificaCondicaoPastas {


    public boolean verificaCondicao(String processo) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:BancoEtiquetasMark.db");
        PreparedStatement stmt;
        ResultSet resultSet;
        stmt = connection.prepareStatement("SELECT * FROM nome_pasta");
        resultSet = stmt.executeQuery();
        while (resultSet.next()){
            String nome = resultSet.getString("nome");

            if (processo.contains(nome)) {
                connection.close();
                return true;
            }
        }
        connection.close();
        return false;
    }

}
