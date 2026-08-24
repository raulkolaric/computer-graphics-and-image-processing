package ui;

/**
 * Tipos de primitivos que podem ser criados na interface.
 *
 * @author Raul Kolaric, Liam Lopes, Rafael Infantini, Guilherme Coutinho
 * @version 2026/08/24
 */
public enum TiposPrimitivos {
    /** Um único ponto. */
    PONTO(1),
    /** Um segmento de reta. */
    RETA(2),
    /** Um retângulo. */
    RETANGULO(2),
    /** Um triângulo. */
    TRIANGULO(3),
    /** Um círculo. */
    CIRCULO(2),
    /** Nenhum primitivo selecionado. */
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
