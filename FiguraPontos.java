import java.awt.*;

/**
 * Contem metodos para desenhar o ponto.
 * 
 * @author Julio
 * @version 20260803
 */
public class FiguraPontos {
    /**
     * desenharPonto - desenha ponto na posicao x,y
     * @param g Graphics - contem funcoes graficas de biblioteca
     * @param x int coordenada x do ponto
     * @param y int coordenada Y do ponto
     * @param nome String nome do ponto
     * @param diametro int diametro do ponto
     */
    public static void desenharPonto(Graphics g, int x, int y, String nome, int diametro){
            Color cor = new Color((int) (Math.random() * 256),  
                    (int) (Math.random() * 256),  
                    (int) (Math.random() * 256));
            PontoGr p = new PontoGr(x, y, cor, nome, diametro);
            p.desenharPonto(g);
    }

   /**
    * desenharPontosAleatorios - desenha varios pontos em posicoes aleatorias e cores aleatorias
    *
    * @param g Graphics - contem funcoes graficas de biblioteca
    * @param qtde int numero de pontos a serem desenhados
    * @param diametro dimensao do ponto
    */
   public static void desenharPontosAleatorios(Graphics g, int qtde, int diametro){

        for(int i=0; i < qtde; i++) {
            int x = (int) (Math.random() * 701); // 701 e 601 largura e altura da janela (em pixels)
            int y = (int) (Math.random() * 601);

            // R, G e B aleatorio
            Color cor = new Color((int) (Math.random() * 256),  
                    (int) (Math.random() * 256),  
                    (int) (Math.random() * 256));
            PontoGr p = new PontoGr(x, y, cor, diametro);
            p.desenharPonto(g);
        }
    }
    
}
