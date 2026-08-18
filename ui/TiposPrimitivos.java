package ui;

/** Tipos de primitivos que podem ser criados na interface. */
public enum TiposPrimitivos {
    PONTO(1), RETA(2), RETANGULO(2), TRIANGULO(3), CIRCULO(2), NENHUM(0);

    private final int quantidadePontos;

    TiposPrimitivos(int quantidadePontos) {
        this.quantidadePontos = quantidadePontos;
    }

    public int getQuantidadePontos() {
        return quantidadePontos;
    }
}
