package com.mycompany.newmark.system;

public class Sistema {

	public static final String VERSAO = "3.8";

	/*
	 * 3.3.3
	 * 
	 * Correção da exibição da concatenação da etiqueta com o subnúcleo.
	 * |Processo_PeticaoInicial - verificarNucleo
	 * 
	 * .2 Remoção da exibição da concatenação do subnúcleo quando não se é possível
	 * localizar a palavra chave
	 * 
	 * .3 (3^4) Correção do booleando "naofoipossivel" que pegava informação do
	 * resultado.getSubnucleo (o que retornava um falso-positivo), para pegar
	 * informação do resultado.getEtiqueta.
	 * |Processo_PeticaoInicial - verificarNucleo
	 * 
	 */

	/*
	 * 3.3.2
	 * 
	 * Correção da pesquisa do pedido que exibia a informação do campo "Etiqueta" na
	 * coluna "Subnúcleo". |Administracao - BuscaID |Chaves_Banco String SUBNUCLEO
	 * |identificadorMateriaDAO - buscaIdentificador - buscaIdentificadorPorId
	 * 
	 * 
	 * Adicionado campo etiqueta ao editar matéria através da janela
	 * "Edição de Matéria Cadastrada". |Controller_TagEdicaoMateria JFXTextField
	 * Etiqueta - Alterar. |IdentificadorMateriaDAO -atualizarIdentificadorMateria
	 * 
	 * Adicionado conversão da String para Maiúsculo dos campos pedido, complemento,
	 * etiqueta ao inserir registro na janela "Identificador de Matérias" na
	 * "Administração". |Administracao -inserirIdentificadorMateria
	 * 
	 * Correção da concatenação do Subnúcleo com a etiqueta. |Triagem_Etiquetas -
	 * triarBanco |Processo_PetiçãoInicial - verificarNucleo
	 * 
	 */

}