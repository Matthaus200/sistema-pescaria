package br.univille.pescaria.service;

/**
 * Lançada quando um registro não pode ser excluído porque outros registros
 * dependem dele (por exemplo, um pescador que tem pescarias).
 * O controller mostra a mensagem para o usuário.
 */
public class ExclusaoNaoPermitidaException extends RuntimeException {

    public ExclusaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }
}
