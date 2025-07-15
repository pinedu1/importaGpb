package br.com.pinedu.importa

import groovy.transform.CompileStatic

import java.security.MessageDigest
import java.text.Collator

/**
 *
 * @author eduardo
 */
@CompileStatic
class ZValue {
	static Integer toInt(final BigDecimal val) {
		if ( !val ) return 0
		if ( val == null ) return 0
		return val.toInteger()
	}
	static Integer toInt(final Integer val) {
		if ( !val ) return 0
		if ( val == null ) return 0
		return val
	}
	static Integer toInt(final Long val) {
		if ( !val ) return 0
		if ( val == null ) return 0
		return val.intValue()
	}
	static Integer toInt(final Double val) {
		if ( !val ) return 0
		if ( val == null ) return 0
		return val.intValue()
	}
	static Integer toInt(final String val) {
		if ( val == null ) return 0
		if ( !val ) return 0
		try {
			return val.toInteger()
		} catch(Exception ignore) {}
	}
	static Integer toInt(final Object val) {
		if ( !val ) return 0
		if ( val == null ) return 0
		return toInt( val.toString() )
	}
	static Long toLong(final Long val) {
		if ( !val ) return 0l
		if ( val == null ) return 0l
		return val
	}
	static Long toLong(final Integer val) {
		if ( !val ) return 0l
		if ( val == null ) return 0l
		return val.longValue()
	}
	static Long toLong(final Double val) {
		if ( !val ) return 0l
		if ( val == null ) return 0l
		return val.longValue()
	}
	static Long toLong(final String val) {
		if ( !val ) return 0l
		if ( val == null ) return 0l
		long ret = 0l
		try {
			ret = Long.parseLong(val)
		} catch(Exception ignore){}
		return ret
	}
	static Long toLong(final Object val) {
		if ( !val ) return 0l
		if ( val == null ) return 0l
		return toLong( val.toString() )
	}
	static Double toDouble(final Double val) {
		if ( val == null ) return 0
		return val
	}
	static Double toDouble(final Float val) {
		if ( val == null ) return 0
		return val.toDouble()
	}
	static Double toDouble(final Object val) {
		if ( val == null ) return 0
		return toDouble(val.toString())
	}
	static Double toDouble(final Long val) {
		if ( val == null ) return 0
		return toDouble(val.toString())
	}
	static Double toDouble(final Integer val) {
		if ( val == null ) return 0
		return toDouble(val.toString())
	}
	static Double toDouble(final String val) {
		if ( val == null ) return 0
		def ret = 0d
		if (!val) return ret
		try {
			ret = Double.parseDouble(val)
		} catch(Exception ignore){}
		return ret
	}
	static Float toFloat(final Float val) {
		if ( val == null ) return 0
		return val
	}
	static Float toFloat(final Double val) {
		if ( val == null ) return 0
		return toDouble(val.toString())
	}
	static Float toFloat(final Long val) {
		if ( val == null ) return 0
		return toDouble(val.toString())
	}
	static Float toFloat(final Integer val) {
		if ( val == null ) return 0
		return toDouble(val.toString())
	}
	static Float toFloat(final String val) {
		if ( val == null ) return 0
		def ret = 0d
		if (!val) return ret
		try {
			ret = Float.parseFloat(val)
		} catch(Exception ignore){}
		return ret
	}
	static Boolean toBoolean(final Object val) {
		if ( val == null ) return Boolean.FALSE
		return toBoolean( val as String )
	}
	static Boolean toBoolean(final Boolean val) {
		return val
	}
	static Boolean toBoolean(final String val) {
		if (val == null) return Boolean.FALSE
		if ( "".equals( val ) ) return Boolean.FALSE
		if ( val.isNumber() ) return ( toInt( val ) > 0)
		if ( "CHECKED".equalsIgnoreCase( val ) ) return Boolean.TRUE
		if ( "TRUE".equalsIgnoreCase( val ) ) return Boolean.TRUE
		if ( "ON".equalsIgnoreCase( val ) ) return Boolean.TRUE
		if ( "SIM".equalsIgnoreCase( val ) ) return Boolean.TRUE
		if ( "YES".equalsIgnoreCase( val ) ) return Boolean.TRUE
		return Boolean.FALSE
	}
	static Boolean toBoolean(final Integer val) {
		return ( val != 0 )
	}
	static Boolean toBoolean(final Long val) {
		return ( val != 0 )
	}
	static Boolean toBoolean(final Float val) {
		return ( val != 0 )
	}
	static Boolean toBoolean(final Double val) {
		return ( val != 0 )
	}
	static String join(final List<String> col, final String delim) {
		final StringBuilder sb = new StringBuilder();
		final Iterator<String> iter = col.iterator();
		if (iter.hasNext())
			sb.append(iter.next());
		while (iter.hasNext()) {
			sb.append(delim);
			sb.append(iter.next());
		}
		return sb.toString();
	}
	static List<Integer> selecaoRecibos(final String sel) {
		final Set<Integer> s = []
		if (sel) {
			final String selecao = sel.replaceAll( ',' , ';' )
			final List c = Arrays.asList( selecao.split( ';' ) )
			for(final String rec: c) {
				if ( rec.contains('-') ) {
					final String[] d = rec.split('-')
					if( ( d[0] && d[0].isNumber() ) && ( d[ 1 ] && d[ 1 ].isNumber() ) ) {
						int ini = toInt( d[ 0 ] )
						int fim = toInt( d[ 1 ] )
						if (ini > fim) {
							final int tr = ini
							ini = fim
							fim = tr
						}
						(ini..fim).each{final int r->
							s << r
						}
					}
				} else if ( rec && rec.isNumber() ) {
					s << toInt( rec )
				}
			}
		}
		return s.sort()
	}
	static String limpaCData(String inStr) {
		if (inStr.size() < 9 ) return inStr
		if ( inStr.substring(0,9) == '<![CDATA[' ) {
			inStr = inStr.substring(9, inStr.size() - 3)
		}
		return inStr
	}
	public static String criaSenha() {
		final List cars = ['ABCDEFGHIJKLMNOPQRSTUVXWYZ', 'abcdefghijklmnopqrstuvxwyz', '1234567890']
		List senha = []
		String c = null
		final java.util.Random r = new java.util.Random()
		for (int i = 0; i < 8; i++) {
			c = cars[ r.nextInt(3) ]
			senha << c[ r.nextInt( c.size() ) ]
		}
		return senha.join('')
	}
	public static int compareTo(String s1, String s2) {
	    Collator c = Collator.getInstance();
	    c.setStrength(Collator.PRIMARY);
	    return c.compare( s1?.toLowerCase().replaceAll(/[^\p{javaLowerCase}]+/, ' ').replaceAll(/\s+/, ' ').trim(), s2?.toLowerCase().replaceAll(/[^\p{javaLowerCase}]+/, ' ').replaceAll(/\s+/, ' ').trim() )
	}
	static BigDecimal toBigDecimal(final Double val) {
		return new BigDecimal( val )
	}
	static BigDecimal toBigDecimal(final Long val) {
		return new BigDecimal( val )
	}
	static BigDecimal toBigDecimal(final Integer val) {
		return new BigDecimal( val )
	}
	static BigDecimal toBigDecimal(final String val) {
		return toBigDecimal( toDouble( val ) )
	}
	public static String calculaMD5( String textIn ) {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(textIn.getBytes());
		byte[] bytes = md.digest();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		String generatedPassword = sb.toString();
		return generatedPassword
	}
}
