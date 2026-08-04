/**
 * Representacao de ponto matematico
 * 
 * @author julio
 *
 */

public class Ponto {
    private double x;
    private double y;
    /**
     * Construtores
     */
    public Ponto() {
        setX(0);
        setY(0);
    }

    public Ponto(Ponto p) {
        setX(p.getX());
        setY(p.getY());
    }
    /**
     * @param x
     * @param y
     */
    public Ponto(double x, double y) {
        setX(x);
        setY(y);
    }
    
    
    /**
     * @return the x
     */
    public double getX() {
        return x;
    }
    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }
    /**
     * @return the y
     */
    public double getY() {
        return y;
    }
    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }
    
    /**
     * Calcula a distancia entre o ponto que vem como parametro
     * 
     * @param p ponto externo
     * 
     * @return d double valor da distancia
     * 
     */
    public double calcularDistancia(Ponto p) {
        
        double d = Math.sqrt(Math.pow(p.getY()-getY(), 2) + Math.pow(p.getX()-getX(), 2));
        
        return(d);
    
    }

    
    @Override
    public String toString() {
        return "Ponto [" + getX() + ", " + getY() +  "]";
    }
}
