package dados.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Tabela;
import models.Usuario;
import services.TblsService;


public class TblUsuariosController {
	
public static List<Usuario> updateListaUsuarios() {
	/*
	 * 1 - Atualiza lista de tabelas, conforme tabela  temporaria
	 * 2 - Atualiza lista de usuarios, conforme caminho da lista da tabela
	 */
	
		Tabela tempTbl = new Tabela(1, null, null);
		
		List tempListTab = TblTblsController.updateListaTabela();
		

		for (Object x : tempListTab) {
		
			tempTbl = TblsService.puxaTabela(tempListTab, tempTbl.getCodigo());
			
		}
		
		String caminho = tempTbl.getCaminho();
		
		List<Usuario> listaDeUsuarios = new ArrayList<Usuario>();
		
		File arquivoUsuarios = new File(caminho);

		
		try (BufferedReader tblUsuarios = new BufferedReader(new FileReader(arquivoUsuarios))) {
			String usuario = tblUsuarios.readLine();
			while (usuario != null) {
				String[] fields = usuario.split(";");
				Integer codigo = Integer.parseInt(fields[0]);
				String nome = fields[1];
				String login = fields[2];
				String senha = fields[3];
				Integer permissoes = Integer.parseInt(fields[4]);
				String email = fields[5];
				String emailGerencia = fields[6];
				Integer setor = Integer.parseInt(fields[7]);
				Integer situacao = Integer.parseInt(fields[8]);
				
				listaDeUsuarios.add(new Usuario(codigo, nome, login, senha, permissoes, email, emailGerencia, setor, situacao));
				usuario = tblUsuarios.readLine();
				
			}
		} catch (IOException e) {
			e.getMessage();
		}
		return listaDeUsuarios;
	}

}
