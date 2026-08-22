package ponto;

/**
 * Representa um ponto no plano cartesiano.
 * 
 * @author julio
 *
 */

public class Ponto {
    private double x;
    private double y;
    /** Cria o ponto na origem. */
    public Ponto() {
        setX(0);
        setY(0);
    }

    /** Cria uma copia do ponto informado.
     * @param p ponto a copiar
     */
    public Ponto(Ponto p) {
        setX(p.getX());
        setY(p.getY());
    }
    /** Cria um ponto com as coordenadas informadas.
     * @param x coordenada horizontal
     * @param y coordenada vertical
     */
    public Ponto(double x, double y) {
        setX(x);
        setY(y);
    }
    
    
    /** @return coordenada horizontal */
    public double getX() {
        return x;
    }
    /** @param x nova coordenada horizontal */
    public void setX(double x) {
        this.x = x;
    }
    /** @return coordenada vertical */
    public double getY() {
        return y;
    }
    /** @param y nova coordenada vertical */
    public void setY(double y) {
        this.y = y;
    }
    
    /**
     * Calcula a distancia euclidiana até outro ponto.
     * 
     * @param p ponto de destino
     * @return distância entre os pontos
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
