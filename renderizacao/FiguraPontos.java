package renderizacao;

import java.awt.*;

import circulo.AlgoritmoCirculo;
import circulo.Circulo;
import circulo.CirculoGrafico;
import ponto.PontoGr;
import reta.EstiloReta;
import reta.Reta;
import reta.RetaGrafica;

/**
 * Fachada legada para desenhar pontos, retas e círculos.
 * 
 * @author Raul Kolaric, Liam Lopes, Rafael Infantini, Guilherme Coutinho
 * @version 2026/08/24
 */
public class FiguraPontos {
    private static RenderizadorPrimitivos renderizador = new RenderizadorManual();

    /**
     * Substitui o renderizador usado pelos métodos legados da fachada.
     *
     * @param novoRenderizador novo renderizador
     * @throws IllegalArgumentException se o renderizador for nulo
     */
    public static void setRenderizador(RenderizadorPrimitivos novoRenderizador) {
        if (novoRenderizador == null) {
            throw new IllegalArgumentException("O renderizador nao pode ser nulo");
        }
        renderizador = novoRenderizador;
    }
    /**
     * Desenha um ponto com cor aleatória e o rótulo informado.
     *
     * @param g superfície de desenho
     * @param x coordenada horizontal do ponto
     * @param y coordenada vertical do ponto
     * @param nome rótulo do ponto
     * @param diametro diâmetro do ponto em pixels
     */
    public static void desenharPonto(Graphics g, int x, int y, String nome, int diametro){
            Color cor = new Color((int) (Math.random() * 256),  
                    (int) (Math.random() * 256),  
                    (int) (Math.random() * 256));
            PontoGr p = new PontoGr(x, y, cor, nome, diametro);
            p.desenharPonto(g);
    }

    /**
     * Desenha pontos com posições e cores aleatórias.
     *
     * @param g superfície de desenho
     * @param qtde quantidade de pontos
     * @param diametro diâmetro de cada ponto em pixels
     */
   public static void desenharPontosAleatorios(Graphics g, int qtde, int diametro){

        for(int i=0; i < qtde; i++) {
            int x = (int) (Math.random() * 701); // Limites da janela em pixels.
            int y = (int) (Math.random() * 601);

            // Sorteia separadamente os componentes RGB.
            Color cor = new Color((int) (Math.random() * 256),  
                    (int) (Math.random() * 256),  
                    (int) (Math.random() * 256));
            PontoGr p = new PontoGr(x, y, cor, diametro);
            p.desenharPonto(g);
        }
    }

    /**
     * Desenha a reta com o renderizador configurado.
     *
     * @param g superfície de desenho
     * @param reta reta a desenhar
     */
    public static void desenharReta(Graphics g, Reta reta){
        RetaGrafica grafica;
        if (reta instanceof RetaGrafica) {
            grafica = (RetaGrafica)reta;
        } else {
            grafica = new RetaGrafica(reta.getP1(), reta.getP2());
        }
        renderizador.desenharReta(g, grafica);
    }

    /**
     * Desenha o círculo com o renderizador configurado.
     *
     * @param g superfície de desenho
     * @param circulo círculo a desenhar
     */
    public static void desenharCirculo(Graphics g, Circulo circulo){
        CirculoGrafico grafico;
        if (circulo instanceof CirculoGrafico) {
            grafico = (CirculoGrafico)circulo;
        } else {
            grafico = new CirculoGrafico(circulo.getCentro(), circulo.getPontoRaio(),
                EstiloReta.PADRAO, AlgoritmoCirculo.SIMETRIA_OCTANTES);
        }
        renderizador.desenharCirculo(g, grafico);
    }
    
}
