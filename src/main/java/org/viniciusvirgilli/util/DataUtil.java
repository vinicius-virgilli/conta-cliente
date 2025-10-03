package org.viniciusvirgilli.util;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class DataUtil {

    private static final String FORMATO_DATA = "dd/MM/yyy HH:mm:ss";

    public static boolean isDataOperacaoValida(String dataOperacao) {
        if (dataOperacao == null) {
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATO_DATA);
            LocalDateTime.parse(dataOperacao, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Integer getHora(String dataOperacao) {
        return Integer.parseInt(dataOperacao.substring(11, 13));
    }
}
