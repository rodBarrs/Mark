package com.mycompany.newmark.Repositories;

import com.mycompany.newmark.LeituraPDF;
import com.mycompany.newmark.Processo_Etiquetar;
import com.mycompany.newmark.entities.EtiquetaObservacao;
import com.mycompany.newmark.entities.InformacoesDosprev;

import com.mycompany.newmark.entities.InformacoesSislabra;
import com.mycompany.newmark.entities.InformacoesUrbano;
import net.sf.cglib.core.Local;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RepositoryMaternidade {

    public boolean clicarDosprev(WebDriver driver, WebDriverWait wait) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS).pageLoadTimeout(1, TimeUnit.SECONDS);
        Thread.sleep(1000);
        List<String> janela = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(janela.get(1));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("treeview-1015")));
        WebElement TabelaTref = driver.findElement(By.id("treeview-1015"));
        List<WebElement> listaMovimentacao = new ArrayList<WebElement>(TabelaTref.findElements(By.cssSelector("tr")));
        for (int i = listaMovimentacao.size(); i > 2; i--) {

            // Providência Jurídica é o título da movimentação
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[" + i + "]/td[2]/div/span")));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr[" + i + "]/td[2]/div/span")));
            if (i > listaMovimentacao.size() - 3) {
                Thread.sleep(500);
            }


            Boolean existeDosPrev = driver.findElement(By.xpath("//tr[" + i + "]/td[2]/div/span")).getText()
                    .toUpperCase().contains("DOSSIÊ PREVIDENCIÁRIO");
            if (existeDosPrev) {
                WebElement dosClick = driver.findElement(By.xpath("//tr[" + i + "]/td[2]/div/span"));
                dosClick.click();
                return true;
            }

        }
        return false;
    }


    public InformacoesDosprev coletarInformacoesDosprev(WebDriver driver, WebDriverWait wait) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS).pageLoadTimeout(1, TimeUnit.SECONDS);
        Thread.sleep(1000);
        InformacoesDosprev informacao = new InformacoesDosprev();
        String dataInicioMaisRecente = "zero";
        String nbMaisRecente = "";
        driver.switchTo().frame(0);
        String dataAjuizamento = driver.findElement(By.xpath("/html/body/div/div[1]/table/tbody/tr[2]/td"))
                .getText();
        String sexo = driver.findElement(By.xpath("/html/body/div/div[1]/table/tbody/tr[11]/td"))
                .getText();
        boolean processoINSS = false;

        String textoNãoContemProcessosINSS = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr[2]/td")).getText();
        if (!textoNãoContemProcessosINSS.contains("Não há relação dos processos movidos pelo autor contra o INSS.")) {

            for (int i = 1; i <= 10; i++) {
                try {
                    String texto = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr[2]/td[3]/table/tbody/tr[" + i + "]/td")).getText();
                    if (texto.contains("INSTITUTO NACIONAL DO SEGURO SOCIAL")) {
                        processoINSS = true;
                        i = 11;
                    }
                } catch (Exception e) {
                    i = 11;
                }
            }

        }
        for (int i = 3; i < 7; i++) {
            String titulo = driver.findElement(By.xpath("/html/body/div/p[" + (i + 1) + "]/b/u")).getText();
            String conteudo = driver.findElement(By.xpath("/html/body/div/div[3]/table/tbody/tr[2]/td")).getText();
            if (titulo.equals("RESUMO INICIAL – DADOS GERAIS DOS REQUERIMENTOS")) {
                if(conteudo.contains("Não foram encontrados requerimentos em nome do autor.")){
                    dataInicioMaisRecente = "";
                    nbMaisRecente = "";
                    i = 8;
                }else {
                    try {
                        WebElement TabelaTref = driver.findElement(By.xpath("/html/body/div/div[" + i + "]"));
                        List<WebElement> listaRelPrev = new ArrayList<WebElement>(TabelaTref.findElements(By.cssSelector("tr")));
                        for (int j = 2; j <= listaRelPrev.size(); j++) {
                            String status = driver.findElement(By.xpath("/html/body/div/div[" + i + "]/table/tbody/tr[" + j + "]/td[6]")).getText();
                            if (status.contains("INDEFERIDO")) {
                                String nbIndeferido = driver.findElement(By.xpath("/html/body/div/div[" + i + "]/table/tbody/tr[" + j + "]/td[1]")).getText();
                                String dataDeInicio = driver.findElement(By.xpath("/html/body/div/div[" + i + "]/table/tbody/tr[" + j + "]/td[3]")).getText();
                                if (dataInicioMaisRecente.contains("zero")) {
                                    dataInicioMaisRecente = dataDeInicio;
                                    nbMaisRecente = nbIndeferido;
                                }
                                if(!dataInicioMaisRecente.equals("")){
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                    formatter = formatter.withLocale(Locale.US);
                                    LocalDate dataAtual = LocalDate.parse(dataInicioMaisRecente, formatter);
                                    LocalDate dataValidacao = LocalDate.parse(dataDeInicio, formatter);
                                    long diferenca = ChronoUnit.DAYS.between(dataValidacao, dataAtual);
                                    System.out.println(diferenca);
                                    if (diferenca < 0) {
                                        dataInicioMaisRecente = dataDeInicio;
                                        nbMaisRecente = driver.findElement(By.xpath("/html/body/div/div[" + i + "]/table/tbody/tr[" + j + "]/td[1]")).getText();
                                    }
                                }


                            }

                        }
                        i = 7;
                    } catch (Exception e) {

                        System.out.println(e);
                    }
                }

            }

        }

        String filiacao = "";
        String origemVinculo = "";
        String nit = "";
//        String dataInicio = "";
//        String dataFim = "";
        String codNB = "";
//        for (int i = 4; i < 7; i++) {
//            try {
//                WebElement TabelaTref = driver.findElement(By.xpath("/html/body/div/div[" + i + "]"));
//                List<WebElement> listaRelPrev = new ArrayList<WebElement>(TabelaTref.findElements(By.cssSelector("tr")));
//                for (int j = listaRelPrev.size(); j > 0; j--) {
//                    if (j == 1){
//                        break;
//                    }
//                    codNB = driver.findElement(By.xpath("/html/body/div/div[" + i + "]/table/tbody/tr[" + j + "]/td[3]")).getText();
//                    if (codNB.length() != 10) {
//
//                        nit = driver.findElement(By.xpath("/html/body/div/div[" + i + "]/table/tbody/tr[" + j + "]/td[2]")).getText();
//                        origemVinculo = driver.findElement(By.xpath("/html/body/div/div[" + i + "]/table/tbody/tr[" + j + "]/td[4]")).getText();
//                        dataInicio = driver.findElement(By.xpath("/html/body/div/div[" + i + "]/table/tbody/tr[" + j + "]/td[5]")).getText();
//                        dataFim = driver.findElement(By.xpath("/html/body/div/div[" + i + "]/table/tbody/tr[" + j + "]/td[6]")).getText();
//                        j = -1;
//                        i = 7;
//                    }
//                }
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//
//        }
        List<InformacoesUrbano> infUrbano = new ArrayList<>();

            for (int z = 6; z < 10; z++) {
                try {
                    String cabecalho = driver.findElement(By.xpath("/html/body/div/div[" + z + "]/p/b/u")).getText();
                    if (cabecalho.contains("COMPETÊNCIAS DETALHADAS")) {
                        for (int i = 1; i < 100 ; i++) {
                            String titulo = driver.findElement(By.xpath(
                                    "/html/body/div/div[" + z + "]/div[" + i + "]/p[1]/b")).getText();
                            if (titulo.contains("Vínculo Previdenciário")) {
                                String colunaCod = driver.findElement(By.xpath(
                                        "/html/body/div/div[" + z + "]/div[" + i + "]/table[1]/tbody/tr[1]/th[2]")).getText();
                                if (colunaCod.contains("Código Emp.")) {
                                    String vinculo = driver.findElement(By.xpath(
                                            "/html/body/div/div[" + z + "]/div[" + i + "]/table[1]/tbody/tr[2]/td[3]")).getText();

                                    String dataInicio = driver.findElement(By.xpath(
                                            "/html/body/div/div[" + z + "]/div[" + i + "]/table[1]/tbody/tr[2]/td[4]")).getText();

                                    String dataFim = driver.findElement(By.xpath(
                                            "/html/body/div/div[" + z + "]/div[" + i + "]/table[1]/tbody/tr[2]/td[5]")).getText();
                                    infUrbano.add(new InformacoesUrbano(vinculo, dataInicio, dataFim));

                                }else {
                                    System.out.println("Entrei no Else");
                                    z=11;
                                    break;
                                }
                            }

                        }
                    }


                } catch (Exception e) {
                    z = 11;
                }

            }






        informacao.setInformacoesUrbanos(infUrbano);
        informacao.setDataDeAjuizamento(dataAjuizamento);
        informacao.setSexo(sexo);
        informacao.setExisteProcessoINSS(processoINSS);
        informacao.setDataInicioIndeferido(dataInicioMaisRecente);
        informacao.setNbProcessoIndeferido(nbMaisRecente);
        informacao.setNit(nit);
        informacao.setNomeEmpresa(origemVinculo);
//        informacao.setDataInicio(dataInicio);
//        informacao.setDataFim(dataFim);

        return informacao;
    }


    public boolean clicarSislabra(WebDriver driver, WebDriverWait wait) throws InterruptedException {

        driver.switchTo().defaultContent();
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS).pageLoadTimeout(1, TimeUnit.SECONDS);
        Thread.sleep(1000);
        List<String> janela = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(janela.get(1));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("treeview-1015")));
        WebElement TabelaTref = driver.findElement(By.id("treeview-1015"));
        List<WebElement> listaMovimentacao = new ArrayList<WebElement>(TabelaTref.findElements(By.cssSelector("tr")));
        for (int i = listaMovimentacao.size(); i > 2; i--) {

            // Providência Jurídica é o título da movimentação
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[" + i + "]/td[2]/div/span")));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr[" + i + "]/td[2]/div/span")));
            if (i > listaMovimentacao.size() - 3) {
                Thread.sleep(500);
            }
            Boolean existeSislabra = false;
            for(int j=3; j>0;){
                try{
                            existeSislabra = driver.findElement(By.xpath("//tr[" + i + "]/td[2]/div/span")).getText()
                            .toUpperCase().contains("BENS") || driver.findElement(By.xpath("//tr[" + i + "]/td[2]/div/span/span["+j+"]")).getText()
                            .toUpperCase().contains("SISLABRA");
                            j=0;
                }catch (Exception e){
                    j--;
                }
            }


            if (existeSislabra) {
                WebElement dosClick = driver.findElement(By.xpath("//tr[" + i + "]/td[2]/div/span"));
                dosClick.click();
                LeituraPDF pdf = new LeituraPDF();
                pdf.apagarPDF();
                return true;
            }

        }
        return false;
    }

    public InformacoesSislabra coletarInformacoesSislabra(WebDriver driver, WebDriverWait wait) throws InterruptedException, IOException {
        LeituraPDF pdf = new LeituraPDF();
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS).pageLoadTimeout(1, TimeUnit.SECONDS);
        InformacoesSislabra informacao = new InformacoesSislabra();
        driver.switchTo().frame(0);
        String processo;


        int cont = 0;
        while (cont <= 2) {

            if (pdf.PDFBaixado()) {
                processo = pdf.lerPDF();
                System.out.println(processo);
                String[] buscaNb = processo.split("\r\n");

                String situacaoEmpAtual;
                String modelo;
                String tipo;
                List<String> situacaoEmpresa = new ArrayList<>();
                List<InformacoesSislabra.InfVeiculo> infVeiculo = new ArrayList<>();
                for (int indexBuscaNb = 0; indexBuscaNb < buscaNb.length; indexBuscaNb++) {
                    if (buscaNb[indexBuscaNb].contains("Situação Empresa:")) {
                        System.out.println(buscaNb[indexBuscaNb] + " : " + indexBuscaNb);
                        situacaoEmpAtual = buscaNb[indexBuscaNb+1];
                        situacaoEmpresa.add(situacaoEmpAtual);

                    }
                    if (buscaNb[indexBuscaNb].contains("MOTOCICLETA") || buscaNb[indexBuscaNb].contains("AUTOMOVEL")) {
                        System.out.println(buscaNb[indexBuscaNb] + " : " + indexBuscaNb);
                        tipo = buscaNb[indexBuscaNb];
                        modelo = buscaNb[indexBuscaNb+2];
                        infVeiculo.add(new InformacoesSislabra.InfVeiculo(modelo,tipo));
                    }

                    if (buscaNb[indexBuscaNb].contains("Cód. Imóvel INCRA")){
                        System.out.println(buscaNb[indexBuscaNb] + " : " + indexBuscaNb);
                    }
                }

                informacao.setInfVeiculo(infVeiculo);
                informacao.setSituacaoEmpresa(situacaoEmpresa);

                break;
            } else {
                pdf.apagarPDF();
            }
            cont++;
        }

        pdf.apagarPDF();
        return informacao;
    }

    public String clicarProcesoAdministrativo(WebDriver driver, WebDriverWait wait, String nbProcessoIndeferido) throws InterruptedException, IOException {

        driver.switchTo().defaultContent();
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS).pageLoadTimeout(1, TimeUnit.SECONDS);
        Thread.sleep(1000);
        List<String> janela = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(janela.get(1));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("treeview-1015")));
        WebElement TabelaTref = driver.findElement(By.id("treeview-1015"));
        List<WebElement> listaMovimentacao = new ArrayList<WebElement>(TabelaTref.findElements(By.cssSelector("tr")));
        for (int i = listaMovimentacao.size(); i > 2; i--) {

            // Providência Jurídica é o título da movimentação
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[" + i + "]/td[2]/div/span")));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr[" + i + "]/td[2]/div/span")));
            if (i > listaMovimentacao.size() - 3) {
                Thread.sleep(500);
            }


            Boolean existeProcessoAdministrativo = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div[2]/div/div[3]/div/table/tbody/tr["+ i +"]/td[2]/div/span/span[1]")).getText()
                    .toUpperCase().contains("PROCESSO ADMINISTRATIVO");
            if (existeProcessoAdministrativo) {
                WebElement dosClick = driver.findElement(By.xpath("//tr[" + i + "]/td[2]/div/span"));
                dosClick.click();

                LeituraPDF pdf = new LeituraPDF();

                driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS).pageLoadTimeout(1, TimeUnit.SECONDS);

                String processo;


                int cont = 0;
                while (cont <= 2) {

                    if (pdf.PDFBaixado()) {
                        processo = pdf.lerPDF();
                        System.out.println(processo);
                        String[] buscaNb = processo.split("\r\n");
                        for (int indexBuscaNb = 0; indexBuscaNb < buscaNb.length; indexBuscaNb++) {
                            if (buscaNb[indexBuscaNb].contains("NB")) {
                                System.out.println(buscaNb[indexBuscaNb] + " : " + indexBuscaNb);
                                String replaceNb = buscaNb[indexBuscaNb].replace(".", "");
                                replaceNb = replaceNb.replace("-", "");
                                if (replaceNb.contains(nbProcessoIndeferido)) {
                                    for(int j = indexBuscaNb; j>0; j--){
                                        if(buscaNb[j].contains("Informe a data do parto, atestado médico ou adoção/guarda para fins de adoção:")){
                                            String dataParto = buscaNb[j].replace("Informe a data do parto, atestado médico ou adoção/guarda para fins de adoção: ","");
                                            return dataParto;
                                        }
                                    }
                                }

                            }
                        }
                        break;
                    } else {
                        pdf.apagarPDF();
                    }
                    cont++;
                }

                pdf.apagarPDF();


            }


        }

        return  "nao achou";
    }

    public EtiquetaObservacao etiquetarMaternidade (WebDriver driver, WebDriverWait wait, InformacoesDosprev infoDosprev, InformacoesSislabra infoSislabra, String assunto, boolean passouSislabra, boolean passouDosprev){
        EtiquetaObservacao etiquetaObservacao = new EtiquetaObservacao();
        Processo_Etiquetar etiquetar = new Processo_Etiquetar();
        String etiqueta = "";
        String observacao = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        formatter = formatter.withLocale(Locale.US);
       //PRESCRIÇÃO
//        if (passouDosprev){
//            if(infoDosprev.getDataInicioIndeferido().equals("") || (infoDosprev.getDataDeAjuizamento().equals(""))){
//                etiqueta = "PRESCRIÇÃO - N; ";
//            }else{
//               if(infoDosprev.getDataDeAjuizamento().equals("") || infoDosprev.getDataInicioIndeferido().equals("")){
//                   etiqueta = "PRESCRIÇÃO - DADOS INSUFICIENTES ";
//               }else{
//                   LocalDate d1 = LocalDate.parse(infoDosprev.getDataDeAjuizamento(), formatter);
//                   LocalDate d2 = LocalDate.parse(infoDosprev.getDataInicioIndeferido(), formatter);
//                   Period period = Period.between(d1,d2);
//                   int difAnos = Math.abs(period.getYears());
//                   if(difAnos>=5){
//                       etiqueta = "PRESCRIÇÃO - S; ";
//                       observacao +="|PRESC-data Ajuizamento: "+infoDosprev.getDataDeAjuizamento()+"Data Indeferido: "+ infoDosprev.getDataInicioIndeferido() + "| ";
//                   }
//               }
//
//            }
//        }else {
//            etiqueta+="PRESCRIÇÃO - DOSSIÊ PREVIDENCIÁRIO NÃO ENCONTRADO; ";
//        }




        //LITISPENDÊNCIA
        if (passouDosprev) {
            if (infoDosprev.isExisteProcessoINSS()) {
                etiqueta += " LITISPENDÊNCIA - S; ";
            }
        }else {
            etiqueta="LITISPENDÊNCIA - DOSSIÊ PREVIDENCIÁRIO NÃO ENCONTRADO; ";
        }


        //PATRIMÔNIO
        if (passouSislabra){
            int contadorVeiculos = 0;
            int contadorMotocilceta = 0;
            int contadorEmpresas = 0;
            int indexVeiculo = 0;

            if(infoSislabra.getInfVeiculo().size() > 0){
                observacao += "Veículos: ";
            }
            for(InformacoesSislabra.InfVeiculo teste : infoSislabra.getInfVeiculo()){
                    if (infoSislabra.getInfVeiculo().get(indexVeiculo).getTipo().contains("MOTOCICLETA")){
                        contadorMotocilceta++;
                    }else {
                        contadorVeiculos++;
                    }

                    observacao +=infoSislabra.getInfVeiculo().get(indexVeiculo).getTipo()+", ";
                    observacao +=infoSislabra.getInfVeiculo().get(indexVeiculo).getModelo()+"; ";
                    indexVeiculo++;

            }
            if(infoSislabra.getSituacaoEmpresa().size() > 0){
                observacao+= "Empresa situação: ";
            }
            for (int z = 0; z < infoSislabra.getSituacaoEmpresa().size(); z++){
                if (!infoSislabra.getSituacaoEmpresa().get(z).equals("Inapta")){
                    contadorEmpresas++;
                    observacao +=infoSislabra.getSituacaoEmpresa()+";";
                }
            }

            if(contadorVeiculos>0 || contadorMotocilceta > 0 || contadorEmpresas>0){
                etiqueta+="PATRIMÔNIO - S; ";

            }
        } else{
            etiqueta += "PATRIMÔNIO - SISLABRA NÃO ENCONTRADO; ";
        }


        //URBANO


//        infoDosprev.getDataFim()

//        if(assunto.contains("SALÁRIO-MATERNIDADE")){
//            etiqueta += "";
////            if ((!passouProcessoAdministrativo || !passouDosprev)){
////                if((!passouDosprev && !passouProcessoAdministrativo)){
////                    etiqueta += "URBANO - PROCESSO ADMINISTRATIVO E DOSSÎE PREVIDENCIÁRIO NÃO ENCONTRADOS ";
////                } else if (passouDosprev == false){
////                    etiqueta += "URBANO - DOSSÎE PREVIDENCIÁRIO NÃO ENCONTRADO";
////                } else if (passouProcessoAdministrativo == false){
////                    etiqueta += "URBANO - PROCESSO ADMINISTRATIVO NÃO ENCONTRADO";
////                }
////
////            }else {
////                if(!infoDosprev.getDataFim().equals("")){
////                    LocalDate data1 = LocalDate.parse(infoDosprev.getDataFim(), formatter);
////                    LocalDate data2 = LocalDate.parse(dataNascimentoCrianca, formatter);
////                    Period period2 = Period.between(data1,data2);
////                    int difMeses = Math.abs(period2.getMonths());
////
////                    if (difMeses < 10){
////                        etiqueta += "URBANO - S ";
////                        observacao += "|URBA - Data nascimento: " + dataNascimentoCrianca + "Data Fim: " + infoDosprev.getDataFim();
////                    }else {
////                        etiqueta += "URBANO - N ";
////                    }
////                }
////                else {
////                    etiqueta += "URBANO - SEM DATA FIM";
////                }
////
////            }
////        } else {
//            if (passouDosprev){
//                if (infoDosprev.getDataFim().equals("")) {
//                    etiqueta += "URBANO - N ";
//                }else {
//                    LocalDate data1 = LocalDate.parse(infoDosprev.getDataFim(), formatter);
//                    LocalDate data2 = LocalDate.parse(infoDosprev.getDataDeAjuizamento(), formatter);
//                    Period period2 = Period.between(data1,data2);
//                    int difAnosUrbano = Math.abs(period2.getYears());
//
//                    if (difAnosUrbano < 15){
//                        etiqueta += "URBANO - S ";
//                        observacao += "Data de Ajuizamento: " + infoDosprev.getDataDeAjuizamento() + "Data Fim: " + infoDosprev.getDataFim();
//                    }else {
//                        etiqueta += "URBANO - N ";
//                    }
//                }
//            } else {
//                etiqueta += "URBANO - DOSSÎE PREVIDENCIÁRIO NÃO ENCONTRADO";
//            }
//            }

        if (etiqueta.equals("LITISPENDÊNCIA - N; PATRIMÔNIO - N; URBANO - N ")){
            etiqueta = "Não contêm impeditivos";
        }

        etiquetaObservacao.setEtiqueta(etiqueta);
        etiquetaObservacao.setObservacao(observacao);

        return etiquetaObservacao;

    }


}

