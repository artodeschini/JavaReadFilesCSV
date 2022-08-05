package org.todeschini.ui;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.todeschini.binarios.OperacoesImplDAO;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class Testes {



	
//	@Test
	public void testCountDistinctUf() {
		int numero = OperacoesImplDAO.getInstace("/Users/Artur/cidades.csv").countDistinct("uf");
		//assertSame("O numero de ocorrencias", numero, 27);
	}
	
//	@Test
//	public void testAssertSame() {
//		int numero = OperacoesImplDAO.getInstace("/Users/Artur/cidades.csv").count();
//		//System.out.println( numero );
//		assertSame("O numero de ocorrencias", numero, 5565);
//	}
	
//	@Test
	public void testeFilter() {
		OperacoesImplDAO.getInstace("/Users/Artur/cidades.csv").filter("name", "Florianópolis");
		
	}

}
