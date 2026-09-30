package renderizacao;

import ponto.Ponto;

/** Transformação composta adaptada do exemplo fornecido pelo professor.
 * Translada a reta à origem, gira até o eixo X, espelha Y e desfaz os passos.
 */
public class TransfGeometricaComposta {
    /** Espelha uma coordenada em relação à reta infinita definida por p1 e p2.
     * @param x coordenada horizontal
     * @param y coordenada vertical
     * @param p1 primeiro ponto da reta
     * @param p2 segundo ponto da reta
     * @return vetor homogêneo com as coordenadas refletidas
     * @throws IllegalArgumentException se as coordenadas não forem finitas ou a reta for degenerada
     */
    public static double [] espelhamentoRetaQquer(double x, double y, Ponto p1, Ponto p2){
        if (p1 == null || p2 == null || !Double.isFinite(x) || !Double.isFinite(y)
                || !Double.isFinite(p1.getX()) || !Double.isFinite(p1.getY())
                || !Double.isFinite(p2.getX()) || !Double.isFinite(p2.getY())) {
            throw new IllegalArgumentException("As coordenadas devem ser finitas");
        }
        double dx = p2.getX() - p1.getX(), dy = p2.getY() - p1.getY();
        if (!Double.isFinite(dx) || !Double.isFinite(dy) || (dx == 0 && dy == 0)) {
            throw new IllegalArgumentException("Defina a reta com dois pontos distintos");
        }
        double[] resultado;
        double xy[] = {x, y, 1};
        double x1 = p1.getX();
        double y1 = p1.getY();
        double theta = Math.atan2(dy, dx);
        double[][] trans_1 = {
                {1, 0, -x1},
                {0, 1, -y1},
                {0, 0, 1}
            };
        double[][] rot_2 = {
                {Math.cos(-theta), -Math.sin(-theta), 0},
                {Math.sin(-theta), Math.cos(-theta), 0},
                {0, 0, 1}
            };
        double[][] espX_3 = {
                {1, 0, 0},
                {0, -1, 0},
                {0, 0, 1}
            };
        double[][] rot_4 = {
                {Math.cos(theta), -Math.sin(theta), 0},
                {Math.sin(theta), Math.cos(theta), 0},
                {0, 0, 1}
            };
        double[][] trans_5 = {
                {1, 0, x1},
                {0, 1, y1},
                {0, 0, 1}
            };

        resultado = Matrizes.multiplicarMatrizVetor(Matrizes.multiplicarMatrizes3x3(Matrizes.multiplicarMatrizes3x3(Matrizes.multiplicarMatrizes3x3(Matrizes.multiplicarMatrizes3x3(trans_5, rot_4), espX_3), rot_2), trans_1), xy);

        if (!Double.isFinite(resultado[0]) || !Double.isFinite(resultado[1])) {
            throw new IllegalArgumentException("Coordenada refletida fora do limite numerico");
        }
        return resultado;
    }
}
