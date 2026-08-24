package reta;

import java.awt.Color;

/** Cor e espessura compartilhadas pelos primitivos graficos. */
public class EstiloReta {
    /** Estilo padrão preto com espessura de um pixel. */
    public static final EstiloReta PADRAO = new EstiloReta(Color.BLACK, 1);

    private final Color cor;
    private final int espessura;

    /** Cria um estilo com a cor e a espessura informadas.
     * @param cor cor do desenho
     * @param espessura espessura em pixels
     */
    public EstiloReta(Color cor, int espessura) {
        if (cor == null) {
            throw new IllegalArgumentException("A cor nao pode ser nula");
        }
        if (espessura < 1) {
            throw new IllegalArgumentException("A espessura deve ser maior ou igual a 1");
        }
        this.cor = cor;
        this.espessura = espessura;
    }

    /** Retorna a cor do estilo.
     * @return cor do desenho
     */
    public Color getCor() {
        return cor;
    }

    /** Retorna a espessura do estilo em pixels.
     * @return espessura do desenho
     */
    public int getEspessura() {
        return espessura;
    }
}
