import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * TiposPrimitivos - constantes com os tipos de primitivos
 * 
 * @author Julio
 * @version 20260803
 */
class Gui extends JFrame {
    TiposPrimitivos tipo = TiposPrimitivos.NENHUM;

    // mensagens
    private JLabel msg = new JLabel("Msg: ");
    private JButton jbPonto = new JButton("Ponto");
    private JToggleButton jtModoReta = new JToggleButton("Modo Reta: OFF");

    // barra de menu
    private JToolBar barraComandos = new JToolBar();

    // Painel de desenho
    private PainelDesenho areaDesenho = new PainelDesenho(msg, tipo);

    /**
     * Gui - define janela de largura e altura
     *
     * @param larg int largura da janela
     * @param alt int altura da janela
     */
    public Gui(int larg, int alt) {
        /**
         * Definicoes de janela
         */
        super("Testa Primitivos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(larg, alt);
        setVisible(true);

        // Adicionando os componentes
        barraComandos.add(jbPonto);
        barraComandos.add(jtModoReta);
        add(barraComandos, BorderLayout.NORTH);                
        add(areaDesenho, BorderLayout.CENTER);                
        add(msg, BorderLayout.SOUTH);

        Eventos eventos = new Eventos();
        jbPonto.addActionListener(eventos);
        jtModoReta.addActionListener(eventos);
    }

    /**
     * Eventos - implementa os eventos da interface ActionListener
     * 
     */
    private class Eventos implements ActionListener{

        TiposPrimitivos tipo = TiposPrimitivos.RETA;

        /**
         * actionPerformed - executa evento do ActionListener
         *
         * @param event A parameter
         */
        public void actionPerformed(ActionEvent event) {            

            if (event.getSource() == jbPonto){
                tipo = TiposPrimitivos.PONTO;

            }     

            if (tipo == TiposPrimitivos.PONTO) {
                // Enviando a Forma a ser desenhada e a cor da linha
                areaDesenho.setTipo( tipo );
            }

            if (event.getSource() == jtModoReta){
                boolean ativo = jtModoReta.isSelected();
                areaDesenho.setModoReta(ativo);
                if (ativo) {
                    jtModoReta.setText("Modo Reta: ON");
                } else {
                    jtModoReta.setText("Modo Reta: OFF");
                }
            }
        }
    } 
}
