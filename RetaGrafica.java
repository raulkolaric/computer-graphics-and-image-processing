import java.awt.Color;
import java.awt.Graphics;

/** Segmento de reta matematico acrescido de cor e espessura. */
public class RetaGrafica extends Reta implements PrimitivoGrafico {
    private final EstiloReta estilo;

    public RetaGrafica(Ponto p1, Ponto p2) {
        this(p1, p2, EstiloReta.PADRAO);
    }

    public RetaGrafica(Ponto p1, Ponto p2, Color cor, int espessura) {
        this(p1, p2, new EstiloReta(cor, espessura));
    }

    public RetaGrafica(Ponto p1, Ponto p2, EstiloReta estilo) {
        super(p1, p2);
        if (estilo == null) {
            throw new IllegalArgumentException("O estilo nao pode ser nulo");
        }
        this.estilo = estilo;
    }

    public EstiloReta getEstilo() {
        return estilo;
    }

    @Override
    public Color getCor() {
        return estilo.getCor();
    }

    @Override
    public int getEspessura() {
        return estilo.getEspessura();
    }

    @Override
    public void desenhar(Graphics g, RenderizadorPrimitivos renderizador) {
        renderizador.desenharReta(g, this);
    }
}
