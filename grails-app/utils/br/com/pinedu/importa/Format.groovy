package br.com.pinedu.importa

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import java.text.*
import java.time.Instant

@CompileStatic
class Format {
	private static final Set<String> MAIUSCULOS = new HashSet<String>(['r', 'av', 'tv'])
	private final static Set<String> EXCESSOES = new HashSet<String>(['de', 'di', 'do', 'da', 'dos', 'das', 'dello', 'della', 'dalla', 'dal', 'del', 'e', 'em', 'na', 'no', 'nas', 'nos', 'van', 'von', 'y', 'o', 'a'])
	private final static Set<String> ROMANOS = new HashSet<String>( ['i', 'ii', 'iii', 'iv', 'v', 'vi', 'vii', 'viii', 'ix', 'x', 'xi', 'xii', 'xiii', 'xiv', 'xv', 'xvi', 'xvii', 'xviii', 'xix', 'xx', 'xxi', 'xxii', 'xxiii', 'xxiv', 'xxv', 'xxvi', 'xxvii', 'xxviii', 'xxix', 'xxx'] )
	private final static NumberFormat NUMBER = NumberFormat.getInstance( )
	public final static Locale LOCALE_BR = Locale.forLanguageTag("pt-BR")
	private final static Locale LOCALE_UTF = Locale.forLanguageTag("UTF")
	private final static DecimalFormatSymbols DFS = new DecimalFormatSymbols( LOCALE_BR )
	private final static SimpleDateFormat SDF = new java.text.SimpleDateFormat( "yyyy-MM-dd", LOCALE_BR )
	private final static SimpleDateFormat SDFL = new java.text.SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ", LOCALE_UTF )
	private final static SimpleDateFormat FORMATO_DATA_SIMPLES = new java.text.SimpleDateFormat( "dd/MM/yyyy", LOCALE_BR )
	private final static SimpleDateFormat FORMATO_DATA_HORA = new java.text.SimpleDateFormat( 'dd/MM/yyyy HH:mm:ss', LOCALE_BR )
	private final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", LOCALE_UTF )
	private final static SimpleDateFormat FORMAT2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", LOCALE_UTF )
	private final static SimpleDateFormat FORMAT4 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", LOCALE_UTF )
	//                       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
	private final static SimpleDateFormat FORMAT3 = new SimpleDateFormat("dd/MM/yyyy hh:mm a", LOCALE_BR )
	private final static SimpleDateFormat FORMATCONTRATO = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", LOCALE_BR )
	private static final Map<Long,String> ORDINAL = [
		0l			: ''
		,1l			: 'Primeiro'
		,2l			: 'Segundo'
		,3l			: 'Terceiro'
		,4l			: 'Quarto'
		,5l			: 'Quinto'
		,6l			: 'Sexto'
		,7l			: 'Setimo'
		,8l			: 'Oitavo'
		,9l			: 'Nono'
		,10l		   : 'Décimo'
		,20l		   : 'Vigésimo'
		,30l		   : 'Trigésimo'
		,40l		   : 'Quadragésimo'
		,50l		   : 'Quiquagésimo'
		,60l		   : 'Sextagésimo'
		,70l		   : 'Septuagésimo'
		,80l		   : 'Octagésimo'
		,90l		   : 'Nonagésimo'
		,100l		  : 'Centésimo'
		,200l		  : 'Ducentésimo'
		,300l		  : 'Tricentésimo'
		,400l		  : 'Quadringentésimo'
		,500l		  : 'Qüingentésimo'
		,600l		  : 'Sexcentésimo'
		,700l		  : 'Septingentésimo'
		,800l		  : 'Octingentésimo'
		,900l		  : 'Noningentésimo'
		,1000l		 : 'Milésimo'
		,1000000l	  : 'Milionésimo'
		,1000000000l   : 'Bilionésimo'
		,1000000000000l: 'Trilionésimo'
	]
	public static String formata(Double valor, int casas = 2, boolean addReal = false) {
		if ( valor == null ) valor = 0d;
		NUMBER.setGroupingUsed(true);
		NUMBER.setMaximumFractionDigits(casas);
		NUMBER.setMinimumFractionDigits(casas);
		NUMBER.setMaximumIntegerDigits(999999999);
		NUMBER.setMinimumIntegerDigits( 1 );
		return (addReal? 'R$ ': '') + NUMBER.format(valor);
	}
	public static String capitalizaNome(String nome) {
		if (!nome) return ''
		return nome?.replaceAll(/\s+/, ' ')?.trim()?.toLowerCase()?.replaceAll(',r', ', r')?.replaceAll(',av', ', av')?.split(' ').collect{String token->
			if (MAIUSCULOS.contains( token )) return token.toUpperCase()
			else if ( ROMANOS.contains( token?.toLowerCase( ) ) ) {
				return token.toUpperCase( )
			} else if ( EXCESSOES.contains( token?.toLowerCase( ) ) ) {
				return token.toLowerCase( )
			}
			return token.capitalize()
		}.join(' ')
	}
	public static String capitalizaNome(Integer nome) {
		return capitalizaNome(nome.toString())
	}
	static String formataValor(valor) {
		if (valor && ZValue.toDouble(valor) > 0) {
			return formata(ZValue.toDouble(valor), 0, false)
		} else {
			return null
		}
	}
	public static String formatTituloSiteSpan(String texto) {
		if ( texto ) {
			List<String> tt = texto?.toLowerCase().split(' ') as List
			String ini = tt[0]
			tt.remove( 0 )
			texto = "${ini?.toUpperCase()}<span>&nbsp;${tt.join( ' ' )?.capitalize()}</span>"
		} else {
			texto='Título<span>Site</site>'
		}
		return texto
	}
	static String formatMoney(final Double val, final boolean dinheiro = false) {
		final BigDecimal valor = ZValue.toBigDecimal(val)
		String fmt = '#,###,##0.00'
		if (dinheiro) {
			fmt = '\u00A4#,###,##0.00'
		}
		return new DecimalFormat(fmt, DFS ).format( valor )
	}
	static String formatMoney(String val, final boolean dinheiro = false) {
		if (!val) val = '0'
		return formatMoney (ZValue.toDouble(val), dinheiro)
	}
	static String formatFixed(final Double val) {
		formatFixed(val, 2)
	}
	static String formatFixed(final BigDecimal val, final int casas) {
		return formatFixed( val.doubleValue(), casas )
	}
	static String formatFixed(final Double val, final int casas) {
		final String fmt = '0' * casas
		return new DecimalFormat(fmt, DFS ).format(val)
	}
	static String formatFixed(final Long val, final int casas) {
		return formatFixed(new Double(val), casas)
	}
	static String formatFixed(final String val, final int casas) {
		final Double valor = ZValue.toDouble(val)
		return formatFixed(valor, casas)
	}
	static String formatDecimalSimples(final Double val){
		return val == 0 ? 0 : new DecimalFormat('#.00', DFS ).format(val)
	}
	static String formatDecimalSimples(final String val){
		return formatDecimalSimples (ZValue.toDouble(val))
	}
	static String formatDecimal(final Double val) {
		return new DecimalFormat('#,###,##0.00', DFS ).format(val)
	}
	static String formatDecimal(final String val) {
		return formatDecimal (ZValue.toDouble(val))
	}
	static String formatPercent(final String val) {
		return formatPercent( ZValue.toDouble(val) )
	}
	static String formatPercent(final double val) {
		final double valor = val
		return (new DecimalFormat('0.00', DFS ).format(valor))+'%'
	}
	static String formatPercent(final float val) {
		return formatPercent( ZValue.toDouble( val ) )
	}
	static String formatPercent(final int val) {
		final double valor = val / 100
		return formatPercent( val )
	}
	static String formatPercent(final long val) {
		final double valor = val / 100
		return formatPercent( val )
	}
	static String formatCPFCNPJ(final Long val) {
		return formatCPFCNPJ( val.toString() )
	}
	static String formatCPFCNPJ(final Integer val) {
		return formatCPFCNPJ( val.toString() )
	}
	static String formatCPFCNPJ(final String val) {
		if (!val) return ''
		final String valor = val.replaceAll(/\D/, '')
		if (!valor) return ''
		if (valor.length() <= 11) {
			return valor.padLeft(11, '0').replaceAll(/([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})/,'$1.$2.$3-$4')
		} else if (valor.length() <= 14) {
			return valor.padLeft(14, '0').replaceAll(/([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})/,'$1.$2.$3/$4-$5')
		}
		return val
	}
	static String formatCEP(final Long val) {
		return formatCEP(val.toString())
	}
	static String formatCEP(final Integer val) {
		return formatCEP(val.toString())
	}
	static String formatCEP(final String val) {
		if (!val) return ''
		final String valor = val.replaceAll(/\D/,'')
		return valor.padLeft(8,'0').replaceAll(/([0-9]{5})([0-9]{3})/, '$1-$2')
	}
	static String formataDataSimples(String data) {
		return formataDataSimples( Date.from( Instant.parse( data ) ) )
	}
	static String formataDataSimples(java.sql.Date data) {
		if (data) return formataDataSimples( new java.util.Date( data.getTime() ) )
		return null
	}
	static String formataDataSimples(java.util.Date data) {
		if (data) return FORMATO_DATA_SIMPLES.format( data )
		return null
	}
	static String formataDataContrato(java.util.Date data) {
		if (data) return FORMATCONTRATO.format( data )
		return null
	}
	static String formataData(String data) {
		return formataData( Date.from( Instant.parse( data ) ) )
	}
	static String formataData(java.sql.Date data) {
		if (data) return formataData( new java.util.Date( data.getTime() ) )
		return null
	}
	static String formataData(java.util.Date data) {
		if (data) return FORMATO_DATA_HORA.format( data )
		return null
	}
	@CompileDynamic
	static String formatDataHora( Date data ) {
		if (!data) return ""
		return SDFL.format(data)
	}
	static Date toDate( String data ) {
		try {
			if ( data.indexOf( 'T' ) < 0 ) {
				return SDF.parse( data )
			} else {
				if ( data.endsWith('Z') ) {
					data = data.replace("Z", "+0000")
				}
				return SDFL.parse( data )
			}
		} catch( Exception e ) {
			e.printStackTrace()
		}
		return null
	}

	public static String toAlfaFoto( int numero ) {
		String[] arrayAlfa = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'X', 'W', 'Y', 'Z']
		StringBuilder nome = new StringBuilder( )
		int tamanho = arrayAlfa.size( )
		int myResto
		int myDiv = numero
		if ( myDiv < 1 ) {
			throw new Exception( 'Não pode contar com número < 1' )
		}
		while ( myDiv > 0 ) {
			myResto = ( myDiv -1 ) % tamanho
			myDiv = ( int )( ( myDiv -1 ) / tamanho )
			nome.insert( 0, arrayAlfa[myResto] )
		}
		return nome.toString( )
	}
	static String normaliza( String texto ) {
		return normalize( texto )
	}
	static String normalize( String texto ) {
		if ( texto == null ) return null
		return java.text.Normalizer.normalize( texto, java.text.Normalizer.Form.NFD )?.replaceAll( "[^\\p{ASCII}]", "" )?.replaceAll( /[^A-Za-z0-9]/, ' ' )?.replaceAll( /\s+/, ' ' )?.toUpperCase( );
	}
	static String normalizePathFoto( String pathFoto ) {
		if ( pathFoto == null ) return null
		return java.text.Normalizer.normalize( pathFoto, java.text.Normalizer.Form.NFD )
			.replaceAll(/\p{InCombiningDiacriticalMarks}+/, '') // Remove acentos
			.replaceAll(/[- ]/, '') // Remove hífens e espaços
	}

/*
	static String normalize( Object value ) {
		if ( value == null ) return null
		final String texto
		if (value instanceof String) {
			texto = (String) value
		} else {
			texto = value.toString()
		}
		return normalize( texto )
	}
*/
	static String normalizeEmail( String texto ) {
		if ( texto == null ) return null
		return java.text.Normalizer.normalize( texto, java.text.Normalizer.Form.NFD )?.replaceAll( "[^\\p{ASCII}]", "" )?.toLowerCase()
	}
	static String toCPFCNPJ( final Long val ) {
		return toCPFCNPJ( val.toString( ) )
	}
	static String toCPFCNPJ( final Integer val ) {
		return toCPFCNPJ( val.toString( ) )
	}
	static String toCPFCNPJ( final String val ) {
		if ( !val ) return ''
		final String valor = val.replaceAll( /\D/, '' )
		if ( !valor ) return ''
		if ( valor.length( ) <= 11 ) {
			return valor.padLeft( 11, '0' ).replaceAll( /([0-9]{3})([0-9]{3})([0-9]{3})([0-9]{2})/,'$1.$2.$3-$4' )
		} else if ( valor.length( ) <= 14 ) {
			return valor.padLeft( 14, '0' ).replaceAll( /([0-9]{2})([0-9]{3})([0-9]{3})([0-9]{4})([0-9]{2})/,'$1.$2.$3/$4-$5' )
		}
		return val
	}
	static String toTelefone( final Long val ) {
		if ( !val ) return ''
		toTelefone( val.toString( ) )
	}
	static String toTelefone( final Integer val ) {
		if ( !val ) return ''
		toTelefone( val.toString( ) )
	}
	static String limpaTelefone( String valor ) {
		if (!valor) return ''
		return valor.replaceAll( /\D/, '' )
	}
static String toTelefone( String valor ) {
		if ( !valor ) return ''
		valor = limpaTelefone( valor )
		if ( !valor ) return ''
		if ( valor.length( ) < 8 ) {
			valor = valor.padLeft( 10, '0' ).replaceAll( /([0-9]{2})([0-9]{4})([0-9]{4})/,'( $1 ) $2-$3' )
		} else if ( valor.length( ) == 8 ) {
			valor = valor.replaceAll( /([0-9]{4})([0-9]{4})/,'$1-$2' )
		} else if ( valor.length( ) == 9 ) {
			valor = valor.replaceAll( /([0-9]{1})([0-9]{4})([0-9]{4})/,'$1.$2-$3' )
		} else if ( valor.length( ) == 10 ) {
			valor = valor.replaceAll( /([0-9]{2})([0-9]{4})([0-9]{4})/,'($1) $2-$3' )
		} else if ( valor.length( ) == 11 ) {
			valor = valor.replaceAll( /([0-9]{2})([0-9]{1})([0-9]{4})([0-9]{4})/,'($1) $2.$3-$4' )
		}
		return valor
	}
	static String formatTelefone(final Long val, final String ddd = '') {
		formatTelefone(val.toString(), ddd)
	}
	static String formatTelefone(final Integer val, final String ddd = '') {
		formatTelefone(val.toString(), ddd)
	}
	static String formatTelefone(final String val, final String ddd = '') {
		if (!val) return ''
		String valor = val.replaceAll(/\D/, '')
		if (!valor) return ''
		return toTelefone("${ddd}${val}" as String)
	}
	static String toCEP( final Long val ) {
		return toCEP( val.toString( ) )
	}
	static String toCEP( final Integer val ) {
		return toCEP( val.toString( ) )
	}
	static String toCEP( final String val ) {
		if ( !val ) return ''
		final String valor = val.replaceAll( /\D/,'' )
		return valor.padLeft( 8,'0' ).replaceAll( /([0-9]{5})([0-9]{3})/, '$1-$2' )
	}
	static String camelCase( String nome ) {
		Set romanos = ['i', 'ii', 'iii', 'iv', 'v', 'vi', 'vii', 'viii', 'ix', 'x', 'xi', 'xii', 'xiii', 'xiv', 'xv', 'xvi', 'xvii', 'xviii', 'xix', 'xx', 'xxi', 'xxii', 'xxiii', 'xxiv', 'xxv', 'xxvi', 'xxvii', 'xxviii', 'xxix', 'xxx'].toSet( )
		if ( !nome ) return ''
		nome = nome.toLowerCase( )
		def retorno = ""
		def nomes = nome.split( ' ' )
		nomes.each { String n->
			if ( !n ) return ''
			if ( n.size( ) == 1 ) {
				retorno += " ${n.toUpperCase( )}"
				return
			}
			n = n.toLowerCase( )
			if ( romanos.contains( n ) ) {
				retorno += " ${n.toUpperCase( )}"
				return
			}
			switch ( n ) {
				case 'em':
				case 'no':
				case 'na':
				case 'do':
				case 'de':
				case 'da':
				case 'dos':
				case 'das':
					retorno += " ${n}"
					break
				default:
					retorno += " ${n[0].toUpperCase( ) + n[1..-1]}"
					break
			}
		}
		if ( retorno.size( ) > 1 ) {
			return "${retorno[0][0].toUpperCase( )}${retorno[1..-1]}".trim( )
		} else {
			return retorno.trim( )
		}
	}
	static String capitalize( String nome ) {
		if ( !nome || nome.size( ) == 0 ) return ''
		if ( nome.size( ) == 1 ) return nome.toUpperCase( )
		nome = nome.toLowerCase( )
		String retorno = ""
		String[] nomes = nome.split( ' ' )
		nomes.each { String n->
			if ( n.size( ) <= 1 ) {
				retorno += n.toUpperCase( )
				return
			}
			n = n.toLowerCase( )
			if ( Format.ROMANOS.contains( n ) ) {
				retorno += " ${n.toUpperCase( )}"
				return
			}
			switch ( n ) {
				case 'do':
				case 'de':
				case 'da':
				case 'dos':
				case 'das':
					retorno += " ${n}"
					break
				default:
					retorno += " ${n[0].toUpperCase( ) + n[1..-1]}"
					break
			}
		}
		return "${retorno[0][0].toUpperCase( )}${retorno[1..-1]}"?.trim( )
	}
	static String enderecoRenderizado( Map mapaRes ) {
		String captApto = null
		if ( mapaRes.unidade ) captApto = Format.capitalize( mapaRes.unidade.toString( ) )
		String captBlc = null
		if ( mapaRes.grupo ) captBlc = Format.capitalize( mapaRes.grupo.toString( ) )
		String ret = "${mapaRes.tipoLogradouro}${mapaRes.tipoLogradouro?'. ': ''}${mapaRes.logradouro}, ${mapaRes.numero}, ${mapaRes.apartamento? ( mapaRes.unidade? ( captApto + ': ' ): '' ) + mapaRes.apartamento: ''}${mapaRes.bloco?' - ': ''}${mapaRes.bloco? ( mapaRes.grupo? captBlc + ': ': '' ) + mapaRes.bloco: ''}${( mapaRes.apartamento || mapaRes.bloco )? ', ':''}${mapaRes.complemento? ' - ': ''}${mapaRes.complemento? mapaRes.complemento: ''}"
		return ret.trim( )
	}
	/*
	 * Pega o ultimo milissegundo de Hoje
	*/
	static Date ultimoMilissegundo(String data) {
		return ultimoMilissegundo( toDate( data ) )
	}
	static Date ultimoMilissegundo(Date data) {
		if (!data) return null
		Calendar c = Calendar.instance
		c.time = data
		c.set(Calendar.HOUR_OF_DAY, 23)
		c.set(Calendar.MINUTE, 59)
		c.set(Calendar.SECOND, 59)
		c.set(Calendar.MILLISECOND, 999)
		data = c.time
		return data
	}
	public static Date primeiroMilissegundo(String data) {
		return primeiroMilissegundo( toDate( data ) )
	}
	public static Date primeiroMilissegundo(Date data) {
		return clearTime( data )
	}
	public static Date clearTime(Date data) {
		if (!data) return null
		Calendar cal = Calendar.getInstance()
		cal.setTime( data )

		cal.set(Calendar.HOUR_OF_DAY, 0)
		cal.set(Calendar.MINUTE, 0)
		cal.set(Calendar.SECOND, 0)
		cal.set(Calendar.MILLISECOND, 0)

		return cal.getTime()
	}
	public static String limpaQuery( String query ) {
		return query?.replaceAll("[\\n\\r\\t]", ' ').replaceAll( /\s+/, ' ' )
	}
	public static String normalizaEndereco(String endereco) {
		final StringBuilder b = new StringBuilder()
		endereco?.toLowerCase()?.split(' ')?.each{String parte->
			if (EXCESSOES.contains(parte)) b.append(parte).append(' ')
			else {
				if (parte) {
					b.append( parte.substring(0, 1).toUpperCase() )
					if ( parte.size() > 1 ) {
						b.append( parte.substring(1))
					}
					b.append( ' ' )
				}
			}
		}
		return b.toString().trim()
	}
	static Boolean validaWhatsApp( String telefone ) {
		return telefone =~ /[0-9]{2}(9[0-9]{8}|[6-9][0-9]{7})/
	}
	public static String preparaSlug(Long referencia, String titulo) {
		preparaSlug( referencia.toString(), titulo)
	}
	public static String preparaSlug(Integer referencia, String titulo) {
		preparaSlug( referencia.toString(), titulo)
	}
	public static String preparaSlug(String referencia, String titulo) {
		titulo = titulo?.replaceAll(/[^A-Za-z0-9ÁÉÍÓÚÂÊÎÔÛÀÈÌÒÙÄËÏÖÜÃÕÇáéíóúâêîôûàèìòùäëïöüãõç]/,'_').replaceAll(/_{1,}/,'_')
		if ( titulo ) {
			List<String> myTit =  Arrays.asList( titulo.split('_') )
			if ( myTit && ( myTit[ -1 ] )?.equals( referencia ) ) {
				return titulo
			} else {
				return "${titulo}_${referencia}"
			}
		} else {
			return "_${referencia}"
		}
	}
	static Date parseData(String dataIn) {
		return FORMATO_DATA_HORA.parse(dataIn)
	}
	static Date parseData3(String data) {
		return FORMAT3.parse( data )
	}
	static Date parseData2(String data) {
		return FORMAT2.parse( data )
	}
	static String formataData1( Date data ) {
		return FORMAT.format( data )
	}
	static String formataData4( Date data ) {
		return FORMAT4.format( data )
	}
	static Date parseData1(String data) {
		return FORMAT.parse( data )
	}
	static Date parseDataSimples(String dataIn) {
		return FORMATO_DATA_SIMPLES.parse( dataIn )
	}
	static Date parseDataSimples1(String dataIn) {
		return SDF.parse(dataIn)
	}
	private final static Map extenso = [
			0l			 : ['Zero', 'Zero']
			, 1l			 : ['Um', 'Um']
			, 2l			 : ['Dois', 'Dois']
			, 3l			 : ['Três', 'Três']
			, 4l			 : ['Quatro', 'Quatro']
			, 5l			 : ['Cinco', 'Cinco']
			, 6l			 : ['Seis', 'Seis']
			, 7l			 : ['Sete', 'Sete']
			, 8l			 : ['Oito', 'Oito']
			, 9l			 : ['Nove', 'Nove']
			, 10l			: ['Dez', 'Dez']
			, 11l			: ['Onze', 'Onze']
			, 12l			: ['Doze', 'Doze']
			, 13l			: ['Treze', 'Treze']
			, 14l			: ['Quatorze', 'Quatorze']
			, 15l			: ['Quinze', 'Quinze']
			, 16l			: ['Dezesseis', 'Dezesseis']
			, 17l			: ['Dezessete', 'Dezessete']
			, 18l			: ['Dezoito', 'Dezoito']
			, 19l			: ['Dezenove', 'Dezenove']
			, 20l			: ['Vinte', 'Vinte']
			, 30l			: ['Trinta', 'Trinta']
			, 40l			: ['Quarenta', 'Quarenta']
			, 50l			: ['Cinquenta', 'Cinqüenta']
			, 60l			: ['Sessenta', 'Sessenta']
			, 70l			: ['Setenta', 'Setenta']
			, 80l			: ['Oitenta', 'Oitenta']
			, 90l			: ['Noventa', 'Noventa']
			, 100l		   : ['Cem', 'Cento']
			, 200l		   : ['Duzentos', 'Duzentos']
			, 300l		   : ['Trezentos', 'Trezentos']
			, 400l		   : ['Quatrocentos', 'Quatrocentos']
			, 500l		   : ['Quinhentos', 'Quinhentos']
			, 600l		   : ['Seiscentos', 'Seiscentos']
			, 700l		   : ['Setecentos', 'Setecentos']
			, 800l		   : ['Oitocentos', 'Oitocentos']
			, 900l		   : ['Novecentos', 'Novecentos']
			, 1000l		  : ['Mil', 'Mil']
			, 10000l		 : ['Milhão', 'Milhões']
			, 100000l		: ['Bilhão', 'Bilhões']
			, 1000000l	   : ['Trilhão', 'Trilhões']
			, 10000000l	  : ['Quatrilhão', 'Quatrilhões']
			, 100000000l	 : ['Quintilhão', 'Quintilhões']
			, 1000000000l	: ['Sextilhão', 'Sextilhões']
			, 10000000000l   : ['Setilhão', 'Setilhões']
			, 100000000000l  : ['Octilhão', 'Octilhões']
			, 1000000000000l : ['Nonilhão', 'Nonilhões']
			, 10000000000000l: ['Decilhão', 'Decilhões']
	]
	static String toExtenso(Double valor, ArrayList<String> moeda, ArrayList<String> fracionario) {
		String extenso = ""
		Long fracao = (long)(((valor % 1) + 0.0001) * 100)
		Double inteiro = ( valor - (fracao / 100) ).toDouble()
		extenso = criaExtenso(inteiro) + (moeda? ' ' + moeda[1].toLowerCase(): '')
		if (fracao > 0) {
			extenso = extenso + ' e ' + (criaExtenso((double)fracao)).toLowerCase() + (fracionario? ' ' + fracionario[1].toLowerCase(): '')
		}
		return extenso
	}
	static String toExtenso(Double valor) {
		return toExtenso(valor, [] as ArrayList<String>, [] as ArrayList<String>)
	}
	static String toExtensoReais(Double valor) {
		return toExtenso(valor,['Real', 'Reais'] as ArrayList<String>, ['Centavo', 'Centavos'] as ArrayList<String>)
	}
	static String criaExtenso(Double valor) {
		StringBuffer retorno = new StringBuffer()
		List<Long> parcelas = new ArrayList()
		int tam, ini, pot, fim
		decompoeDouble(valor, parcelas)
		tam = parcelas.size()
		pot = (int)(tam / 3)
		ini = (tam - 1)
		if (tam <= 3) {
			pot = 0
			fim = 0
		} else if ((tam % 3) > 0) {
			fim=tam-(tam % 3)
		} else {
			pot--
			fim = tam - 3
		}
		parceladora(parcelas, retorno, ini, fim, pot)
		String ret=retorno.toString()
		return ret
	}
	static void decompoeDouble(Double valor, List<Long> parcelas) {
		if (valor <= 9) parcelas.add( valor.longValue() )
		else {
			Long resto = (long)valor.doubleValue() % 10
			parcelas.add(resto)
			decompoeDouble((long)((valor - resto) / 10), parcelas)
		}
	}
	private static void decompoeLong(Long valor,List<Long> parcelas) {
		if (valor<=9) {
			parcelas.add((long)valor.longValue())
		} else {
			Long resto=(long)valor.doubleValue() % 10
			parcelas.add(resto)
			decompoeLong( (long)((valor - resto) / 10), parcelas)
		}
	}
	@CompileDynamic
	static void parceladora(List<Long> parcelas, StringBuffer retorno, int ini, int fim, int potencia) {
		int subPotencia = (ini - fim)
		long soma = 0
		boolean dezena = false
		boolean poePotencia = false
		int plural = 0
		for (int i = ini; i >= fim; i--) {
			if (dezena || ((subPotencia == 1) && (parcelas[i] == 1))) {
				dezena = (subPotencia != 0)
				soma += ( parcelas[i] * Math.pow(10, subPotencia--) ).toLong()
			} else {
				soma = parcelas[i] * (long)Math.pow(10, subPotencia--)
			}
			if (!dezena) {
				if (soma > 0) {
					poePotencia = true
					plural = 0
					if (soma > 1) plural = 1
					if (retorno.length() > 0)	{
						if (i == ini) retorno.append(', ')
						else retorno.append(' e ')
						String ext = extenso[soma][plural]
						retorno.append( ext.toLowerCase() )
					}
					else {
						retorno.append(extenso[soma][plural])
					}
					soma = 0
				}
			}
		}
		if (potencia>0)	{
			if (poePotencia) {
				soma = (long)Math.pow(10, (potencia) * 3)
				retorno.append(' ')
				retorno.append(extenso[soma][plural].toLowerCase())
			}
			parceladora(parcelas, retorno, fim - 1, fim - 3, potencia - 1)
		}
	}
	public static String textoDataNota(Date data) {

		Integer dia, mes, ano
		String diaExtenso, anoExtenso
		StringBuffer buffer = new StringBuffer()

		Locale brasil = new Locale("pt", "BR")
		Calendar diaAtual = Calendar.getInstance()
		diaAtual.setTime(data)
		diaAtual.set(Calendar.AM_PM, Calendar.PM)
		diaAtual.set(Calendar.HOUR, 00)
		diaAtual.set(Calendar.MINUTE, 01)
		DateFormatSymbols dfs = new DateFormatSymbols(brasil)

		dia = diaAtual.get(Calendar.DAY_OF_MONTH)
		mes = diaAtual.get(Calendar.MONTH)
		ano = diaAtual.get(Calendar.YEAR)

		String[] meses = dfs.getMonths()

		diaExtenso = toExtenso((double) dia)
		anoExtenso = toExtenso((double) ano)

		return buffer.append("Ao(s) ")
				.append(diaExtenso)
				.append(" dias do mês de ")
				.append(meses[mes])
				.append(" de ")
				.append(anoExtenso)
				.append(", pagarei(mos) por esta única via de \"NOTA PROMISSÓRIA\" à: ")
				.toString()
	}
	public static String toDataExtenso(Date data) {

		Integer dia, mes, ano
		String diaExtenso, anoExtenso
		StringBuffer buffer = new StringBuffer()

		Calendar diaAtual = Calendar.getInstance()
		diaAtual.setTime(data)
		diaAtual.set(Calendar.AM_PM, Calendar.PM)
		diaAtual.set(Calendar.HOUR, 00)
		diaAtual.set(Calendar.MINUTE, 01)
		DateFormatSymbols dfs = new DateFormatSymbols( LOCALE_BR )

		dia = diaAtual.get(Calendar.DAY_OF_MONTH)
		mes = diaAtual.get(Calendar.MONTH)
		ano = diaAtual.get(Calendar.YEAR)

		String[] meses = dfs.getMonths()

		diaExtenso = toExtenso((double) dia)
		anoExtenso = toExtenso((double) ano)

		return buffer
				.append(diaExtenso)
				.append(" dias do mês de ")
				.append( meses[mes] )
				.append(" de ")
				.append( anoExtenso )
				.toString()
	}
	static String emissao(Date data) {
		Integer dia, mes, ano
		String diaExtenso, anoExtenso
		StringBuffer buffer = new StringBuffer()

		Locale brasil = new Locale("pt", "BR")
		Calendar diaAtual = Calendar.getInstance()
		diaAtual.setTime(data)
		diaAtual.set(Calendar.AM_PM, Calendar.PM)
		diaAtual.set(Calendar.HOUR, 00)
		diaAtual.set(Calendar.MINUTE, 01)
		DateFormatSymbols dfs = new DateFormatSymbols(brasil)

		dia = diaAtual.get(Calendar.DAY_OF_MONTH)
		mes = diaAtual.get(Calendar.MONTH)
		ano = diaAtual.get(Calendar.YEAR)

		String[] meses = dfs.getMonths()

		diaExtenso = toExtenso((double) dia)
		anoExtenso = toExtenso((double) ano)

		return buffer.append(dia)
				.append(" de ")
				.append(meses[mes])
				.append(" de ")
				.append(ano)
				.toString()
	}
	//
	public static String toOrdinal(Long valor) {
		List<Long> parcelas = new ArrayList<Long>()
		StringBuffer ordinais = new StringBuffer()
		int tam, ini, fim
		double pot
		decompoeLong(valor, parcelas)
		tam = parcelas.size()
		pot = (tam / 3)
		ini = (tam - 1)
		if (tam <= 3) {
			pot = 0
			fim = 0
		} else if ((tam % 3) > 0) {
			fim = tam - (tam % 3)
		} else {
			pot--
			fim = (tam - 3)
		}
		parceladoraOrdinal(parcelas, ordinais, ini, fim, pot )
		String ret = ordinais.toString()
		return ret
	}
	private static void parceladoraOrdinal(List<Long> parcelas, StringBuffer ordinais, int ini, int fim, double potencia) {
		parceladoraOrdinal( parcelas, ordinais, ini, fim, ZValue.toInt(potencia) )
	}
	private static void parceladoraOrdinal(List<Long> parcelas, StringBuffer ordinais, int ini, int fim, int potencia) {
		Long y = 0
		int soma = 0
		int j = ini - fim
		for (int i = ini; i >= fim; i--) {
			y = (long)( parcelas.get( i ) * Math.pow( 10, j ) )
			soma += (int)y
			if (i == parcelas.size() - 1) {
				ordinais.append(ORDINAL.get(y.longValue()))
			} else {
				ordinais.append(ORDINAL.get(y.longValue()).toLowerCase())
			}
			if (soma>0 && i>0) {
				ordinais.append(" ")
			}
			j--
		}
		if (potencia > 0) {
			if (soma > 0) {
				y = Math.pow(10L, 3 * potencia).toLong()
				ordinais.append(ORDINAL.get(y.longValue()).toLowerCase()).append(", ")
			}
			parceladora(parcelas, ordinais, (int)(fim - 1), (int)(fim - 3), --potencia)
		}
	}

}
