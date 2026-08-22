package circulo;

import java.awt.Color;
import java.awt.Graphics;

import ponto.Ponto;
import renderizacao.PrimitivoGrafico;
import renderizacao.RenderizadorPrimitivos;
import reta.EstiloReta;

/** Círculo com estilo e algoritmo de rasterização. */
public class CirculoGrafico extends Circulo implements PrimitivoGrafico {
    private final EstiloReta estilo;
    private final AlgoritmoCirculo algoritmo;

    /** Cria um círculo gráfico com cor, espessura e algoritmo.
     * @param centro centro do círculo
     * @param pontoRaio ponto que define o raio
     * @param cor cor do círculo
     * @param espessura espessura em pixels
     * @param algoritmo algoritmo de rasterização
     */
    public CirculoGrafico(Ponto centro, Ponto pontoRaio, Color cor, int espessura,
                          AlgoritmoCirculo algoritmo) {
        this(centro, pontoRaio, new EstiloReta(cor, espessura), algoritmo);
    }

    /** Cria um círculo gráfico com estilo e algoritmo.
     * @param centro centro do círculo
     * @param pontoRaio ponto que define o raio
     * @param estilo estilo de renderização
     * @param algoritmo algoritmo de rasterização
     */
    public CirculoGrafico(Ponto centro, Ponto pontoRaio, EstiloReta estilo,
                          AlgoritmoCirculo algoritmo) {
        super(centro, pontoRaio);
        if (estilo == null || algoritmo == null) {
            throw new IllegalArgumentException("Estilo e algoritmo nao podem ser nulos");
        }
        this.estilo = estilo;
        this.algoritmo = algoritmo;
    }

    /** Retorna o algoritmo usado para rasterizar o círculo.
     * @return algoritmo de rasterização
     */
    public AlgoritmoCirculo getAlgoritmo() {
        return algoritmo;
    }

    /** @return cor do círculo */
    @Override
    public Color getCor() {
        return estilo.getCor();
    }

    /** @return espessura do círculo em pixels */
    @Override
    public int getEspessura() {
        return estilo.getEspessura();
    }

    /** Desenha o círculo usando o renderizador informado.
     * @param g superfície de desenho
     * @param renderizador renderizador a utilizar
     */
    @Override
    public void desenhar(Graphics g, RenderizadorPrimitivos renderizador) {
        renderizador.desenharCirculo(g, this);
    }
}
