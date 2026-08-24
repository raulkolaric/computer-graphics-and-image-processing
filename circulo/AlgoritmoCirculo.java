package circulo;

/** Algoritmos manuais disponiveis para rasterizar a circunferencia. */
public enum AlgoritmoCirculo {
    /** Equação reduzida com raiz quadrada. */
    EQUACAO_REDUZIDA("Equacao reduzida"),
    /** Equações paramétricas com seno e cosseno. */
    PARAMETRICO("Seno e cosseno"),
    /** Algoritmo do ponto médio com oito reflexos. */
    SIMETRIA_OCTANTES("Simetria (8 octantes)");

    private final String descricao;

    AlgoritmoCirculo(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
