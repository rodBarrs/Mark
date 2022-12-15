/**
 * @author Felipe Marques, Gabriel Ramos, Rafael Henrique e Adriano Vilhena 
 * 
 */
package com.mycompany.newmark;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

public class Tratamento {

    public String tratamento(String processo) {
        processo = processo.toUpperCase();
        /* Gambiarra */
        processo = StringUtils.normalizeSpace(processo);
        processo = StringUtils.normalizeSpace(processo);
        processo = StringUtils.normalizeSpace(processo);
        processo = StringUtils.deleteWhitespace(processo);

        return processo;
    }
}
