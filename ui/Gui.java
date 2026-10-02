package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.BoxLayout;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import circulo.AlgoritmoCirculo;
import persistencia.NomesProjeto;

/**
 * Janela de edição, seleção, exclusão e persistência dos primitivos gráficos.
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
    private final JToggleButton jtSelecao = new JToggleButton("Selecionar");
    private final JToggleButton jtEspelhar = new JToggleButton("Espelhar");
    private final JButton jbEspelharSelecionado = new JButton("Espelhar selecionado");
    private final JButton jbCor = new JButton("Cor");
    private final JButton jbRedesenhar = new JButton("Redesenhar");
    private final JButton jbLimpar = new JButton("Limpar");
    private final JButton jbExcluir = new JButton("Excluir selecionado");
    private final JButton jbSalvar = new JButton("Salvar projeto");
    private final JButton jbSalvarComo = new JButton("Salvar como");
    private final JButton jbRecarregar = new JButton("Abrir projeto");
    private final JSpinner jsEspessura = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    private final JComboBox<AlgoritmoCirculo> jcAlgoritmo =
        new JComboBox<AlgoritmoCirculo>(AlgoritmoCirculo.values());
    private final JComboBox<Object> jcFiltroRedesenho = new JComboBox<Object>(new Object[] {
        "Todos", TiposPrimitivos.PONTO, TiposPrimitivos.RETA,
        TiposPrimitivos.RETANGULO, TiposPrimitivos.TRIANGULO, TiposPrimitivos.CIRCULO });
    private final JToolBar barraComandos = new JToolBar();
    private final JToolBar barraEstilo = new JToolBar();
    private final JToolBar barraCena = new JToolBar();
    private final JToolBar barraArquivo = new JToolBar();
    private final PainelDesenho areaDesenho =
        new PainelDesenho(msg, TiposPrimitivos.NENHUM);
    private Path arquivoProjeto;

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
        barraArquivo.setFloatable(false);

        ButtonGroup modos = new ButtonGroup();
        modos.add(jtPonto);
        modos.add(jtReta);
        modos.add(jtRetangulo);
        modos.add(jtTriangulo);
        modos.add(jtCirculo);
        modos.add(jtSelecao);

        barraComandos.add(jtPonto);
        barraComandos.add(Box.createHorizontalStrut(4));
        barraComandos.add(jtReta);
        barraComandos.add(Box.createHorizontalStrut(4));
        barraComandos.add(jtRetangulo);
        barraComandos.add(Box.createHorizontalStrut(4));
        barraComandos.add(jtTriangulo);
        barraComandos.add(Box.createHorizontalStrut(4));
        barraComandos.add(jtCirculo);
        barraComandos.add(Box.createHorizontalStrut(4));
        barraComandos.add(jtSelecao);
        barraComandos.add(Box.createHorizontalStrut(4));
        barraComandos.add(jtEspelhar);
        barraComandos.add(jbEspelharSelecionado);
        for (JToggleButton botao : new JToggleButton[] {
                jtPonto, jtReta, jtRetangulo, jtTriangulo, jtCirculo, jtSelecao, jtEspelhar }) {
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
        barraCena.add(jbExcluir);
        barraArquivo.add(jbSalvar);
        barraArquivo.add(jbSalvarComo);
        barraArquivo.add(jbRecarregar);

        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.add(barraComandos);
        menu.add(barraEstilo);
        menu.add(barraCena);
        menu.add(barraArquivo);
        add(menu, BorderLayout.NORTH);
        add(areaDesenho, BorderLayout.CENTER);
        add(msg, BorderLayout.SOUTH);

        Eventos eventos = new Eventos();
        jtPonto.addActionListener(eventos);
        jtReta.addActionListener(eventos);
        jtRetangulo.addActionListener(eventos);
        jtTriangulo.addActionListener(eventos);
        jtCirculo.addActionListener(eventos);
        jtSelecao.addActionListener(eventos);
        jtEspelhar.addActionListener(eventos);
        jbEspelharSelecionado.addActionListener(eventos);
        jbCor.addActionListener(eventos);
        jbRedesenhar.addActionListener(eventos);
        jbLimpar.addActionListener(eventos);
        jbExcluir.addActionListener(eventos);
        jbSalvar.addActionListener(eventos);
        jbSalvarComo.addActionListener(eventos);
        jbRecarregar.addActionListener(eventos);
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
            } else if (origem == jtSelecao) {
                areaDesenho.setTipo(TiposPrimitivos.SELECAO);
            } else if (origem == jtEspelhar) {
                areaDesenho.setEspelhamento(jtEspelhar.isSelected());
            } else if (origem == jbEspelharSelecionado) {
                areaDesenho.espelharSelecionado();
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
            } else if (origem == jbExcluir) {
                msg.setText(areaDesenho.excluirSelecionado()
                    ? "Primitivo excluido" : "Selecione um primitivo para excluir");
            } else if (origem == jbSalvar || origem == jbSalvarComo) {
                Path destino = selecionarArquivo(true, origem == jbSalvarComo);
                if (destino == null) return;
                try {
                    Files.createDirectories(destino.getParent());
                    PainelDesenho.ResultadoSalvamento resultado = areaDesenho.salvarProjeto(destino);
                    arquivoProjeto = destino;
                    if (resultado.getErroImagem() == null) {
                        msg.setText("JSON e JPEG salvos: " + arquivoProjeto.getFileName());
                    } else {
                        mostrarErro("JSON salvo; JPEG falhou. Uma imagem anterior pode estar desatualizada",
                            resultado.getErroImagem());
                    }
                } catch (IOException | IllegalArgumentException erro) {
                    mostrarErro("Nao foi possivel salvar o projeto", erro);
                }
            } else if (origem == jbRecarregar) {
                Path origemProjeto = selecionarArquivo(false, false);
                if (origemProjeto == null) return;
                try {
                    areaDesenho.carregarProjeto(origemProjeto);
                    arquivoProjeto = origemProjeto;
                    msg.setText("Projeto aberto: " + arquivoProjeto.getFileName());
                } catch (IOException | IllegalArgumentException erro) {
                    mostrarErro("Nao foi possivel abrir o projeto", erro);
                }
            }
        }
    }

    private Path selecionarArquivo(boolean salvar, boolean como) {
        Path pasta = arquivoProjeto == null ? Path.of("saves").toAbsolutePath() : arquivoProjeto.getParent();
        JFileChooser seletor = new JFileChooser(pasta.toFile());
        seletor.setFileFilter(new FileNameExtensionFilter("Projetos JSON (*.json)", "json"));
        if (salvar && (como || arquivoProjeto == null)) {
            sugerirNome(seletor, pasta);
            seletor.addPropertyChangeListener(JFileChooser.DIRECTORY_CHANGED_PROPERTY, event -> {
                if (seletor.getCurrentDirectory() != null)
                    sugerirNome(seletor, seletor.getCurrentDirectory().toPath());
            });
        } else if (arquivoProjeto != null) {
            seletor.setSelectedFile(arquivoProjeto.toFile());
        }
        int resultado = salvar ? seletor.showSaveDialog(this) : seletor.showOpenDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) return null;
        Path arquivo = seletor.getSelectedFile().toPath().toAbsolutePath();
        if (salvar) {
            arquivo = NomesProjeto.json(arquivo);
            java.util.List<Path> conflitos = NomesProjeto.conflitos(arquivo);
            if (!conflitos.isEmpty() && JOptionPane.showConfirmDialog(this,
                    "Substituir os seguintes destinos?\n" + conflitos.stream()
                        .map(Path::toString).collect(java.util.stream.Collectors.joining("\n")),
                    "Salvar projeto", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return null;
        }
        return arquivo;
    }

    private void sugerirNome(JFileChooser seletor, Path pasta) {
        try {
            seletor.setSelectedFile(NomesProjeto.proximo(pasta).toFile());
        } catch (IOException erro) {
            msg.setText("Nao foi possivel sugerir um numero: " + erro.getMessage() + "; digite um nome");
            seletor.setSelectedFile(null);
        }
    }

    private void mostrarErro(String titulo, Exception erro) {
        msg.setText(titulo + ": " + erro.getMessage());
        JOptionPane.showMessageDialog(this, erro.getMessage(), titulo, JOptionPane.ERROR_MESSAGE);
    }
}
