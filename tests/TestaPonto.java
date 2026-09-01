package tests;

import ponto.Ponto;

/**
 * Executa um exemplo simples de criação e uso de pontos.
 *
 * @author Raul Kolaric, Liam Lopes, Rafael Infantini, Guilherme Coutinho
 * @version 2026/08/24
 */
public class TestaPonto{
    /** Executa o exemplo de pontos.
     * @param args argumentos da linha de comando, não utilizados
     */
    public static void main(String args[]) {
        Ponto p1 = new Ponto();
        Ponto p2 = new Ponto(1, 1);
        Ponto p3 = new Ponto(2.0, 2.0);
        Ponto p4 = new Ponto(p3);
        System.out.println("p1 = " + p1);
        System.out.println("p2 = " + p2);
        System.out.println("p3 = " + p3);
        System.out.println("p4 = " + p4);
        System.out.println("distancia entre p2 e p3 = " + p2.calcularDistancia(p3));
    }
}
