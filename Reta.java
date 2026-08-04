
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
        setP1(new Ponto(x1, y1));
        setP2(new Ponto(x2, y2));
    }
    
    public Reta(double x1, double y1, double x2, double y2) {
        setP1(new Ponto(x1, y1));
        setP2(new Ponto(x2, y2));
    }
    
    public Reta(Ponto p1, Ponto p2) {
        setP1(p1);
        setP2(p2);
    }
    
    public Reta (Reta r){
        setP1(r.getP1());
        setP2(r.getP2());
    }
    
    public void setP1(Ponto p){
        this.p1 = p;
    }
    
    public void setP2(Ponto p){
        this.p2 = p;
    }
    
    public Ponto getP1(){
        return this.p1;
    }
    
    public Ponto getP2(){
        return this.p2;
    }
    public double calcularM(){
        // m = (y2-y1)/(x2-x1)
        double m = (getP2().getY() - getP1().getY())/(getP2().getX() - getP1().getX());
        return m;
    }
     public double calcularB(){
        //b = y1 - mx1
        double b = getP1().getY() - calcularM()*getP1().getX();
        return b;
    }
    
    
    /**
     * Method toString
     *
     * @return The return value
     */
    public String toString(){
        String s = "P1: " + getP1().toString() + " P2: " + getP2().toString();
        s = s + "\nEq. da reta: y = " + calcularM() + "*x + " + calcularB();
        return s;
    }
   
}
