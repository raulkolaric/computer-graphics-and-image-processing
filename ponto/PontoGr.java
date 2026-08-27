package ponto;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Ponto com cor de desenho, rótulo e diâmetro.
 *
 * @author Raul Kolaric, Liam Lopes, Rafael Infantini, Guilherme Coutinho
 * @version 2026/08/24
 */
public class PontoGr extends Ponto {
    Color corPto = Color.BLACK; // Cor do ponto.
    String nomePto = ""; // Rótulo do ponto.
    Color corNomePto  = Color.BLACK; // Cor do rótulo.
    int diametro = 1; // Diâmetro do ponto em pixels.
 
    /**
     * Cria um ponto gráfico nas coordenadas informadas.
     *
     * @param x coordenada X do ponto
     * @param y coordenada Y do ponto
     */
    PontoGr(int x, int y){
        super((double)x, (double)y);
        setCorPto(Color.black);     
        setCorNomePto(Color.black);     
        setNomePto("");     
    }

    /**
     * Cria um ponto gráfico com a cor informada.
     *
     * @param x coordenada X do ponto
     * @param y coordenada Y do ponto
     * @param cor cor do ponto
     */
    PontoGr(int x, int y, Color cor){
        super((double)x, (double)y);
        setCorPto(cor);     
        setCorNomePto(Color.black);     
        setNomePto("");     
    }

    /**
     * Cria um ponto gráfico com cor e diâmetro.
     * 
     * @param x coordenada horizontal
     * @param y coordenada vertical
     * @param corPonto cor do ponto
     * @param diametro diâmetro do ponto em pixels
     */
    public PontoGr(int x, int y, Color corPonto, int diametro){
        this(x, y, corPonto);
        setDiametro(diametro);
    }

    /**
     * Cria um ponto gráfico com cor, rótulo e diâmetro.
     * 
     * @param x coordenada horizontal
     * @param y coordenada vertical
     * @param corPonto cor do ponto
     * @param nomePonto rótulo do ponto
     * @param diametro diâmetro do ponto em pixels
     */
    public PontoGr(int x, int y, Color corPonto, String nomePonto, int diametro){
        this(x, y, corPonto, diametro);
        setNomePto(nomePonto);
    }
    /**
     * Cria um ponto gráfico com rótulo.
     *
     * @param x coordenada X do ponto
     * @param y coordenada Y do ponto
     * @param cor cor do ponto
     * @param str nome do ponto
     */
    PontoGr(int x, int y, Color cor, String str){
        super((double)x, (double)y);
        setCorPto(cor);     
        setCorNomePto(Color.black);     
        setNomePto(str);     
    }

    /**
     * Cria uma cópia do ponto gráfico com outra cor.
     *
     * @param p2d ponto gráfico a copiar
     * @param cor nova cor do ponto
     */
    PontoGr(PontoGr p2d, Color cor){
        super(p2d);     
        setCorPto(cor);     
        setCorNomePto(Color.black);     
        setNomePto("");     
    }

    /**
     * Cria um ponto gráfico na origem.
     */
    PontoGr(){
        super();     
        setCorPto(Color.black);     
        setCorNomePto(Color.black);     
        setNomePto("");     
    }


    /**
     * Retorna a cor do ponto.
     *
     * @return cor do ponto
     */
    public Color getCorPto() {
        return corPto;
    }

    /**
     * Define a cor do ponto.
     *
     * @param corPto nova cor do ponto
     */
    public void setCorPto(Color corPto) {
        this.corPto = corPto;
    }

    /**
     * Retorna o rótulo do ponto.
     *
     * @return rótulo do ponto
     */
    public String getNomePto() {
        return nomePto;
    }

    /**
     * Define o rótulo do ponto.
     *
     * @param nomePto novo rótulo
     */
    public void setNomePto(String nomePto) {
        this.nomePto = nomePto;
    }

    /**
     * Retorna a cor do rótulo.
     *
     * @return cor do rótulo
     */
    public Color getCorNomePto() {
        return corNomePto;
    }

    /**
     * Define a cor do rótulo.
     *
     * @param corNomePto nova cor do rótulo
     */
    public void setCorNomePto(Color corNomePto) {
        this.corNomePto = corNomePto;
    }

    /**
     * Retorna o diâmetro em pixels.
     *
     * @return diâmetro do ponto
     */
    public int getDiametro() {
        return diametro;
    }

    /**
     * Define o diâmetro em pixels.
     *
     * @param diametro novo diâmetro
     */
    public void setDiametro(int diametro) {
        this.diametro = diametro;
    }

    /**
     * Desenha o ponto e seu rótulo.
     * 
     * @param g superfície de desenho
     */
    public void desenharPonto(Graphics g){
        // Desenha o ponto como um círculo preenchido.
        g.setColor(getCorPto());
        g.fillOval((int)getX() -(getDiametro()/2), (int)getY() - (getDiametro()/2), getDiametro(), getDiametro());

        // Posiciona o rótulo à direita do ponto.
        g.setColor(getCorNomePto());
        g.drawString(getNomePto(), (int)getX() + getDiametro(), (int)getY());
    }
}
