package ponto;

/**
 * Representa um ponto mutável no plano cartesiano bidimensional.
 * 
 * @author Raul Kolaric, Liam Lopes, Rafael Infantini, Guilherme Coutinho
 * @version 2026/08/24
 */
public class Ponto {
    private double x;
    private double y;

    /** Cria um ponto na origem, com coordenadas {@code (0, 0)}. */
    public Ponto() {
        setX(0);
        setY(0);
    }

    /**
     * Cria uma cópia do ponto informado.
     *
     * @param p ponto a copiar
     */
    public Ponto(Ponto p) {
        setX(p.getX());
        setY(p.getY());
    }

    /**
     * Cria um ponto com as coordenadas informadas.
     *
     * @param x coordenada no eixo horizontal
     * @param y coordenada no eixo vertical
     */
    public Ponto(double x, double y) {
        setX(x);
        setY(y);
    }

    /**
     * Retorna a coordenada horizontal.
     *
     * @return coordenada horizontal deste ponto
     */
    public double getX() {
        return x;
    }

    /**
     * Define a coordenada horizontal.
     *
     * @param x nova coordenada horizontal
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Retorna a coordenada vertical.
     *
     * @return coordenada vertical deste ponto
     */
    public double getY() {
        return y;
    }

    /**
     * Define a coordenada vertical.
     *
     * @param y nova coordenada vertical
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Calcula a distância euclidiana deste ponto até o ponto informado.
     * 
     * @param p ponto de destino
     * @return distância cartesiana entre os dois pontos
     */
    public double calcularDistancia(Ponto p) {
        
        double d = Math.sqrt(Math.pow(p.getY()-getY(), 2) + Math.pow(p.getX()-getX(), 2));
        
        return(d);
    
    }

    
    /**
     * Retorna a representação textual deste ponto.
     *
     * @return texto no formato {@code Ponto [x, y]}
     */
    @Override
    public String toString() {
        return "Ponto [" + getX() + ", " + getY() +  "]";
    }
}
