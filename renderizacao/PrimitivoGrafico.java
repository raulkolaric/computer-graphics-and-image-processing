package renderizacao;

import java.awt.Color;
import java.awt.Graphics;

/** Contrato para objetos que podem ser armazenados e redesenhados. */
public interface PrimitivoGrafico {
    /** Desenha o primitivo.
     * @param g superfície de desenho
     * @param renderizador renderizador a utilizar
     */
    void desenhar(Graphics g, RenderizadorPrimitivos renderizador);

    /** @return cor usada no desenho */
    Color getCor();

    /** @return espessura usada no desenho, em pixels */
    int getEspessura();
}
