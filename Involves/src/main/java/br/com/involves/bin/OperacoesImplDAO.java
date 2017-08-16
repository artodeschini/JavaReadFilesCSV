package br.com.involves.bin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Artur
 * 
 * Essa classe implementa as funcionalidades especificadas
 *
 */
public class OperacoesImplDAO implements OperacoesDAO {
	
	private static final String DELIMITER = ",";
	private File file;
	
	private volatile static OperacoesImplDAO instace;
	
	/**
	 * @author Artur
	 * 
	 * Este metodo obtem uma instancia da classe pelo padrao conhecido como Singleton
	 * 
	 * @param String path local onde será lido o arquivo desde a raiz dele
	 * @return OperacoesImplDAO
	 */
	public static OperacoesImplDAO getInstace(String path) {
		if ( instace == null ) {
			synchronized ( OperacoesImplDAO.class ) {
				if ( instace == null ) {
					instace = new OperacoesImplDAO(path);
				}
			}
		}
		
		return instace;
	}
	
	/**
	 * @author Artur
	 * 
	 * Obter o Buffer para ler o arquivo 
	 * 
	 * Obtenho o enconde do arquivo para ter certeza de poder ler corretamente os acentos
	 * 
	 * @return BufferedReader
	 * @throws RuntimeException
	 */
	private BufferedReader getBufferedReader() throws RuntimeException {
		try {
			
			FileReader fileReader = new FileReader(file);
			String encode = fileReader.getEncoding();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encode));
			return reader;
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Erro ao ler o arquivo");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Erro ao ler o arquivo");
		}
	}
	
	/**
	 * Construtor privada para obter uma instancia dessa classe utilize o metodo static getInstace
	 * @param String path
	 */
	private OperacoesImplDAO(String path) {
		file = new File(path);
		this.getBufferedReader();
	}

	/**
	 * return int numero linhas do arquivo
	 */
	public int count() {
		BufferedReader br = this.getBufferedReader();
		String line;
		int count = -1;
		try {
			line = br.readLine();
			
			while (line != null) {
				//pula a primeira linha
				line = br.readLine();
				
				count++;
			}
			
			return count;
			
		} catch (IOException e) {
			throw new RuntimeException("Erro ao encontrar o arquivo");
		}
	}
	
	/**
	 * Metodo utilizado para encontra o indice da coluna ma matriz de dados
	 * @param String line
	 * @param String propriedade
	 * @return int
	 */
	private int findIndex(String line, String propriedade) {
		String[] cabecario = line.split(DELIMITER);
		int index = -1;
		
		for (int i = 0; i < cabecario.length; i++) {
			if ( cabecario[i].equalsIgnoreCase( propriedade ) ) {
				return i;
			}
		}
		
		return index;
	}
	
	/**
	 * Metodo que conta as diferente ocorrencias da propriedade
	 * 
	 * @return int
	 * @param String propriedade
	 */
	public int countDistinct(String propriedade) {
		BufferedReader br = this.getBufferedReader();
		
		String line;
		
		try {
			
			line = br.readLine();
			
			int index = this.findIndex(line, propriedade);
			
			//otimizacao se nao encontrou a propriedade no arquivo
			if ( index == -1 ) {
				throw new RuntimeException("Propriedade " + propriedade + " nao encontrada digite uma propriedade valida " + line);
			}
			
			
			/**
			 * Utilizo a interface Set que ao permite repeticoes para realizar a contagem
			 */
			Set<String> set = new LinkedHashSet<String>();
			
			String[] prorpiedades = null;
			String s;
			
			//pula a primeira linha
			line = br.readLine();
			
			while (line != null) {
				prorpiedades = line.split(DELIMITER);
				
				s = prorpiedades[ index ];
				
				
				set.add( s );
				
				line = br.readLine();
			}
			
			return set.size(); // retorno a tamanho do size que nao permite ocorrencias repetidas
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	/**
	 * @author Artur
	 * 
	 * O metodo retorna as ocorrencias que o valor for encontrado como igual na propriedade passados por parametro 
	 * 
	 * @param String propriedade, valor
	 * @return Collection 
	 */
	public Collection<String> filter(String propriedade, String valor) {
		BufferedReader br = this.getBufferedReader();
		
		String line;
		
		/*
		 *  Observacao
		 *  
		 *  aqui foi onde mais tive duvida no que fazer com relacao a performace adicionar a uma colecao
		 *  
		 *  no quesito de performace o ideal seria imprimir direto na tela 
		 * 
		 *  porem pensei em ter flexibilidade para codigos futuros no caso outra User Interface
		 *  
		 *  e pensando em SOLID o principio Single Responsibility Principle dessa forma deixei a parte logica separada da que faz a entrada e saida dos dados 
		 */
		List<String> valores = new LinkedList<String>();
		
		try {
			
			line = br.readLine();
			
			int index = this.findIndex(line, propriedade);
			
			//otimizacao se nao encontrou a propriedade no arquivo
			if ( index == -1 ) {
				throw new RuntimeException("Propriedade " + propriedade + " nao encontrada digite uma propriedade valida " + line);
			}
			
			//requisito diz para adicionar o cabecario
			valores.add( line );
			
			String[] prorpiedades = null;
			String s;
			
			//pula a primeira linha
			line = br.readLine();
			
			while (line != null) {
				prorpiedades = line.split(DELIMITER);
				
				s = prorpiedades[ index ];
				
				if ( s.equals( valor ) ) {
					valores.add( line );
				}
				
				
				line = br.readLine();
				
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return valores;
	}

}
