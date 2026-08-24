package triangulo;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ponto.Ponto;
import renderizacao.PrimitivoGrafico;
import renderizacao.RenderizadorPrimitivos;
import reta.EstiloReta;
import reta.RetaGrafica;

/**
 * Triângulo definido por três vértices e composto por três retas.
 *
 * @author Raul Kolaric, Liam Lopes, Rafael Infantini, Guilherme Coutinho
 * @version 2026/08/24
 */
public class Triangulo implements PrimitivoGrafico {
    private final EstiloReta estilo;
    private final List<RetaGrafica> retas;

    /** Cria um triângulo com cor e espessura.
     * @param p1 primeiro vértice
     * @param p2 segundo vértice
     * @param p3 terceiro vértice
     * @param cor cor das retas
     * @param espessura espessura das retas em pixels
     * @throws IllegalArgumentException se algum ponto ou a cor for nulo, ou se a espessura for menor que um
     */
    public Triangulo(Ponto p1, Ponto p2, Ponto p3, Color cor, int espessura) {
        this(p1, p2, p3, new EstiloReta(cor, espessura));
    }

    /** Cria um triângulo com o estilo informado.
     * @param p1 primeiro vértice
     * @param p2 segundo vértice
     * @param p3 terceiro vértice
     * @param estilo estilo das retas
     * @throws IllegalArgumentException se algum ponto ou o estilo for nulo
     */
    public Triangulo(Ponto p1, Ponto p2, Ponto p3, EstiloReta estilo) {
        if (p1 == null || p2 == null || p3 == null || estilo == null) {
            throw new IllegalArgumentException("Pontos e estilo nao podem ser nulos");
        }
        this.estilo = estilo;
        this.retas = Collections.unmodifiableList(Arrays.asList(
            new RetaGrafica(p1, p2, estilo),
            new RetaGrafica(p2, p3, estilo),
            new RetaGrafica(p3, p1, estilo)
        ));
    }

    /** Retorna uma visão não modificável com cópias defensivas das três retas.
     * @return lista não modificável de novas instâncias das retas do triângulo
     */
    public List<RetaGrafica> getRetas() {
        return copiarRetas();
    }

    /** @return cor das retas do triângulo */
    @Override
    public Color getCor() {
        return estilo.getCor();
    }

    /** @return espessura das retas, em pixels */
    @Override
    public int getEspessura() {
        return estilo.getEspessura();
    }

    /** Desenha as três retas do triângulo.
     * @param g superfície de desenho
     * @param renderizador renderizador a utilizar
     */
    @Override
    public void desenhar(Graphics g, RenderizadorPrimitivos renderizador) {
        for (RetaGrafica reta : retas) {
            reta.desenhar(g, renderizador);
        }
    }

    private List<RetaGrafica> copiarRetas() {
        RetaGrafica[] copias = new RetaGrafica[retas.size()];
        for (int i = 0; i < retas.size(); i++) {
            RetaGrafica reta = retas.get(i);
            copias[i] = new RetaGrafica(reta.getP1(), reta.getP2(), estilo);
        }
        return Collections.unmodifiableList(Arrays.asList(copias));
    }
}
