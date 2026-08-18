package renderizacao;

import java.awt.Graphics;

import circulo.CirculoGrafico;
import reta.RetaGrafica;

/**
 * Estrategia de renderizacao. Uma implementacao futura pode delegar para
 * Graphics.drawLine/drawOval sem alterar os primitivos ou o painel.
 */
public interface RenderizadorPrimitivos {
    void desenharReta(Graphics g, RetaGrafica reta);

    void desenharCirculo(Graphics g, CirculoGrafico circulo);
}
