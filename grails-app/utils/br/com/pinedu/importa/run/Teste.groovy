package br.com.pinedu.importa.run

import groovy.sql.Sql

class Teste {
	private static Long GRUPO_ADMINISTRADORES = 1l;
	private static Long GRUPO_SUPERUSUARIOS = 2l;
	private static Long GRUPO_USUARIOS = 3l;
	private static Long GRUPO_SECRETARIADO = 4l;
	private static Long GRUPO_GERCONF = 9l;
	private static Long GRUPO_CORRETORES = 5l;
	private static Long GRUPO_GERENTES = 6l;
	
	static main(args) {
		Sql db = Sql.newInstance('jdbc:postgresql://192.168.0.15:5432/focalimoveis', 'focalimoveis', 'pnxVW15', 'org.postgresql.Driver')
		Long id
		Long idMenu
		Long idSubMenu
		Long idSubSubMenu
		List<Long> ids = []
		StringBuilder sb = new StringBuilder()
		
		db.execute( "SELECT setval('seq_menu', 1, false);" )
		db.execute( "DELETE FROM usuario_atalho;" )
		db.execute( "DELETE FROM grupo_menu WHERE menu_id > 0;" )
		db.execute( "DELETE FROM menu_regra WHERE menu_id > 0;" )
		db.execute( "DELETE FROM menu WHERE id > 0;" )
		/*
		 * Empresa
		 */
		idMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Empresa', version: 0, action: null, aplicacao_id: null, dica: 'Empresa, Lojas e cadastros pertinentes a organização', icone: 'empresa', mneumonic: 'z', pai_id: null, posicao: 1010, sistema: true, tipo: 'MENU'])[0][0]
		ids << idMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Cadastro da Empresa', version: 0, action: 'Empresas', aplicacao_id: null, dica: 'Define a qualificação da Empresa', icone: 'empresa', mneumonic: 'm', pai_id: idMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_EMPRESA%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Lojas', version: 0, action: 'Lojas', aplicacao_id: null, dica: 'Lojas e Filiais', icone: 'loja', mneumonic: 'j', pai_id: idMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_LOJA%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Parametros e configurações', version: 0, action: 'ParametroSistemas', aplicacao_id: null, dica: 'Parametros e configurações do sistema', icone: 'confParametro', mneumonic: 'p', pai_id: idMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_PARAMETROSISTEMA%\';', [id: id] )
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Papéis de Parede', version: 0, action: 'PapelParedes', aplicacao_id: null, dica: 'Cadastro de imagens para papel de parede', icone: 'papelParede', mneumonic: 'p', pai_id: idMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		sb.setLength(0)
		sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
		ids.eachWithIndex{Long zid, int i->
			if ( i > 0 ) sb.append(', ')
			sb.append( "($GRUPO_GERCONF, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
		}
		sb.append( ') AS Q (grupo_id, menu_id);' )
		db.executeInsert( sb.toString() )
		/*
		 * Utilitarios
		 */
		ids = []
		idMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Utilitários', version: 0, action: null, aplicacao_id: null, dica: 'Ferramentas e Utilitários do sistema', icone: 'utilitarios', mneumonic: 'U', pai_id: null, posicao: 1060, sistema: true, tipo: 'MENU'])[0][0]
		ids << idMenu
			
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Mensagens', version: 0, action: 'Mensagens', aplicacao_id: null, dica: 'Sistema Mensagens para o Usuário', icone: 'mensagem', mneumonic: 'm', pai_id: idMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
			
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Mensagem Direta', version: 0, action: 'MensagemDiretas', aplicacao_id: null, dica: 'Enviar mensagem direta para usuário ativo', icone: 'mensagemDireta', mneumonic: 'm', pai_id: idMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Agenda', version: 0, action: 'Agendas', aplicacao_id: null, dica: 'Agenda de tarefas pessoal e Corporativa', icone: 'agenda', mneumonic: 'a', pai_id: idMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		
		sb.setLength(0)
		sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
		ids.eachWithIndex{Long zid, int i->
			if ( i > 0 ) sb.append(', ')
			sb.append( "($GRUPO_USUARIOS, $zid)" )
		}
		sb.append( ') AS Q (grupo_id, menu_id);' )
		db.executeInsert( sb.toString() )
		/*
		 * Preferencias
		 */
		ids = []
		idMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Preferências', version: 0, action: null, aplicacao_id: null, dica: 'Preferências do usuário', icone: 'preferencias', mneumonic: 'p', pai_id: null, posicao: 1070, sistema: true, tipo: 'MENU'])[0][0]
		ids << idMenu
		id << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Alterar senha', version: 0, action: 'AlterarSenha', aplicacao_id: null, dica: 'Alterar minha senha', icone: 'changePassword', mneumonic: 's', pai_id: idMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Alterar Tema', version: 0, action: 'AlterarTema', aplicacao_id: null, dica: 'Alterar tema de exibição', icone: 'changeTheme', mneumonic: 't', pai_id: idMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: '-', version: 0, action: null, aplicacao_id: null, dica: null, icone: null, mneumonic: null, pai_id: idMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Papel de Parede', version: 0, action: 'PapelParedes', aplicacao_id: null, dica: 'Definir Papel de Parede', icone: 'papelParede', mneumonic: 'p', pai_id: idMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		
		sb.setLength(0)
		sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
		ids.eachWithIndex{Long zid, int i->
			if ( i > 0 ) sb.append(', ')
			sb.append( "($GRUPO_USUARIOS, $zid)" )
		}
		sb.append( ') AS Q (grupo_id, menu_id);' )
		db.executeInsert( sb.toString() )
		/*
		 * Segurancao
		*/
		ids = []
		idMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Segurança', version: 0, action: null, aplicacao_id: null, dica: 'Controle de Acesso ao Sistema', icone: 'controleAcessos', mneumonic: 'A', pai_id: null, posicao: 1090, sistema: true, tipo: 'MENU'])[0][0]
		ids << idMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Cadastro de Usuários', version: 0, action: 'Users', aplicacao_id: null, dica: 'Cadastro de Usuários para Permissões no Sistema', icone: 'user', mneumonic: 'u', pai_id: idMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_USER%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Cadastro de Grupos', version: 0, action: 'Grupos', aplicacao_id: null, dica: 'Cadastro de Grupos para Permissões no Sistema', icone: 'grupo', mneumonic: 'r', pai_id: idMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_GRUPO%\';', [id: id] )

		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Cadastro de Regras', version: 0, action: 'Roles', aplicacao_id: null, dica: 'Cadastro de Regras para Permissões no Sistema', icone: 'role', mneumonic: 'r', pai_id: idMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_ROLE%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Usuários ativos', version: 0, action: 'UsuariosAtivos', aplicacao_id: null, dica: 'Usuários ativos no Sistema', icone: 'user', mneumonic: 'r', pai_id: idMenu, posicao: 1050, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
			
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Cadastro de Menus', version: 0, action: 'Menus', aplicacao_id: null, dica: 'Cadastro de Menus', icone: 'menu', mneumonic: 'u', pai_id: idMenu, posicao: 1070, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_MENU%\';', [id: id] )
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica, alinhamento) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica, :alinhamento);'
			, [nome: 'Logout', version: 0, action: 'Logout', aplicacao_id: null, dica: 'Sair do Sistema', icone: 'logout', mneumonic: 'u', pai_id: null, posicao: 1100, sistema: true, tipo: 'ITEM', alinhamento: 'R'])[0][0]
		
		sb.setLength(0)
		sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
		ids.eachWithIndex{Long zid, int i->
			if ( i > 0 ) sb.append(', ')
			sb.append( "($GRUPO_GERCONF, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
		}
		sb.append( ') AS Q (grupo_id, menu_id);' )
		db.executeInsert( sb.toString() )
		/*
		 * 
		 * INTERESSADOS
		 * 
		 */
		ids = []
		idMenu = db.executeInsert("INSERT INTO menu( id, version, action, aplicacao_id, dica, icone, mneumonic, nome, pai_id, posicao, sistema, tipo) VALUES (nextval(\'seq_menu\'), :version, :action, :aplicacao_id, :dica, :icone, :mneumonic, :nome, :pai_id, :posicao, :sistema, :tipo);"
			, [version: 0, action: null, aplicacao_id: 3, dica: 'Rotinas de suporte à Interessados', icone: 'interessado', mneumonic: 'T', nome: 'Interessados', pai_id: null, posicao: 1010, sistema: true, tipo: 'MENU'])[0][0]
		ids << idMenu
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Cadastro de Interessados', version: 0, action: 'CadastroInteressadosCorretores', aplicacao_id: 3, dica: 'Atendimento à Interessados', icone: 'interessado', mneumonic: 'i', pai_id: idMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_INTERESSADO\\_%\';', [id: id] )
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Listagem de Interessados', version: 0, action: 'RelatorioInteressadoListas', aplicacao_id: 3, dica: 'Lista interessados ativos do Corretor', icone: 'interessadoRel', mneumonic: 'i', pai_id: idMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Primeiro Atendimento', version: 0, action: 'RelatorioInteressadoAtendimentos', aplicacao_id: 3, dica: 'Lista interessados pelo primeiro atendimento', icone: 'interessadoRel', mneumonic: 'a', pai_id: idMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Último Contato', version: 0, action: 'RelatorioInteressadoContatos', aplicacao_id: 3, dica: 'Lista interessados pelo último contato', icone: 'interessadoRel', mneumonic: 'u', pai_id: idMenu, posicao: 1050, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Interesses não Definidos', version: 0, action: 'RelatorioInteressadoIncompletos', aplicacao_id: 3, dica: 'Lista interessados com interesses não Definidos', icone: 'interessadoRel', mneumonic: 'd', pai_id: idMenu, posicao: 1060, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Expiração de Interessados', version: 0, action: 'RelatorioInteressadoExpiracaos', aplicacao_id: 3, dica: 'Lista interessados à expirar', icone: 'interessadoRel', mneumonic: 'p', pai_id: idMenu, posicao: 1070, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Novas Entradas de Imóveis', version: 0, action: 'RelatorioInteressadoNovasEntradas', aplicacao_id: 3, dica: 'Novos Imóveis cadastrados que atendem as exigências de seus interessados', icone: 'interessadoRel', mneumonic: 's', pai_id: idMenu, posicao: 1080, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Interessados Excluídos', version: 0, action: 'RelatorioInteressadoInativo', aplicacao_id: 3, dica: 'Lista interessados excluídos', icone: 'interessadoRel', mneumonic: 's', pai_id: idMenu, posicao: 1090, sistema: true, tipo: 'ITEM'])[0][0]
		
		sb.setLength(0)
		sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
		ids.eachWithIndex{Long zid, int i->
			if ( i > 0 ) sb.append(', ')
			sb.append( "($GRUPO_CORRETORES, $zid), ($GRUPO_GERENTES, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
		}
		sb.append( ') AS Q (grupo_id, menu_id);' )
		db.executeInsert( sb.toString() )
		
		ids = []
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idMenu, posicao: 1100, sistema: true, tipo: 'ITEM'])[0][0]
			
		idSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Gerência de Interessados', version: 0, action: null, aplicacao_id: 3, dica: 'Gerência de Interessados', icone: 'interessado', mneumonic: 'n', pai_id: idMenu, posicao: 1110, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubMenu
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Promover interessados expirados', version: 0, action: 'InteressadoExpiradoPromover', aplicacao_id: 3, dica: 'Promover interessados expirador para segundo atendimento', icone: 'interessadoCor', mneumonic: 'm', pai_id: idSubMenu, posicao: 1120, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Acompanhamento de Excluídos', version: 0, action: 'RelatorioInteressadoExcluido', aplicacao_id: 3, dica: 'Acompanhamento interessados excluídos', icone: 'interessadoExcluido', mneumonic: 's', pai_id: idSubMenu, posicao: 1130, sistema: true, tipo: 'ITEM'])[0][0]
			
		sb.setLength(0)
		sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
		ids.eachWithIndex{Long zid, int i->
			if ( i > 0 ) sb.append(', ')
			sb.append( "($GRUPO_GERENTES, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
		}
		sb.append( ') AS Q (grupo_id, menu_id);' )
		db.executeInsert( sb.toString() )

		ids = []
		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Entradas de Interessados', version: 0, action: null, aplicacao_id: 3, dica: 'Entradas de Interessados', icone: 'interessado', mneumonic: 'I', pai_id: idMenu, posicao: 1140, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Interessados via Site', version: 0, action: 'PreInteressados', aplicacao_id: 3, dica: 'Entradas de Interessados via Site aguardando promoção', icone: 'interessadoSite', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_PREINTERESSADO%\';', [id: id] )
		sb.setLength(0)
		sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
		ids.eachWithIndex{Long zid, int i->
			if ( i > 0 ) sb.append(', ')
			sb.append( "($GRUPO_SECRETARIADO, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
		}
		sb.append( ') AS Q (grupo_id, menu_id);' )
		db.executeInsert( sb.toString() )
		ids = []
		/*
		 *
		 * IMOVEIS
		 *
		 */
		idMenu = db.executeInsert("INSERT INTO menu( id, version, action, aplicacao_id, dica, icone, mneumonic, nome, pai_id, posicao, sistema, tipo) VALUES (nextval(\'seq_menu\'), :version, :action, :aplicacao_id, :dica, :icone, :mneumonic, :nome, :pai_id, :posicao, :sistema, :tipo);"
			, [version: 0, action: null, aplicacao_id: 3, dica: 'Rotinas de suporte à Imóveis, Condomínios e Edifícios', icone: 'imovel', mneumonic: 'T', nome: 'Imóveis Condomínios e Edifícios', pai_id: null, posicao: 20, sistema: true, tipo: 'MENU'])[0][0]
		ids << idMenu
		// IMOVEIS
		idSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Controle de Imóveis', version: 0, action: null, aplicacao_id: 3, dica: 'Controle de Imóveis', icone: 'imovel', mneumonic: 'I', pai_id: idMenu, posicao: 1010, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubMenu
		sb.setLength(0)
		sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
		ids.eachWithIndex{Long zid, int i->
			if ( i > 0 ) sb.append(', ')
			sb.append( "($GRUPO_SECRETARIADO, $zid), ($GRUPO_GERENTES, $zid), ($GRUPO_CORRETORES, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
		}
		sb.append( ') AS Q (grupo_id, menu_id);' )
		db.executeInsert( sb.toString() )
		
			ids = []
			id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Cadastros de Imóveis', version: 0, action: 'Imoveis', aplicacao_id: 3, dica: 'Cadastros de Imóveis', icone: 'imovel', mneumonic: 'l', pai_id: idSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
			ids << id
			db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_IMOVEL\\_%\';', [id: id] )
		
			sb.setLength(0)
			sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
			ids.eachWithIndex{Long zid, int i->
				if ( i > 0 ) sb.append(', ')
				sb.append( "($GRUPO_SECRETARIADO, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
			}
			sb.append( ') AS Q (grupo_id, menu_id);' )
			db.executeInsert( sb.toString() )
		
			ids = []
			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Consulta Imóvel', version: 0, action: 'CorretorConsultaImoveis', aplicacao_id: 3, dica: 'Consulta Imóvel por Referência', icone: 'imovel', mneumonic: 'c', pai_id: idSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		
			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Pesquisa Imóveis por Endereço', version: 0, action: 'PesquisaImovelEndereco', aplicacao_id: 3, dica: 'Pesquisa Imóveis por Endereço, Edifício ou Condomínio', icone: 'pesquisaEndereco', mneumonic: 'q', pai_id: idSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		
			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]

			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Ficha do Imóvel', version: 0, action: 'RelatorioFichaImovel', aplicacao_id: 3, dica: 'Ficha do Imóvel', icone: 'imovelRel', mneumonic: 'o', pai_id: idSubMenu, posicao: 1050, sistema: true, tipo: 'ITEM'])[0][0]
		
			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Listagem de Imóveis', version: 0, action: 'RelatorioImovelLista', aplicacao_id: 3, dica: 'Listagem Geral de Imóveis', icone: 'imovelRel', mneumonic: 's', pai_id: idSubMenu, posicao: 1060, sistema: true, tipo: 'ITEM'])[0][0]
		
			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Atualizar Preços', version: 0, action: 'RelatorioImovelAtualizacao', aplicacao_id: 3, dica: 'Listagem de Imóveis com data de Próxima atualização de preço nos próximos X dias', icone: 'imovelRel', mneumonic: 'a', pai_id: idSubMenu, posicao: 1070, sistema: true, tipo: 'ITEM'])[0][0]
		
			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Imóveis sem anúncio no período', version: 0, action: 'RelatorioImovelSemAnuncio', aplicacao_id: 3, dica: 'Listagem de Imóveis sem anúncio no período', icone: 'imovelRel', mneumonic: 'x', pai_id: idSubMenu, posicao: 1080, sistema: true, tipo: 'ITEM'])[0][0]
		
			sb.setLength(0)
			sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
			ids.eachWithIndex{Long zid, int i->
				if ( i > 0 ) sb.append(', ')
				sb.append( "($GRUPO_CORRETORES, $zid), ($GRUPO_SECRETARIADO, $zid), ($GRUPO_GERENTES, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
			}
			sb.append( ') AS Q (grupo_id, menu_id);' )
			db.executeInsert( sb.toString() )
			
			ids = []
			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubMenu, posicao: 1090, sistema: true, tipo: 'ITEM'])[0][0]
			idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Captação de Imóveis', version: 0, action: null, aplicacao_id: 3, dica: 'Captações de Imóveis', icone: 'corretor', mneumonic: 'p', pai_id: idSubMenu, posicao: 1100, sistema: true, tipo: 'MENU'])[0][0]
			ids << idSubSubMenu
		
				ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: 'Transferir Captações', version: 0, action: 'TransferirCaptacoes', aplicacao_id: 3, dica: 'Transferir Captações de Imóveis', icone: 'transferir', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		
				ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		
				idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: 'Estatísticas de Captação', version: 0, action: null, aplicacao_id: 3, dica: 'Estatísticas de Captação de Imóveis', icone: 'estatisticaCaptacao', mneumonic: 'c', pai_id: idSubSubMenu, posicao: 1030, sistema: true, tipo: 'MENU'])[0][0]
				ids << idSubSubMenu
		
					ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
						, [nome: '...Por Corretor', version: 0, action: 'EstatisticaCaptacoes', aplicacao_id: 3, dica: 'Estatísticas de Captações de Imóveis por Corretor', icone: 'estatisticaCaptacao', mneumonic: 'c', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		
				sb.setLength(0)
				sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
				ids.eachWithIndex{Long zid, int i->
					if ( i > 0 ) sb.append(', ')
					sb.append( "($GRUPO_SECRETARIADO, $zid), ($GRUPO_GERENTES, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
				}
				sb.append( ') AS Q (grupo_id, menu_id);' )
				db.executeInsert( sb.toString() )
		
				ids = []
		
			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubMenu, posicao: 1110, sistema: true, tipo: 'ITEM'])[0][0]
		
			id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Cadastro de Fotos em Imóveis', version: 0, action: 'ImovelFotos', aplicacao_id: 3, dica: 'Cadastro de Fotografias em Imóveis', icone: 'fotoImovel', mneumonic: 'f', pai_id: idSubMenu, posicao: 1120, sistema: true, tipo: 'ITEM'])[0][0]
			ids << id
			db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_IMOVELFOTO%\';', [id: id] )
		
			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Imóveis sem Fotos', version: 0, action: 'RelatorioImovelSemFotos', aplicacao_id: 3, dica: 'Imóveis sem Fotos', icone: 'fotoImovelRel', mneumonic: 'x', pai_id: idSubMenu, posicao: 1130, sistema: true, tipo: 'ITEM'])[0][0]
				
			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubMenu, posicao: 1140, sistema: true, tipo: 'ITEM'])[0][0]
		
			idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Imóveis Excluídos', version: 0, action: null, aplicacao_id: 3, dica: 'Controle de Imóveis Excluídos', icone: 'imovelExcluido', mneumonic: 'x', pai_id: idSubMenu, posicao: 1150, sistema: true, tipo: 'MENU'])[0][0]
			ids << idSubSubMenu
		
				id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: 'Imóveis Excluídos', version: 0, action: 'ImoveisExcluidos', aplicacao_id: 3, dica: 'Cadastro de Imóveis Excluídos', icone: 'imovelExcluido', mneumonic: 'i', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
				ids << id
				db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_IMOVEL_MORTO%\' OR authority ILIKE \'ROLE_IMOVEL_TEMPORARIO%\';', [id: id] )
		
				ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		
				ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: 'Listagem de Imóveis Excluídos', version: 0, action: 'RelatorioImovelListaExcluidos', aplicacao_id: 3, dica: 'Listagem Geral de Imóveis Excluídos', icone: 'imovelExcluidoRel', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		
				ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: 'Pesquisa Imóveis Excluídos por Endereço', version: 0, action: 'PesquisaImovelEnderecoExcluidos', aplicacao_id: 3, dica: 'Pesquisa Imóveis Excluídos por Endereço', icone: 'pesquisaEnderecoExcluido', mneumonic: 'q', pai_id: idSubSubMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		
			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubMenu, posicao: 1160, sistema: true, tipo: 'ITEM'])[0][0]
			idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Entrada de Pré Imóveis', version: 0, action: '', aplicacao_id: 3, dica: 'Entrada de Pré Imóveis', icone: 'imovelInternet', mneumonic: 's', pai_id: idSubMenu, posicao: 1170, sistema: true, tipo: 'MENU'])[0][0]
			ids << idSubSubMenu
		
				ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: 'Pré Imóveis à Verificar', version: 0, action: 'ImoveisInternet', aplicacao_id: 3, dica: 'Pré Imóveis à Verificar', icone: 'imovelInternet', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
				db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_IMOVEL_PREIMOVEL%\';', [id: id] )
		
			idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Fichas Compradas', version: 0, action: '', aplicacao_id: 3, dica: 'Fichas Compradas', icone: 'fichaComprada', mneumonic: 's', pai_id: idSubMenu, posicao: 1180, sistema: true, tipo: 'MENU'])[0][0]
			ids << idSubSubMenu
		
				ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: 'Importar Fichas Compradas', version: 0, action: 'FichasCompradas', aplicacao_id: 3, dica: 'Fichas Compradas (Central de Cadastro Imobiliário)', icone: 'fichaCompradaCCI', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
				db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_FICHACOMPRADA%\';', [id: id] )
		
				
			sb.setLength(0)
			sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
			ids.eachWithIndex{Long zid, int i->
				if ( i > 0 ) sb.append(', ')
				sb.append( "($GRUPO_SECRETARIADO, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
			}
			sb.append( ') AS Q (grupo_id, menu_id);' )
			db.executeInsert( sb.toString() )
		
			//Edificios
			ids = []
			idSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Controle de Edifícios', version: 0, action: null, aplicacao_id: 3, dica: 'Controle de Edifícios', icone: 'edificio', mneumonic: 'E', pai_id: idMenu, posicao: 1020, sistema: true, tipo: 'MENU'])[0][0]
			ids << idSubMenu
				sb.setLength(0)
				sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
				ids.eachWithIndex{Long zid, int i->
					if ( i > 0 ) sb.append(', ')
					sb.append( "($GRUPO_SECRETARIADO, $zid), ($GRUPO_GERENTES, $zid), ($GRUPO_CORRETORES, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
				}
				sb.append( ') AS Q (grupo_id, menu_id);' )
				db.executeInsert( sb.toString() )
		
				ids = []
		
				id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: 'Cadastros de Edifícios', version: 0, action: 'Edificios', aplicacao_id: 3, dica: 'Cadastros de Edifícios/Loteamentos', icone: 'edificio', mneumonic: 'f', pai_id: idSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
				ids << id
				db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_EDIFICIO%\';', [id: id] )
		
				sb.setLength(0)
				sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
				ids.eachWithIndex{Long zid, int i->
					if ( i > 0 ) sb.append(', ')
					sb.append( "($GRUPO_SECRETARIADO, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
				}
				sb.append( ') AS Q (grupo_id, menu_id);' )
				db.executeInsert( sb.toString() )
		
				ids = []
		
				id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: 'Consulta Edifícios/Loteamentos', version: 0, action: 'CorretorConsultaEdificios', aplicacao_id: 3, dica: 'Consulta Edifícios', icone: 'edificio', mneumonic: 's', pai_id: idSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
				ids << id
		
				id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
				ids << id
		
				ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: 'Ficha do Edifício', version: 0, action: 'RelatorioFichaEdificio', aplicacao_id: 3, dica: 'Ficha do Edifício', icone: 'edificioRel', mneumonic: 'f', pai_id: idSubMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		
				id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: 'Listagem de Edifícios', version: 0, action: 'RelatorioEdificioLista', aplicacao_id: 3, dica: 'Listagem de Edifícios ou Loteamentos', icone: 'edificioRel', mneumonic: 'e', pai_id: idSubMenu, posicao: 1050, sistema: true, tipo: 'ITEM'])[0][0]
				ids << id
		
				sb.setLength(0)
				sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
				ids.eachWithIndex{Long zid, int i->
					if ( i > 0 ) sb.append(', ')
					sb.append( "($GRUPO_SECRETARIADO, $zid), ($GRUPO_GERENTES, $zid), ($GRUPO_CORRETORES, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
				}
				sb.append( ') AS Q (grupo_id, menu_id);' )
				db.executeInsert( sb.toString() )
		
				ids = []
		
				id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubMenu, posicao: 1060, sistema: true, tipo: 'ITEM'])[0][0]
				ids << id
		
				id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: 'Cadastro de Fotos em Edifícios', version: 0, action: 'EdificioFotos', aplicacao_id: 3, dica: 'Cadastro de Fotografias em Edifícios', icone: 'fotoImovel', mneumonic: 'f', pai_id: idSubMenu, posicao: 1070, sistema: true, tipo: 'ITEM'])[0][0]
				ids << id
				db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_EDIFICIOFOTO%\';', [id: id] )
		
				id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
					, [nome: 'Edifícios sem Fotos', version: 0, action: 'RelatorioEdificioSemFotos', aplicacao_id: 3, dica: 'Edifícios sem Fotos', icone: 'fotoImovelRel', mneumonic: 'f', pai_id: idSubMenu, posicao: 1080, sistema: true, tipo: 'ITEM'])[0][0]
				ids << id
		
		
				sb.setLength(0)
				sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
				ids.eachWithIndex{Long zid, int i->
					if ( i > 0 ) sb.append(', ')
					sb.append( "($GRUPO_SECRETARIADO, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
				}
				sb.append( ') AS Q (grupo_id, menu_id);' )
				db.executeInsert( sb.toString() )
		
			//Condominios
			ids = []
			idSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Controle de Condomínios', version: 0, action: null, aplicacao_id: 3, dica: 'Controle de Condomínios/Empreendimentos', icone: 'condominio', mneumonic: 'C', pai_id: idMenu, posicao: 1030, sistema: true, tipo: 'MENU'])[0][0]
			ids << idSubMenu
				
			sb.setLength(0)
			sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
			ids.eachWithIndex{Long zid, int i->
				if ( i > 0 ) sb.append(', ')
				sb.append( "($GRUPO_SECRETARIADO, $zid), ($GRUPO_GERENTES, $zid), ($GRUPO_CORRETORES, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
			}
			sb.append( ') AS Q (grupo_id, menu_id);' )
			db.executeInsert( sb.toString() )
		
			ids = []
		
			id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Cadastros de Condomínios/Empreendimentos', version: 0, action: 'Condominios', aplicacao_id: 3, dica: 'Cadastros de Condomínios/Empreendimentos', icone: 'condominio', mneumonic: 'c', pai_id: idSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
			ids << id
		
			db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_CONDOMINIO%\';', [id: id] )
			sb.setLength(0)
			sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
			ids.eachWithIndex{Long zid, int i->
				if ( i > 0 ) sb.append(', ')
				sb.append( "($GRUPO_SECRETARIADO, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
			}
			sb.append( ') AS Q (grupo_id, menu_id);' )
			db.executeInsert( sb.toString() )
		
			ids = []
		
			id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Consulta Condomínios/Empreendimentos', version: 0, action: 'CorretorConsultaCondominios', aplicacao_id: 3, dica: 'Consulta Condomínios', icone: 'condominio', mneumonic: 'a', pai_id: idSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
			ids << id
		
			id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
			ids << id
		
			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Ficha do Condomínio', version: 0, action: 'RelatorioFichaCondominio', aplicacao_id: 3, dica: 'Ficha do Condomínio', icone: 'condominioRel', mneumonic: 'f', pai_id: idSubMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		
			id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Listagem de Condomínios', version: 0, action: 'RelatorioCondominioLista', aplicacao_id: 3, dica: 'Listagem de Condomínios ou Empreendimentos', icone: 'condominioRel', mneumonic: 'c', pai_id: idSubMenu, posicao: 1050, sistema: true, tipo: 'ITEM'])[0][0]
			ids << id
		
			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubMenu, posicao: 1060, sistema: true, tipo: 'ITEM'])[0][0]
		
			ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Cadastro de Fotos em Condomínios', version: 0, action: 'CondominioFotos', aplicacao_id: 3, dica: 'Cadastro de Fotografias em Condomínios', icone: 'fotoImovel', mneumonic: 'j', pai_id: idSubMenu, posicao: 1070, sistema: true, tipo: 'ITEM'])[0][0]
			db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_CONDOMINIOFOTO%\';', [id: id] )
		
			id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
				, [nome: 'Condomínios sem Fotos', version: 0, action: 'RelatorioCondominioSemFotos', aplicacao_id: 3, dica: 'Condomínios sem Fotos', icone: 'fotoImovelRel', mneumonic: 's', pai_id: idSubMenu, posicao: 1080, sistema: true, tipo: 'ITEM'])[0][0]
			ids << id
		
			sb.setLength(0)
			sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
			ids.eachWithIndex{Long zid, int i->
				if ( i > 0 ) sb.append(', ')
				sb.append( "($GRUPO_SECRETARIADO, $zid), ($GRUPO_GERENTES, $zid), ($GRUPO_CORRETORES, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
			}
			sb.append( ') AS Q (grupo_id, menu_id);' )
			db.executeInsert( sb.toString() )
		/*
		 * Anuncio
		 * */
		ids = []
		idMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Anúncios', version: 0, action: null, aplicacao_id: 3, dica: 'Anúncio/Promoção de Imóveis', icone: 'anuncio', mneumonic: 'n', pai_id: null, posicao: 1040, sistema: true, tipo: 'MENU'])[0][0]
		ids << idMenu
		
		idSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Controle de Anúncios', version: 0, action: null, aplicacao_id: 3, dica: 'Controle de Anúncios em Imóveis', icone: 'anuncios', mneumonic: 'P', pai_id: idMenu, posicao: 1070, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Anunciar Imóvel', version: 0, action: 'Anuncios', aplicacao_id: 3, dica: 'Anunciar Imóveis', icone: 'anuncio', mneumonic: 'n', pai_id: idSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_ANUNCIO%\';', [id: id] )
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Listagem de Anúncios por Período', version: 0, action: 'RelatorioAnuncioLista', aplicacao_id: 3, dica: 'Listagem de Anúncios por Período', icone: 'anuncioRel', mneumonic: 'a', pai_id: idSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Anúncios por data de Criação', version: 0, action: 'RelatorioAnuncioCadastroLista', aplicacao_id: 3, dica: 'Anúncios por data de Criação', icone: 'anuncioRel', mneumonic: 'ç', pai_id: idSubMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: '-', version: 0, action: null, aplicacao_id: null, dica: null, icone: null, mneumonic: null, pai_id: idSubMenu, posicao: 1050, sistema: true, tipo: 'ITEM'])[0][0]
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Campanhas sem Anúncios no Período', version: 0, action: 'RelatorioCampanhaSemAnuncio', aplicacao_id: 3, dica: 'Listagem de Campanhas sem Anúncios no Período', icone: 'anuncioRel', mneumonic: 'c', pai_id: idSubMenu, posicao: 1060, sistema: true, tipo: 'ITEM'])[0][0]
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Imóveis anunciados do período', version: 0, action: 'RelatorioImovelComAnuncio', aplicacao_id: 3, dica: 'Listagem de Imóveis anunciados do período', icone: 'anuncioRel', mneumonic: 'i', pai_id: idSubMenu, posicao: 1070, sistema: true, tipo: 'ITEM'])[0][0]
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Retorno de Interessados por Campanha', version: 0, action: 'RelatorioRetornoInteressadoCampanha', aplicacao_id: 3, dica: 'Retorno de Interessados por Campanha', icone: 'anuncioRel', mneumonic: 'r', pai_id: idSubMenu, posicao: 1080, sistema: true, tipo: 'ITEM'])[0][0]
		//
		idSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Controle de Placas', version: 0, action: null, aplicacao_id: 3, dica: 'Controle de Placas em Imóveis', icone: 'placaImovel', mneumonic: 'P', pai_id: idMenu, posicao: 1060, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Placas em Imóveis', version: 0, action: 'ImovelPlacas', aplicacao_id: 3, dica: 'Colocar, retirar e vistoriar Placas em Imóveis', icone: 'placaImovel', mneumonic: 'p', pai_id: idSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_IMOVELPLACA\\_%\';', [id: id] )
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Solicitar Retirada', version: 0, action: 'ImovelPlacasRetirar', aplicacao_id: 3, dica: 'Solicitar Retirada de placa', icone: 'placaImovel', mneumonic: 'r', pai_id: idSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_IMOVELPLACARETIRADA%\';', [id: id] )
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Placas em Imóveis à verificar', version: 0, action: 'RelatorioImovelVerificacaoPlaca', aplicacao_id: 3, dica: 'Placas em Imóveis à verificar', icone: 'placaImovelRel', mneumonic: 'v', pai_id: idSubMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Imóveis sem Placas', version: 0, action: 'RelatorioImovelSemPlaca', aplicacao_id: 3, dica: 'Imóveis sem Placas', icone: 'placaImovelRel', mneumonic: 's', pai_id: idSubMenu, posicao: 1050, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Placas em Imóveis à retirar', version: 0, action: 'RelatorioImovelPlacaRetirar', aplicacao_id: 3, dica: 'Placas em Imóveis à retirar', icone: 'placaImovelRel', mneumonic: 't', pai_id: idSubMenu, posicao: 1060, sistema: true, tipo: 'ITEM'])[0][0]
		
		idSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Promoção de Imóveis', version: 0, action: null, aplicacao_id: 3, dica: 'Adicionar Imóveis em Promoção', icone: 'imovelPromocao', mneumonic: 'P', pai_id: idMenu, posicao: 1070, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Adicionar Imóvel em Promoção', version: 0, action: 'ImovelPromocoes', aplicacao_id: 3, dica: 'Promover Imóvel', icone: 'imovelPromocao', mneumonic: 'm', pai_id: idSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_IMOVELPROMOCAO%\';', [id: id] )
		
		
		idSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Portais', version: 0, action: null, aplicacao_id: 3, dica: 'Portais de Imóveis', icone: 'portal', mneumonic: 'p', pai_id: idMenu, posicao: 1080, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubMenu
		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Zap', version: 0, action: null, aplicacao_id: 3, dica: 'Portal Zap', icone: 'zap', mneumonic: 'z', pai_id: idSubMenu, posicao: 1010, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Seleciona Imóveis Zap', version: 0, action: 'SelecaoImoveisZap', aplicacao_id: 3, dica: 'Seleciona Imóveis para exportação Zap', icone: 'zap', mneumonic: 'x', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Configura Zap', version: 0, action: 'ConfiguraZap', aplicacao_id: 3, dica: 'Parâmetros e Configurações Zap', icone: 'zap', mneumonic: 'm', pai_id: idSubSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_ZAPCONF%\';', [id: id] )
		ids << id
			
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Imóvel Zap', version: 0, action: 'ConfiguraTipoImovelZap', aplicacao_id: 3, dica: 'Configurações de Tipo de Imóveis Zap', icone: 'zap', mneumonic: 't', pai_id: idSubSubMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_ZAPTIPODEPENDENCIA%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Dependências Zap', version: 0, action: 'ConfiguraTipoDependenciaZap', aplicacao_id: 3, dica: 'Configurações de Tipo de Dependências Zap', icone: 'zap', mneumonic: 'd', pai_id: idSubSubMenu, posicao: 1050, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id

		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Portal Imóvel Web', version: 0, action: null, aplicacao_id: 3, dica: 'ImovelWeb', icone: 'imovelWeb', mneumonic: 'p', pai_id: idSubMenu, posicao: 1020, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Seleciona Imóveis ImóvelWeb', version: 0, action: 'SelecaoImoveisImoWeb', aplicacao_id: 3, dica: 'Seleciona Imóveis para exportação Imóvel Web', icone: 'imovelWeb', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Configura ImóvelWeb', version: 0, action: 'ConfiguraImovelWeb', aplicacao_id: 3, dica: 'Parâmetros e Configurações ImóvelWeb', icone: 'imovelWeb', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_IMOVELWEBCONF%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Imóvel ImóvelWeb', version: 0, action: 'ConfiguraTipoImovelImovelWeb', aplicacao_id: 3, dica: 'Tipo de Imóvel ImóvelWeb', icone: 'imovelWeb', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_IMOVELWEBTIPODEPENDENCIA%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Dependências ImóvelWeb', version: 0, action: 'ConfiguraTipoDependenciaImovelWeb', aplicacao_id: 3, dica: 'Configurações de Tipo de Dependências ImóvelWeb', icone: 'imovelWeb', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1050, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Dependências Empreendimentos ImóvelWeb', version: 0, action: 'ConfiguraTipoDependenciaEmpreImovelWeb', aplicacao_id: 3, dica: 'Configurações de Tipo de Dependências Empreendimentos ImóvelWeb', icone: 'imovelWeb', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1060, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		
		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Portal TiqueImóvel', version: 0, action: null, aplicacao_id: 3, dica: 'TiqueImovel', icone: 'tiqueImovel', mneumonic: 'p', pai_id: idSubMenu, posicao: 1030, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Seleciona Imóveis TiqueImovel', version: 0, action: 'SelecaoImoveisTiqueImovel', aplicacao_id: 3, dica: 'Seleciona Imóveis para exportação TiqueImovelb', icone: 'tiqueImovel', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Configura TiqueImovel', version: 0, action: 'ConfiguraTiqueImovel', aplicacao_id: 3, dica: 'Parâmetros e Configurações TiqueImovel', icone: 'tiqueImovel', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_TIQUEIMOVELCONF%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Imóvel TiqueImovel', version: 0, action: 'ConfiguraTipoImovelTiqueImovel', aplicacao_id: 3, dica: 'Tipo de Imóvel TiqueImovel', icone: 'tiqueImovel', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_TIQUEIMOVELTIPODEPENDENCIA%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Dependências TiqueImovel', version: 0, action: 'ConfiguraTipoDependenciaTiqueImovel', aplicacao_id: 3, dica: 'Configurações de Tipo de Dependências TiqueImovel', icone: 'tiqueImovel', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1050, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		
		
		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Portal VivaReal', version: 0, action: null, aplicacao_id: 3, dica: 'VivaReal', icone: 'vivaReal', mneumonic: 'p', pai_id: idSubMenu, posicao: 1040, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Seleciona Imóveis VivaReal', version: 0, action: 'SelecaoImoveisVivaReal', aplicacao_id: 3, dica: 'Seleciona Imóveis para exportação VivaReal', icone: 'vivaReal', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Configura VivaReal', version: 0, action: 'ConfiguraVivaReal', aplicacao_id: 3, dica: 'Parâmetros e Configurações VivaReal', icone: 'vivaReal', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_VIVAREALCONF%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Imóvel VivaReal', version: 0, action: 'ConfiguraTipoImovelVivaReal', aplicacao_id: 3, dica: 'Tipo de Imóvel VivaReal', icone: 'vivaReal', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_VIVAREALTIPODEPENDENCIA%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Dependências VivaReal', version: 0, action: 'ConfiguraTipoDependenciaVivaReal', aplicacao_id: 3, dica: 'Configurações de Tipo de Dependências VivaReal', icone: 'vivaReal', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1050, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		
		sb.setLength(0)
		sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
		ids.eachWithIndex{Long zid, int i->
			if ( i > 0 ) sb.append(', ')
			sb.append( "($GRUPO_SECRETARIADO, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
		}
		sb.append( ') AS Q (grupo_id, menu_id);' )
		db.executeInsert( sb.toString() )
		/*
		 * Propostas
		 * */
		ids = []
		idSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Propostas', version: 0, action: null, aplicacao_id: 3, dica: 'Controle de Propostas', icone: 'proposta', mneumonic: 'p', pai_id: null, posicao: 1050, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Cadastro de Propostas', version: 0, action: 'Propostas', aplicacao_id: 3, dica: 'Incluir, Liberar e acompanhar propostas em imóveis', icone: 'proposta', mneumonic: 'c', pai_id: idSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_PROPOSTA%\';', [id: id] )
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Expiração de Propostas', version: 0, action: 'RelatorioPropostaExpiracao', aplicacao_id: 3, dica: 'Listagem de Expiração de Propostas', icone: 'propostaRel', mneumonic: 'x', pai_id: idSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Listagem de Propostas', version: 0, action: 'RelatorioPropostaLista', aplicacao_id: 3, dica: 'Listagem de Propostas', icone: 'propostaRel', mneumonic: 'e', pai_id: idSubMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Listagem de Propostas Pendentes', version: 0, action: 'RelatorioPropostaPendenteLista', aplicacao_id: 3, dica: 'Listagem de Propostas Pendentes', icone: 'propostaRel', mneumonic: 'p', pai_id: idSubMenu, posicao: 1050, sistema: true, tipo: 'ITEM'])[0][0]
		
		sb.setLength(0)
		sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
		ids.eachWithIndex{Long zid, int i->
			if ( i > 0 ) sb.append(', ')
			sb.append( "($GRUPO_GERENTES, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
		}
		sb.append( ') AS Q (grupo_id, menu_id);' )
		db.executeInsert( sb.toString() )
		/*
		 * Vendas
		 * */
		ids = []
		idMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Vendas', version: 0, action: null, aplicacao_id: 3, dica: 'Controle de Vendas', icone: 'venda', mneumonic: 'n', pai_id: null, posicao: 1050, sistema: true, tipo: 'MENU'])[0][0]
		ids << idMenu
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Cadastro de Venda', version: 0, action: 'Vendas', aplicacao_id: 3, dica: 'Cadastrar venda de imóvel', icone: 'venda', mneumonic: 'a', pai_id: idMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_VENDA%\';', [id: id] )
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: '-', version: 0, action: null, aplicacao_id: 3, dica: null, icone: null, mneumonic: null, pai_id: idMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Distribuição de Comissões no período', version: 0, action: 'VendaDistribuicaoComissoes', aplicacao_id: 3, dica: 'Distribuição de Comissões no período', icone: 'vendaRel', mneumonic: 's', pai_id: idMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		
		sb.setLength(0)
		sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
		ids.eachWithIndex{Long zid, int i->
			if ( i > 0 ) sb.append(', ')
			sb.append( "($GRUPO_GERENTES, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
		}
		sb.append( ') AS Q (grupo_id, menu_id);' )
		db.executeInsert( sb.toString() )
		/*
		 * Diversos
		 * */
		ids = []
		idSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Ferramentas', version: 0, action: null, aplicacao_id: 3, dica: 'Ferramentas Diversas', icone: 'confParametro', mneumonic: 'v', pai_id: null, posicao: 1060, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubMenu

		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Controle de Chaves', version: 0, action: null, aplicacao_id: 3, dica: 'Controle de Chaves', icone: 'chaves', mneumonic: 'H', pai_id: idSubMenu, posicao: 1010, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
			
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Quadro de Chaves', version: 0, action: 'ControleChaves', aplicacao_id: 3, dica: 'Quadro de Chaves', icone: 'chaves', mneumonic: 'v', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_QUADROCHAVESPOSICAO%\';', [id: id] )
		
		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Registro de Ligações', version: 0, action: null, aplicacao_id: 3, dica: 'Registro de Ligações Realizadas e Recebidas', icone: 'telefone', mneumonic: 'v', pai_id: idSubMenu, posicao: 1020, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Cadastro Ligações', version: 0, action: 'RegistroLigacoes', aplicacao_id: 3, dica: 'Cadastro de Ligações Realizadas e Recebidas', icone: 'telefone', mneumonic: 'g', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_RECEPCAOTELEFONEMA%\';', [id: id] )
		
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: '-', version: 0, action: null, aplicacao_id: null, dica: null, icone: null, mneumonic: null, pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
			
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Listagem de Ligações', version: 0, action: 'RelatorioRegistroLigacoes', aplicacao_id: 3, dica: 'Listagem dos Registro de Ligações Realizadas e Recebidas', icone: 'telefoneRel', mneumonic: 'l', pai_id: idSubSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id

		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: '-', version: 0, action: null, aplicacao_id: null, dica: null, icone: null, mneumonic: null, pai_id: idSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
			
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Nota Promissória', version: 0, action: 'NotaPromissorias', aplicacao_id: 3, dica: 'Confecção e impressão de Nota Promissória', icone: 'notapromissoria', mneumonic: 'n', pai_id: idSubMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_NOTAPROMISSORIA%\';', [id: id] )
		
		sb.setLength(0)
		sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
		ids.eachWithIndex{Long zid, int i->
			if ( i > 0 ) sb.append(', ')
			sb.append( "($GRUPO_SECRETARIADO, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
		}
		sb.append( ') AS Q (grupo_id, menu_id);' )
		db.executeInsert( sb.toString() )

		/*
		 * Configurações
		 * */
		ids = []
		idSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Configurações', version: 0, action: null, aplicacao_id: 3, dica: 'Configurações Gerais do Sistema', icone: 'conf', mneumonic: 'g', pai_id: null, posicao: 1200, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubMenu
		//
		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Imóveis', version: 0, action: null, aplicacao_id: 3, dica: 'Controle de Imóveis', icone: 'imovel', mneumonic: 'I', pai_id: idSubMenu, posicao: 1010, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Carteira para Imóveis', version: 0, action: 'CarteiraImoveis', aplicacao_id: 3, dica: 'Define carteiras para Imóveis', icone: 'carteira', mneumonic: 't', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_CARTEIRA%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Finalidade de Imóveis', version: 0, action: 'FinalidadeImoveis', aplicacao_id: 3, dica: 'Define finalidades para os imóveis cadastrados (Residencial, Comercial, Residencial/Comercial, Estabelecimento)', icone: 'finalidadeImovel', mneumonic: 't', pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_FINALIDADEIMOVEL%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Segmento de Imóveis', version: 0, action: 'SegmentoImoveis', aplicacao_id: 3, dica: 'Define Segmentação para os imóveis cadastrados', icone: 'segmentoImovel', mneumonic: 't', pai_id: idSubSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_SEGMENTO%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Concorrentes', version: 0, action: 'Concorrentes', aplicacao_id: 3, dica: 'Define um cardápio de Concorrentes para controle de Exclusões', icone: 'concorrente', mneumonic: 'n', pai_id: idSubSubMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_CONCORRENTE%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Classificação de Imóveis', version: 0, action: 'ClasseImoveis', aplicacao_id: 3, dica: 'Define um cardápio de Classificação de Imóveis para Avaliação de Preços', icone: 'classeImovel', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1050, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_CLASSEIMOVEL%\';', [id: id] )
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Quadro de Chaves', version: 0, action: 'QuadroChaves', aplicacao_id: 3, dica: 'Define Quadro para Controle de Chaves', icone: 'chaves', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1060, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_QUADROCHAVES\\_%\';', [id: id] )
		//
		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Interessados/Compradores', version: 0, action: null, aplicacao_id: 3, dica: 'Controle de Interessados', icone: 'interessado', mneumonic: 'n', pai_id: idSubMenu, posicao: 1020, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Classificação para Interessados', version: 0, action: 'ClasseInteressados', aplicacao_id: 3, dica: 'Define classificações a serem atribuídas aos Compradores/Interessados a fim de facilitar seu manipulamento', icone: 'classeInteressado', mneumonic: 's', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_CLASSEINTERESSADO%\';', [id: id] )
		//
		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Corretores/Equipes', version: 0, action: null, aplicacao_id: 3, dica: 'Corretores, Equipes e participantes envolvidos na corretagem de Imóveis', icone: 'corretorEquipe', mneumonic: 'Q', pai_id: idSubMenu, posicao: 1030, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Cadastro de Corretores', version: 0, action: 'Corretores', aplicacao_id: 3, dica: 'Corretores/Promotores de Imóveis', icone: 'corretorCadastro', mneumonic: 'r', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_CORRETOR%\';', [id: id] )
			
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Cadastro de Equipes', version: 0, action: 'Equipes', aplicacao_id: 3, dica: 'Equipes de Corretores/Promotores de Imóveis', icone: 'equipe', mneumonic: 'q', pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_EQUIPE%\';', [id: id] )
		//
		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Imóveis', version: 0, action: null, aplicacao_id: 3, dica: 'Cadastros de Tipo de Imóveis e suas dependências', icone: 'tipoImovel', mneumonic: 'T', pai_id: idSubMenu, posicao: 1040, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Imóveis', version: 0, action: 'TipoImoveis', aplicacao_id: 3, dica: 'Tipo de Imóvel', icone: 'tipoImovel', mneumonic: 'I', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_TIPOIMOVEL%\';', [id: id] )
		ids << db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Dependências', version: 0, action: 'TipoDependencias', aplicacao_id: 3, dica: 'Tipo de dependências para configurar o Tipo de Imóvel', icone: 'tipoDependencia', mneumonic: 'p', pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_TIPODEPENDENCIA%\';', [id: id] )
		//
		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Cidades, Regiões e Bairros', version: 0, action: 'cidade', aplicacao_id: 3, dica: 'Cidades Regiões e Bairros onde a empresa atua', icone: 'cidade', mneumonic: 'C', pai_id: idSubMenu, posicao: 1050, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Cidades', version: 0, action: 'CidadeCorretagems', aplicacao_id: 3, dica: 'Cidades', icone: 'cidade', mneumonic: 'C', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_CIDADECORRETAGEM%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Regiões', version: 0, action: 'RegiaoCorretagems', aplicacao_id: 3, dica: 'Regiões ou Macro Bairros', icone: 'regiao', mneumonic: 'R', pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_REGIAOCORRETAGEM%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Bairros', version: 0, action: 'BairroCorretagems', aplicacao_id: 3, dica: 'Bairros para localizar seus Imóveis', icone: 'bairro', mneumonic: 'B', pai_id: idSubSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_BAIRROCORRETAGEM%\';', [id: id] )
		//
		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Anúncios/Veículos de Comunicação', version: 0, action: null, aplicacao_id: 3, dica: 'Cadastros pertinentes ao controle de Anúncios e Seus Veículos de Comunicação', icone: 'anuncios', mneumonic: 'v', pai_id: idSubMenu, posicao: 1060, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Campanhas de Marketing', version: 0, action: 'Campanhas', aplicacao_id: 3, dica: 'Campanhas de Marketing para publicação de Anúncios', icone: 'campanhas', mneumonic: 'm', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_CAMPANHA%\';', [id: id] )
			
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Veículos de Comunicações', version: 0, action: 'VeiculoComunicacaos', aplicacao_id: 3, dica: 'Veículos de Comunicações para Criação de Campanhas', icone: 'veiculoComunicacao', mneumonic: 'v', pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_VEICULO%\';', [id: id] )
			
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Mídias', version: 0, action: 'MidiaTipos', aplicacao_id: 3, dica: 'Tipo de Mídias para Publicação em Campanhas', icone: 'midiaTipo', mneumonic: 'p', pai_id: idSubSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_MIDIA%\';', [id: id] )
		//
		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Pessoas e Relacionados', version: 0, action: null, aplicacao_id: 3, dica: 'Pessoas relacionadas ao sistema', icone: 'pessoa', mneumonic: 'C', pai_id: idSubMenu, posicao: 1070, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Pessoas', version: 0, action: 'Pessoas', aplicacao_id: 3, dica: 'Cadastro Pessoas Envolvidas ao Sistema', icone: 'pessoa', mneumonic: 'P', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_PESSOA%\';', [id: id] )
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Endereçamento', version: 0, action: 'TipoEnderecos', aplicacao_id: 3, dica: 'Tipo de Endereçamento', icone: 'tipoEndereco', mneumonic: 'c', pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_TIPOENDERECO%\';', [id: id] )
		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Correios', version: 0, action: null, aplicacao_id: 3, dica: 'Cadastros de Logradouros e localidades que não estão no Correios', icone: 'mailbox', mneumonic: 'r', pai_id: idSubMenu, posicao: 1080, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Estados', version: 0, action: 'Estados', aplicacao_id: 3, dica: 'Estados da Federação', icone: 'mailbox', mneumonic: 'E', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_ESTADO%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Cidades', version: 0, action: 'Cidades', aplicacao_id: 3, dica: 'Cidades da Federação', icone: 'mailbox', mneumonic: 'C', pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_CIDADE\\_\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Bairros', version: 0, action: 'Bairros', aplicacao_id: 3, dica: 'Bairros da Federação', icone: 'mailbox', mneumonic: 'B', pai_id: idSubSubMenu, posicao: 1030, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_BAIRRO\\_\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Logradouros', version: 0, action: 'Logradouros', aplicacao_id: 3, dica: 'Logradouros da Federação', icone: 'mailbox', mneumonic: 'L', pai_id: idSubSubMenu, posicao: 1040, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_LOGRADOURO%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Tipo de Logradouros', version: 0, action: 'TipoLogradouros', aplicacao_id: 3, dica: 'Cadastro Auxiliar de Tipo de Logradouros', icone: 'mailbox', mneumonic: 'r', pai_id: idSubSubMenu, posicao: 1050, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_TIPOLOGRADOURO%\';', [id: id] )
		
/*		idSubSubMenu = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Diversos', version: 0, action: null, aplicacao_id: 3, dica: 'Cadastros de Apoio', icone: 'diversos', mneumonic: 'v', pai_id: idSubMenu, posicao: 1090, sistema: true, tipo: 'MENU'])[0][0]
		ids << idSubSubMenu
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Moedas de Apuração', version: 0, action: 'Moedas', aplicacao_id: 3, dica: 'Moedas/Índices nacionais e extrangeiros para cotação de imóveis', icone: 'coin', mneumonic: 'm', pai_id: idSubSubMenu, posicao: 1010, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_MOEDA%\';', [id: id] )
		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Índices de Correção', version: 0, action: 'MoedaIndices', aplicacao_id: 3, dica: 'Valor de apuração dos Índices para valorização de imóveis', icone: 'moedaIndice', mneumonic: 'i', pai_id: idSubSubMenu, posicao: 1020, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_MOEDAINDICE%\';', [id: id] )
*/		
		id = db.executeInsert('INSERT INTO menu( id, nome, posicao, pai_id, icone, action, tipo, version, aplicacao_id, mneumonic, sistema, dica) VALUES (nextval(\'seq_menu\'), :nome, :posicao, :pai_id, :icone, :action, :tipo, :version, :aplicacao_id, :mneumonic, :sistema, :dica);'
			, [nome: 'Textos para relatórios', version: 0, action: 'TextoRelatorios', aplicacao_id: 3, dica: 'Textos e Declarações para relatórios', icone: 'textoRelatorio', mneumonic: 'i', pai_id: idSubMenu, posicao: 1090, sistema: true, tipo: 'ITEM'])[0][0]
		ids << id
		db.executeInsert( 'INSERT INTO menu_regra SELECT :id as id_menu, id AS role_id FROM regra WHERE authority ILIKE \'ROLE_TEXTOSISTEMA%\';', [id: id] )
		
		sb.setLength(0)
		sb.append( 'INSERT INTO grupo_menu(grupo_id, menu_id) SELECT * FROM ( VALUES' )
		ids.eachWithIndex{Long zid, int i->
			if ( i > 0 ) sb.append(', ')
			sb.append( "($GRUPO_GERCONF, $zid), ($GRUPO_SUPERUSUARIOS, $zid), ($GRUPO_ADMINISTRADORES, $zid)" )
		}
		sb.append( ') AS Q (grupo_id, menu_id);' )
		db.executeInsert( sb.toString() )
	}
}
