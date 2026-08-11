import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Retangulo definido por dois cantos opostos e composto por quatro retas. */
public class Retangulo implements PrimitivoGrafico {
    private final EstiloReta estilo;
    private final List<RetaGrafica> retas;

    public Retangulo(Ponto canto1, Ponto canto2, Color cor, int espessura) {
        this(canto1, canto2, new EstiloReta(cor, espessura));
    }

    public Retangulo(Ponto canto1, Ponto canto2, EstiloReta estilo) {
        if (canto1 == null || canto2 == null || estilo == null) {
            throw new IllegalArgumentException("Pontos e estilo nao podem ser nulos");
        }
        this.estilo = estilo;
        Ponto superiorEsquerdo = new Ponto(canto1.getX(), canto1.getY());
        Ponto superiorDireito = new Ponto(canto2.getX(), canto1.getY());
        Ponto inferiorDireito = new Ponto(canto2.getX(), canto2.getY());
        Ponto inferiorEsquerdo = new Ponto(canto1.getX(), canto2.getY());
        this.retas = Collections.unmodifiableList(Arrays.asList(
            new RetaGrafica(superiorEsquerdo, superiorDireito, estilo),
            new RetaGrafica(superiorDireito, inferiorDireito, estilo),
            new RetaGrafica(inferiorDireito, inferiorEsquerdo, estilo),
            new RetaGrafica(inferiorEsquerdo, superiorEsquerdo, estilo)
        ));
    }

    public List<RetaGrafica> getRetas() {
        return copiarRetas();
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
        for (RetaGrafica reta : retas) {
            reta.desenhar(g, renderizador);
        }
    }

    private List<RetaGrafica> copiarRetas() {
        RetaGrafica[] copias = new RetaGrafica[retas.size()];
        for (int i = 0; i < retas.size(); i++) {
            RetaGrafica reta = retas.get(i);
            copias[i] = new RetaGrafica(reta.getP1(), reta.getP2(), estilo);
        }
        return Collections.unmodifiableList(Arrays.asList(copias));
    }
}
