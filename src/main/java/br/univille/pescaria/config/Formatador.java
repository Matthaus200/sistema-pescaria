package br.univille.pescaria.config;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Formata números no padrão brasileiro para as páginas Thymeleaf.
 * Uso no template: ${@fmt.num(captura.pesoKg)} → "2,4" (sem zeros sobrando no fim).
 */
@Component("fmt")
public class Formatador {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    public String num(Number valor) {
        if (valor == null) {
            return "—";
        }
        DecimalFormat formato = new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(PT_BR));
        return formato.format(valor);
    }
}
