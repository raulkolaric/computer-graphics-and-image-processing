package circulo;

/** Algoritmos manuais disponiveis para rasterizar a circunferencia. */
public enum AlgoritmoCirculo {
    EQUACAO_REDUZIDA("Equacao reduzida"),
    PARAMETRICO("Seno e cosseno"),
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
