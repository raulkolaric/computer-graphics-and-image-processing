package triangulo;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ponto.Ponto;
import renderizacao.PrimitivoGrafico;
import renderizacao.RenderizadorPrimitivos;
import reta.EstiloReta;
import reta.RetaGrafica;

/** Triangulo definido por tres vertices e composto por tres retas. */
public class Triangulo implements PrimitivoGrafico {
    private final EstiloReta estilo;
    private final List<RetaGrafica> retas;

    public Triangulo(Ponto p1, Ponto p2, Ponto p3, Color cor, int espessura) {
        this(p1, p2, p3, new EstiloReta(cor, espessura));
    }

    public Triangulo(Ponto p1, Ponto p2, Ponto p3, EstiloReta estilo) {
        if (p1 == null || p2 == null || p3 == null || estilo == null) {
            throw new IllegalArgumentException("Pontos e estilo nao podem ser nulos");
        }
        this.estilo = estilo;
        this.retas = Collections.unmodifiableList(Arrays.asList(
            new RetaGrafica(p1, p2, estilo),
            new RetaGrafica(p2, p3, estilo),
            new RetaGrafica(p3, p1, estilo)
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
