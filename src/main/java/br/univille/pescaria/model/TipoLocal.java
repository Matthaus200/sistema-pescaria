package br.univille.pescaria.model;

/**
 * Tipos de local de pesca. É salvo no banco como texto (ex.: "RIO").
 */
public enum TipoLocal {
    RIO("Rio"),
    LAGO("Lago"),
    REPRESA("Represa"),
    MAR("Mar"),
    PESQUE_PAGUE("Pesque-pague");

    private final String descricao;

    TipoLocal(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
