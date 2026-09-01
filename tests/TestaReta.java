package tests;

import reta.Reta;

/**
 * Executa um exemplo simples de criação e exibição de uma reta.
 *
 * @author Raul Kolaric, Liam Lopes, Rafael Infantini, Guilherme Coutinho
 * @version 2026/08/24
 */
public class TestaReta
{
    /** Executa o exemplo de reta.
     * @param args argumentos da linha de comando, não utilizados
     */
    public static void main(String args[]) {
        Reta r = new Reta(10, 10, 20, 30);
        System.out.println("Reta: " + r);
    }
}
