package reta;

import java.awt.Color;
import java.awt.Graphics;

import ponto.Ponto;
import renderizacao.PrimitivoGrafico;
import renderizacao.RenderizadorPrimitivos;

/** Segmento de reta com cor e espessura para renderização. */
public class RetaGrafica extends Reta implements PrimitivoGrafico {
    private final EstiloReta estilo;

    /** Cria um segmento com o estilo padrão.
     * @param p1 primeiro extremo
     * @param p2 segundo extremo
     */
    public RetaGrafica(Ponto p1, Ponto p2) {
        this(p1, p2, EstiloReta.PADRAO);
    }

    /** Cria um segmento com cor e espessura.
     * @param p1 primeiro extremo
     * @param p2 segundo extremo
     * @param cor cor do segmento
     * @param espessura espessura em pixels
     */
    public RetaGrafica(Ponto p1, Ponto p2, Color cor, int espessura) {
        this(p1, p2, new EstiloReta(cor, espessura));
    }

    /** Cria um segmento com o estilo informado.
     * @param p1 primeiro extremo
     * @param p2 segundo extremo
     * @param estilo estilo de renderização
     */
    public RetaGrafica(Ponto p1, Ponto p2, EstiloReta estilo) {
        super(p1, p2);
        if (estilo == null) {
            throw new IllegalArgumentException("O estilo nao pode ser nulo");
        }
        this.estilo = estilo;
    }

    /** Retorna o estilo usado pelo segmento.
     * @return estilo usado pelo segmento
     */
    public EstiloReta getEstilo() {
        return estilo;
    }

    /** @return cor do segmento */
    @Override
    public Color getCor() {
        return estilo.getCor();
    }

    /** @return espessura do segmento em pixels */
    @Override
    public int getEspessura() {
        return estilo.getEspessura();
    }

    /** Desenha o segmento usando o renderizador informado.
     * @param g superfície de desenho
     * @param renderizador renderizador a utilizar
     */
    @Override
    public void desenhar(Graphics g, RenderizadorPrimitivos renderizador) {
        renderizador.desenharReta(g, this);
    }
}
