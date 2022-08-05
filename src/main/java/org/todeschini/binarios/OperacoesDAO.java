package org.todeschini.binarios;

import java.util.stream.Stream;

public interface OperacoesDAO {
	
	int count();
	
	int countDistinct(String propriedade);
	
	Stream<String> filter(String propriedade, String valor);

}
