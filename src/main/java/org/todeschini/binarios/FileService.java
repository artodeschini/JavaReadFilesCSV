package org.todeschini.binarios;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Artur
 */
public class FileService implements IFileService {

    private static final String DELIMITER = ",";
    private final File file;

    private volatile static FileService instace;

    /**
     * @param path local onde será lido o arquivo desde a raiz dele
     * @return OperacoesImplDAO
     * @author Artur
     * <p>
     * Este metodo obtem uma instancia da classe pelo padrao conhecido como Singleton
     */
    public static FileService getInstace(String path) {
        if (instace == null) {
            synchronized (FileService.class) {
                if (instace == null) {
                    instace = new FileService(path);
                }
            }
        }

        return instace;
    }

    /**
     * @return BufferedReader
     * @throws RuntimeException
     * @author Artur
     * <p>
     * Obter o Buffer para ler o arquivo
     * <p>
     * Obtenho o enconde do arquivo para ter certeza de poder ler corretamente os acentos
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
     *
     * @param path
     */
    private FileService(String path) {
        file = new File(path);
        this.getBufferedReader();
    }

    /**
     * return int numero linhas do arquivo
     */
    public long count() {
        Path path = Paths.get(file.getPath());
        long lines = 0;

        try {

            lines = Files.lines(path).count();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines - 1;
    }


    /**
     * Metodo utilizado para encontra o indice da coluna ma matriz de dados
     *
     * @param line
     * @param propriedade
     * @return int
     */
    private int findIndex(String line, String propriedade) {
        String[] cabecario = line.split(DELIMITER);
        int index = -1;

        for (int i = 0; i < cabecario.length; i++) {
            if (cabecario[i].equalsIgnoreCase(propriedade)) {
                return i;
            }
        }

        return index;
    }

    /**
     * Metodo que conta as diferente ocorrencias da propriedade
     *
     * @param propriedade
     * @return int
     */
    public long countDistinct(String propriedade) {
        BufferedReader br = this.getBufferedReader();

        String line;

        try {

            line = br.readLine();

            int index = this.findIndex(line, propriedade);

            //otimizacao se nao encontrou a propriedade no arquivo
            if (index == -1) {
                throw new RuntimeException("Propriedade " + propriedade + " nao encontrada digite uma propriedade valida " + line);
            }


            /**
             * Utilizo a interface Set que nao permite repeticoes para realizar a contagem
             */
            Set<String> set = new LinkedHashSet<String>();

            String[] prorpiedades = null;
            String s;

            line = br.readLine();

            while (line != null) {
                prorpiedades = line.split(DELIMITER);

                s = prorpiedades[index];


                set.add(s);

                line = br.readLine();
            }

            return set.size(); // retorno a tamanho do size que nao permite ocorrencias repetidas

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * @param propriedade, valor
     * @return Collection
     * @author Artur
     * <p>
     * O metodo retorna as ocorrencias que o valor for encontrado como igual na propriedade passados por parametro
     */
    public Stream<String> filter(String propriedade, String valor) {
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
        //List<String> valores = new LinkedList<String>();
        //Stream<String> String = Stream.builder().build();
        Stream.Builder<String> valores = Stream.builder();

        try {

            line = br.readLine();

            int index = this.findIndex(line, propriedade);

            //otimizacao se nao encontrou a propriedade no arquivo
            if (index == -1) {
                throw new RuntimeException("Propriedade " + propriedade + " nao encontrada digite uma propriedade valida " + line);
            }

            //requisito diz para adicionar o cabecario
            valores.add(line);

            String[] prorpiedades = null;
            String s;

            //pula a primeira linha
            line = br.readLine();

            while (line != null) {
                prorpiedades = line.split(DELIMITER);

                s = prorpiedades[index];

                if (s.equals(valor)) {
                    valores.add(line);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return valores.build();//.stream();
    }
}
