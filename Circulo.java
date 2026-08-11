/**
 * Circulo definido por um ponto central e um ponto sobre a circunferencia.
 *
 * @author Julio
 * @version 20260811
 */
public class Circulo {
    private Ponto centro;
    private Ponto pontoRaio;

    /**
     * Constroi um circulo a partir do centro e de um ponto que define o raio.
     *
     * @param centro ponto central do circulo
     * @param pontoRaio ponto sobre a circunferencia
     */
    public Circulo(Ponto centro, Ponto pontoRaio) {
        this.centro = centro;
        this.pontoRaio = pontoRaio;
    }

    public Ponto getCentro() {
        return centro;
    }

    public void setCentro(Ponto centro) {
        this.centro = centro;
    }

    public Ponto getPontoRaio() {
        return pontoRaio;
    }

    public void setPontoRaio(Ponto pontoRaio) {
        this.pontoRaio = pontoRaio;
    }

    /**
     * Retorna a distancia entre o centro e o ponto que define o raio.
     *
     * @return raio do circulo
     */
    public double getRaio() {
        return centro.calcularDistancia(pontoRaio);
    }
}
