package br.com.pinedu.importa

import groovy.sql.BatchingPreparedStatementWrapper
import groovy.sql.GroovyResultSet
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import br.com.pinedu.importa.pattern.Command

class Logradouro implements Command {
	private static Boolean DEBUG = Boolean.FALSE
	Map mapaCidade = [:]
	Map mapaBairro = [:]
	Map mapaTipoLogradouro = [:]
	Map mapaTipoCidade = [:]
	Map mapaDistrito = [:]
	Map mapaLogradouroDependencia = [:]
	String path = null
	Sql db
	Long id = 0
	Boolean sistema = true
	Long version = 0
	private String montaNomeComplemento( String nome, String tipoLogradouro, String lote, String nomeComplemento, String numeroComplemento ) {
		String a = "${nome} ${lote} ${nomeComplemento} ${numeroComplemento}"
		return a.replaceAll( /\s+/, ' ' )?.trim()
	}

	@Override
	public void execute() {
		if ( Boolean.TRUE.equals( DEBUG ) ) {
			leLogradouro(path, 'SP')
		} else {
			db.eachRow('SELECT e.uf as uf FROM estado AS e ORDER BY e.uf asc;') { uf ->
				leLogradouro(path, uf.uf)
			}
		}
	}
	@Override
	public void leArquivo(String path) throws Exception {
		this.path = path
	}
	private void leLogradouro(String path, String uf) throws Exception {
		println uf
		File arquivoLogradouro = new File("${path}/DNE_GU_${uf}_LOGRADOUROS.TXT")
		List<Map<String, Object>> inserts = []
		List<Map<String, Object>> updates = []
		Map<String, Integer> schema = [:]
		Map<Long, Long> mapaLogradouroChaveDne = [:]
		db.eachRow( 'SELECT id, chavedne FROM logradouro' ) { GroovyResultSet grs->
			mapaLogradouroChaveDne[ ZValue.toLong( grs.chavedne ) ] = ZValue.toLong( grs.id )
		}

		arquivoLogradouro.eachLine("ISO-8859-1") { String linha ->
			String primeiroCaracter = linha.substring(0, 1)
			if ( "#" == primeiroCaracter || "C" == primeiroCaracter) return
			Map dados = [:]
			switch (primeiroCaracter) {
				case 'D':
					dados = registroDados(linha)
					break;
				case 'S':
					dados = registroSeccionamento(linha)
					break;
				case 'N':
					dados = registroLote(linha)
					break;
				case 'K':
					dados = registroComplemento(linha)
					break;
				case 'Q':
					dados = registroComplemento2(linha)
					break;
			}
			Long chaveDne = ZValue.toLong( dados.chaveDne )
			Long lograouroId = mapaLogradouroChaveDne[ chaveDne ]
			if ( !mapaLogradouroChaveDne.containsKey( chaveDne ) ) {
				inserts << [
					version: version
					, abrvact: dados.abrvACT
					, bairro_fim_id: dados.bairroFim
					, bairro_ini_id: dados.bairroIni
					, cep: dados.cep
					, chavedne: ( ZValue.toLong( dados.chaveDne ) > 0 )? ZValue.toLong( dados.chaveDne ): null
					, cidade_id: dados.cidade
					, complemento: dados.complemento
					, distrito_id: dados.distrito
					, final_trecho: dados.finalTrecho
					, grande_usuario: dados.grandeUsuario
					, inicio_trecho: dados.inicioTrecho
					, nome: dados.nome
					, nome_complemento: dados.nomeComplemento
					, nome_lote: dados.nomeLote
					, numero_complemento: dados.numeroComlemento
					, paridade: dados.paridade
					, preposicao: dados.preposicao
					, sistema: sistema
					, status_logradouro: dados. statusLogradouro
					, tipo_log_id: dados.tipoLog
					, titulo: dados.titulo
					, nome_normalizado: Format.normaliza( dados.nome )
				]
			} else {
				updates << [
					id: mapaLogradouroChaveDne[ chaveDne ]
					,abrvact: dados.abrvACT
					, bairro_fim_id: dados.bairroFim
					, bairro_ini_id: dados.bairroIni
					, cep: dados.cep
					, chavedne: ( ZValue.toLong( dados.chaveDne ) > 0 )? ZValue.toLong( dados.chaveDne ): null
					, cidade_id: dados.cidade
					, complemento: dados.complemento
					, distrito_id: dados.distrito
					, final_trecho: dados.finalTrecho
					, grande_usuario: dados.grandeUsuario
					, inicio_trecho: dados.inicioTrecho
					, nome: dados.nome
					, nome_complemento: dados.nomeComplemento
					, nome_lote: dados.nomeLote
					, numero_complemento: dados.numeroComlemento
					, paridade: dados.paridade
					, preposicao: dados.preposicao
					, sistema: true
					, status_logradouro: dados. statusLogradouro
					, tipo_log_id: dados.tipoLog
					, titulo: dados.titulo
					, nome_normalizado: Format.normaliza( dados.nome )
				]
			}
		}
		if ( inserts ) {
			db.withBatch(500, "INSERT INTO logradouro(version,  abrvact,  bairro_fim_id, bairro_ini_id,   cep, chavedne,  cidade_id,   complemento, distrito_id,   final_trecho,  grande_usuario, inicio_trecho,   nome, nome_complemento,   nome_lote,  numero_complemento, paridade,   preposicao,  sistema, status_logradouro,  tipo_log_id, titulo, nome_normalizado) VALUES (:version, :abrvact, :bairro_fim_id, :bairro_ini_id, :cep, :chavedne, :cidade_id, :complemento, :distrito_id, :final_trecho, :grande_usuario, :inicio_trecho, :nome, :nome_complemento, :nome_lote, :numero_complemento, :paridade, :preposicao, :sistema, :status_logradouro, :tipo_log_id, :titulo, :nome_normalizado);") { BatchingPreparedStatementWrapper bpsw ->
				inserts.each {
					bpsw.addBatch(it)
				}
			}
		}
		if ( updates ) {
			db.withBatch(500, "UPDATE logradouro SET version=1, abrvact = :abrvact, bairro_fim_id = :bairro_fim_id, bairro_ini_id = :bairro_ini_id, cep = :cep, chavedne = :chavedne, cidade_id = :cidade_id, complemento = :complemento, distrito_id = :distrito_id, final_trecho = :final_trecho, grande_usuario = :grande_usuario, inicio_trecho = :inicio_trecho, nome = :nome, nome_complemento = :nome_complemento, nome_lote = :nome_lote, numero_complemento = :numero_complemento, paridade = :paridade, preposicao = :preposicao, sistema = :sistema, status_logradouro = :status_logradouro, tipo_log_id = :tipo_log_id, titulo = :titulo, nome_normalizado = :nome_normalizado WHERE id = :id;") { BatchingPreparedStatementWrapper bpsw ->
				updates.each {
					bpsw.addBatch(it)
				}
			}
		}
		mapaLogradouroChaveDne = [:]
		db.eachRow( 'SELECT id, chavedne FROM logradouro' ) { GroovyResultSet grs->
			mapaLogradouroChaveDne[ ZValue.toLong( grs.chavedne ) ] = ZValue.toLong( grs.id )
		}
		db.withBatch(500, 'UPDATE logradouro SET logradouro_pai_id = :logradouroPaiId WHERE id = :id;') { BatchingPreparedStatementWrapper bpsw ->
			mapaLogradouroDependencia.each{ Long chaveDne, Long chavePaiDne ->
				Long id = mapaLogradouroChaveDne[ chaveDne ]
				Long idPai = mapaLogradouroChaveDne[ chavePaiDne ]
				if ( chavePaiDne > 0 && id > 0 ) {
					bpsw.addBatch(logradouroPaiId: idPai, id: id)
				}
			}
		}
	}
	private Map registroDados( String linha ) {
		Map<String, Integer> schema = [
			primeiroCaracter: 1
			, siglaUf: 2
			, separador_01: 6
			, chaveCidadeDne: 8
			, nomeCid: 72
			, separador_02: 5
			, chaveBairroIniDne: 8
			, nomeBairroIniDne: 72
			, separador_03: 5
			, chaveBairroFimDne: 8
			, nomeBairroFimDne: 72
			, tipoLogradouro: 26
			, preposicao: 3
			, titulo: 72
			, separador_04: 6
			, chaveDne: 8
			, nome: 72
			, abrvACT: 36
			, complemento: 36
			, cep: 8
			, grandeUsuario: 1
			, ativo: 1
			, separador_05: 104
		]
		
		Map<String, Object> result = FixedWidthParser.parse( linha, schema )
		String aux = ''
		Long chaveCidadeDne = ZValue.toLong( result.chaveCidadeDne )
		
		Long bairroIni = null
		Long bairroFim = null
		Long tipoLogradouro = null
		Long cidade = null
		Long distrito = null
		
		Long bairroIniDNE = ZValue.toLong( result.chaveBairroIniDne )
		Long bairroFimDNE = ZValue.toLong( result.chaveBairroFimDne )
		Long chaveDne = ZValue.toLong( result.chaveDne )
		String tipoLog = result.tipoLogradouro
		cidade = mapaCidade[chaveCidadeDne]
		if (!mapaTipoCidade.containsKey(chaveCidadeDne)) {
			Map tipoCidade = db.firstRow("SELECT tipo as tipo FROM cidade WHERE chavedne = :chavedne", [chavedne: cidade])
			if (tipoCidade != null) {
				mapaTipoCidade[chaveCidadeDne] = tipoCidade.tipo
			}
		}
		if (mapaTipoCidade[chaveCidadeDne] == 'D' || mapaTipoCidade[chaveCidadeDne] == 'P' ) {
			distrito = cidade
			if (!mapaDistrito.containsKey(chaveCidadeDne)) {
				mapaDistrito[chaveCidadeDne] = db.firstRow("SELECT cidade_pai_id as id FROM cidade WHERE chavedne = :chavedne", [chavedne: distrito]).id
			}
			cidade = mapaDistrito[chaveCidadeDne]
		} else {
			distrito = null
		}
		if (bairroIniDNE) {
			bairroIni = mapaBairro[bairroIniDNE]
		}
		if (bairroFimDNE) {
			bairroFim = mapaBairro[bairroFimDNE]
		}

		if (tipoLog != '') {
			tipoLogradouro = mapaTipoLogradouro[tipoLog]
		}

		Map dados = [
				nome: linha.substring(374, 446).trim()
				, abrvACT: linha.substring(446, 482).trim()
				, cep: linha.substring(518, 526).trim()
				, tipoLog: tipoLogradouro
				, cidade: cidade
				, distrito: distrito
				, bairroIni: bairroIni
				, bairroFim: bairroFim
				, complemento: linha.substring(482, 518).trim()
				, preposicao: linha.substring(285, 288).trim()
				, titulo: linha.substring(288, 360).trim()
				, chaveDne: chaveDne
				, sistema: true
				, ativo: linha.substring(528, 529) == 'S'
				, grandeUsuario: linha.substring(526, 527).trim()
				, statusLogradouro: linha.substring(0, 1).trim()
				, ativo: linha.substring(527, 528) == 'S'
		]
		return dados
	}
	private Map registroSeccionamento(String linha) {
		Map<String, Integer> schema = [
			primeiroCaracter: 1
			, siglaUf: 2
			, separador_01: 6
			, chaveCidadeDne: 8
			, nomeCid: 72
			, separador_02: 5
			, chaveBairroIniDne: 8
			, nomeBairroIniDne: 72
			, separador_03: 5
			, chaveBairroFimDne: 8
			, nomeBairroFimDne: 72
			, tipoLogradouro: 26
			, preposicao: 3
			, titulo: 72
			, separador_04: 6
			, chaveDne: 8
			, nome: 72
			, abrvACT: 36
			, complemento: 36
			, cep: 8
			, grandeUsuario: 1
			, inicioTrecho: 11
			, finalTrecho: 11
			, paridade: 1
			, chaveSeccionamentoDne: 8
			, ativo: 1
			, separador_05: 73
		]
		Map<String, Object> result = FixedWidthParser.parse( linha, schema )
		String aux = ''
		Long chaveCidadeDne = ZValue.toLong( result.chaveCidadeDne )
		Long bairroIniDNE = ZValue.toLong( result.chaveBairroIniDne )
		Long bairroFimDNE = ZValue.toLong( result.chaveBairroFimDne )
		Long chaveDne = ZValue.toLong( result.chaveSeccionamentoDne )
		Float trechoIni = ZValue.toFloat( result.inicioTrecho )
		Float trechoFim = ZValue.toFloat( result.finalTrecho )
		Long logradouroPaiDNE = ZValue.toLong( result.chaveDne )
		String tipoLog = result.tipoLogradouro
		Long bairroIni = null
		Long bairroFim = null
		Long cidade = null
		Long distrito = null
		Long tipoLogradouro = null
		Long logradouroPai = null
		//
		cidade = mapaCidade[chaveCidadeDne]
		if (!mapaTipoCidade.containsKey(chaveCidadeDne)) {
			Map tipoCidade = db.firstRow("SELECT tipo as tipo FROM cidade WHERE chavedne = :chavedne", [chavedne: cidade])
			mapaTipoCidade[chaveCidadeDne] = tipoCidade.tipo
		}
		if (mapaTipoCidade[chaveCidadeDne] == 'D' || mapaTipoCidade[chaveCidadeDne] == 'P' ) {
			distrito = cidade
			if (!mapaDistrito.containsKey(chaveCidadeDne)) {
				mapaDistrito[chaveCidadeDne] = db.firstRow("SELECT cidade_pai_id as id FROM cidade WHERE chavedne = :chavedne", [chavedne: distrito]).id
			}
			cidade = mapaDistrito[chaveCidadeDne]
		} else {
			distrito = null
		}
		if (bairroIniDNE) {
			bairroIni = mapaBairro[bairroIniDNE]
		}
		if (bairroFimDNE) {
			bairroFim = mapaBairro[bairroFimDNE]
		}
		if (tipoLog != '') {
			tipoLogradouro = mapaTipoLogradouro[tipoLog]
		}
		if (logradouroPaiDNE && (logradouroPaiDNE > 0)) {
			mapaLogradouroDependencia[chaveDne] = logradouroPaiDNE
		}

		Map dados = [
			nome: result.nome
			, abrvACT: result.abrvACT
			, cep: result.cep
			, tipoLog: tipoLogradouro
			, cidade: cidade
			, distrito: distrito
			, bairroIni: bairroIni
			, bairroFim: bairroFim
			, complemento: result.complemento
			, preposicao: result.preposicao
			, titulo: result.titulo
			, chaveDne: chaveDne
			, sistema: true
			, grandeUsuario: result.grandeUsuario
			, statusLogradouro: result.primeiroCaracter
			, inicioTrecho: trechoIni
			, finalTrecho: trechoFim
			, paridade: result.paridade
			, logradouroPai: logradouroPai
			, ativo: (result.ativo == 'S')
		]
		return dados
	}
	private Map registroLote(String linha) {
		Map<String, Integer> schema = [
			primeiroCaracter: 1
			, siglaUf: 2
			, separador_01: 6
			, chaveCidadeDne: 8
			, nomeCid: 72
			, separador_02: 5
			, chaveBairroIniDne: 8
			, nomeBairroIniDne: 72
			, separador_03: 5
			, chaveBairroFimDne: 8
			, nomeBairroFimDne: 72
			, tipoLogradouro: 26
			, preposicao: 3
			, titulo: 72
			, separador_04: 6
			, chaveDne: 8
			, nome: 72
			, abrvACT: 36
			, complemento: 36
			, cep: 8
			, grandeUsuario: 1
			, lote: 11
			, chaveLoteDne: 8
			, ativo: 1
			, separador_05: 85
		]

		Map<String, Object> result = FixedWidthParser.parse( linha, schema )
		String aux = ''

		Long chaveCidadeDne = ZValue.toLong( result.chaveCidadeDne )
		Long bairroIniDNE = ZValue.toLong( result.chaveBairroIniDne )
		Long bairroFimDNE = ZValue.toLong( result.chaveBairroFimDne )
		Long chaveDne = ZValue.toLong( result.chaveLoteDne )
		Long logradouroPaiDNE = ZValue.toLong( result.chaveDne )
		String tipoLog = result.tipoLogradouro
		Long bairroIni = null
		Long bairroFim = null
		Long cidade = null
		Long distrito = null
		Long tipoLogradouro = null
		Long logradouroPai = null

		cidade = mapaCidade[chaveCidadeDne]
		if (!mapaTipoCidade.containsKey(chaveCidadeDne)) {
			Map tipoCidade = db.firstRow("SELECT tipo as tipo FROM cidade WHERE chavedne = :chavedne", [chavedne: cidade])
			mapaTipoCidade[chaveCidadeDne] = tipoCidade.tipo
		}
		if (mapaTipoCidade[chaveCidadeDne] == 'D' || mapaTipoCidade[chaveCidadeDne] == 'P' ) {
			distrito = cidade
			if (!mapaDistrito.containsKey(chaveCidadeDne)) {
				mapaDistrito[chaveCidadeDne] = db.firstRow("SELECT cidade_pai_id as id FROM cidade WHERE chavedne = :chavedne", [chavedne: distrito]).id
			}
			cidade = mapaDistrito[chaveCidadeDne]
		} else {
			distrito = null
		}

		if (bairroIniDNE) {
			bairroIni = mapaBairro[bairroIniDNE]
		}
		if (bairroFimDNE) {
			bairroFim = mapaBairro[bairroFimDNE]
		}
		if (tipoLog != '') {
			tipoLogradouro = mapaTipoLogradouro[tipoLog]
		}
		if (logradouroPaiDNE && (logradouroPaiDNE > 0)) {
			mapaLogradouroDependencia[chaveDne] = logradouroPaiDNE
		}

		Map dados = [
			nome: result.nome
			, abrvACT: result.abrvACT
			, cep: result.cep
			, tipoLog: tipoLogradouro
			, cidade: cidade
			, distrito: distrito
			, bairroIni: bairroIni
			, bairroFim: bairroFim
			, complemento: result.complemento
			, preposicao: result.preposicao
			, titulo: result.titulo
			, chaveDne: result.chaveDne
			, sistema: true
			, grandeUsuario: result.grandeUsuario
			, statusLogradouro: result.primeiroCaracter
			, numeroComplemento: result.complemento
			, nomeLote: result.lote
			, logradouroPai: logradouroPai
			, ativo: (result.ativo == 'S')
		]
		return dados
	}
	private Map registroComplemento(String linha) {
		Map<String, Integer> schema = [
			primeiroCaracter: 1
			, siglaUf: 2
			, separador_01: 6
			, chaveCidadeDne: 8
			, nomeCid: 72
			, separador_02: 5
			, chaveBairroIniDne: 8
			, nomeBairroIniDne: 72
			, separador_03: 5
			, chaveBairroFimDne: 8
			, nomeBairroFimDne: 72
			, tipoLogradouro: 26
			, preposicao: 3
			, titulo: 72
			, separador_04: 6
			, chaveDne: 8
			, nome: 72
			, abrvACT: 36
			, complemento: 36
			, cep: 8
			, grandeUsuario: 1
			, lote: 11
			, nomeComplemento: 36
			, numeroComplemento: 11
			, chaveLoteDne: 8
			, chaveComplementoDne: 8
			, ativo: 1
			, separador_05: 30
		]

		Map<String, Object> result = FixedWidthParser.parse( linha, schema )
		String aux = ''
		String cep = result.cep

		Long chaveCidadeDne = ZValue.toLong( result.chaveCidadeDne )
		Long bairroIniDNE = ZValue.toLong( result.chaveBairroIniDne )
		Long bairroFimDNE = ZValue.toLong( result.chaveBairroFimDne )
		Long chaveDne = ZValue.toLong( result.chaveComplementoDne )
		Long logradouroPaiDNE = ZValue.toLong( result.chaveLoteDne )
		String tipoLog = result.tipoLogradouro
		Long bairroIni = null
		Long bairroFim = null
		Long cidade = null
		Long distrito = null
		Long tipoLogradouro = null
		Long logradouroPai = null

		cidade = mapaCidade[chaveCidadeDne]
		if (!mapaTipoCidade.containsKey(chaveCidadeDne)) {
			Map tipoCidade = db.firstRow("SELECT tipo as tipo FROM cidade WHERE chavedne = :chavedne", [chavedne: cidade])
			mapaTipoCidade[chaveCidadeDne] = tipoCidade.tipo
		}
		if (mapaTipoCidade[chaveCidadeDne] == 'D' || mapaTipoCidade[chaveCidadeDne] == 'P' ) {
			distrito = cidade
			if (!mapaDistrito.containsKey(chaveCidadeDne)) {
				mapaDistrito[chaveCidadeDne] = db.firstRow("SELECT cidade_pai_id as id FROM cidade WHERE chavedne = :chavedne", [chavedne: distrito]).id
			}
			cidade = mapaDistrito[chaveCidadeDne]
		} else {
			distrito = null
		}

		if (bairroIniDNE) {
			bairroIni = mapaBairro[bairroIniDNE]
		}
		if (bairroFimDNE) {
			bairroFim = mapaBairro[bairroFimDNE]
		}
		if (tipoLog != '') {
			tipoLogradouro = mapaTipoLogradouro[tipoLog]
		}
		if (logradouroPaiDNE && (logradouroPaiDNE > 0)) {
			mapaLogradouroDependencia[chaveDne] = logradouroPaiDNE
		}
		String nomeComplemento = montaNomeComplemento( result.nome, result.tipoLogradouro, result.lote, result.nomeComplemento, result.numeroComplemento )

		Map dados = [
			nome: nomeComplemento
			, abrvACT: result.abrvACT
			, cep: result.cep
			, tipoLog: tipoLogradouro
			, cidade: cidade
			, distrito: distrito
			, bairroIni: bairroIni
			, bairroFim: bairroFim
			, complemento: result.complemento
			, preposicao: result.preposicao
			, titulo: result.titulo
			, chaveDne: result.chaveDne
			, sistema: true
			, grandeUsuario: result.grandeUsuario
			, statusLogradouro: result.primeiroCaracter
			, numeroComplemento: result.complemento
			, nomeLote: result.lote
			, logradouroPai: logradouroPai
			, ativo: (result.ativo == 'S')
		]
		return dados
	}
	private Map registroComplemento2(String linha) {
		Map<String, Integer> schema = [
			primeiroCaracter: 1
			, siglaUf: 2
			, separador_01: 6
			, chaveCidadeDne: 8
			, nomeCid: 72
			, separador_02: 5
			, chaveBairroIniDne: 8
			, nomeBairroIniDne: 72
			, separador_03: 5
			, chaveBairroFimDne: 8
			, nomeBairroFimDne: 72
			, tipoLogradouro: 26
			, preposicao: 3
			, titulo: 72
			, separador_04: 6
			, chaveDne: 8
			, nome: 72
			, abrvACT: 36
			, complemento: 36
			, cep: 8
			, grandeUsuario: 1
			, chaveLoteDne: 11
			, nomeComplemento1: 36
			, numeroComplemento1: 11
			, nomeComplemento2: 11
			, numeroComplemento2: 11
			, chaveLoteDne: 8
			, chaveComplemento1Dne: 8
			, chaveComplemento2Dne: 8
			, separador_05: 1
		]

		Map<String, Object> result = FixedWidthParser.parse( linha, schema )
		String aux = ''

		Long chaveCidadeDne = ZValue.toLong( result.chaveCidadeDne )
		Long bairroIniDNE = ZValue.toLong( result.chaveBairroIniDne )
		Long bairroFimDNE = ZValue.toLong( result.chaveBairroFimDne )
		Long chaveDne = ZValue.toLong( result.chaveComplementoDne )
		Long logradouroPaiDNE = ZValue.toLong( result.chaveLoteDne )
		String tipoLog = result.tipoLogradouro
		Long bairroIni = null
		Long bairroFim = null
		Long cidade = null
		Long distrito = null
		Long tipoLogradouro = null
		Long logradouroPai = null
		Long chaveLoteDNE = ZValue.toLong( result.chaveLoteDne )
		
		cidade = mapaCidade[chaveCidadeDne]
		if (!mapaTipoCidade.containsKey(chaveCidadeDne)) {
			Map tipoCidade = db.firstRow("SELECT tipo as tipo FROM cidade WHERE chavedne = :chavedne", [chavedne: cidade])
			mapaTipoCidade[chaveCidadeDne] = tipoCidade.tipo
		}
		if (mapaTipoCidade[chaveCidadeDne] == 'D' || mapaTipoCidade[chaveCidadeDne] == 'P' ) {
			mapaDistrito[chaveCidadeDne] = cidade; distrito = cidade
			cidade = db.firstRow("SELECT cidade_pai_id as id FROM cidade WHERE chavedne = :chavedne", [chavedne: distrito]).id
		} else {
			distrito = null
		}
		cidade = mapaCidade[chaveCidadeDne]
		if (!mapaTipoCidade.containsKey(chaveCidadeDne)) {
			Map tipoCidade = db.firstRow("SELECT tipo as tipo FROM cidade WHERE chavedne = :chavedne", [chavedne: cidade])
			mapaTipoCidade[chaveCidadeDne] = tipoCidade.tipo
		}
		if (mapaTipoCidade[chaveCidadeDne] == 'D' || mapaTipoCidade[chaveCidadeDne] == 'P' ) {
			distrito = cidade
			if (!mapaDistrito.containsKey(chaveCidadeDne)) {
				mapaDistrito[chaveCidadeDne] = db.firstRow("SELECT cidade_pai_id as id FROM cidade WHERE chavedne = :chavedne", [chavedne: distrito]).id
			}
			cidade = mapaDistrito[chaveCidadeDne]
		} else {
			distrito = null
		}
		if (bairroIniDNE) {
			bairroIni = mapaBairro[bairroIniDNE]
		}
		if (bairroFimDNE) {
			bairroFim = mapaBairro[bairroFimDNE]
		}
		if (tipoLog != '') {
			tipoLogradouro = mapaTipoLogradouro[tipoLog]
		}
		if (logradouroPaiDNE && (logradouroPaiDNE > 0)) {
			mapaLogradouroDependencia[chaveDne] = logradouroPaiDNE
		}

		Map dados = [
			nome: result.nome
			, abrvACT: result.abrvACT
			, cep: result.cep
			, tipoLog: tipoLogradouro
			, cidade: cidade
			, distrito: distrito
			, bairroIni: bairroIni
			, bairroFim: bairroFim
			, complemento: result.complemento
			, preposicao: result.preposicao
			, titulo: result.titulo
			, chaveDne: result.chaveDne
			, sistema: true
			, grandeUsuario: result.grandeUsuario
			, statusLogradouro: result.primeiroCaracter
			, nomeComplemento: result.nomeComplemento1
			, numeroComplemento: result.numeroComplemento1
			, chaveComplementoDne: ZValue.toLong( result.chaveComplemento1Dne )
			, nomeComplemento2: result.nomeComplemento2
			, numeroComplemento2: result.numeroComplemento2
			, chaveComplemento2Dne: ZValue.toLong( result.chaveComplemento2Dne )
			, nomeLote: result.lote
			, logradouroPai: logradouroPai
			, ativo: (result.ativo == 'S')
		]
		return dados
	}
	@Override
	public void criaTabela() {
		db.execute("""
DROP INDEX IF EXISTS "idxLogAtivo";
DROP INDEX IF EXISTS "idxLogBaiFim";
DROP INDEX IF EXISTS "idxLogBaiIni";
DROP INDEX IF EXISTS "idxLogCep";
DROP INDEX IF EXISTS "idxLogChaveDNE";
DROP INDEX IF EXISTS "idxLogCid";
DROP INDEX IF EXISTS "idxLogNome";
DROP INDEX IF EXISTS "idxLogNomeNormal";
DROP INDEX IF EXISTS "idxLogPai";
DROP INDEX IF EXISTS "idxLogTipoLog";
		""")
		db.execute("DROP TABLE IF EXISTS logradouro CASCADE;")
		db.execute("""
CREATE TABLE IF NOT EXISTS logradouro (
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    version bigint NOT NULL,
    sistema boolean NOT NULL,
    ativo boolean,
    bairro_ini_id bigint,
    grande_usuario character varying(1),
    numero_complemento character varying(11),
    status_logradouro character varying(1),
    cidade_id bigint NOT NULL,
    nome_complemento character varying(36),
    bairro_fim_id bigint,
    inicio_trecho real,
    nome character varying(80) NOT NULL,
    nome_lote character varying(11),
    preposicao character varying(255),
    paridade character varying(1),
    final_trecho real,
    titulo character varying(72),
    logradouro_pai_id bigint,
    abrvact character varying(40),
    cep character varying(8) NOT NULL,
    tipo_log_id bigint,
    complemento character varying(36),
    nome_normalizado character varying(80),
    chavedne integer,
    distrito_id bigint,
    CONSTRAINT "logradouroPK" PRIMARY KEY (id),
    CONSTRAINT "FK93tfea11f272blnxneh8n3ckb" FOREIGN KEY (tipo_log_id)
        REFERENCES tipo_logradouro (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "FKciava83lcjsrr1tw7to5mmk6j" FOREIGN KEY (distrito_id)
        REFERENCES cidade (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "FKhib8282pn8wdqo8jr03wf98tu" FOREIGN KEY (bairro_fim_id)
        REFERENCES bairro (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "FKneb9muh1cao3k2v1gf3129b82" FOREIGN KEY (bairro_ini_id)
        REFERENCES bairro (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "FKrjp5f5funfiq32ud2pic8666h" FOREIGN KEY (logradouro_pai_id)
        REFERENCES logradouro (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "FKsqhqblp4bm9mdo0ck39seqjci" FOREIGN KEY (cidade_id)
        REFERENCES cidade (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
		""")
		db.execute("""
CREATE INDEX IF NOT EXISTS "idxLogAtivo" ON logradouro USING btree (ativo ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS "idxLogBaiFim" ON logradouro USING btree (bairro_fim_id ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS "idxLogBaiIni" ON logradouro USING btree (bairro_ini_id ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS "idxLogCep" ON logradouro USING btree (cep ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS "idxLogChaveDNE" ON logradouro USING btree (chavedne ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS "idxLogCid" ON logradouro USING btree (cidade_id ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS "idxLogNomeNormal" ON logradouro USING btree (nome_normalizado ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS "idxLogNome" ON logradouro USING btree (nome ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS "idxLogPai" ON logradouro USING btree (logradouro_pai_id ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS "idxLogTipoLog" ON logradouro USING btree (tipo_log_id ASC NULLS LAST);
""")
	}
	@Override
	public void finaliza() {
	}
}
