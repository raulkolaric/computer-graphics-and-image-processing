package reta;

import ponto.Ponto;


/**
 * REta matematica.
 *
 * @author Julio
 * @version 12/08/2020
 */
public class Reta {
 
    // Atributos da reta
    private Ponto p1, p2;

    /**
     * Constructor for objects of class Reta
     */
    public Reta(int x1, int y1, int x2, int y2) {
        this.p1 = new Ponto(x1, y1);
        this.p2 = new Ponto(x2, y2);
    }
    
    public Reta(double x1, double y1, double x2, double y2) {
        this.p1 = new Ponto(x1, y1);
        this.p2 = new Ponto(x2, y2);
    }
    
    public Reta(Ponto p1, Ponto p2) {
        this.p1 = copiarPonto(p1, "P1");
        this.p2 = copiarPonto(p2, "P2");
    }
    
    public Reta (Reta r){
        if (r == null) {
            throw new IllegalArgumentException("A reta nao pode ser nula");
        }
        this.p1 = r.getP1();
        this.p2 = r.getP2();
    }
    
    public void setP1(Ponto p){
        this.p1 = copiarPonto(p, "P1");
    }
    
    public void setP2(Ponto p){
        this.p2 = copiarPonto(p, "P2");
    }
    
    public Ponto getP1(){
        return new Ponto(this.p1);
    }
    
    public Ponto getP2(){
        return new Ponto(this.p2);
    }

    private static Ponto copiarPonto(Ponto ponto, String nome) {
        if (ponto == null) {
            throw new IllegalArgumentException(nome + " nao pode ser nulo");
        }
        return new Ponto(ponto);
    }

    public boolean isVertical(){
        return Double.compare(p1.getX(), p2.getX()) == 0;
    }

    public boolean isDegenerada(){
        return isVertical() && Double.compare(p1.getY(), p2.getY()) == 0;
    }

    public double calcularM(){
        if (isVertical()) {
            throw new IllegalStateException("Reta vertical nao possui coeficiente angular finito");
        }
        // m = (y2-y1)/(x2-x1)
        double m = (p2.getY() - p1.getY())/(p2.getX() - p1.getX());
        return m;
    }
     public double calcularB(){
        if (isVertical()) {
            throw new IllegalStateException("Reta vertical nao possui coeficiente linear na forma y=mx+b");
        }
        //b = y1 - mx1
        double b = p1.getY() - calcularM()*p1.getX();
        return b;
    }
    
    
    /**
     * Method toString
     *
     * @return The return value
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
