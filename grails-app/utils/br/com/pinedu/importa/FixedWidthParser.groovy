package br.com.pinedu.importa

import groovy.transform.CompileStatic

class FixedWidthParser {
	static Map<String, Object> parse(String line, Map<String, Integer> schema) {
		Map<String, Object> result = [:]
		Integer pos = 0
		schema.each { String fieldName, Integer width ->
			result[fieldName] = line.substring( pos, Math.min(pos + width, line.length() ) )?.trim()
			pos += width
		}
		return result
	}
}
