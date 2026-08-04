import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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

    // vetores para guardar os pontos e as retas desenhados
    private PontoGr[] pontos = new PontoGr[100];
    private Reta[] retas = new Reta[50];
    private int nPontos = 0;
    private int nRetas = 0;

    // modo reta ativo ou nao
    private boolean modoReta = false;
    // contador de pontos clicados no modo reta (para formar pares)
    private int nPontosModoReta = 0;

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
     * setModoReta - define se o modo reta esta ativo ou nao
     *
     * @param modoReta boolean true para ativar o modo reta
     */
    public void setModoReta(boolean modoReta){
        this.modoReta = modoReta;
        // ao ativar o modo reta, reinicia a contagem de pares
        nPontosModoReta = 0;
    }

    /**
     * getModoReta - retorna se o modo reta esta ativo ou nao
     *
     * @return boolean true se o modo reta esta ativo
     */
    public boolean getModoReta(){
        return this.modoReta;
    }

    /**
     * paintComponent - metodo para desenhar
     *
     * @param g A parameter
     */
    public void paintComponent(Graphics g) {   
        desenharRetas(g);
        desenharPontos(g);
    }

    /**
     * desenharPontos - desenha todos os pontos clicados na tela
     *
     * @param g Graphics - para desenhar
     */
    private void desenharPontos(Graphics g){
        for (int i = 0; i < nPontos; i++) {
            pontos[i].desenharPonto(g);
        }
    }

    /**
     * desenharRetas - desenha todas as retas formadas pelos pares de pontos
     *
     * @param g Graphics - para desenhar
     */
    private void desenharRetas(Graphics g){
        for (int i = 0; i < nRetas; i++) {
            FiguraPontos.desenharReta(g, retas[i]);
        }
    }

    // Capturando os Eventos com o mouse
    /**
     * Method mousePressed
     *
     * @param e MouseEvent - click do mouse
     */
    public void mousePressed(MouseEvent e) { 
        xMouse = e.getX();
        yMouse = e.getY();

        // cria um novo ponto na posicao do clique com cor aleatoria
        Color cor = new Color((int) (Math.random() * 256),  
                (int) (Math.random() * 256),  
                (int) (Math.random() * 256));
        PontoGr ponto = new PontoGr(xMouse, yMouse, cor, "p" + nPontos, 10);
        pontos[nPontos] = ponto;
        nPontos++;

        // a cada dois pontos clicados no modo reta, cria uma reta entre eles
        if (modoReta) {
            nPontosModoReta++;
            if (nPontosModoReta % 2 == 0) {
                Reta reta = new Reta(pontos[nPontos - 2], pontos[nPontos - 1]);
                retas[nRetas] = reta;
                nRetas++;
            }
        }

        this.msg.setText("Ponto: (" + xMouse + ", " + yMouse + ")");
        repaint();
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
    }
}