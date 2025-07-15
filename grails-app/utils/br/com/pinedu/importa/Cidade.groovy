package br.com.pinedu.importa

import groovy.sql.BatchingPreparedStatementWrapper
import groovy.sql.GroovyResultSet
import groovy.sql.Sql

import java.io.File;
import java.util.Map;

import br.com.pinedu.importa.pattern.Command

class Cidade implements Command {
	File arquivoCidade
	File arquivoFxCidade
	Map mapaEstado
	Map mapaCidade = [:]
	Map mapaTipoCidade = [:]
	Sql db
	Long id = 0
	Long version = 0
	String cep1i = null
	String cep1f = null
	String cep2i = null
	String cep2f = null
	Boolean sistema = Boolean.TRUE
	@Override
	public void criaTabela() {
		db.execute("DROP TABLE IF EXISTS cidade CASCADE;")
		db.execute("DROP SEQUENCE IF EXISTS seq_cidade;")
		db.execute("""
DROP INDEX IF EXISTS "idxCidAtivo";
DROP INDEX IF EXISTS "idxCidCep";
DROP INDEX IF EXISTS "idxCidChaveDNE";
DROP INDEX IF EXISTS "idxCidNome";
DROP INDEX IF EXISTS "idxCidSit";
DROP INDEX IF EXISTS "idxCidTipo";
DROP INDEX IF EXISTS "idxCidUF";
DROP INDEX IF EXISTS "idxCidadeNomeNormalizado";
DROP INDEX IF EXISTS "idxDistCid";
		""")
		db.execute("""
CREATE TABLE IF NOT EXISTS cidade (
    id bigint NOT NULL,
    version bigint NOT NULL,
    sigladrect character varying(255),
    cep2i character varying(8),
    sistema boolean NOT NULL,
    ativo boolean,
    codibge integer,
    cidade_pai_id bigint,
    cep2f character varying(8),
    nome character varying(72) NOT NULL,
    uf_id bigint NOT NULL,
    tipo character varying(1),
    abrvact character varying(36),
    cep character varying(8),
    cep1f character varying(8),
    situacao character varying(1),
    cep1i character varying(8),
    nome_normalizado character varying(72),
    chavedne integer,
    CONSTRAINT "cidadePK" PRIMARY KEY (id),
    CONSTRAINT "FK76kduk86xxb5qr3dw0ol6sim4" FOREIGN KEY (uf_id)
        REFERENCES estado (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "FK7kynuml1xtgolifxg3uia1xxk" FOREIGN KEY (cidade_pai_id)
        REFERENCES cidade (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);""")
		db.execute("CREATE SEQUENCE seq_cidade;")
		db.execute("""
CREATE INDEX IF NOT EXISTS "idxCidAtivo" ON cidade USING btree (ativo);
CREATE INDEX IF NOT EXISTS "idxCidCep" ON cidade USING btree (cep);
CREATE INDEX IF NOT EXISTS "idxCidChaveDNE" ON cidade USING btree (chavedne);
CREATE INDEX IF NOT EXISTS "idxCidNome" ON cidade USING btree (nome);
CREATE INDEX IF NOT EXISTS "idxCidSit" ON cidade USING btree (situacao);
CREATE INDEX IF NOT EXISTS "idxCidTipo" ON cidade USING btree (tipo);
CREATE INDEX IF NOT EXISTS "idxCidUF" ON cidade USING btree (uf_id);
CREATE INDEX IF NOT EXISTS "idxCidadeNomeNormalizado" ON cidade USING btree (nome_normalizado);
CREATE INDEX IF NOT EXISTS "idxDistCid" ON cidade USING btree (cidade_pai_id);
""")
	}

	@Override
	public void execute() {
		List<Map<String, Object>> inserts = []
		List<Map<String, Object>> updates = []
		Map<Long, Long> mapaCidadeComCidadePai = [:]
		Map<String, Integer> schema = [
			primeiroCaracter: 1
			, pais: 2
			, siglaUF: 2
			, separador_01: 5
			, chaveDne: 9
			, nome: 72
			, cep: 8
			, abrvACT: 36
			, tipoLocalidade: 1
			, situacao: 1
			, separador_02: 6
			, chaveSuborDNE: 8
			, siglaRect: 3
			, codIBGE: 7
			, separador_03: 1
		]
		db.eachRow( 'SELECT id, chavedne FROM cidade' ) { GroovyResultSet grs->
			mapaCidade[ ZValue.toLong( grs.chavedne ) ] = ZValue.toLong( grs.id )
		}
		arquivoCidade.eachLine("ISO-8859-1") { String linha ->
			if (linha.size() <= 0) return
			String primeiroCaracter = linha.substring(0, 1)
			if ( "#" == primeiroCaracter || "C" == primeiroCaracter) return
			Map<String, Object> result = FixedWidthParser.parse( linha, schema )
			String abrvACT = result.abrvACT
			String siglaUF = result.siglaUF
			String nome = result.nome
			String cep = result.cep
			String tipoLocalidade = result.tipoLocalidade
			String situacao = result.situacao
			String siglaRect = result.siglaRect
			Long chaveDne = ZValue.toLong( result.chaveDne )
			Long codIBGE = ZValue.toLong( result.codIBGE )
			if ( ''.equals( result.codIBGE ) ) {
				codIBGE = null
			}
			Long chaveSuborCEP = null
			Long chaveSuborDNE = ZValue.toLong( result.chaveSuborDNE )

			Map cidade = mapaCidade[ chaveDne ]
			mapaTipoCidade[ chaveDne ] = tipoLocalidade
			Long cidadePai
			if ( chaveSuborDNE > 0) {
				mapaCidadeComCidadePai[ chaveDne ] = chaveSuborDNE
				if ( mapaCidade.containsKey( chaveSuborDNE ) && ( mapaCidade[chaveSuborDNE] > 0 ) ) {
					cidadePai = mapaCidade[chaveSuborDNE]
				}
			}
			if (cidade == null) {
				inserts << [version: version, abrvact: abrvACT, cep: cep, chavedne: chaveDne, codibge: codIBGE, nome: nome, sigladrect: siglaRect, sistema: sistema, situacao: situacao, tipo: tipoLocalidade, uf_id: mapaEstado[siglaUF], nome_normalizado: Format.normaliza( nome ), ativo: true]
			} else {
				updates << [version: 1      , abrvact: abrvACT, cep: cep, chavedne: chaveDne, codibge: codIBGE, nome: nome, sigladrect: siglaRect, sistema: sistema, situacao: situacao, tipo: tipoLocalidade, uf_id: mapaEstado[siglaUF], nome_normalizado: Format.normaliza( nome ), ativo: true, id: cidade.id]
			}
		}
		if ( inserts ) {
			db.withBatch(500, "INSERT INTO cidade( id, version, abrvact, cep, chavedne, codibge, nome, sigladrect, sistema, situacao, tipo, uf_id, nome_normalizado, ativo) VALUES ( nextval('seq_cidade'), :version, :abrvact, :cep, :chavedne, :codibge, :nome, :sigladrect, :sistema, :situacao, :tipo, :uf_id, :nome_normalizado, :ativo);") { BatchingPreparedStatementWrapper bpsw ->
				inserts.each {
					bpsw.addBatch(it)
				}
			}
		}
		if ( updates ) {
			db.withBatch(500, "UPDATE cidade SET version=:version, abrvact=:abrvact, cep=:cep, chavedne=:chavedne, codibge=:codibge, nome=:nome, sigladrect=:sigladrect, situacao=:situacao, tipo=:tipo, uf_id=:uf_id, nome_normalizado=:nome_normalizado WHERE id = :id;") { BatchingPreparedStatementWrapper bpsw ->
				updates.each {
					bpsw.addBatch(it)
				}
			}
		}
		db.eachRow( 'SELECT id, chavedne FROM cidade' ) { GroovyResultSet grs->
			mapaCidade[ ZValue.toLong( grs.chavedne ) ] = ZValue.toLong( grs.id )
		}
		if ( mapaCidadeComCidadePai ) {
			db.withBatch(500, "UPDATE cidade SET cidade_pai_id=:cidadePai WHERE id = :id;") { BatchingPreparedStatementWrapper bpsw ->
				mapaCidadeComCidadePai.each { Long chaveDne, Long chaveDnePai ->
					Long id = mapaCidade[chaveDne]
					Long idPai = mapaCidade[chaveDnePai]
					if ((id && (id > 0)) && (idPai && (idPai > 0))) {
						bpsw.addBatch(cidadePai: idPai, id: id)
					}
				}
			}
		}
		//mapaCidadeComCidadePai[ chaveDne ] = chaveSuborDNE

/*
		arquivoCidade.eachLine("ISO-8859-1") { String linha ->
			if (linha.size() <= 0) return
			String primeiroCaracter = linha.substring(0, 1) 
			if ( "#" == primeiroCaracter || "C" == primeiroCaracter) return
			Map<String, Object> result = FixedWidthParser.parse( linha, schema )
			String abrvACT = result.abrvACT
			String siglaUF = result.siglaUF
			String nome = result.nome
			String cep = result.cep
			String tipoLocalidade = result.tipoLocalidade
			String situacao = result.situacao
			String siglaRect = result.siglaRect
			Long chaveDne = ZValue.toLong( result.chaveDne )
			Long codIBGE = ZValue.toLong( result.codIBGE )
			if ( ''.equals( result.codIBGE ) ) {
				codIBGE = null
			}
			cep1i = null
			cep1f = null
			cep2i = null
			cep2f = null
			Long chaveSuborCEP = null
			Long chaveSuborDNE = ZValue.toLong( result.chaveSuborDNE )
			
			Map cidade = db.firstRow("SELECT id as id, version as version FROM cidade WHERE chavedne = :chavedne", [chavedne: chaveDne])
			Map cidadePai = null
			if (chaveSuborDNE) {
				cidadePai = db.firstRow("SELECT id as id FROM cidade WHERE chavedne = :chavedne", [chavedne: chaveSuborDNE])
			}
			mapaTipoCidade[ chaveDne ] = tipoLocalidade
			if (cidade == null) {
				id = db.executeInsert("INSERT INTO cidade( id, version, abrvact, cep, cep1f, cep1i, cep2f, cep2i,  chavedne, cidade_pai_id, codibge, nome, sigladrect, sistema,  situacao, tipo, uf_id, nome_normalizado, ativo) VALUES (nextval('seq_cidade'), :version, :abrvact, :cep, :cep1f, :cep1i, :cep2f, :cep2i, :chavedne, :cidade_pai_id, :codibge, :nome, :sigladrect, :sistema, :situacao, :tipo, :uf_id, :nome_normalizado, :ativo);"
					, [version: version, abrvact: abrvACT, cep: cep, cep1f: cep1f, cep1i: cep1i, cep2f: cep2f, cep2i: cep2i, chavedne: chaveDne, cidade_pai_id: cidadePai?.id, codibge: codIBGE, nome: nome, sigladrect: siglaRect, sistema: sistema, situacao: situacao, tipo: tipoLocalidade, uf_id: mapaEstado[siglaUF], nome_normalizado: Format.normaliza( nome ), ativo: true])[0][0]
				mapaCidade[chaveDne] = id
			} else {
				db.executeUpdate("UPDATE cidade SET version=:version, abrvact=:abrvact, cep=:cep, cep1f=:cep1f, cep1i=:cep1i, cep2f=:cep2f, cep2i=:cep2i, chavedne=:chavedne, cidade_pai_id=:cidade_pai_id, codibge=:codibge, nome=:nome, sigladrect=:sigladrect, situacao=:situacao, tipo=:tipo, uf_id=:uf_id, nome_normalizado=:nome_normalizado WHERE id = :id;"
					, [id: cidade.id, version: cidade.version + 1, abrvact: abrvACT, cep: cep, cep1f: cep1f, cep1i: cep1i, cep2f: cep2f, cep2i: cep2i, chavedne: chaveDne, cidade_pai_id: cidadePai?.id, codibge: codIBGE, nome: nome, sigladrect: siglaRect, situacao: situacao, tipo: tipoLocalidade, uf_id: mapaEstado[siglaUF], nome_normalizado: Format.normaliza( nome ), ativo: true])
				mapaCidade[chaveDne] = cidade.id
			}
		}
*/
		schema = [
				primeiroCaracter: 1
				, siglaUF: 2
				, separador_01: 2
				, chaveUfDne: 2
				, separador_02: 6
				, chaveDne: 8
				, nome: 72
				, qtdeFx: 2
				, separador_03: 1
				, ordem: 2
				, separador_04: 1
				, cep1i: 8
				, separador_05: 1
				, cep1f: 8
				, tipoFx: 1
				, situacao: 1
		]
		List<Map<String, Object>> listaCep1 = []
		List<Map<String, Object>> listaCep2 = []
		arquivoFxCidade.eachLine("ISO-8859-1") { String linha ->
			if (linha.size() <= 0) return
			String primeiroCaracter = linha.substring(0, 1)
			if ( "#" == primeiroCaracter || "C" == primeiroCaracter) return
			Map<String, Object> result = FixedWidthParser.parse( linha, schema )
			Long chaveDne = ZValue.toLong( result.chaveDne )
			Long seq = ZValue.toLong( result.ordem )
			if (seq == 1) {
				cep1i = result.cep1i
				cep1f = result.cep1f
				Long idCidade = mapaCidade[chaveDne]
				if ( idCidade ) {
					listaCep1 << [idCidade: idCidade, cep2i: result.cep1i, cep2f: result.cep1f ]
				}
			} else if (seq == 2) {
				cep2i = result.cep1i
				cep2f = result.cep1f
				Long idCidade = mapaCidade[chaveDne]
				if ( idCidade ) {
					listaCep2 << [idCidade: idCidade, cep2i: result.cep1i, cep2f: result.cep1f ]
				}
			}
		}
		if (listaCep1) {
			db.withBatch(500, 'UPDATE cidade SET cep1f=:cep1f, cep1i=:cep1i WHERE id = :id;') { BatchingPreparedStatementWrapper bpsw ->
				listaCep1.each { Map<String, Object> c ->
					bpsw.addBatch(id: c.idCidade, cep1f: c.cep1f, cep1i: c.cep1i)
				}
			}
		}
		if (listaCep2) {
			db.withBatch(500, 'UPDATE cidade SET cep2f=:cep2f, cep2i=:cep2i WHERE id = :id;') { BatchingPreparedStatementWrapper bpsw ->
				listaCep2.each { Map<String, Object> c ->
					bpsw.addBatch(id: c.idCidade, cep2f: c.cep2f, cep2i: c.cep2i)
				}
			}
		}
	}
	@Override
	public void leArquivo(String path) throws Exception {
		arquivoCidade = new File("${path}/DNE_GU_LOCALIDADES.TXT")
		arquivoFxCidade = new File("${path}/DNE_GU_FAIXAS_CEP_LOCALIDADE.TXT")
	}

	@Override
	public void finaliza() {
	}
}
