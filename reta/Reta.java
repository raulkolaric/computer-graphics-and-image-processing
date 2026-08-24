package reta;

import ponto.Ponto;


/**
 * Reta matemática definida por dois pontos extremos.
 *
 * @author Julio
 * @version 12/08/2020
 */
public class Reta {
 
    // Atributos da reta
    private Ponto p1, p2;

    /** Cria uma reta a partir de coordenadas inteiras.
     * @param x1 coordenada horizontal do primeiro ponto
     * @param y1 coordenada vertical do primeiro ponto
     * @param x2 coordenada horizontal do segundo ponto
     * @param y2 coordenada vertical do segundo ponto
     */
    public Reta(int x1, int y1, int x2, int y2) {
        this.p1 = new Ponto(x1, y1);
        this.p2 = new Ponto(x2, y2);
    }
    
    /** Cria uma reta a partir de coordenadas reais.
     * @param x1 coordenada horizontal do primeiro ponto
     * @param y1 coordenada vertical do primeiro ponto
     * @param x2 coordenada horizontal do segundo ponto
     * @param y2 coordenada vertical do segundo ponto
     */
    public Reta(double x1, double y1, double x2, double y2) {
        this.p1 = new Ponto(x1, y1);
        this.p2 = new Ponto(x2, y2);
    }
    
    /** Cria uma reta copiando os dois pontos extremos.
     * @param p1 primeiro ponto
     * @param p2 segundo ponto
     */
    public Reta(Ponto p1, Ponto p2) {
        this.p1 = copiarPonto(p1, "P1");
        this.p2 = copiarPonto(p2, "P2");
    }
    
    /** Cria uma copia da reta informada.
     * @param r reta a copiar
     */
    public Reta (Reta r){
        if (r == null) {
            throw new IllegalArgumentException("A reta nao pode ser nula");
        }
        this.p1 = r.getP1();
        this.p2 = r.getP2();
    }
    
    /** Define o primeiro extremo da reta.
     * @param p novo primeiro extremo
     */
    public void setP1(Ponto p){
        this.p1 = copiarPonto(p, "P1");
    }
    
    /** Define o segundo extremo da reta.
     * @param p novo segundo extremo
     */
    public void setP2(Ponto p){
        this.p2 = copiarPonto(p, "P2");
    }
    
    /** Retorna uma cópia do primeiro extremo.
     * @return cópia do primeiro extremo
     */
    public Ponto getP1(){
        return new Ponto(this.p1);
    }
    
    /** Retorna uma cópia do segundo extremo.
     * @return cópia do segundo extremo
     */
    public Ponto getP2(){
        return new Ponto(this.p2);
    }

    private static Ponto copiarPonto(Ponto ponto, String nome) {
        if (ponto == null) {
            throw new IllegalArgumentException(nome + " nao pode ser nulo");
        }
        return new Ponto(ponto);
    }

    /** Verifica se a reta é vertical.
     * @return {@code true} quando os extremos têm a mesma coordenada X
     */
    public boolean isVertical(){
        return Double.compare(p1.getX(), p2.getX()) == 0;
    }

    /** Verifica se a reta é degenerada.
     * @return {@code true} quando os dois extremos coincidem
     */
    public boolean isDegenerada(){
        return isVertical() && Double.compare(p1.getY(), p2.getY()) == 0;
    }

    /** Calcula o coeficiente angular da reta.
     * @return coeficiente angular
     * @throws IllegalStateException se a reta for vertical
     */
    public double calcularM(){
        if (isVertical()) {
            throw new IllegalStateException("Reta vertical nao possui coeficiente angular finito");
        }
        // m = (y2-y1)/(x2-x1)
        double m = (p2.getY() - p1.getY())/(p2.getX() - p1.getX());
        return m;
    }
    /** Calcula o coeficiente linear na forma {@code y = mx + b}.
     * @return coeficiente linear
     * @throws IllegalStateException se a reta for vertical
     */
     public double calcularB(){
        if (isVertical()) {
            throw new IllegalStateException("Reta vertical nao possui coeficiente linear na forma y=mx+b");
        }
        //b = y1 - mx1
        double b = p1.getY() - calcularM()*p1.getX();
        return b;
    }
    
    
    /**
     * Retorna a representação textual da reta e de sua equação.
     * @return representação textual da reta
     */
    public String toString(){
        String s = "P1: " + getP1().toString() + " P2: " + getP2().toString();
        if (isVertical()) {
            s = s + "\nEq. da reta vertical: x = " + p1.getX();
        } else {
            s = s + "\nEq. da reta: y = " + calcularM() + "*x + " + calcularB();
        }
        return s;
    }
   
}
