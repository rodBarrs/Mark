/**
 * @author Felipe Marques, Gabriel Ramos, Rafael Henrique e Adriano Vilhena
 *
 * Classe responsável por etiquetar o processo no grid após a triagem
 */
package com.mycompany.newmark;

import org.openqa.selenium.*;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Keys;
import com.mycompany.newmark.system.Sistema;

public class Processo_Etiquetar {


	public void etiquetar(WebDriver driver, WebDriverWait wait, Chaves_Resultado resultado, Chaves_Configuracao configuracao)
			throws InterruptedException {

		String etiqueta = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[2]/div/div[2]/div/div[4]/div/table/tbody/tr/td[9]/div")).getText();
		Actions action = new Actions(driver);
		int i = 4;
		cliqueTabela(driver, wait, action, i);

		///////////////////////
		String preEtiqueta = driver.findElement(By.xpath("//fieldset[5]/div/span/div/table[4]/tbody/tr/td[2]/input")).getAttribute("value");

		if (preEtiqueta.contains("CIT")) {
			preEtiqueta = "PJE CITAÇÃO - ";
		} else if (preEtiqueta.contains("INT")) {
			preEtiqueta = "PJE INTIMAÇÃO - ";
		} else {
			preEtiqueta = "";
		}
		resultado.setEtiqueta(preEtiqueta + resultado.getEtiqueta());
//
//		if (preEtiqueta.contains("CIT")) {
//			preEtiqueta = "rodcadu PJE CITAÇÃO - ";
//		} else if (preEtiqueta.contains("INT")) {
//			preEtiqueta = "rodcadu PJE INTIMAÇÃO - ";
//		} else {
//			preEtiqueta = "rodcadu -";
//		}
//		resultado.setEtiqueta(preEtiqueta + resultado.getEtiqueta());

		///////////////////////
		if (resultado.getEtiqueta().equals("")){
			resultado.setEtiqueta("NÃO FOI POSSÍVEL LOCALIZAR FRASE CHAVE ATUALIZADA");
		}
			driver.findElement(By.xpath("//fieldset[5]/div/span/div/table[4]/tbody/tr/td[2]/input")).clear();
			driver.findElement(By.xpath("//div/div/span/div/table/tbody/tr/td[2]/textarea")).clear();
			if (configuracao.isConcatenacao()){
				driver.findElement(By.xpath("//fieldset[5]/div/span/div/table[4]/tbody/tr/td[2]/input"))
						.sendKeys(etiqueta +"/"+ resultado.getEtiqueta());
			} else {
				driver.findElement(By.xpath("//fieldset[5]/div/span/div/table[4]/tbody/tr/td[2]/input"))
						.sendKeys(resultado.getEtiqueta());
			}

		if(Chaves_Resultado.getSeqPeticao() != null) {

			driver.findElement(By.xpath("//div/div/span/div/table/tbody/tr/td[2]/textarea"))
					.sendKeys("MARK" + Sistema.VERSAO + "\nSEQ PETIÇÃO INICIAL: " + Chaves_Resultado.getSeqPeticao()
							+ "\nFRASE CHAVE PETIÇÃO INICIAL: " + Chaves_Resultado.getPalavraChavePeticao());
		}else {
			//CASO HAJA TRIAGEM PADRÃO

			driver.findElement(By.xpath("//div/div/span/div/table/tbody/tr/td[2]/textarea"))
					.sendKeys("MARK" + Sistema.VERSAO + "\nSEQ: "
							+ resultado.getLocal() + "\nFRASE CHAVE: " + resultado.getPalavraChave());
		}

			for (int x = 13; x< 25;){
				try {
					driver.findElement(By.xpath("/html/body/div["+ x + "]/div[2]/div/div[2]/div/div[2]/div/div/a[2]/span/span/span[1]")).click();
				//	webElement.click();
					x = 25;
				}
				catch (Exception e){
					x++;
				}
			}







//		webElement.sendKeys(Keys.TAB);
//		Thread.sleep(500);
//		webElement.sendKeys(Keys.TAB);
//		webElement.sendKeys(Keys.TAB);
//		webElement.sendKeys(Keys.TAB);
//		webElement.sendKeys(Keys.TAB);
//		webElement.sendKeys(Keys.TAB);

	//	webElement.sendKeys(Keys.ENTER);

//		action.sendKeys(Keys.TAB).build().perform();
//		action.sendKeys(Keys.TAB).build().perform();
//		action.sendKeys(Keys.ENTER).build().perform();
	}

	public void cliqueTabela(WebDriver driver, WebDriverWait wait, Actions action, int i) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			System.out.println("Td: " + i);
			wait.until(ExpectedConditions
					.elementToBeClickable(By.xpath("//td["+i+"]/div")));
			WebElement ele = driver.findElement(By.xpath("//td["+i+"]/div"));
//			action.moveToElement(driver.findElement(By.xpath("//td["+i+"]/div"))).doubleClick().build().perform();
//			driver.findElement(By.xpath("//td["+i+"]/div")).click();
			//Thread.sleep(500);
			//driver.findElement(By.xpath("//td["+i+"]/div")).click();
			js.executeScript(("var evt = document.createEvent('MouseEvents');"+
					"evt.initMouseEvent('dblclick',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"+
					"arguments[0].dispatchEvent(evt);"), ele);
			//action.doubleClick(driver.findElement(By.xpath("//td["+i+"]/div"))).build().perform();
			wait.until(ExpectedConditions
					.elementToBeClickable(By.xpath("//fieldset[5]/div/span/div/table[4]/tbody/tr/td[2]/input")));

		} catch (Exception e) {
			i++;
			cliqueTabela(driver, wait, action, i);
		}

	}
}
