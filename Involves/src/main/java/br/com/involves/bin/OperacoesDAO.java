package br.com.involves.bin;

import java.util.Collection;

public interface OperacoesDAO {
	
	int count();
	
	int countDistinct(String propriedade);
	
	Collection<String> filter(String propriedade, String valor);

}
