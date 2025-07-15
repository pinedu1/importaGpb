package br.com.pinedu.importa.run

import java.util.UUID
import groovy.sql.Sql

class Teste1 {
	static main(args) {
		Sql db = Sql.newInstance('jdbc:postgresql://192.168.0.15:5432/focalimoveis', 'focalimoveis', 'pnxVW15', 'org.postgresql.Driver')
		db.execute('DELETE FROM zap_imovel; DELETE FROM viva_real_imovel; DELETE FROM viva_real_imovel; DELETE FROM imovel_web_imovel;')
		int i = -1
		db.eachRow("SELECT i.id as id, (SELECT COUNT(id) FROM imovel_foto AS if WHERE if.imovel_id = i.id) as qtdeFoto FROM imovel AS i WHERE i.status_imovel = 'D' ORDER BY qtdeFoto DESC;") { row ->
			String uuid = UUID.randomUUID().toString().replaceAll( /-/, '' )
			i = i+1
			db.executeInsert('INSERT INTO zap_imovel(id, imovel_id, ordem, tipo_destaque) VALUES (:id, :imovelId, :ordem, :tipoDestaque);'
				, [id: uuid, imovelId: row.id, ordem: i, tipoDestaque: 0])
			db.executeInsert('INSERT INTO viva_real_imovel(id, imovel_id, ordem, tipo_destaque) VALUES (:id, :imovelId, :ordem, :tipoDestaque);'
				, [id: uuid, imovelId: row.id, ordem: i, tipoDestaque: 0])
			db.executeInsert('INSERT INTO tique_imovel_imovel(id, imovel_id, ordem, tipo_destaque) VALUES (:id, :imovelId, :ordem, :tipoDestaque);'
				, [id: uuid, imovelId: row.id, ordem: i, tipoDestaque: 0])
			db.executeInsert('INSERT INTO imovel_web_imovel(id, imovel_id, ordem, tipo_destaque) VALUES (:id, :imovelId, :ordem, :tipoDestaque);'
				, [id: uuid, imovelId: row.id, ordem: i, tipoDestaque: 0])
		}
	}
}
