
import ui.Gui;

/**
 * Ponto de entrada da aplicação de desenho de primitivos gráficos.
 *
 * @author Raul Kolaric, Liam Lopes, Rafael Infantini, Guilherme Coutinho
 * @version 2026/08/24
 */
public class App {
    /** Inicia a aplicação gráfica na fila de eventos do Swing.
     * @param args argumentos da linha de comando, não utilizados
     */
    public static void main(String args[]) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Gui(900, 600); // define dimensao da janela (em pixels)
            }
        });
    }
    
}
