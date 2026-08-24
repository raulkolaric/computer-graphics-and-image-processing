package ponto;

/**
 * Representa um ponto no plano cartesiano.
 * 
 * @author Raul Kolaric, Liam Lopes, Rafael Infantini, Guilherme Coutinho
 * @version 2026/08/24
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
    
    
    /** Retorna a coordenada horizontal.
     * @return coordenada horizontal
     */
    public double getX() {
        return x;
    }
    /** Define a coordenada horizontal.
     * @param x nova coordenada horizontal
     */
    public void setX(double x) {
        this.x = x;
    }
    /** Retorna a coordenada vertical.
     * @return coordenada vertical
     */
    public double getY() {
        return y;
    }
    /** Define a coordenada vertical.
     * @param y nova coordenada vertical
     */
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

    
    /**
     * Retorna as coordenadas do ponto em formato textual.
     *
     * @return coordenadas no formato {@code (x, y)}
     */
    @Override
    public String toString() {
        return "Ponto [" + getX() + ", " + getY() +  "]";
    }
}
