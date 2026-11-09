package br.univille.pescaria.controller;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Configuração aplicada a todos os formulários: tira espaços das pontas dos textos
 * e transforma campos vazios em null (assim um campo opcional em branco não vira "").
 */
@ControllerAdvice
public class FormularioAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
