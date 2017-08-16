package br.com.involves.ui;

import java.util.Collection;
import java.util.Scanner;

import br.com.involves.bin.OperacoesImplDAO;
import br.com.involves.bin.OperacoesDAO;

public class Menu {
	
	private static String SELECIONE_ARQUIVO = "Selecione o caminho do arquivo desejado";
	private static String HELP_MSG = "Use comand count *, count distinct <propriedade>, filter <propriedade> <valor>, exit ";
	private static String HELLO = "Bem vindo ao sistema para ver opcoes digite -help";
	private static String VERSION = "Operacoes com arquivos version 0.1";
	private static String COMMAND_NOT_FOUND = "Comando nao encontrado";
	private static String EXIT = "exit";
	
	private void showMessage(Object msg) {
		System.out.println( msg );
	}
	
	public void showSelectFile() {
		showMessage(SELECIONE_ARQUIVO);
	}
	
	public void execute() {
		showMessage(HELLO);
		showSelectFile();
		Scanner scanner = new Scanner(System.in);
		
		OperacoesDAO operacoes = null;
		
		// forca o usuario a selecionar um arquivo antes de ir adiante
		try {
			operacoes = OperacoesImplDAO.getInstace( scanner.nextLine() );
		} catch (Exception e) {
			 while ( operacoes == null) {
				 showMessage("Nao foi possivel carregar o arquivo");
				 showSelectFile();
				 operacoes = OperacoesImplDAO.getInstace( scanner.nextLine() );
			 }
		}
		
		showMessage( HELP_MSG );
		
		String action = scanner.nextLine();
		String lower = action.toLowerCase();
		
		String[] campos;
		Collection<String> col;
		
		while ( !lower.equals( EXIT ) ) {
			
			if ( lower.contains( "count" ) ) {
				
				if ( lower.equals( "count *" ) ) {
					showMessage( operacoes.count() );
				
				} else if ( lower.contains( "count distinct" ) ) {
					campos = action.split(" ");
					
					if ( campos.length >= 3 ) {
						try {
							showMessage( operacoes.countDistinct( campos[2] ) );
						} catch (Exception e) {
							showMessage( e.getMessage() );
						}
					} else {
						showMessage("voce deve utilizar o comando count distinct com uma propriedade");
					}
				} else {
					showMessage("Voce deve utilizar count * ou count distinct <propriedade>");
				}
			} else if ( lower.contains( "filter" ) ) {
				campos = action.split(" ");
				
				if ( campos.length >= 2 ) {
					try {
						
						//O ternario se aplica para os campos capital e alternative_name que podem ter campos sem texto algum
						col = operacoes.filter( campos[1], this.buildParametroValue( action , campos.length >= 3 ? campos[2] : "" ) );
						
						for (String row : col) {
							showMessage( row );
						}
					} catch (Exception e) {
						showMessage( e.getMessage() );
					}
				} else {
					showMessage("Voce deve utilizar o comando filter com uma propriedade e um valor sintaxe filter <propriedade> <valor>" );
				}
				
			} else if ( lower.contains( "-version" ) ) {
				showMessage( VERSION );
			} else {
				showMessage( COMMAND_NOT_FOUND );
			}
			
			//muda o comando
			action = scanner.nextLine();
			lower = action.toLowerCase();
		}
		
		showMessage("Good Bye!");
		scanner.close();
		
	}
	
	public String buildParametroValue(String comando, String valor) {
		
		if ( comando != null && !comando.equals("") && valor != null && !valor.equals("") ) {
			return comando.substring( ( comando.indexOf( valor ) ), comando.length() );
		}
		
		return "";
	}
	
	public static void main(String[] args) {
		Menu menu = new Menu();
		menu.execute();
	}
	
}
