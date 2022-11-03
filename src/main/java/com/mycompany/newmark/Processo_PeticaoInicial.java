/**
 * @author Felipe Marques, Gabriel Ramos, Rafael Henrique e Adriano Vilhena
 */
package com.mycompany.newmark;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import net.sourceforge.htmlunit.corejs.javascript.ast.ThrowStatement;

public class Processo_PeticaoInicial {
	private String localTriagem = "PET";
	private Chaves_Configuracao debugPi;

	public Chaves_Resultado peticaoInicial(WebDriver driver, WebDriverWait wait, Chaves_Configuracao config,
										   String banco, Chaves_Configuracao debugPi) throws Exception {
		Tratamento tratamento = new Tratamento();
		Triagem_Condicao cond = new Triagem_Condicao();
		Chaves_Resultado resultado = new Chaves_Resultado();
		LeituraPDF pdf = new LeituraPDF();
		Actions action = new Actions(driver);
		String documentoPeticaoInicial = "";
		this.debugPi = debugPi;

		// Limpa conteúdo estáticos da Chaves_Resultado
		Chaves_Resultado.setSeqPeticao("");
		Chaves_Resultado.setPalavraChavePeticao("");

		// Aguarda até que tabela com as movimentações (treeview) esteja carregada
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("treeview-1015")));

		// Armazena todas as movimentações num ArrayList
		WebElement TabelaTref = driver.findElement(By.id("treeview-1015"));
		List<WebElement> listaMovimentacao = new ArrayList<WebElement>(TabelaTref.findElements(By.cssSelector("tr")));

		// Aguarda até que o iframe esteja carregado e então envia o Driver para o
		// iframe (para que possa interagir com o interior do iframe)
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("iframe-myiframe")));
		WebElement capa = driver.findElement(By.id("iframe-myiframe"));
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(capa));
		do {
			try {
				wait.until(ExpectedConditions.elementToBeClickable(By.tagName("html")));
				wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
				driver.findElement(By.tagName("html")).click();
				break;
			} catch (Exception e) {
				//
			}
		} while (true);
		// Aguarda até que o campo "órgão julgador" esteja carregado e então salva seu
		// conteúdo

		String orgaoJulgador = "";
		int x = 4;
		try {
			wait.until(ExpectedConditions
					.presenceOfElementLocated(By.xpath("/html/body/div/div[" + x + "]/table/tbody/tr[3]/td[2]")));
			orgaoJulgador = orgaoNaDiv(x, driver);
		} catch (Exception e) {
			orgaoJulgador = orgaoNaDiv(x + 1, driver);
		}

		// Devolve o driver para a página
		driver.switchTo().defaultContent();

		// Seta previamente a etiqueta com erro
		resultado.setLocal("PETIÇÃO INICIAL");
		resultado.setEtiqueta("NÃO FOI POSSÍVEL LOCALIZAR PASTA DE PETIÇÃO INICIAL");
		resultado.setPalavraChave("");

		// Verifica a existência de uma pasta na posição 1 do sapiens (isso significaria
		// que existe uma possível petição inicial com nome diferente)
		Boolean existePasta = driver.findElement(By.xpath("//tr[2]/td[2]/div/img[1]")).getAttribute("class")
				.contains("x-tree-expander");
		// Itera a lista de movimentação procurando por "Petição Inicial" ou uma pasta
		// no index 1
		for (int i = 2; i < listaMovimentacao.size(); i++) {

			// Providência Jurídica é o título da movimentação
			Boolean existePeticaoInicial = driver.findElement(By.xpath("//tr[" + i + "]/td[2]/div/span")).getText()
					.toUpperCase().contains("PETIÇÃO INICIAL");
			if (existePeticaoInicial || existePasta) {

				resultado.setEtiqueta("NÃO FOI POSSÍVEL LOCALIZAR ARQUIVO DE PETIÇÃO INICIAL");

				// Clica na div da Providência Jurídica

				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("ext-gen1020")));
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//tr[\" + i + \"]/td/div/span/span[1]")));
				driver.findElement(By.xpath("//tr[" + i + "]/td/div")).click();

				// Verifica se a movimentação é do tipo PDF
				Boolean movimentacaoTemPDF = driver.findElement(By.xpath("//tr[" + i + "]/td[2]/div/span/span[2]"))
						.getText().toUpperCase().contains("PDF");

				// CAPA
				if (movimentacaoTemPDF) {

					pdf.apagarPDF();

					for (int a = 0; a < 2; a++) {
						if (pdf.PDFBaixado()) {
							documentoPeticaoInicial = pdf.lerPDF();
							documentoPeticaoInicial = tratamento.tratamento(documentoPeticaoInicial);
							break;
						} else {

							driver.findElement(By.xpath("//tr[" + i + "]/td/div")).click();

						}
					}

				} else {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.id("iframe-myiframe")));
					WebElement iframe = driver.findElement(By.id("iframe-myiframe"));
					wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframe));

					// Laço para garantir que o clique seja feito no HTML dentro do iframe
					// Isso garante que o conteúdo do iframe que contém o documento da movimentação
					// clicada já carregou
					boolean flag = true;
					do {
						try {
							wait.until(ExpectedConditions.elementToBeClickable(By.tagName("html")));
							wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
							driver.findElement(By.tagName("html")).click();
							flag = false;
						} catch (Exception e) {
							//
						}
					} while (flag);

					driver.switchTo().defaultContent();

					Thread.sleep(500);
					action.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\u0061')).perform();
					action.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\u0063')).perform();
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					DataFlavor flavor = DataFlavor.stringFlavor;
					Thread.sleep(500);
					documentoPeticaoInicial = clipboard.getData(flavor).toString().toUpperCase();
					documentoPeticaoInicial = tratamento.tratamento(documentoPeticaoInicial);
				}

				Boolean contemPeticaoInicial = cond.verificaCondicao(documentoPeticaoInicial, "PET", banco);
				if (Boolean.FALSE.equals(contemPeticaoInicial)) {

					int proximaLinha = Integer
							.parseInt(driver.findElement(By.xpath("//tr[" + (i + 1) + "]/td/div")).getText()) + 1;

					// Clica na seta para expandir a pasta
					driver.findElement(By.xpath("//tr[" + i + "]/td[2]/div/img[1]")).click();

					// Itera pela pasta
					for (int j = i + 1; j < proximaLinha; j++) {
						pdf.apagarPDF();
						wait.until(ExpectedConditions
								.presenceOfElementLocated(By.xpath("//tr[" + j + "]/td[2]/div/span/span[1]")));
						wait.until(ExpectedConditions
								.elementToBeClickable(By.xpath("//tr[" + j + "]/td[2]/div/span/span[1]")));

						Thread.sleep(500);

						wait.until(
								ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"ext-gen1124\"]")));
						// Clica para abrir o PDF
						driver.findElement(By.xpath("//tr[" + j + "]/td[2]/div")).click();

						Boolean movimentacaoPastaContemPDF = driver
								.findElement(By.xpath("//tr[" + j + "]/td[2]/div/span/span[2]")).getText()
								.contains("PDF");

						if (movimentacaoPastaContemPDF) {
							Boolean pdfBaixado = pdf.PDFBaixado();

							for (int a = 0; a < 2; a++) {
								if (pdfBaixado) {
									String processo = pdf.lerPDF().toUpperCase();
									if (cond.verificaCondicao(processo, "PET",banco)) {
										String posicaoDaPeticao = String.valueOf(j - 1);
										Chaves_Resultado.setSeqPeticao("(" + posicaoDaPeticao + ")");


										resultado = verificarNucleo(processo, orgaoJulgador, banco);
										System.out.println("Retorno 1 - " + resultado.getSubnucleo());


										String nucleo = resultado.getEtiqueta();
						//				 resultado = triagemPadrao(driver, wait, config, banco, i, true, nucleo);
										resultado.setDriver(driver);
										return resultado;
									}
								} else {
									driver.findElement(By.xpath("//tr[" + j + "]/td[2]/div")).click();
								}
							}

						} else {
							wait.until(ExpectedConditions.presenceOfElementLocated(By.id("iframe-myiframe")));
							WebElement iframe = driver.findElement(By.id("iframe-myiframe"));
							wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframe));

							boolean flag = true;
							do {
								try {
									wait.until(ExpectedConditions.elementToBeClickable(By.tagName("html")));
									wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
									driver.findElement(By.tagName("html")).click();
									flag = false;
								} catch (Exception e) {
									//
								}

							} while (flag);

							driver.switchTo().defaultContent();

							Thread.sleep(500);
							action.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\u0061')).perform();
							action.keyDown(Keys.CONTROL).sendKeys(String.valueOf('\u0063')).perform();
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							DataFlavor flavor = DataFlavor.stringFlavor;
							Thread.sleep(500);
							String processo = clipboard.getData(flavor).toString().toUpperCase();
							processo = tratamento.tratamento(processo);

							contemPeticaoInicial = cond.verificaCondicao(processo, "PET", banco);
							if (contemPeticaoInicial) {
								String posicaoDaPeticao = String.valueOf(j - 1);
								Chaves_Resultado.setSeqPeticao("(" + posicaoDaPeticao + ")");


								resultado = verificarNucleo(processo, orgaoJulgador, banco);
								System.out.println("Retorno 2 - " + resultado.getSubnucleo());


								String nucleo = resultado.getEtiqueta();
						//		 resultado = triagemPadrao(driver, wait, config, banco, i, true, nucleo);
								resultado.setDriver(driver);
								return resultado;
							}
						}
					}

					Chaves_Resultado.setSeqPeticao("PETIÇÃO NÃO ENCONTRADA");
					// resultado = triagemPadrao(driver, wait, config, banco, i, true, "");
					resultado.setDriver(driver);
					return resultado;

				} else {
					String posicaoDaPeticao = String.valueOf(i - 1);
					Chaves_Resultado.setSeqPeticao("(" + posicaoDaPeticao + ")");


					resultado = verificarNucleo(documentoPeticaoInicial, orgaoJulgador, banco);
					System.out.println("Retorno 3 - " + resultado.getSubnucleo());


					System.out.println("");
					// resultado = triagemPadrao(driver, wait, config, banco, i, false,
					// resultado.getEtiqueta());
					resultado.setDriver(driver);
					return resultado;
				}

			}
		}
		Chaves_Resultado.setSeqPeticao("PETIÇÃO NÃO ENCONTRADA");
		//resultado = triagemPadrao(driver, wait, config, banco, 0, false, "");
		resultado.setDriver(driver);
		return resultado;

	}

	private String orgaoNaDiv(int x, WebDriver driver) throws Exception {
		String orgaoJulgador = null;
		try {
			orgaoJulgador = driver.findElement(By.xpath("/html/body/div/div[" + x + "]/table/tbody/tr[3]/td[2]"))
					.getText();
			return orgaoJulgador;
		} catch (Exception e) {
			x++;
			if (x < 15) {
				return orgaoJulgador = orgaoNaDiv(x, driver);
			} else {
				throw new Exception("Não achou o órgão julgador");
			}
		}
	}

	private Chaves_Resultado verificarNucleo(String processo, String orgaoJulgador, String banco) {
		Triagem_Etiquetas triagem = new Triagem_Etiquetas();
		// Identifica a matéria e salva na variável resultado

		Chaves_Resultado resultado = triagem.triarBanco(processo, banco, localTriagem, "PETIÇÃO INCIAL", true);
		Chaves_Resultado.setPalavraChavePeticao(resultado.getPalavraChave());

		String nucleo = resultado.getSubnucleo();

		if (debugPi.isDebugpi()) {
			JOptionPane.showMessageDialog(null, "CONDIÇÃO VÁLIDA");
			JOptionPane.showMessageDialog(null, "NÚCLEO IDENTIFICADO: " + nucleo);
			JOptionPane.showMessageDialog(null, "PALAVRA CHAVE NÚCLEO: " + resultado.getPalavraChave());
		}

		// Valida Subnúcleo
		Boolean SSEASValido = resultado.getSubnucleo().contains("ER-SEAS")
				&& (orgaoJulgador.contains("JUIZADO ESPECIAL") || orgaoJulgador.contains("VARA FEDERAL")
				|| orgaoJulgador.contains("JEF"));
		Boolean SBIValido = resultado.getSubnucleo().toUpperCase().contains("ETR-BI") && (orgaoJulgador.toUpperCase().contains("JUIZADO ESPECIAL"));
		Boolean TRUValido = orgaoJulgador.toUpperCase().contains("FEDERAL");
		/// NÃO SE SABE SE O CORRETO É O ORGAO OU O SUBNUCLEO
		Boolean naoFoiPossivel = resultado.getEtiqueta().toUpperCase().contains("NÃO FOI POSSÍVEL");

		System.out.println("Subnúcleo - " + resultado.getSubnucleo());
		System.out.println("Órgão Julgador - " + orgaoJulgador);

		System.out.println("-----------------------------------------");

		System.out.println("SSEASValido - " + SSEASValido);
		System.out.println("SBISValido - " + SBIValido);
		System.out.println("TRUValido - " + TRUValido);
		System.out.println("Não foi possível - " + naoFoiPossivel);

		System.out.println("-----------------------------------------");

		if (naoFoiPossivel) {
			System.out.println("Entrou na condição 0!");
			return resultado;
		} else if (SSEASValido || SBIValido) {
			System.out.println("Entrou na condição 1!");
			System.out.println("Subnúcleo colocado - " + resultado.getSubnucleo());
			atualizarEtiqueta(resultado);
			return resultado;
		} else if (TRUValido) {
			System.out.println("Entrou na condição 2");
			resultado.setSubnucleo("ER-TRU");
			atualizarEtiqueta(resultado);
			return resultado;
		}
		System.out.println("Entrou na condição 3");
		resultado.setSubnucleo("PREV/LOCAL");
		atualizarEtiqueta(resultado);
		return resultado;
	}

	private void atualizarEtiqueta(Chaves_Resultado resultado) {
		if (resultado.getEtiqueta().isEmpty()) {
			resultado.setEtiqueta(resultado.getSubnucleo());
		} else {
			resultado.setEtiqueta(resultado.getSubnucleo() + "/ " + resultado.getEtiqueta());
		}
		System.out.println("Subnúcleo colocado - " + resultado.getSubnucleo());
	}

	public Chaves_Resultado triagemPadrao(WebDriver driver,
										  WebDriverWait wait, Chaves_Configuracao configs, String banco, int
												  indexPeticao, boolean dentroDaPasta, String nucleo) throws
			InterruptedException, SQLException, UnsupportedFlavorException, IOException {


		System.out.println("-------------------------------------------------");
		System.out.println("INICIO");
		Chaves_Resultado resultado = new Chaves_Resultado();
		resultado.setSubnucleo("");
		if (dentroDaPasta) {
			System.out.println("AVISANDO DENTRO DA PASTA");
			try {
				System.out.println("TRY DENTRO DA PASTA");
				driver.findElement(By.xpath("//tr[" + indexPeticao + "]/td/div/img")).click();
			} catch (Exception e) {
				System.out.println("CATCH DENTRO DA PASTA");
				//
			}
		}

		// Clica na capa

		driver.findElement(By.xpath("//tr[1]/td/div/img")).click();
		System.out.println("CLICOU NA PASTA");

		switch (configs.getTipoTriagem()) {
			case "MOV":
				System.out.println("chamando movimentação");
				resultado = new Processo_Movimentacao().movimentacao(driver, wait, configs, banco);
				break;
			case "DOC":
				System.out.println("chamando documento ");
				resultado = new Processo_Documento().documento(driver, wait, configs, banco);
				break;
			case "COM":
				System.out.println("chamando COM ");
				resultado = new Processo_Movimentacao().movimentacao(driver, wait, configs, banco);
				if (resultado.getEtiqueta().contains("NÃO FOI POSSÍVEL LOCALIZAR FRASE CHAVE ATUALIZADA")) {
					// System.out.println("chamando documento 409");
					resultado = new Processo_Documento().documento(driver, wait, configs, banco);
				}
				break;
		}


		String etiqueta = resultado.getEtiqueta().toUpperCase().replaceAll("-", "").replaceAll(" ", "");

		if (etiqueta.contains("PJEFEDERAL") || etiqueta.contains("PJEPAR") || etiqueta.contains("PJEESTADUAL")) {
			resultado.setDriver(driver);
			resultado.setEtiqueta(resultado.getEtiqueta());
			return resultado;
		}

		String etiquetaFinal = resultado.getEtiqueta();
		System.out.println("etiqueta Final " + etiquetaFinal);
		resultado.setEtiqueta(etiquetaFinal);
		resultado.setDriver(driver);

		resultado.setEtiqueta(resultado.getEtiqueta());
		return resultado;

	}
		}




