package br.com.pinedu.importa.run

import br.com.pinedu.importa.Bairro
import br.com.pinedu.importa.Cidade
import br.com.pinedu.importa.Estado
import br.com.pinedu.importa.Pais
import br.com.pinedu.importa.TipoLogradouro
import br.com.pinedu.importa.Logradouro

import groovy.sql.*
class Run {
	private static PRODUCTION = Boolean.FALSE
	static main(args) {
		Sql db = Sql.newInstance('jdbc:postgresql://127.0.0.1:5432/pnd', 'pnd', 'x8DwsDRMUvxqrq#L', 'org.postgresql.Driver')
		String path = "/pinedu/modelo/DNE_MD/eDNE_Basico_25062/Fixo/"

		//Pais
		println 'Pais'
		Pais pais = new Pais(db: db)
		if (PRODUCTION == Boolean.FALSE) {
			pais.criaTabela()
		}
		pais.leArquivo(path)
		pais.execute()
		pais.finaliza()
		//Estado
		println 'Estado'
		Estado estado = new Estado(db: db)
		if (PRODUCTION == Boolean.FALSE) {
			estado.criaTabela()
		}
		estado.leArquivo(path)
		estado.execute()
		estado.finaliza() 
		//TipoLogradouro
		println 'TipoLogradouro'
		TipoLogradouro tipoLogradouro = new TipoLogradouro(db: db)
		if (PRODUCTION == Boolean.FALSE) {
			tipoLogradouro.criaTabela()
		}
		tipoLogradouro.leArquivo(path)
		tipoLogradouro.execute()
		tipoLogradouro.finaliza()
		//Cidade
		println 'Cidade'
		Cidade cidade = new Cidade(db: db, mapaEstado: estado.mapaEstado)
		if (PRODUCTION == Boolean.FALSE) {
			cidade.criaTabela()
		}
		cidade.leArquivo(path)
		cidade.execute()
		cidade.finaliza()
		
		//Bairro
		println 'Bairro'
		Bairro bairro = new Bairro(db: db, mapaCidade: cidade.mapaCidade )
		if (PRODUCTION == Boolean.FALSE) {
			bairro.criaTabela()
		}
		bairro.leArquivo(path)
		bairro.execute()
		bairro.finaliza()
		
		//Logradouro
		println 'Logradouro'
		Logradouro logradouro = new Logradouro(db: db, mapaCidade: cidade.mapaCidade, mapaTipoCidade: cidade.mapaTipoCidade, mapaBairro: bairro.mapaBairro, mapaTipoLogradouro: tipoLogradouro.mapaTipoLogradouro )
		if (PRODUCTION == Boolean.FALSE) {
			logradouro.criaTabela()
		}
		logradouro.leArquivo(path)
		logradouro.execute()
		logradouro.finaliza()

		//Fim
		println "Terminou"
	}

}
