package renderizacao;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Contrato para objetos que podem ser armazenados e redesenhados.
 *
 * @author Raul Kolaric, Liam Lopes, Rafael Infantini, Guilherme Coutinho
 * @version 2026/08/24
 */
public interface PrimitivoGrafico {
    /** Desenha o primitivo.
     * @param g superfície de desenho
     * @param renderizador renderizador a utilizar
     */
    void desenhar(Graphics g, RenderizadorPrimitivos renderizador);

    /** Retorna a cor usada no desenho.
     * @return cor usada no desenho
     */
    Color getCor();

    /** Retorna a espessura usada no desenho.
     * @return espessura em pixels
     */
    int getEspessura();
}
