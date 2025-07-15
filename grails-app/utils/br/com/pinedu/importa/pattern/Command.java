package br.com.pinedu.importa.pattern;

import java.io.Serializable;


public interface Command extends Serializable {
	void criaTabela();
	void execute();
	void leArquivo(String path) throws Exception;
	void finaliza();
}
