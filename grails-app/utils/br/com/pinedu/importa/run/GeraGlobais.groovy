package br.com.pinedu.importa.run

import br.com.pinedu.importa.Format
import groovy.sql.*

class GeraGlobais {
	private static Map<String, String> myParidade = ['I': 'LADO IMPAR', 'P': 'LADO PAR']
	private static String ENCODE_OUT_WINDOWS = 'ISO-8859-1'
	private static String ENCODE_OUT_LINUX = 'UTF-8'
	private static String LINE_TERMINATOR_WINDOWS = '\r\n'
	private static String LINE_TERMINATOR_LINUX = '\n'
	private static String path = "/home/eduardo/Downloads/GPB"

	static main(args) {
		String lineTerminator = LINE_TERMINATOR_WINDOWS
		String encodeOutput = ENCODE_OUT_WINDOWS
		if ( true ) {
			lineTerminator = LINE_TERMINATOR_LINUX
			encodeOutput = ENCODE_OUT_LINUX
		}
		Sql db = Sql.newInstance('jdbc:postgresql://127.0.0.1:5432/pnd', 'pnd', 'x8DwsDRMUvxqrq#L', 'org.postgresql.Driver')
		Map<String, StringBuilder> mapaEstados = new HashMap<String, StringBuilder>()
		Map mapCidade = [:]
		StringBuilder mySb = new StringBuilder("~Format=5.S~${lineTerminator}27 Jul 2016   5:27 PM   Cache${lineTerminator}")
		List<String> myIbge = new LinkedList<String>()
		File pasta = new File( path )
		
		if (! pasta.exists() ) {
			pasta.mkdirs()
		}
		def sql = """
			select 
				l.nome as logradouro, l.cep as cep, c.nome as cidade, b.nome as bairro, tl.sigla as tipo, l.chavedne as chavedne, l.inicio_trecho as do, l.final_trecho as ate, l.paridade as paridade, l.titulo as titulo, l.preposicao as preposicao, e.uf as uf 
			from
				logradouro as l
				inner join cidade as c on l.cidade_id = c.id
				inner join bairro as b on l.bairro_ini_id = b.id
				inner join tipo_logradouro as tl on l.tipo_log_id = tl.id
				inner join estado as e on c.uf_id = e.id
				order by e.uf asc, c.nome asc, l.nome asc, tl.sigla asc, l.cep asc;
		"""
		db.eachRow(sql, {row->
			if ( !mapaEstados.containsKey( row.uf ) ) {
				StringBuilder sb = new StringBuilder("~Format=5.S~${lineTerminator}27 Jul 2016   5:27 PM   Cache${lineTerminator}")
				mapaEstados.put( row.uf, sb )
				
				db.eachRow( "select e.nome as nome, e.uf as uf, e.cep1i as cep1i, e.cep1f as cep1f, e.cep2i as cep2i, e.cep2f as cep2f from estado as e where e.uf = :uf;", [uf: row.uf], {rowe->
					List<String> pieces = new ArrayList<String>()
					pieces.add( normaliza( rowe.nome ) )
					pieces.add( rowe.cep1i?: '' )
					pieces.add( rowe.cep1f?: '' )
					pieces.add( rowe.cep2i?: '' )
					pieces.add( rowe.cep2f?: '' )
					String global = "^GPB(\"${ row.uf }\")${lineTerminator}"
					String conteudo = "${pieces.join('^')}${lineTerminator}"
					sb.append( global ).append( conteudo )
					mySb.append( global ).append( conteudo )
				} )
				mapCidade[ row.uf ] = [:]
				println "Estado: ${row.uf}"
			}
			String nomeCidade = normaliza( row.cidade )
			StringBuilder sb = mapaEstados.get( row.uf )
			if ( !mapCidade[ row.uf ].containsKey( nomeCidade ) ) {
				db.eachRow( "select c.nome as nome, c.cep as cep, c.chavedne as chavedne, c.cep1i as cep1i, c.cep1f as cep1f, c.cep2i as cep2i, c.cep2f as cep2f, c.codibge as codibge, c.situacao as situacao from cidade as c inner join estado as e on e.id=c.uf_id where e.uf= :uf and c.nome = :cidade;", [uf: row.uf, cidade: row.cidade], {rowc->
					List<String> pieces = new LinkedList<String>()
					pieces.add( rowc.cep?: '' )
					pieces.add( rowc.situacao == 'C'? '1': '0' )
					pieces.add( '' )
					pieces.add( '' )
					pieces.add( '' )
					pieces.add( '' )
					pieces.add( rowc.chavedne )
					pieces.add( rowc.cep1i?: '' )
					pieces.add( rowc.cep1f?: '' )
					pieces.add( rowc.cep2i?: '' )
					pieces.add( rowc.cep2f?: '' )
					pieces.add( rowc.codibge?: '' )
					String global = "^GPB(\"${ row.uf }\",\"${ nomeCidade }\")${lineTerminator}"
					String conteudo = "${pieces.join('^')}${lineTerminator}"
					sb.append( global ).append( conteudo )
					mySb.append( global ).append( conteudo )
					
					if ( rowc.codibge ) {
						myIbge.add( "^GPBIBGE(\"${ row.uf }\",${rowc.codibge})" )
						myIbge.add( row.cidade )
						myIbge.add( "^GPBIBGE(\"${ row.uf }\",\"{\",\"${ nomeCidade }\",${rowc.codibge})" )
						myIbge.add( '' )
					}
					
					mapCidade[ row.uf ].put( nomeCidade, '1' )
				})
			}
			String nome = row.logradouro
			if ( row.preposicao ) {
				if ( row.titulo ) {
					nome = "${row.titulo} ${row.preposicao} ${row.logradouro}"
				} else {
					nome = "${row.preposicao} ${row.logradouro}"
				}
			} else if ( row.titulo ) {
				nome = "${row.titulo} ${row.logradouro}"
			}
			String trecho = "TODO"
			if ( row['do'] ) {
				trecho = "DO ${row['do'].intValue()}"
			}
			if ( row['ate'] ) {
				if ( !row['do'] ) {
					trecho = ""
				} else {
					trecho += " "
				}
				trecho += "ATE ${row['ate'].intValue()}"
			}
			if ( row['paridade'] && myParidade.containsKey( row['paridade'] ) ) {
				trecho += " ${myParidade.get( row['paridade'] )}"
			}
			List<String> pieces = new LinkedList<String>()
			pieces.add( row.bairro )
			pieces.add("")
			pieces.add("")
			pieces.add("")
			pieces.add("")
			pieces.add("")
			pieces.add( row.chavedne )
			String global = "^GPB(\"${ row.uf }\",\"${ nomeCidade }\",\" CENTRO\",\"${ normaliza( nome ) }\",\"${ normaliza( row.tipo ) }\",\"${ trecho }\",\"${ row.cep }\")${lineTerminator}"
			String conteudo = "${pieces.join('^')}${lineTerminator}"
			sb.append( global ).append( conteudo )
			mySb.append( global ).append( conteudo )
		})
		
		File diretorio = new File( path )
		if ( !diretorio.exists() ) {
			diretorio.mkdirs()
		}
		StringBuilder sbTip = new StringBuilder()
		db.eachRow( "SELECT t.id as id, t.sigla as sigla, t.nome as nome, t.chavedne as chavedne, (SELECT count(l.id) from logradouro as l where l.tipo_log_id=t.id) as qtde from tipo_logradouro as t;", {rowTipo->
			List<String> pieces = new LinkedList<String>()
			pieces.add( normaliza( rowTipo.nome ) )
			pieces.add( rowTipo.chavedne )
			pieces.add( rowTipo.qtde )
			String conteudo = "${pieces.join('^')}${lineTerminator}"
			String global = "^GPBTIP(\"${ normaliza( rowTipo.sigla ) }\")${lineTerminator}"
			sbTip.append( global ).append( conteudo )
			mySb.append( global ).append( conteudo )
		})
		
		mapaEstados.each{String key, StringBuilder value->
			File file = new File("$path/GPB_${key}.GSA")
			file.withWriter( encodeOutput ) {writer ->
				writer.writeLine value.toString()
			}
		}
		for (String ibge: myIbge) {
			mySb.append( ibge ).append( lineTerminator )
		}
		File file = new File("$path/GPB.GSA")
		file.withWriter( encodeOutput ) { writer ->	
			writer.writeLine mySb.toString()
		}
		sbTip.insert( 0, "~Format=5.S~${lineTerminator}27 Jul 2016   5:27 PM   Cache${lineTerminator}" )
		File fileTip = new File("$path/GPB_TIP.GSA")
		fileTip.withWriter( encodeOutput ) { writer ->
			writer.writeLine sbTip.toString()
		}
	}
	static String normaliza(String texto) {
		return Format.normaliza( texto )
	}
}
