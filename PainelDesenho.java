import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JLabel;

/**
 * Painel para desenhar primitivos graficos
 * 
 * @author Julio 
 * @version 20260803
 */
public class PainelDesenho extends JPanel implements MouseListener, MouseMotionListener {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JLabel msg;
    TiposPrimitivos tipo;
    int divisoes;
    int xMouse, yMouse;
    boolean primeiraVez = true;
    int nPto = 0;

    /**
     * COnstrutor para objetos da classe PainelDesenho
     */
    public PainelDesenho(JLabel msg, TiposPrimitivos tipo) {
        this.tipo = tipo;
        this.msg = msg;
        //       this.setBackground(Color.black);
        this.addMouseListener(this); 
        this.addMouseMotionListener(this);

    }
    public void setTipo(TiposPrimitivos tipo){
        this.tipo = tipo;
    }

    public TiposPrimitivos getTipo(){
        return this.tipo;
    }

    /**
     * paintComponent - metodo para desenhar
     *
     * @param g A parameter
     */
    public void paintComponent(Graphics g) {   
        if (! primeiraVez) {
            FiguraPontos.desenharPonto(g, xMouse, yMouse, "p"+nPto, 10);
            //FiguraPontos.desenharPontosAleatorios(g, 200, 10);
        }
    }

    // Capturando os Eventos com o mouse
    /**
     * Method mousePressed
     *
     * @param e MouseEvent - click do mouse
     */
    public void mousePressed(MouseEvent e) { 
        primeiraVez = false;
        xMouse = e.getX();
        yMouse = e.getY();
        Graphics g = getGraphics();  
        paint(g); // Aciona paintComponent
        nPto++;

    }     

    public void mouseReleased(MouseEvent e) { 
    }           

    public void mouseClicked(MouseEvent e) {
        this.msg.setText("CLICOU: " + e.getButton());
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    /**
     * mouseMoved evento de movimentação do mouse. Mostra posicao do mouse no painel
     *
     * @param e A parameter
     */
    public void mouseMoved(MouseEvent e) {
        this.msg.setText("("+e.getX() + ", " + e.getY() + ")");
        //System.out.println("("+e.getX() + ", " + e.getY() + ")");
    }
}
