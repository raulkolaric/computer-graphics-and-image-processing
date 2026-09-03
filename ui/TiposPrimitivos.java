package ui;

/**
 * Modos de interação disponíveis na interface para criar ou selecionar elementos.
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
    /** Seleção de um primitivo existente. */
    SELECAO(1),
    /** Nenhum primitivo selecionado. */
    NENHUM(0);

    private final int quantidadePontos;

    TiposPrimitivos(int quantidadePontos) {
        this.quantidadePontos = quantidadePontos;
    }

    /** Retorna quantos cliques o modo exige para concluir uma interação.
     * Para {@link #SELECAO}, o clique seleciona um elemento; para {@link #NENHUM},
     * nenhum clique é aceito.
     * @return quantidade de cliques exigida pelo modo
     */
    public int getQuantidadePontos() {
        return quantidadePontos;
    }
}
