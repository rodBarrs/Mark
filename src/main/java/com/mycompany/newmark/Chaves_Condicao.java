/**
 * @author Felipe Marques, João Paulo, Gabriel Ramos, Rafael Henrique
 * 
 * Classe de chaves para CABECALHO e PROVJURI
 */

package com.mycompany.newmark;

public class Chaves_Condicao {
    private String TIPO = "";     //Identificara se a string abaixo pertence a Providencia ou ao Cabeçalho
    private String TEXTO = "";    //String com o texto para a triagem de Providencia ou Cabeçalho

    private String BANCO = "";

    public String getTIPO() {
        return TIPO;
    }

    public void setTIPO(String TIPO) {
        this.TIPO = TIPO;
    }

    public String getTEXTO() {
        return TEXTO;
    }

    public void setTEXTO(String TEXTO) {
        this.TEXTO = TEXTO;
    }

    public String getBANCO() {
        return BANCO;
    }

    public void setBANCO(String BANCO) {
        this.BANCO = BANCO;
    }
}