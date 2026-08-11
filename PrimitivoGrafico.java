import java.awt.Color;
import java.awt.Graphics;

/** Contrato comum para objetos que podem ser armazenados e redesenhados. */
public interface PrimitivoGrafico {
    void desenhar(Graphics g, RenderizadorPrimitivos renderizador);

    Color getCor();

    int getEspessura();
}
