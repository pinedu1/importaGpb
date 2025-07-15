package br.com.pinedu.importa

import groovy.sql.BatchingPreparedStatementWrapper
import groovy.sql.GroovyResultSet
import groovy.sql.Sql

import java.util.Map;

import br.com.pinedu.importa.pattern.Command

class Bairro implements Command {
	private static Boolean DEBUG = true
	File arquivoBairro
	File arquivoFxBairro
	Map mapaCidade = [:]
	Map mapaBairro = [:]
	Sql db
	Long id = 0
	String cep1i = ''
	String cep1f = ''
	String cep2i = ''
	String cep2f = ''
	Boolean sistema = true
	@Override
	public void execute() {
		List<Map<String, Object>> inserts = []
		List<Map<String, Object>> updates = []
		Map<String, Integer> schema = [
			primeiroCaracter: 1
			, sigla: 2
			, separador_01: 6
			, chaveCidadeDne: 8
			, nomeCid: 72
			, separador_02: 5
			, chaveDne: 8
			, nome: 72
			, abrvACT: 36
			, separador_03: 1
		]
		db.eachRow( 'SELECT id, chavedne FROM bairro' ) { GroovyResultSet grs->
			mapaBairro[ ZValue.toLong( grs.chavedne ) ] = ZValue.toLong( grs.id )
		}
		arquivoBairro.eachLine("ISO-8859-1") { String linha ->
			String primeiroCaracter = linha.substring(0, 1)
			if ( "#" == primeiroCaracter || "C" == primeiroCaracter) return
			Map<String, Object> result = FixedWidthParser.parse( linha, schema )
			String sigla = result.sigla
			Long chaveCidadeDne = ZValue.toLong( result.chaveCidadeDne )
			Long chaveDne = ZValue.toLong( result.chaveDne )
			String nome = result.nome
			String abrvACT = result.abrvACT
			Long bairro = mapaBairro[ chaveDne ]
			if (bairro == null) {
				inserts << [version: 0, abrvact: abrvACT, chavedne: chaveDne, cidade_id: mapaCidade[chaveCidadeDne], nome: nome, sistema: sistema, nome_normalizado: Format.normaliza( nome )]
			} else {
				updates << [version: 1, abrvact: abrvACT, chavedne: chaveDne, cidade_id: mapaCidade[chaveCidadeDne], nome: nome, sistema: sistema, nome_normalizado: Format.normaliza( nome ), id: bairro]
			}
		}
		if ( inserts ) {
			db.withBatch(500, "INSERT INTO bairro(id, version, abrvact, chavedne, cidade_id, nome, sistema, nome_normalizado) VALUES (nextval('seq_bairro'), :version, :abrvact, :chavedne, :cidade_id, :nome, :sistema, :nome_normalizado);") { BatchingPreparedStatementWrapper bpsw ->
				inserts.each {
					bpsw.addBatch(it)
				}
			}
		}
		if ( updates ) {
			db.withBatch(500, "UPDATE bairro SET version=:version, abrvact=:abrvact, chavedne=:chavedne, cidade_id=:cidade_id, nome=:nome, nome_normalizado=:nome_normalizado WHERE id = :id;") { BatchingPreparedStatementWrapper bpsw ->
				updates.each {
					bpsw.addBatch(it)
				}
			}
		}
		db.eachRow( 'SELECT id, chavedne FROM bairro' ) { GroovyResultSet grs->
			mapaBairro[ ZValue.toLong( grs.chavedne ) ] = ZValue.toLong( grs.id )
		}
/*
		arquivoBairro.eachLine("ISO-8859-1") { String linha ->
			String primeiroCaracter = linha.substring(0, 1)
			if ( "#" == primeiroCaracter || "C" == primeiroCaracter) return
			Map<String, Object> result = FixedWidthParser.parse( linha, schema )
			String sigla = result.sigla
			Long chaveCidadeDne = ZValue.toLong( result.chaveCidadeDne )
			Long chaveDne = ZValue.toLong( result.chaveDne )
			String nome = result.nome
			String abrvACT = result.abrvACT
			cep1i = null
			cep1f = null
			cep2i = null
			cep2f = null
			Map bairro = mapaBairro[ chaveDne ]
			if (bairro == null) {
				inserts <<
				id = db.executeInsert("INSERT INTO bairro(id, version, abrvact, cep1f, cep1i, cep2f, cep2i, chavedne, cidade_id, distrito_id, nome, sistema, nome_normalizado) VALUES (nextval('seq_bairro'), :version, :abrvact, :cep1f, :cep1i, :cep2f, :cep2i, :chavedne, :cidade_id, :distrito_id, :nome, :sistema, :nome_normalizado);"
					, [version: 0, abrvact: abrvACT, cep1f: cep1f, cep1i: cep1i, cep2f: cep2f, cep2i: cep2i, chavedne: chaveDne, cidade_id: mapaCidade[chaveCidadeDne], distrito_id: null, nome: nome, sistema: sistema, nome_normalizado: Format.normaliza( nome )])[0][0]
				mapaBairro[chaveDne] = id
			} else {
				db.executeUpdate("UPDATE bairro SET version=:version, abrvact=:abrvact, cep1f=:cep1f, cep1i=:cep1i, cep2f=:cep2f, cep2i=:cep2i, chavedne=:chavedne, cidade_id=:cidade_id, distrito_id=:distrito_id, nome=:nome, nome_normalizado=:nome_normalizado WHERE id = :id;"
					, [id: bairro.id, version: bairro.version+1, abrvact: abrvACT, cep1f: cep1f, cep1i: cep1i, cep2f: cep2f, cep2i: cep2i, chavedne: chaveDne, cidade_id: mapaCidade[chaveCidadeDne], distrito_id: null, nome: nome, sistema: sistema, nome_normalizado: Format.normaliza( nome )])
			}
		}*/
		schema = [
			primeiroCaracter: 1
			, separador_00: 2
			, chaveUfDne: 2
			, siglaUF: 2
			, separador_01: 6
			, chaveCidDne: 8
			, nomeCid: 72
			, separador_02: 5
			, chaveDne: 8
			, nome: 72
			, qtdeFx: 2
			, separador_03: 1
			, ordem: 2
			, separador_04: 1
			, cep1i: 8
			, separador_05: 1
			, cep1f: 8
			, separador_06: 1
		]
		List<Map<String, Object>> listaCep1 = []
		List<Map<String, Object>> listaCep2 = []
		arquivoFxBairro.eachLine("ISO-8859-1") { String linha ->
			if (linha.size() <= 0) return
			String primeiroCaracter = linha.substring(0, 1)
			if ( "#" == primeiroCaracter || "C" == primeiroCaracter) return
			Map<String, Object> result = FixedWidthParser.parse( linha, schema )
			Long seq = ZValue.toLong( result.ordem )
			if (seq == 1) {
				Long chaveDne = ZValue.toLong( result.chaveDne )
				Long idBairro = mapaBairro[chaveDne]
				if ( idBairro ) {
					listaCep1 << [idBairro: idBairro, cep1i: result.cep1i, cep1f: result.cep1f]
				}
			} else if (seq == 2) {
				Long chaveDne = ZValue.toLong( result.chaveDne )
				Long idBairro = mapaBairro[chaveDne]
				if ( idBairro ) {
					listaCep2 << [idBairro: idBairro, cep2i: result.cep1i, cep2f: result.cep1f]
				}
			}
		}
		if ( listaCep1 ) {
			db.withBatch(500, 'UPDATE bairro SET cep1f=:cep1f, cep1i=:cep1i WHERE id = :id;') { BatchingPreparedStatementWrapper bpsw ->
				listaCep1.each { Map<String, Object> c ->
					bpsw.addBatch(id: c.idBairro, cep1f: c.cep1f, cep1i: c.cep1i)
				}
			}
		}
		if ( listaCep2 ) {
			db.withBatch(500, 'UPDATE bairro SET cep2f=:cep2f, cep2i=:cep2i WHERE id = :id;') { BatchingPreparedStatementWrapper bpsw ->
				listaCep2.each { Map<String, Object> c ->
					bpsw.addBatch(id: c.idBairro, cep2f: c.cep2f, cep2i: c.cep2i)
				}
			}
		}
	}
	@Override
	public void leArquivo(String path) throws Exception {
		arquivoBairro = new File("${path}/DNE_GU_BAIRROS.TXT")
		arquivoFxBairro = new File("${path}/DNE_GU_FAIXAS_CEP_BAIRRO.TXT")
	}
	@Override
	public void criaTabela() {
		db.execute("DROP SEQUENCE IF EXISTS seq_bairro;")
		db.execute("""
DROP INDEX IF EXISTS "idxBaiAtivo";
DROP INDEX IF EXISTS "idxBaiChaveDNE";
DROP INDEX IF EXISTS "idxBaiCid";
DROP INDEX IF EXISTS "idxBaiDis";
DROP INDEX IF EXISTS "idxBaiNome";
DROP INDEX IF EXISTS "idxBairroNomeNormalizado";
		""")
		db.execute("DROP TABLE IF EXISTS bairro CASCADE;")
		db.execute("""
CREATE TABLE IF NOT EXISTS bairro (
    id bigint NOT NULL,
    version bigint NOT NULL,
    cep2i character varying(8),
    sistema boolean NOT NULL,
    ativo boolean,
    cidade_id bigint NOT NULL,
    cep2f character varying(8),
    nome character varying(72) NOT NULL,
    abrvact character varying(40),
    cep1f character varying(8),
    cep1i character varying(8),
    nome_normalizado character varying(72),
    chavedne integer,
    distrito_id bigint,
    CONSTRAINT "bairroPK" PRIMARY KEY (id),
    CONSTRAINT "FKgmx8his7a51210gcsaunulx8b" FOREIGN KEY (cidade_id)
        REFERENCES cidade (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "FKr5exwfqaqxjcb8qtbtqnf7ppg" FOREIGN KEY (distrito_id)
        REFERENCES cidade (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
		""")
		db.execute("""
CREATE INDEX IF NOT EXISTS "idxBaiAtivo" ON bairro USING btree (ativo);
CREATE INDEX IF NOT EXISTS "idxBaiChaveDNE" ON bairro USING btree (chavedne);
CREATE INDEX IF NOT EXISTS "idxBaiCid" ON bairro USING btree (cidade_id);
CREATE INDEX IF NOT EXISTS "idxBaiDis" ON bairro USING btree (distrito_id);
CREATE INDEX IF NOT EXISTS "idxBaiNome" ON bairro USING btree (nome);
CREATE INDEX IF NOT EXISTS "idxBairroNomeNormalizado" ON bairro USING btree (nome_normalizado);
CREATE SEQUENCE seq_bairro;
""")
	}
	@Override
	public void finaliza() {
	}
}
