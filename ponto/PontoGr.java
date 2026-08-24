package ponto;

import java.awt.Color;
import java.awt.Graphics;

/** Ponto com cor de desenho, rótulo e diâmetro. */
public class PontoGr extends Ponto {
    Color corPto = Color.BLACK; // cor do ponto
    String nomePto = ""; // nome do ponto
    Color corNomePto  = Color.BLACK; // cor do nome (string) do ponto  
    int diametro = 1; // diâmetro do ponto, padrão = 1
 
    // Construtores
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
     * Constroi um ponto na posicao x, y e com os atributos
     * 
     * @param x coordenada x
     * @param y coordenada y
     * @param corPonto cor do ponto a ser construído
     * @param diametro diâmetro do ponto em pixels
     */
    public PontoGr(int x, int y, Color corPonto, int diametro){
        this(x, y, corPonto);
        setDiametro(diametro);
    }

    /**
     * Constroi um ponto na posicao x, y e com os atributos
     * 
     * @param x coordenada x
     * @param y coordenada y
     * @param corPonto cor do ponto a ser construído
     * @param nomePonto nome do ponto
     * @param diametro diametro do ponto
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


    /** Retorna a cor do ponto.
     * @return cor do ponto
     */
    public Color getCorPto() {
        return corPto;
    }

    /** Define a cor do ponto.
     * @param corPto nova cor do ponto
     */
    public void setCorPto(Color corPto) {
        this.corPto = corPto;
    }

    /** Retorna o rótulo do ponto.
     * @return rótulo do ponto
     */
    public String getNomePto() {
        return nomePto;
    }

    /** Define o rótulo do ponto.
     * @param nomePto novo rótulo
     */
    public void setNomePto(String nomePto) {
        this.nomePto = nomePto;
    }

    /** Retorna a cor do rótulo.
     * @return cor do rótulo
     */
    public Color getCorNomePto() {
        return corNomePto;
    }

    /** Define a cor do rótulo.
     * @param corNomePto nova cor do rótulo
     */
    public void setCorNomePto(Color corNomePto) {
        this.corNomePto = corNomePto;
    }

    /** Retorna o diâmetro em pixels.
     * @return diâmetro do ponto
     */
    public int getDiametro() {
        return diametro;
    }

    /** Define o diâmetro em pixels.
     * @param diametro novo diâmetro
     */
    public void setDiametro(int diametro) {
        this.diametro = diametro;
    }

    /**
     * desenha um ponto utilizando o oval 
     * 
     * @param g contexto grafico
     */
    public void desenharPonto(Graphics g){
        // desenha ponto como um oval
        g.setColor(getCorPto());
        g.fillOval((int)getX() -(getDiametro()/2), (int)getY() - (getDiametro()/2), getDiametro(), getDiametro());

        // desenha nome do ponto
        g.setColor(getCorNomePto());
        g.drawString(getNomePto(), (int)getX() + getDiametro(), (int)getY());
    }
}
