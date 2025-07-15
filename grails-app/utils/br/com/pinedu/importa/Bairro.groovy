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
			db.withBatch(500, "INSERT INTO bairro(version, abrvact, chavedne, cidade_id, nome, sistema, nome_normalizado) VALUES (:version, :abrvact, :chavedne, :cidade_id, :nome, :sistema, :nome_normalizado);") { BatchingPreparedStatementWrapper bpsw ->
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
		db.execute("""
DROP INDEX IF EXISTS "idxBaiAtivo";
DROP INDEX IF EXISTS "idxBaiNome";
DROP INDEX IF EXISTS "idxBaiCid";
DROP INDEX IF EXISTS "idxBaiChaveDNE";
DROP INDEX IF EXISTS "idxBairroNomeNormalizado";
DROP INDEX IF EXISTS "idxBaiDis";
		""")
		db.execute("DROP TABLE IF EXISTS bairro CASCADE;")
		db.execute("""
CREATE TABLE IF NOT EXISTS public.bairro (
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
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
        REFERENCES public.cidade (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT "FKr5exwfqaqxjcb8qtbtqnf7ppg" FOREIGN KEY (distrito_id)
        REFERENCES public.cidade (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
		""")
		db.execute("""
CREATE INDEX IF NOT EXISTS "idxBaiAtivo" ON bairro USING btree (ativo ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS "idxBaiChaveDNE" ON bairro USING btree (chavedne ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS "idxBaiCid" ON bairro USING btree (cidade_id ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS "idxBaiDis" ON bairro USING btree (distrito_id ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS "idxBaiNome" ON bairro USING btree (nome ASC NULLS LAST);
CREATE INDEX IF NOT EXISTS "idxBairroNomeNormalizado" ON bairro USING btree (nome_normalizado ASC NULLS LAST);
""")
	}
	@Override
	public void finaliza() {
	}
}
