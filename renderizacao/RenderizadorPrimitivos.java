package renderizacao;

import java.awt.Graphics;

import circulo.CirculoGrafico;
import reta.RetaGrafica;

/**
 * Estrategia de renderizacao. Uma implementacao futura pode delegar para
 * Graphics.drawLine/drawOval sem alterar os primitivos ou o painel.
 */
public interface RenderizadorPrimitivos {
    /** Renderiza uma reta gráfica.
     * @param g superfície de desenho
     * @param reta reta a renderizar
     */
    void desenharReta(Graphics g, RetaGrafica reta);

    /** Renderiza um círculo gráfico.
     * @param g superfície de desenho
     * @param circulo círculo a renderizar
     */
    void desenharCirculo(Graphics g, CirculoGrafico circulo);
}
