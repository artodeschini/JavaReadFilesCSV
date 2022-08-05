package org.todeschini.ui;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Stream;

import org.todeschini.binarios.DirectoryUtils;
import org.todeschini.binarios.FileService;
import org.todeschini.binarios.IFileService;

public class Menu {

    private static final String SELECIONE_ARQUIVO = "Selecione o caminho do arquivo desejado";
    private static final String HELP_MSG = "Use comand count *, count distinct <propriedade>, filter <propriedade> <valor>, exit ";
    private static final String HELLO = "Bem vindo ao sistema para ver opcoes digite -help";
    private static final String VERSION = "Operacoes com arquivos version 0.1";
    private static final String COMMAND_NOT_FOUND = "Comando nao encontrado";
    private static final String EXIT = "exit";

    private void showMessage(Object msg) {
        System.out.println(msg);
    }

    public void execute() {
        showMessage(HELLO);
        Scanner scanner = new Scanner(System.in);

        Optional<String> pathCsvFile;

        IFileService operacoes = null;

        try {
            //String jarPath = DirectoryUtils.getPathApp(this.getClass());
            String jarPath = getClass()
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();

            jarPath = jarPath.contains("target") ? jarPath.split("target")[0] : jarPath;

            pathCsvFile = DirectoryUtils.selecionaArquivo(jarPath);

        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }

        if (pathCsvFile.isPresent()) {

            operacoes = FileService.getInstace(pathCsvFile.get());
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
            System.out.print("Deu m");
        }
    }

    public String buildParametroValue(String comando, String valor) {

        if (comando != null && !comando.equals("") && valor != null && !valor.equals("")) {
            return comando.substring((comando.indexOf(valor)));
        }

        return "";
    }
//
//	public static void main(String[] args) {
//		Menu menu = new Menu();
//		menu.execute();
//	}

}
