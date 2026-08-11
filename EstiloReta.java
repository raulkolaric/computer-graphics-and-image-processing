import java.awt.Color;

/** Cor e espessura compartilhadas pelos primitivos graficos. */
public class EstiloReta {
    public static final EstiloReta PADRAO = new EstiloReta(Color.BLACK, 1);

    private final Color cor;
    private final int espessura;

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

    public Color getCor() {
        return cor;
    }

    public int getEspessura() {
        return espessura;
    }
}
