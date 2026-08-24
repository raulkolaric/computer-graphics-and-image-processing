package ui;

/** Tipos de primitivos que podem ser criados na interface. */
public enum TiposPrimitivos {
    /** A single point. */
    PONTO(1),
    /** A line segment. */
    RETA(2),
    /** A rectangle. */
    RETANGULO(2),
    /** A triangle. */
    TRIANGULO(3),
    /** A circle. */
    CIRCULO(2),
    /** No primitive selected. */
    NENHUM(0);

    private final int quantidadePontos;

    TiposPrimitivos(int quantidadePontos) {
        this.quantidadePontos = quantidadePontos;
    }

    /** Retorna quantos cliques são necessários para criar o primitivo.
     * @return quantidade de cliques
     */
    public int getQuantidadePontos() {
        return quantidadePontos;
    }
}
