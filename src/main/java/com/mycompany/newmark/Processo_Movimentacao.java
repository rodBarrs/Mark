/**
 * @author Felipe Marques, Gabriel Ramos, Rafael Henrique e Adriano Vilhena 
 * 
 */
package com.mycompany.newmark;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Processo_Movimentacao {

    public Chaves_Resultado movimentacao(WebDriver driver, WebDriverWait wait, Chaves_Configuracao config, String bancos) throws InterruptedException, SQLException{
        Chaves_Resultado resultado = new Chaves_Resultado();
        resultado.setEtiqueta("NÃO FOI POSSÍVEL LOCALIZAR FRASE CHAVE ATUALIZADA");
        Triagem_Etiquetas triagem = new Triagem_Etiquetas();
        VerificarData verificarData = new VerificarData();
        Triagem_Condicao condicao = new Triagem_Condicao();
        String linhaMovimentacao = "";
        String condicaoProv = "PRO";
        String localTriagem = "MOV";
        
        //Verifica se a movimentação já esta carregada
        WebElement TabelaTref = null;
        boolean teste = false;
        try{
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("treeview-1015")));
            TabelaTref = driver.findElement(By.id("treeview-1015"));
        }
        catch (Exception erro){
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("treeview-1015")));
                    try {
                        TabelaTref = driver.findElement(By.id("treeview-1015"));
                        teste = true;
                        break;
                    }
                    catch (Exception f) {
                    }
                }
                if (teste = true) {
                    break;
                } else {
                    driver.navigate().refresh();
                }
            }
        }
        
        TabelaTref = driver.findElement(By.id("treeview-1015"));
        List<WebElement> listaMovimentacao = new ArrayList<WebElement>(TabelaTref.findElements(By.cssSelector("tr")));

        int limite = 10;
        WebElement movimentacaoAtual;
        for (int i = listaMovimentacao.size(); i>0 && limite>0; i--) {
            movimentacaoAtual = driver.findElement(By.xpath("//tr[" + i + "]/td[2]/div"));
            if (verificarData.verificar(movimentacaoAtual.getText(), config.getIntervaloDias()) /*|| config.isTriarAntigo()*/) {
                if (condicao.verificaCondicao(movimentacaoAtual.getText(),condicaoProv,bancos)){
                    limite--;
                    Boolean identificadoDePeticao = false;
                    resultado = triagem.triarBanco(movimentacaoAtual.getText(), bancos, localTriagem, config.getTipoTriagem(), identificadoDePeticao);
                    if (!resultado.getEtiqueta().contains("NÃO FOI POSSÍVEL LOCALIZAR FRASE CHAVE ATUALIZADA")
                            && !resultado.getEtiqueta().contains("ERRO EM TRIAGEM: PDF NÃO PESQUISÁVEL")) {
                        linhaMovimentacao = driver.findElement(By.xpath("//tr[" + i + "]/td/div")).getText();
                        resultado.setLocal("MOV " +  linhaMovimentacao);
                        resultado.setDriver(driver);
                        return resultado;
                    }
                }
            }
            //Volta pro FOR
        }
        resultado.setDriver(driver);
        return resultado;
    }
}