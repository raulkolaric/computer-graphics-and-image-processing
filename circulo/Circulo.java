package circulo;

import ponto.Ponto;

/**
 * Circulo definido por um ponto central e um ponto sobre a circunferencia.
 *
 * @author Raul Kolaric, Liam Lopes, Rafael Infantini, Guilherme Coutinho
 * @version 2026/08/24
 */
public class Circulo {
    private Ponto centro;
    private Ponto pontoRaio;

    /**
     * Constroi um circulo a partir do centro e de um ponto que define o raio.
     *
     * @param centro ponto central do circulo
     * @param pontoRaio ponto sobre a circunferencia
     * @throws IllegalArgumentException se algum ponto for nulo
     */
    public Circulo(Ponto centro, Ponto pontoRaio) {
        this.centro = copiarPonto(centro, "O centro");
        this.pontoRaio = copiarPonto(pontoRaio, "O ponto do raio");
    }

    /** Retorna uma cópia defensiva do centro do círculo.
     * @return nova instância com as coordenadas do centro
     */
    public Ponto getCentro() {
        return new Ponto(centro);
    }

    /** Define o centro do círculo.
     * @param centro novo centro
     * @throws IllegalArgumentException se o centro for nulo
     */
    public void setCentro(Ponto centro) {
        this.centro = copiarPonto(centro, "O centro");
    }

    /** Retorna uma cópia defensiva do ponto que define o raio.
     * @return nova instância com as coordenadas do ponto do raio
     */
    public Ponto getPontoRaio() {
        return new Ponto(pontoRaio);
    }

    /** Define o ponto sobre a circunferência.
     * @param pontoRaio novo ponto do raio
     * @throws IllegalArgumentException se o ponto for nulo
     */
    public void setPontoRaio(Ponto pontoRaio) {
        this.pontoRaio = copiarPonto(pontoRaio, "O ponto do raio");
    }

    /**
     * Retorna a distancia entre o centro e o ponto que define o raio.
     *
     * @return raio do circulo
     */
    public double getRaio() {
        return centro.calcularDistancia(pontoRaio);
    }

    private static Ponto copiarPonto(Ponto ponto, String nome) {
        if (ponto == null) {
            throw new IllegalArgumentException(nome + " nao pode ser nulo");
        }
        return new Ponto(ponto);
    }
}
