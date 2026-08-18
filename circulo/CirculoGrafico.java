package circulo;

import java.awt.Color;
import java.awt.Graphics;

import ponto.Ponto;
import renderizacao.PrimitivoGrafico;
import renderizacao.RenderizadorPrimitivos;
import reta.EstiloReta;

/** Circulo matematico acrescido de estilo e algoritmo de rasterizacao. */
public class CirculoGrafico extends Circulo implements PrimitivoGrafico {
    private final EstiloReta estilo;
    private final AlgoritmoCirculo algoritmo;

    public CirculoGrafico(Ponto centro, Ponto pontoRaio, Color cor, int espessura,
                          AlgoritmoCirculo algoritmo) {
        this(centro, pontoRaio, new EstiloReta(cor, espessura), algoritmo);
    }

    public CirculoGrafico(Ponto centro, Ponto pontoRaio, EstiloReta estilo,
                          AlgoritmoCirculo algoritmo) {
        super(centro, pontoRaio);
        if (estilo == null || algoritmo == null) {
            throw new IllegalArgumentException("Estilo e algoritmo nao podem ser nulos");
        }
        this.estilo = estilo;
        this.algoritmo = algoritmo;
    }

    public AlgoritmoCirculo getAlgoritmo() {
        return algoritmo;
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
        renderizador.desenharCirculo(g, this);
    }
}
