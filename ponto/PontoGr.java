package ponto;

import java.awt.Color;
import java.awt.Graphics;

public class PontoGr extends Ponto {
    Color corPto = Color.BLACK; // cor do ponto
    String nomePto = ""; // nome do ponto
    Color corNomePto  = Color.BLACK; // cor do nome (string) do ponto  
    int diametro = 1; // diametro do ponto, default = 1
 
    // Construtores
    /**
     * PontoGr Constructor
     *
     * @param x int coordenada x do ponto
     * @param y int coordenada Y do ponto
     */
    PontoGr(int x, int y){
        super((double)x, (double)y);
        setCorPto(Color.black);     
        setCorNomePto(Color.black);     
        setNomePto("");     
    }

    /**
     * PontoGr Constructor
     *
     * @param x int coordenada x do ponto
     * @param y int coordenada Y do ponto
     * @param cor int cor do ponto
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
     * @param corPonto corReta do ponto a ser construido
     * @param diametro diametro do ponto
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
     * @param corPonto corReta do ponto a ser construido
     * @param nomePonto nome do ponto
     * @param diametro diametro do ponto
     */
    public PontoGr(int x, int y, Color corPonto, String nomePonto, int diametro){
        this(x, y, corPonto, diametro);
        setNomePto(nomePonto);
    }
    /**
     * PontoGr Constructor
     *
     * @param x int coordenada x do ponto
     * @param y int coordenada Y do ponto
     * @param cor int cor do ponto
     * @param str String nome do ponto
     */
    PontoGr(int x, int y, Color cor, String str){
        super((double)x, (double)y);
        setCorPto(cor);     
        setCorNomePto(Color.black);     
        setNomePto(str);     
    }

    /**
     * PontoGr Constructor
     *
     * @param p2d PontoGr
     * @param cor int cor do ponto
     */
    PontoGr(PontoGr p2d, Color cor){
        super(p2d);     
        setCorPto(cor);     
        setCorNomePto(Color.black);     
        setNomePto("");     
    }

    /**
     * PontoGr Constructor
     *
     */
    PontoGr(){
        super();     
        setCorPto(Color.black);     
        setCorNomePto(Color.black);     
        setNomePto("");     
    }


    /**
     * @return the corPto
     */
    public Color getCorPto() {
        return corPto;
    }

    /**
     * @param corPto the corPto to set
     */
    public void setCorPto(Color corPto) {
        this.corPto = corPto;
    }

    /**
     * @return the nomePto
     */
    public String getNomePto() {
        return nomePto;
    }

    /**
     * @param nomePto the nomePto to set
     */
    public void setNomePto(String nomePto) {
        this.nomePto = nomePto;
    }

    /**
     * @return the corNomePto
     */
    public Color getCorNomePto() {
        return corNomePto;
    }

    /**
     * @param corNomePto the corNomePto to set
     */
    public void setCorNomePto(Color corNomePto) {
        this.corNomePto = corNomePto;
    }

    /**
     * @return the diametro
     */
    public int getDiametro() {
        return diametro;
    }

    /**
     * @param diametro the diametro to set
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
