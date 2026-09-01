package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;

import circulo.AlgoritmoCirculo;

/** Janela de edicao dos primitivos graficos. */
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
    private final JButton jbSalvar = new JButton("Salvar projeto");
    private final JButton jbRecarregar = new JButton("Recarregar projeto");
    private final JSpinner jsEspessura = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    private final JComboBox<AlgoritmoCirculo> jcAlgoritmo =
        new JComboBox<AlgoritmoCirculo>(AlgoritmoCirculo.values());
    private final JToolBar barraComandos = new JToolBar();
    private final JToolBar barraProjeto = new JToolBar();
    private final PainelDesenho areaDesenho =
        new PainelDesenho(msg, TiposPrimitivos.NENHUM);
    private final Path arquivoProjeto = Path.of("projeto-anterior.json").toAbsolutePath();

    public Gui(int larg, int alt) {
        super("Primitivos Graficos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ButtonGroup modos = new ButtonGroup();
        modos.add(jtPonto);
        modos.add(jtReta);
        modos.add(jtRetangulo);
        modos.add(jtTriangulo);
        modos.add(jtCirculo);

        barraComandos.add(jtPonto);
        barraComandos.add(jtReta);
        barraComandos.add(jtRetangulo);
        barraComandos.add(jtTriangulo);
        barraComandos.add(jtCirculo);
        barraComandos.addSeparator();
        barraComandos.add(jbCor);
        barraComandos.add(new JLabel(" Espessura: "));
        barraComandos.add(jsEspessura);
        barraComandos.add(new JLabel(" Circulo: "));
        barraComandos.add(jcAlgoritmo);
        barraComandos.addSeparator();
        barraComandos.add(jbRedesenhar);
        barraComandos.add(jbLimpar);
        barraProjeto.add(jbSalvar);
        barraProjeto.add(jbRecarregar);

        JPanel barrasSuperiores = new JPanel(new GridLayout(2, 1));
        barrasSuperiores.add(barraComandos);
        barrasSuperiores.add(barraProjeto);
        add(barrasSuperiores, BorderLayout.NORTH);
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
        jbSalvar.addActionListener(eventos);
        jbRecarregar.addActionListener(eventos);
        jsEspessura.addChangeListener(event ->
            areaDesenho.setEspessuraAtual((Integer)jsEspessura.getValue()));
        jcAlgoritmo.addActionListener(event -> areaDesenho.setAlgoritmoCirculo(
            (AlgoritmoCirculo)jcAlgoritmo.getSelectedItem()));

        jcAlgoritmo.setSelectedItem(AlgoritmoCirculo.SIMETRIA_OCTANTES);
        jbCor.setBackground(Color.BLACK);
        atualizarDisponibilidadeRecarga();
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
                Color selecionada = JColorChooser.showDialog(
                    Gui.this, "Cor dos proximos primitivos", areaDesenho.getCorAtual());
                if (selecionada != null) {
                    areaDesenho.setCorAtual(selecionada);
                    jbCor.setBackground(selecionada);
                }
            } else if (origem == jbRedesenhar) {
                areaDesenho.redesenhar();
                msg.setText("Cena redesenhada a partir da estrutura de dados");
            } else if (origem == jbLimpar) {
                areaDesenho.limpar();
                msg.setText("Cena limpa");
            } else if (origem == jbSalvar) {
                try {
                    areaDesenho.salvarProjeto(arquivoProjeto);
                    atualizarDisponibilidadeRecarga();
                    msg.setText("Projeto salvo em " + arquivoProjeto.getFileName());
                } catch (IOException erro) {
                    mostrarErro("Nao foi possivel salvar o projeto", erro);
                }
            } else if (origem == jbRecarregar) {
                try {
                    areaDesenho.carregarProjeto(arquivoProjeto);
                    msg.setText("Projeto anterior recarregado");
                } catch (IOException erro) {
                    mostrarErro("Nao foi possivel recarregar o projeto", erro);
                }
            }
        }
    }

    private void atualizarDisponibilidadeRecarga() {
        jbRecarregar.setEnabled(Files.isRegularFile(arquivoProjeto));
    }

    private void mostrarErro(String titulo, IOException erro) {
        msg.setText(titulo + ": " + erro.getMessage());
        JOptionPane.showMessageDialog(this, erro.getMessage(), titulo, JOptionPane.ERROR_MESSAGE);
    }
}
