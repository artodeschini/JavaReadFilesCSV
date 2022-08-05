package org.todeschini.ui;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Stream;

import org.todeschini.binarios.OperacoesImplDAO;
import org.todeschini.binarios.OperacoesDAO;

public class Menu {

    private static String SELECIONE_ARQUIVO = "Selecione o caminho do arquivo desejado";
    private static String HELP_MSG = "Use comand count *, count distinct <propriedade>, filter <propriedade> <valor>, exit ";
    private static String HELLO = "Bem vindo ao sistema para ver opcoes digite -help";
    private static String VERSION = "Operacoes com arquivos version 0.1";
    private static String COMMAND_NOT_FOUND = "Comando nao encontrado";
    private static String EXIT = "exit";

    private void showMessage(Object msg) {
        System.out.println(msg);
    }

    private Optional<String> selecionaArquivo(String pathApp) {
        // Creates an array in which we will store the names of files and directories
//		String[] pathnames;

        // Creates a new File instance by converting the given pathname string
        // into an abstract pathname
        File dirApp = new File(pathApp);

        // Populates the array with names of files and directories
//		pathnames = f.list();

        // For each pathname in the pathnames array
//		for (String pathname : dirApp.list()) {
//			// Print the names of files and directories
////
//			if ( pathname.toLowerCase().contains(".csv" ) ) {
//				p
//			}
//		}
        //list.stream().map(MyObject::getName).filter(name::equals).findFirst().isPresent();
        return Arrays.stream(dirApp.list()).filter(f -> f.toLowerCase().contains(".csv")).findFirst();

        //csv.isPresent()
    }

    public void execute() {
        showMessage(HELLO);
        Scanner scanner = new Scanner(System.in);

        Optional<String> pathCsv;

        OperacoesDAO operacoes = null;

        try {
            String jarPath = getClass()
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();

//            System.out.println(jarPath);

            if (jarPath.contains("target")) {
                System.out.printf(jarPath.split("target")[0]);
                pathCsv = selecionaArquivo(jarPath.split("target")[0]);
            } else {
                pathCsv = selecionaArquivo(jarPath);
            }

        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }

        if (pathCsv.isPresent()) {

            operacoes = OperacoesImplDAO.getInstace(pathCsv.get());
            showMessage(HELP_MSG);

            String action = scanner.nextLine();
            String lower = action.toLowerCase();

            String[] campos;
            Stream<String> col;

            while (!lower.equals(EXIT)) {

                if (lower.contains("count")) {

                    if (lower.equals("count *")) {
                        showMessage(operacoes.count());

                    } else if (lower.contains("count distinct")) {
                        campos = action.split(" ");

                        if (campos.length >= 3) {
                            try {
                                showMessage(operacoes.countDistinct(campos[2]));
                            } catch (Exception e) {
                                showMessage(e.getMessage());
                            }
                        } else {
                            showMessage("voce deve utilizar o comando count distinct com uma propriedade");
                        }
                    } else {
                        showMessage("Voce deve utilizar count * ou count distinct <propriedade>");
                    }
                } else if (lower.contains("filter")) {
                    campos = action.split(" ");

                    if (campos.length >= 2) {
                        try {

                            //O ternario se aplica para os campos capital e alternative_name que podem ter campos sem texto algum
                            col = operacoes.filter(campos[1], this.buildParametroValue(action, campos.length >= 3 ? campos[2] : "")).findAny().stream();

//						for (String row : col) {
//							showMessage( row );
//						}
                            col.forEach(row -> showMessage(row));
                        } catch (Exception e) {
                            showMessage(e.getMessage());
                        }
                    } else {
                        showMessage("Voce deve utilizar o comando filter com uma propriedade e um valor sintaxe filter <propriedade> <valor>");
                    }

                } else if (lower.contains("-version")) {
                    showMessage(VERSION);
                } else {
                    showMessage(COMMAND_NOT_FOUND);
                }

                //muda o comando
                action = scanner.nextLine();
                lower = action.toLowerCase();
            }

            showMessage("Good Bye!");
            scanner.close();

        } else {
            System.out.printf("Deu m");
        }
    }

    public String buildParametroValue(String comando, String valor) {

        if (comando != null && !comando.equals("") && valor != null && !valor.equals("")) {
            return comando.substring((comando.indexOf(valor)), comando.length());
        }

        return "";
    }
//
//	public static void main(String[] args) {
//		Menu menu = new Menu();
//		menu.execute();
//	}

}
