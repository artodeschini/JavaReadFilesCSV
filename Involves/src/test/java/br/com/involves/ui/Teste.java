package br.com.involves.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import javax.swing.JFileChooser;

import br.com.involves.bin.OperacoesImplDAO;
import br.com.involves.bin.OperacoesDAO;

public class Teste {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		JFileChooser chooser = new JFileChooser();
		
		int open = chooser.showOpenDialog(null);
		
		if ( open == JFileChooser.APPROVE_OPTION ) {
			
			
			File file = chooser.getSelectedFile();//.getAbsolutePath();
			FileReader fr = new FileReader(file);
			String encode = fr.getEncoding();
			System.out.println( encode );
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encode));
			
			//OperacoesDAO operador = OperacoesImplDAO.getInstace(chooser.getSelectedFile().getAbsolutePath());
			//System.out.println( operador.countDistinct("uf") );
			//Collection col = operador.filter("uf", "SC");
			//Collection<String> col = operador.filter("capital", "");
			
			//for (Object object : col) {
				//System.out.println( object );
			//}
			
		} 

	}

}
