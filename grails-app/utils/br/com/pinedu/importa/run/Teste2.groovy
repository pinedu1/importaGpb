package br.com.pinedu.importa.run

import groovy.sql.Sql

class Teste2 {
	static main(args) {
		groovy.sql.Sql db = Sql.newInstance('jdbc:postgresql://127.0.0.1:5432/pinedu', 'pinedu', '142536', 'org.postgresql.Driver')
		criaTabela( db )
		db.executeUpdate("""
			copy tipo_logradouro from '/home/eduardo/teste/modelo/tipo_logradouro.copy';
			copy estado from '/home/eduardo/teste/modelo/estado.copy';
			copy cidade from '/home/eduardo/teste/modelo/cidade.copy';
			copy bairro from '/home/eduardo/teste/modelo/bairro.copy';
			copy logradouro from '/home/eduardo/teste/modelo/logradouro.copy';
			
			select setval('seq_estado', (select max(id)+1 from estado)) as estado, setval('seq_cidade', (select max(id)+1 from cidade)) as cidade, setval('seq_bairro', (select max(id)+1 from bairro)) as bairro, setval('seq_logradouro', (select max(id)+1 from logradouro)) as logradouro, setval('seq_tipo_logradouro', (select max(id)+1 from tipo_logradouro)) as tipo_logradouro;
		""")
		//select tl.sigla, l.nome, b.nome,l.cep,c.nome,u.uf from logradouro as l inner join bairro as b on b.id = l.bairro_ini_id inner join tipo_logradouro as tl on tl.id = l.tipo_log_id inner join cidade as c on c.id=l.cidade_id inner join estado as u on c.uf_id=u.id where l.cep like '122446__' order by l.nome asc;
	}
	public static void criaTabela( groovy.sql.Sql db ) {
		db.execute("""
			CREATE OR REPLACE FUNCTION normaliza(TEXT) RETURNS TEXT AS
			\$BODY\$
				DECLARE
				ENTRA ALIAS FOR \$1;
				BEGIN
				RETURN upper(translate(regexp_replace(ENTRA,'([[:space:]]{2,})',' ','g'),'áéíóúàèìòùãõâêîôôäëïöüçñÁÉÍÓÚÀÈÌÒÙÃÕÂÊÎÔÛÄËÏÖÜÇÑ','aeiouaeiouaoaeiooaeioucnAEIOUAEIOUAOAEIOOAEIOUCN'));
				END;
			\$BODY\$ LANGUAGE plpgsql IMMUTABLE COST 100;
			
			CREATE OR REPLACE FUNCTION normaliza(VARCHAR) RETURNS VARCHAR AS
			\$BODY\$
				DECLARE
				ENTRA ALIAS FOR \$1;
				BEGIN
				RETURN upper(translate(regexp_replace(ENTRA,'([[:space:]]{2,})',' ','g'),'áéíóúàèìòùãõâêîôôäëïöüçñÁÉÍÓÚÀÈÌÒÙÃÕÂÊÎÔÛÄËÏÖÜÇÑ','aeiouaeiouaoaeiooaeioucnAEIOUAEIOUAOAEIOOAEIOUCN'));
				END;
			\$BODY\$ LANGUAGE plpgsql IMMUTABLE COST 100;
			
			CREATE OR REPLACE FUNCTION normaliza(CHARACTER)
				 RETURNS CHARACTER AS
			\$BODY\$
				DECLARE
				ENTRA ALIAS FOR \$1;
				BEGIN
				RETURN upper(translate(regexp_replace(ENTRA,'([[:space:]]{2,})',' ','g'),'áéíóúàèìòùãõâêîôôäëïöüçñÁÉÍÓÚÀÈÌÒÙÃÕÂÊÎÔÛÄËÏÖÜÇÑ','aeiouaeiouaoaeiooaeioucnAEIOUAEIOUAOAEIOOAEIOUCN'));
				END;
			\$BODY\$ LANGUAGE plpgsql IMMUTABLE COST 100;
		""")
		/**
		 * Estado
		 */
		db.execute("DROP SEQUENCE IF EXISTS seq_estado;")
		db.execute("""
			DROP INDEX IF EXISTS estado_cep1f_uniq;
			DROP INDEX IF EXISTS estado_cep2f_uniq;
			DROP INDEX IF EXISTS estado_cep2i_uniq;
			DROP INDEX IF EXISTS estado_chavedne_uniq;
			DROP INDEX IF EXISTS "estado_idxUfCep";
			DROP INDEX IF EXISTS "estado_idxUfNome";
			DROP INDEX IF EXISTS estado_uf_uniq;
		""")
		db.execute("DROP TABLE IF EXISTS estado CASCADE;")
		db.execute("""
			CREATE TABLE estado
			(
			  id bigint NOT NULL,
			  version bigint NOT NULL,
			  abrvact character varying(20),
			  cep1f character varying(8),
			  cep1i character varying(8),
			  cep2f character varying(8),
			  cep2i character varying(8),
			  chavedne integer NOT NULL,
			  nome character varying(30) NOT NULL,
			  sistema boolean NOT NULL,
			  uf character varying(2) NOT NULL,
			  CONSTRAINT "estadoPK" PRIMARY KEY (id)
			);
		""")
		db.execute("""
			CREATE UNIQUE INDEX estado_cep1f_uniq ON estado (cep1f);
			CREATE UNIQUE INDEX estado_cep2f_uniq ON estado (cep2f);
			CREATE UNIQUE INDEX estado_cep2i_uniq ON estado (cep2i);
			CREATE UNIQUE INDEX estado_chavedne_uniq ON estado (chavedne);
			CREATE INDEX "estado_idxUfCep" ON estado (cep1i);
			CREATE INDEX "estado_idxUfNome" ON estado (nome);
			CREATE UNIQUE INDEX estado_uf_uniq ON estado (uf);
		""")
		db.execute("CREATE SEQUENCE seq_estado;")
		/**
		 * Cidade
		 */
		db.execute("DROP TABLE IF EXISTS cidade CASCADE;")
		db.execute("DROP SEQUENCE IF EXISTS seq_cidade;")
		db.execute("""
			DROP INDEX IF EXISTS idxcidadenomenormalizado;
			DROP INDEX IF EXISTS "idxDistCid";
			DROP INDEX IF EXISTS "idxCidadeNomeNormalizado";
			DROP INDEX IF EXISTS "idxCidUF";
			DROP INDEX IF EXISTS "idxCidTipo";
			DROP INDEX IF EXISTS "idxCidSit";
			DROP INDEX IF EXISTS "idxCidNome";
			DROP INDEX IF EXISTS "idxCidChaveDNE";
			DROP INDEX IF EXISTS "idxCidChaveCep";
			DROP INDEX IF EXISTS "idxCidCep";
			DROP INDEX IF EXISTS cidade_cep2i_uniq;
			DROP INDEX IF EXISTS cidade_cep2f_uniq;
			DROP INDEX IF EXISTS cidade_cep1i_uniq;
			DROP INDEX IF EXISTS cidade_cep1f_uniq;
		""")
		db.execute("""
				CREATE TABLE cidade (
				  id bigint NOT NULL,
				  version bigint NOT NULL,
				  abrvact character varying(25),
				  cep character varying(8),
				  cep1f character varying(8),
				  cep1i character varying(8),
				  cep2f character varying(8),
				  cep2i character varying(8),
				  chavedne integer,
				  cidade_pai_id bigint,
				  codibge integer,
				  nome character varying(72) NOT NULL,
				  sigladrect character varying(255),
				  sistema boolean NOT NULL,
				  situacao character varying(1),
				  tipo character varying(1),
				  uf_id bigint NOT NULL,
				  nome_normalizado character varying(72) NOT NULL,
				  CONSTRAINT "cidadePK" PRIMARY KEY (id),
				  CONSTRAINT cidade_cidade_id_FK FOREIGN KEY (cidade_pai_id)
				      REFERENCES cidade (id) MATCH SIMPLE
				      ON UPDATE NO ACTION ON DELETE NO ACTION,
				  CONSTRAINT cidade_estado_id_FK FOREIGN KEY (uf_id)
				      REFERENCES estado (id) MATCH SIMPLE
				      ON UPDATE NO ACTION ON DELETE NO ACTION
				);
		""")
		db.execute("CREATE SEQUENCE seq_cidade;")
		db.execute("""
			CREATE INDEX "idxCidChaveDNE" ON cidade(chavedne);
			CREATE UNIQUE INDEX cidade_cep1f_uniq ON cidade (cep1f);
			CREATE UNIQUE INDEX cidade_cep1i_uniq ON cidade (cep1i);
			CREATE UNIQUE INDEX cidade_cep2f_uniq ON cidade (cep2f);
			CREATE UNIQUE INDEX cidade_cep2i_uniq ON cidade (cep2i);
			CREATE INDEX "idxCidCep" ON cidade (cep);
			CREATE INDEX "idxCidNome" ON cidade (nome);
			CREATE INDEX "idxCidSit" ON cidade (situacao);
			CREATE INDEX "idxCidTipo" ON cidade (tipo);
			CREATE INDEX "idxCidUF" ON cidade (uf_id);
			CREATE INDEX "idxDistCid" ON cidade (cidade_pai_id);
			CREATE INDEX "idxCidadeNomeNormalizado" ON cidade (nome_normalizado);
		""")
		/**
		 * Bairro
		 */
		db.execute("DROP SEQUENCE IF EXISTS seq_bairro;")
		db.execute("""
			DROP INDEX IF EXISTS "idxBaiChaveDNE";
			DROP INDEX IF EXISTS "idxBaiCid";
			DROP INDEX IF EXISTS "idxBaiDis";
			DROP INDEX IF EXISTS "idxBaiNome";
			DROP INDEX IF EXISTS "idxBairroNomeNormalizado";
			DROP INDEX IF EXISTS "idxCidadeFrnKeyCidade";
			DROP INDEX IF EXISTS "idxDistritoFrnKeyCidade";
		""")
		db.execute("DROP TABLE IF EXISTS bairro CASCADE;")
		db.execute("""
			CREATE TABLE bairro
			(
			  id bigint NOT NULL,
			  version bigint NOT NULL,
			  abrvact character varying(40),
			  cep1f character varying(8),
			  cep1i character varying(8),
			  cep2f character varying(8),
			  cep2i character varying(8),
			  chavedne integer,
			  cidade_id bigint NOT NULL,
			  distrito_id bigint,
			  nome character varying(72) NOT NULL,
			  sistema boolean NOT NULL,
			  nome_normalizado character varying(72) NOT NULL,
			  CONSTRAINT "bairroPK" PRIMARY KEY (id),
			  CONSTRAINT bairro_cidade_FK FOREIGN KEY (cidade_id)
			      REFERENCES cidade (id) MATCH SIMPLE
			      ON UPDATE NO ACTION ON DELETE NO ACTION,
			  CONSTRAINT bairro_distrito_FK FOREIGN KEY (distrito_id)
			      REFERENCES cidade (id) MATCH SIMPLE
			      ON UPDATE NO ACTION ON DELETE NO ACTION
			);
		""")
		db.execute("""
			CREATE INDEX "idxBaiChaveDNE" ON bairro (chavedne);
			CREATE INDEX "idxBaiNome" ON bairro (nome);
			CREATE INDEX "idxBairroNomeNormalizado" ON bairro (nome_normalizado);
			CREATE INDEX "idxCidadeFrnKeyCidade" on bairro (cidade_id);
			CREATE INDEX "idxDistritoFrnKeyCidade" on bairro (distrito_id);
		""")
		db.execute("CREATE SEQUENCE seq_bairro;")
		/**
		 * Tipo de Logradouro
		 */
		db.execute("DROP SEQUENCE IF EXISTS seq_tipo_logradouro;")
		db.execute("""
			 DROP INDEX IF EXISTS tipologradouro_chavedne_uniq;
			 DROP INDEX IF EXISTS "idxTipLogAtivo";
			 DROP INDEX IF EXISTS "idxTipLogNome";
			 DROP INDEX IF EXISTS "idxTipLogSigla";
		""")
		db.execute("DROP TABLE IF EXISTS tipo_logradouro CASCADE;")
		db.execute("""
			CREATE TABLE tipo_logradouro
			(
			  id bigint NOT NULL,
			  version bigint NOT NULL,
			  ativo boolean,
			  chavedne bigint,
			  nome character varying(72) NOT NULL,
			  sigla character varying(40) NOT NULL,
			  sistema boolean NOT NULL,
			  CONSTRAINT "tipo_logradouPK" PRIMARY KEY (id)
			)
		""")
		db.execute("CREATE SEQUENCE seq_tipo_logradouro;")
		db.execute("""
			CREATE UNIQUE INDEX tipologradouro_chavedne_uniq ON tipo_logradouro (chavedne);
			CREATE INDEX "idxTipLogAtivo" ON tipo_logradouro (ativo);
			CREATE INDEX "idxTipLogNome" ON tipo_logradouro (nome);
			CREATE INDEX "idxTipLogSigla" ON tipo_logradouro (sigla);
		""")
		/**
		 * Tipo de Logradouro
		 */
		db.execute("DROP SEQUENCE IF EXISTS seq_logradouro;")
		db.execute("""
			DROP INDEX IF EXISTS "idxLogCep";
			DROP INDEX IF EXISTS "idxLogChaveDNE";
			DROP INDEX IF EXISTS "idxLogNome";
			DROP INDEX IF EXISTS "idxLogNomeNormalizado";
			DROP INDEX IF EXISTS "idxLogPai";
			DROP INDEX IF EXISTS "idxLogTipoLog";
			DROP INDEX IF EXISTS "idxBairroIniFrnKey";
			DROP INDEX IF EXISTS "idxBairroFimFrnKey";
			DROP INDEX IF EXISTS "idxCidadeFrnKey";
			DROP INDEX IF EXISTS "idxDistritoFrnKey";
		""")
		db.execute("DROP TABLE IF EXISTS logradouro CASCADE;")
		db.execute("""
			CREATE TABLE logradouro
			(
			  id bigint NOT NULL,
			  version bigint NOT NULL,
			  abrvact character varying(40),
			  bairro_fim_id bigint,
			  bairro_ini_id bigint,
			  cep character varying(8) NOT NULL,
			  chavedne integer,
			  cidade_id bigint NOT NULL,
			  complemento character varying(36),
			  distrito_id bigint,
			  final_trecho real,
			  grande_usuario character varying(1),
			  inicio_trecho real,
			  logradouro_pai_id bigint,
			  nome character varying(72) NOT NULL,
			  nome_complemento character varying(36),
			  nome_lote character varying(11),
			  numero_complemento character varying(11),
			  paridade character varying(1),
			  preposicao character varying(255),
			  sistema boolean NOT NULL,
			  status_logradouro character varying(1),
			  tipo_log_id bigint,
			  titulo character varying(72),
			  nome_normalizado character varying(72) NOT NULL,
			  CONSTRAINT "logradouroPK" PRIMARY KEY (id),
			  CONSTRAINT logradouro_tipo_logradouro_FK FOREIGN KEY (tipo_log_id)
			      REFERENCES tipo_logradouro (id) MATCH SIMPLE
			      ON UPDATE NO ACTION ON DELETE NO ACTION,
			  CONSTRAINT logradouro_bairro_fim_FK FOREIGN KEY (bairro_fim_id)
			      REFERENCES bairro (id) MATCH SIMPLE
			      ON UPDATE NO ACTION ON DELETE NO ACTION,
			  CONSTRAINT logradouro_bairro_ini_FK FOREIGN KEY (bairro_ini_id)
			      REFERENCES bairro (id) MATCH SIMPLE
			      ON UPDATE NO ACTION ON DELETE NO ACTION,
			  CONSTRAINT logradouro_cidade_FK FOREIGN KEY (cidade_id)
			      REFERENCES cidade (id) MATCH SIMPLE
			      ON UPDATE NO ACTION ON DELETE NO ACTION,
			  CONSTRAINT logradouro_logradouro_pai_FK FOREIGN KEY (logradouro_pai_id)
			      REFERENCES logradouro (id) MATCH SIMPLE
			      ON UPDATE NO ACTION ON DELETE NO ACTION,
			  CONSTRAINT logradouro_distrito_FK FOREIGN KEY (distrito_id)
			      REFERENCES cidade (id) MATCH SIMPLE
			      ON UPDATE NO ACTION ON DELETE NO ACTION
			);
		""")
		db.execute("""
			CREATE INDEX "idxLogCep" ON logradouro (cep);
			CREATE INDEX "idxLogChaveDNE" ON logradouro (chavedne);
			CREATE INDEX "idxLogNome" ON logradouro (nome);
			CREATE INDEX "idxLogNomeNormalizado" ON logradouro(nome_normalizado);
			CREATE INDEX "idxLogPai" ON logradouro (logradouro_pai_id);
			CREATE INDEX "idxLogTipoLog" ON logradouro (tipo_log_id);
			CREATE INDEX "idxBairroIniFrnKey" ON logradouro (bairro_ini_id);
			CREATE INDEX "idxBairroFimFrnKey" ON logradouro (bairro_fim_id);
			CREATE INDEX "idxCidadeFrnKey" ON logradouro (cidade_id);
			CREATE INDEX "idxDistritoFrnKey" ON logradouro (distrito_id);
		""")
		db.execute("CREATE SEQUENCE seq_logradouro;")
	}
}
