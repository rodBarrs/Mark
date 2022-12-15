/**
 * @author Felipe Marques, João Paulo, Gabriel Ramos, Rafael Henrique
 * 
 * Reponsável por armazenar as configurações de triagem setadas pelo usuário
 */

package com.mycompany.newmark;

public class Chaves_Configuracao {
    private Integer intervaloDias; 
    private String TipoTriagem = "";     //Varchar de 3 - COM (completa) - MOV (movimentação) - DOC (documento)
    private boolean JuntManual;
    private boolean LaudoPericial;
    private boolean PeticaoInicial;

    private boolean concatenacao;
    private boolean debugpi = false;
        
    public Integer getIntervaloDias() {
        return intervaloDias;
    }

    public void setIntervaloDias(Integer TriarAntigo) {
        this.intervaloDias = TriarAntigo;
    }

    public String getTipoTriagem() {
        return TipoTriagem;
    }

    public void setTipoTriagem(String TipoTriagem) {
        this.TipoTriagem = TipoTriagem;
    }

    public boolean isJuntManual() {
        return JuntManual;
    }

    public void setJuntManual(boolean JuntManual) {
        this.JuntManual = JuntManual;
    }

    public boolean isLaudoPericial() {
        return LaudoPericial;
    }

    public void setLaudoPericial(boolean LaudoPericial) {
        this.LaudoPericial = LaudoPericial;
    }

    public boolean isPeticaoInicial() {
        return PeticaoInicial;
    }

    public void setPeticaoInicial(boolean PeticaoInicial) {
        this.PeticaoInicial = PeticaoInicial;
    }

    public boolean isConcatenacao() {
        return concatenacao;
    }

    public void setConcatenacao(boolean concatenacao) {
        this.concatenacao = concatenacao;
    }

    @Override
    public String toString() {
        return "Configuracao:"
                + "\nTriarAntigo: " + intervaloDias
                + "\nTipoTriagem: " + TipoTriagem
                + "\nJuntManual: " + JuntManual
                + "\nLaudoPericial: " + LaudoPericial
                + "\nPetiçãoInicial: " + PeticaoInicial;
    }

	public boolean isDebugpi() {
		return debugpi;
	}

	public void setDebugpi(boolean debugpi) {
		this.debugpi = debugpi;
	}

}