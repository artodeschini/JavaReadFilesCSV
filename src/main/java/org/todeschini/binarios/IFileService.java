package org.todeschini.binarios;

import java.util.stream.Stream;

public interface IFileService {
	
	long count();
	
	long countDistinct(String propriedade);
	
	Stream<String> filter(String propriedade, String valor);
}
