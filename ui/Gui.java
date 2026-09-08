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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.BoxLayout;

import circulo.AlgoritmoCirculo;

/**
 * Janela de edicao dos primitivos graficos.
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

    /** Cria e exibe a janela da aplicação.
     * @param larg largura da janela em pixels
     * @param alt altura da janela em pixels
     */
    public Gui(int larg, int alt) {
        super("Primitivos Graficos | Paint RGB");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        barraEstilo.add(new JLabel(" Espessura: "));
        barraEstilo.add(jsEspessura);
        barraEstilo.add(new JLabel(" Circulo: "));
        barraEstilo.add(jcAlgoritmo);

        barraCena.add(new JLabel(" Redesenhar: "));
        barraCena.add(jcFiltroRedesenho);
        barraCena.add(jbRedesenhar);
        barraCena.add(jbLimpar);

        barraComandos.setFloatable(false);
        barraEstilo.setFloatable(false);
        barraCena.setFloatable(false);
        jbLimpar.setText("Limpar tela");
        jbLimpar.setToolTipText("Oculta os desenhos. Use Redesenhar para restaura-los.");
        for (JToggleButton botao : new JToggleButton[] {
                jtPonto, jtReta, jtRetangulo, jtTriangulo, jtCirculo }) {
            botao.setPreferredSize(new java.awt.Dimension(90, 36));
        }
        JPanel ferramentas = new JPanel(new BorderLayout(8, 4));
        ferramentas.setBorder(BorderFactory.createTitledBorder("Ferramentas e formas"));
        ferramentas.add(barraComandos, BorderLayout.CENTER);
        ferramentas.add(barraEstilo, BorderLayout.SOUTH);

        JPanel menu = new JPanel(new BorderLayout(10, 6));
        menu.setBackground(new Color(242, 246, 250));
        menu.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        menu.add(ferramentas, BorderLayout.CENTER);
        menu.add(criarCores(), BorderLayout.EAST);
        menu.add(barraCena, BorderLayout.SOUTH);
        add(menu, BorderLayout.NORTH);

        JPanel bancada = new JPanel(new BorderLayout());
        bancada.setBackground(new Color(205, 215, 226));
        bancada.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        bancada.add(areaDesenho, BorderLayout.CENTER);
        add(bancada, BorderLayout.CENTER);
        msg.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        add(msg, BorderLayout.SOUTH);
        Eventos eventos = new Eventos();
        jtPonto.addActionListener(eventos);
        jtReta.addActionListener(eventos);
        jtRetangulo.addActionListener(eventos);
        jtTriangulo.addActionListener(eventos);
        jtCirculo.addActionListener(eventos);
        jbRedesenhar.addActionListener(eventos);
        jbLimpar.addActionListener(eventos);
        jsEspessura.addChangeListener(event ->
            areaDesenho.setEspessuraAtual((Integer)jsEspessura.getValue()));
        jcAlgoritmo.addActionListener(event -> areaDesenho.setAlgoritmoCirculo(
            (AlgoritmoCirculo)jcAlgoritmo.getSelectedItem()));

        jcAlgoritmo.setSelectedItem(AlgoritmoCirculo.SIMETRIA_OCTANTES);
        jtPonto.setSelected(true);
        areaDesenho.setTipo(TiposPrimitivos.PONTO);
        setMinimumSize(new java.awt.Dimension(1000, 520));
        setSize(Math.max(larg, 1000), Math.max(alt, 520));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private final JSpinner[] rgb = {
        new JSpinner(new SpinnerNumberModel(0, 0, 255, 1)),
        new JSpinner(new SpinnerNumberModel(0, 0, 255, 1)),
        new JSpinner(new SpinnerNumberModel(0, 0, 255, 1))
    };
    private final JLabel amostra = new JLabel("  #000000  ", JLabel.CENTER);
    private boolean sincronizandoCor;

    private JPanel criarCores() {
        JPanel cores = new JPanel(new BorderLayout(8, 6));
        cores.setBorder(BorderFactory.createTitledBorder("Cores"));
        JPanel paleta = new JPanel(new java.awt.GridLayout(2, 10, 3, 3));
        int[] valores = {
            0x000000, 0x7F7F7F, 0x880015, 0xED1C24, 0xFF7F27,
            0xFFF200, 0x22B14C, 0x00A2E8, 0x3F48CC, 0xA349A4,
            0xFFFFFF, 0xC3C3C3, 0xB97A57, 0xFFAEC9, 0xFFC90E,
            0xEFE4B0, 0xB5E61D, 0x99D9EA, 0x7092BE, 0xC8BFE7
        };
        for (int valor : valores) {
            final Color cor = new Color(valor);
            JButton botao = new JButton();
            botao.setPreferredSize(new java.awt.Dimension(24, 24));
            botao.setBackground(cor);
            botao.setOpaque(true);
            botao.setContentAreaFilled(true);
            botao.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            String nome = String.format("#%06X", valor);
            botao.setToolTipText(nome);
            botao.getAccessibleContext().setAccessibleName("Selecionar cor " + nome);
            botao.addActionListener(event -> selecionarCor(cor));
            paleta.add(botao);
        }
        JPanel controles = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 0));
        String[] nomes = {"R", "G", "B"};
        for (int i = 0; i < rgb.length; i++) {
            JLabel rotulo = new JLabel(nomes[i]);
            rotulo.setLabelFor(rgb[i]);
            rgb[i].setPreferredSize(new java.awt.Dimension(58, 26));
            rgb[i].getAccessibleContext().setAccessibleName(nomes[i] + " de 0 a 255");
            // Commit valido a cada digitacao, sem exigir Enter antes de desenhar.
            javax.swing.JFormattedTextField campo =
                ((JSpinner.DefaultEditor)rgb[i].getEditor()).getTextField();
            ((javax.swing.text.DefaultFormatter)campo.getFormatter()).setCommitsOnValidEdit(true);
            rgb[i].addChangeListener(event -> {
                if (!sincronizandoCor) {
                    selecionarCor(new Color((Integer)rgb[0].getValue(),
                        (Integer)rgb[1].getValue(), (Integer)rgb[2].getValue()));
                }
            });
            controles.add(rotulo);
            controles.add(rgb[i]);
        }
        amostra.setOpaque(true);
        amostra.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        amostra.setPreferredSize(new java.awt.Dimension(100, 28));
        JPanel atual = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 0));
        atual.add(new JLabel("Cor atual"));
        atual.add(amostra);
        cores.add(paleta, BorderLayout.NORTH);
        cores.add(controles, BorderLayout.CENTER);
        cores.add(atual, BorderLayout.SOUTH);
        selecionarCor(Color.BLACK);
        return cores;
    }

    private void selecionarCor(Color cor) {
        sincronizandoCor = true;
        try {
            rgb[0].setValue(cor.getRed());
            rgb[1].setValue(cor.getGreen());
            rgb[2].setValue(cor.getBlue());
        } finally {
            sincronizandoCor = false;
        }
        areaDesenho.setCorAtual(cor);
        amostra.setBackground(cor);
        amostra.setForeground((299 * cor.getRed() + 587 * cor.getGreen()
            + 114 * cor.getBlue()) > 128000 ? Color.BLACK : Color.WHITE);
        amostra.setText(String.format("#%02X%02X%02X",
            cor.getRed(), cor.getGreen(), cor.getBlue()));
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

