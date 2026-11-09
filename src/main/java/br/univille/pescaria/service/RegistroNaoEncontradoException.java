package br.univille.pescaria.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Lançada quando um registro procurado pelo id não existe.
 * O Spring responde com 404 e mostra a página templates/error/404.html.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RegistroNaoEncontradoException extends RuntimeException {

    public RegistroNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
