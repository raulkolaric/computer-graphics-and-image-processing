package renderizacao;

import java.util.List;
import circulo.CirculoGrafico;
import ponto.Ponto;
import retangulo.Retangulo;
import reta.EstiloReta;
import reta.RetaGrafica;
import triangulo.Triangulo;

/** Reflexão pela projeção ortogonal sobre uma reta infinita. */
public final class Espelhamento {
    private Espelhamento() { }

    /** Retorna uma cópia refletida; os extremos da reta devem ser distintos e finitos.
     * @param ponto ponto original
     * @param p1 primeiro ponto da reta
     * @param p2 segundo ponto da reta
     * @return ponto refletido, sem arredondamento
     * @throws IllegalArgumentException se os pontos forem nulos ou inválidos
     */
    public static Ponto refletir(Ponto ponto, Ponto p1, Ponto p2) {
        if (ponto == null || p1 == null || p2 == null) {
            throw new IllegalArgumentException("Pontos sao obrigatorios");
        }
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        double comprimento = Math.hypot(dx, dy);
        if (!Double.isFinite(comprimento) || comprimento == 0) {
            throw new IllegalArgumentException("Defina a reta com dois pontos distintos");
        }
        dx /= comprimento;
        dy /= comprimento;
        double projecao = (ponto.getX() - p1.getX()) * dx
            + (ponto.getY() - p1.getY()) * dy;
        double x = 2 * (p1.getX() + projecao * dx) - ponto.getX();
        double y = 2 * (p1.getY() + projecao * dy) - ponto.getY();
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("As coordenadas devem ser finitas");
        }
        return new Ponto(x, y);
    }

    /** Copia a geometria e o estilo de uma forma refletida.
     * @param original forma original
     * @param p1 primeiro ponto da reta
     * @param p2 segundo ponto da reta
     * @return forma refletida
     * @throws IllegalArgumentException se a forma não for suportada ou a reta for inválida
     */
    public static PrimitivoGrafico refletir(PrimitivoGrafico original, Ponto p1, Ponto p2) {
        if (original == null) throw new IllegalArgumentException("Selecione um primitivo");
        EstiloReta estilo = new EstiloReta(original.getCor(), original.getEspessura());
        if (original instanceof RetaGrafica) {
            RetaGrafica reta = (RetaGrafica)original;
            return new RetaGrafica(refletir(reta.getP1(), p1, p2),
                refletir(reta.getP2(), p1, p2), estilo);
        }
        if (original instanceof CirculoGrafico) {
            CirculoGrafico circulo = (CirculoGrafico)original;
            return new CirculoGrafico(refletir(circulo.getCentro(), p1, p2),
                refletir(circulo.getPontoRaio(), p1, p2), estilo, circulo.getAlgoritmo());
        }
        if (original instanceof Triangulo) {
            List<Ponto> v = ((Triangulo)original).getVertices();
            return new Triangulo(refletir(v.get(0), p1, p2), refletir(v.get(1), p1, p2),
                refletir(v.get(2), p1, p2), estilo);
        }
        if (original instanceof Retangulo) {
            List<Ponto> v = ((Retangulo)original).getVertices();
            return new Retangulo(refletir(v.get(0), p1, p2), refletir(v.get(1), p1, p2),
                refletir(v.get(2), p1, p2), refletir(v.get(3), p1, p2), estilo);
        }
        throw new IllegalArgumentException("Primitivo nao suportado");
    }
}
