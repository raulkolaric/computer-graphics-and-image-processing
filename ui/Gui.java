package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.BoxLayout;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import circulo.AlgoritmoCirculo;

/**
 * Janela de edição dos primitivos gráficos.
 *
 * @author Raul Kolaric, Liam Lopes, Rafael Infantini, Guilherme Coutinho
 * @version 2026/08/24
 */
public class Gui extends JFrame {
    private static final long serialVersionUID = 1L;

    private final JLabel msg = new JLabel("Selecione um primitivo");
    private final JToggleButton jtPonto = new JToggleButton("Ponto");
    private final JToggleButton jtReta = new JToggleButton("Reta");
    private final JToggleButton jtRetangulo = new JToggleButton("Retangulo");
    private final JToggleButton jtTriangulo = new JToggleButton("Triangulo");
    private final JToggleButton jtCirculo = new JToggleButton("Circulo");
    private final JButton jbCor = new JButton("Cor");
    private final JButton jbRedesenhar = new JButton("Redesenhar");
    private final JButton jbLimpar = new JButton("Limpar");
    private final JSpinner jsEspessura = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    private final JComboBox<AlgoritmoCirculo> jcAlgoritmo =
        new JComboBox<AlgoritmoCirculo>(AlgoritmoCirculo.values());
    private final JComboBox<Object> jcFiltroRedesenho = new JComboBox<Object>(new Object[] {
        "Todos", TiposPrimitivos.PONTO, TiposPrimitivos.RETA,
        TiposPrimitivos.RETANGULO, TiposPrimitivos.TRIANGULO, TiposPrimitivos.CIRCULO });
    private final JToolBar barraComandos = new JToolBar();
    private final JToolBar barraEstilo = new JToolBar();
    private final JToolBar barraCena = new JToolBar();
    private final PainelDesenho areaDesenho =
        new PainelDesenho(msg, TiposPrimitivos.NENHUM);

    /**
     * Cria e exibe a janela da aplicação.
     *
     * @param larg largura da janela em pixels
     * @param alt altura da janela em pixels
     */
    public Gui(int larg, int alt) {
        super("Primitivos Graficos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        barraComandos.setFloatable(false);
        barraEstilo.setFloatable(false);
        barraCena.setFloatable(false);

        ButtonGroup modos = new ButtonGroup();
        modos.add(jtPonto);
        modos.add(jtReta);
        modos.add(jtRetangulo);
        modos.add(jtTriangulo);
        modos.add(jtCirculo);

        barraComandos.add(jtPonto);
        barraComandos.add(Box.createHorizontalStrut(4));
        barraComandos.add(jtReta);
        barraComandos.add(Box.createHorizontalStrut(4));
        barraComandos.add(jtRetangulo);
        barraComandos.add(Box.createHorizontalStrut(4));
        barraComandos.add(jtTriangulo);
        barraComandos.add(Box.createHorizontalStrut(4));
        barraComandos.add(jtCirculo);
        for (JToggleButton botao : new JToggleButton[] {
                jtPonto, jtReta, jtRetangulo, jtTriangulo, jtCirculo }) {
            botao.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            botao.setBorderPainted(true);
            botao.addItemListener(event -> botao.setBorder(BorderFactory.createLineBorder(
                event.getStateChange() == ItemEvent.SELECTED ? Color.BLUE : Color.GRAY,
                event.getStateChange() == ItemEvent.SELECTED ? 2 : 1)));
        }
        barraEstilo.add(jbCor);
        barraEstilo.add(new JLabel(" Espessura: "));
        barraEstilo.add(jsEspessura);
        barraEstilo.add(new JLabel(" Circulo: "));
        barraEstilo.add(jcAlgoritmo);

        barraCena.add(new JLabel(" Redesenhar: "));
        barraCena.add(jcFiltroRedesenho);
        barraCena.add(jbRedesenhar);
        barraCena.add(jbLimpar);

        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.add(barraComandos);
        menu.add(barraEstilo);
        menu.add(barraCena);
        add(menu, BorderLayout.NORTH);
        add(areaDesenho, BorderLayout.CENTER);
        add(msg, BorderLayout.SOUTH);

        Eventos eventos = new Eventos();
        jtPonto.addActionListener(eventos);
        jtReta.addActionListener(eventos);
        jtRetangulo.addActionListener(eventos);
        jtTriangulo.addActionListener(eventos);
        jtCirculo.addActionListener(eventos);
        jbCor.addActionListener(eventos);
        jbRedesenhar.addActionListener(eventos);
        jbLimpar.addActionListener(eventos);
        jsEspessura.addChangeListener(event ->
            areaDesenho.setEspessuraAtual((Integer)jsEspessura.getValue()));
        jcAlgoritmo.addActionListener(event -> areaDesenho.setAlgoritmoCirculo(
            (AlgoritmoCirculo)jcAlgoritmo.getSelectedItem()));

        jcAlgoritmo.setSelectedItem(AlgoritmoCirculo.SIMETRIA_OCTANTES);
        jtPonto.setSelected(true);
        areaDesenho.setTipo(TiposPrimitivos.PONTO);
        jbCor.setBackground(Color.BLACK);
        setSize(larg, alt);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class Eventos implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object origem = event.getSource();
            if (origem == jtPonto) {
                areaDesenho.setTipo(TiposPrimitivos.PONTO);
            } else if (origem == jtReta) {
                areaDesenho.setTipo(TiposPrimitivos.RETA);
            } else if (origem == jtRetangulo) {
                areaDesenho.setTipo(TiposPrimitivos.RETANGULO);
            } else if (origem == jtTriangulo) {
                areaDesenho.setTipo(TiposPrimitivos.TRIANGULO);
            } else if (origem == jtCirculo) {
                areaDesenho.setTipo(TiposPrimitivos.CIRCULO);
            } else if (origem == jbCor) {
                JColorChooser seletor = new JColorChooser(areaDesenho.getCorAtual());
                AbstractColorChooserPanel[] paineis = seletor.getChooserPanels();
                seletor.setChooserPanels(new AbstractColorChooserPanel[] { paineis[0] });
                seletor.setPreviewPanel(new JPanel());
                JDialog dialogo = JColorChooser.createDialog(Gui.this,
                    "Cor dos proximos primitivos", true, seletor, confirmacao -> {
                        Color selecionada = seletor.getColor();
                        areaDesenho.setCorAtual(selecionada);
                        jbCor.setBackground(selecionada);
                    }, null);
                dialogo.setVisible(true);
            } else if (origem == jbRedesenhar) {
                Object filtro = jcFiltroRedesenho.getSelectedItem();
                areaDesenho.redesenhar(filtro instanceof TiposPrimitivos
                    ? (TiposPrimitivos)filtro : null);
                msg.setText("Cena redesenhada a partir da estrutura de dados");
            } else if (origem == jbLimpar) {
                areaDesenho.limpar();
                msg.setText("Cena limpa");
            }
        }
    }
}
