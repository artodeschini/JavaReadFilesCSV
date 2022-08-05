package org.todeschini.binarios;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;

public class DirectoryUtils {

    public static Optional<String> selecionaArquivo(String pathApp) {
        File applicationDirectory = new File(pathApp);
        return Arrays.stream(applicationDirectory.list()).filter(f -> f.toLowerCase().contains(".csv")).findFirst();
    }
//
//    public static String getPathApp(Class clazz) throws URISyntaxException {
//        return clazz.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
//    }
}
