package renderizacao;

/** Multiplicação 3x3 adaptada do exemplo fornecido pelo professor. */
public class Matrizes {
    public static double[] multiplicarMatrizVetor(double[][] matriz, double[] vetor) {

        validar(matriz);
        if (vetor == null || vetor.length != 3) {
            throw new IllegalArgumentException("O vetor deve ter 3 elementos");
        }

        double[] resultado = new double[3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                resultado[i] += matriz[i][j] * vetor[j];
            }
        }

        return resultado;
    }

    public static double[][] multiplicarMatrizes3x3(double[][] ma, double[][] mb) {

        validar(ma);
        validar(mb);

        double[][] resultado = new double[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    resultado[i][j] += ma[i][k] * mb[k][j];
                }
            }
        }

        return resultado;
    }
    private static void validar(double[][] matriz) {
        if (matriz == null || matriz.length != 3) {
            throw new IllegalArgumentException("A matriz deve ser 3x3");
        }
        for (double[] linha : matriz) {
            if (linha == null || linha.length != 3) {
                throw new IllegalArgumentException("A matriz deve ser 3x3");
            }
        }
    }
}
