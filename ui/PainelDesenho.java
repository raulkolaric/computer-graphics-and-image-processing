package ui;

import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

import circulo.AlgoritmoCirculo;
import circulo.CirculoGrafico;
import ponto.Ponto;
import ponto.PontoGr;
import retangulo.Retangulo;
import renderizacao.PrimitivoGrafico;
import renderizacao.Espelhamento;
import renderizacao.RenderizadorManual;
import renderizacao.RenderizadorPrimitivos;
import reta.EstiloReta;
import reta.RetaGrafica;
import triangulo.Triangulo;
import persistencia.PersistenciaProjeto;
import persistencia.ExportacaoJPEG;
import persistencia.NomesProjeto;

/**
 * Painel que recebe pontos pelo mouse, armazena a cena, exibe prévias e
 * permite selecionar, excluir e redesenhar os elementos. As listas exibidas
 * referenciam objetos retidos; Limpar apenas os oculta. Salvar usa uma cópia
 * ordenada dessas listas, sem considerar o histórico de nomes de arquivo.
 *
 * @author Raul Kolaric, Liam Lopes, Rafael Infantini, Guilherme Coutinho
 * @version 2026/08/24
 */
public class PainelDesenho extends JPanel implements MouseListener, MouseMotionListener {
    private static final long serialVersionUID = 1L;
    private static final double MARGEM_SELECAO = 5.0;

    private final JLabel msg;
    private final List<PontoGr> pontos = new ArrayList<PontoGr>();
    private final List<PontoGr> pontosVisiveis = new ArrayList<PontoGr>();
    private final List<PrimitivoGrafico> primitivos = new ArrayList<PrimitivoGrafico>();
    private final List<PrimitivoGrafico> primitivosVisiveis = new ArrayList<PrimitivoGrafico>();
    private final List<Ponto> pontosPendentes = new ArrayList<Ponto>();

    private boolean espelhamentoAtivo;
    /** Controla a exibição do guia sem perder um eixo automático completo. */
    private boolean mostrarEixo;
    private transient Object fonteReflexao;
    private transient Ponto reflexaoP1, reflexaoPrevia;
    private Ponto eixoP1, eixoP2, previaEixo;
    private TiposPrimitivos tipo;
    private RenderizadorPrimitivos renderizador;
    private Ponto pontoPrevia;
    private PontoGr pontoSelecionado;
    private PrimitivoGrafico primitivoSelecionado;
    private Color corAtual = Color.BLACK;
    private int espessuraAtual = 1;
    private AlgoritmoCirculo algoritmoCirculo = AlgoritmoCirculo.SIMETRIA_OCTANTES;

    /** Cria um painel usando o renderizador manual padrão.
     * @param msg etiqueta usada para exibir mensagens
     * @param tipo tipo de primitivo inicialmente selecionado
     * @throws IllegalArgumentException se algum argumento for nulo
     */
    public PainelDesenho(JLabel msg, TiposPrimitivos tipo) {
        this(msg, tipo, new RenderizadorManual());
    }

    /** Cria um painel com o renderizador informado.
     * @param msg etiqueta usada para exibir mensagens
     * @param tipo tipo de primitivo inicialmente selecionado
     * @param renderizador renderizador dos primitivos
     * @throws IllegalArgumentException se algum argumento for nulo
     */
    public PainelDesenho(JLabel msg, TiposPrimitivos tipo,
                          RenderizadorPrimitivos renderizador) {
        if (msg == null || tipo == null || renderizador == null) {
            throw new IllegalArgumentException("Mensagem, tipo e renderizador sao obrigatorios");
        }
        this.tipo = tipo;
        this.msg = msg;
        this.renderizador = renderizador;
        setBackground(Color.WHITE);
        addMouseListener(this);
        addMouseMotionListener(this);
        registerKeyboardAction(event -> cancelarInteracao(),
            javax.swing.KeyStroke.getKeyStroke("ESCAPE"), WHEN_IN_FOCUSED_WINDOW);
    }

    /** Seleciona o modo e descarta pontos pendentes e a seleção atual.
     * @param tipo novo tipo de primitivo
     * @throws IllegalArgumentException se o tipo for nulo
     */
    public void setTipo(TiposPrimitivos tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo nao pode ser nulo");
        }
        this.tipo = tipo;
        cancelarReflexaoSelecionada();
        pontosPendentes.clear();
        pontoPrevia = null;
        limparSelecao();
        msg.setText(espelhamentoAtivo && eixoP2 == null
            ? "Espelhar: clique em p1 e p2 da reta, depois desenhe normalmente"
            : "Modo: " + tipo);
        repaint();
    }

    /** Ativa a cópia automática e solicita dois cliques para definir uma nova reta.
     * O tipo de primitivo escolhido permanece ativo. Desativar cancela a entrada
     * pendente e preserva as cópias já armazenadas.
     * @param ativo indica se novas formas devem ser espelhadas
     */
    public void setEspelhamento(boolean ativo) {
        cancelarReflexaoSelecionada();
        espelhamentoAtivo = ativo;
        mostrarEixo = ativo;
        eixoP1 = eixoP2 = previaEixo = null;
        pontosPendentes.clear();
        pontoPrevia = null;
        limparSelecao();
        msg.setText(ativo ? "Espelhar: clique em p1 e p2 da reta, depois desenhe normalmente"
            : "Espelhamento desativado");
        repaint();
    }

    /** Inicia uma cópia da seleção por dois cliques, sem alterar o eixo automático.
     * @return true se havia um objeto selecionado
     */
    public boolean espelharSelecionado() {
        if (!temSelecao()) {
            msg.setText("Selecione um ponto ou forma antes de espelhar");
            return false;
        }
        fonteReflexao = pontoSelecionado != null ? pontoSelecionado : primitivoSelecionado;
        reflexaoP1 = reflexaoPrevia = null;
        pontosPendentes.clear();
        pontoPrevia = null;
        msg.setText("Espelhar selecionado: clique em p1 da reta (Esc cancela)");
        repaint();
        return true;
    }

    private void cancelarReflexaoSelecionada() {
        fonteReflexao = null;
        reflexaoP1 = reflexaoPrevia = null;
    }

    private void cancelarInteracao() {
        cancelarReflexaoSelecionada();
        pontosPendentes.clear();
        pontoPrevia = null;
        if (eixoP2 == null) eixoP1 = previaEixo = null;
        limparSelecao();
        msg.setText("Entrada cancelada");
        repaint();
    }

    private void receberEixoSelecionado(Ponto ponto) {
        if (reflexaoP1 == null) {
            reflexaoP1 = reflexaoPrevia = ponto;
            msg.setText("Espelhar selecionado: clique em p2 da reta");
        } else {
            try {
                if (fonteReflexao instanceof PontoGr) {
                    PontoGr copia = espelharPonto((PontoGr)fonteReflexao, reflexaoP1, ponto);
                    pontos.add(copia);
                    pontosVisiveis.add(copia);
                } else {
                    PrimitivoGrafico copia = Espelhamento.refletir(
                        (PrimitivoGrafico)fonteReflexao, reflexaoP1, ponto);
                    RenderizadorManual.validar(copia);
                    primitivos.add(copia);
                    primitivosVisiveis.add(copia);
                }
                cancelarReflexaoSelecionada();
                limparSelecao();
                msg.setText("Copia espelhada criada");
            } catch (IllegalArgumentException erro) {
                msg.setText(erro.getMessage() + "; escolha outro p2 ou Esc");
            }
        }
        repaint();
    }

    /** Retorna o tipo de primitivo selecionado.
     * @return tipo selecionado
     */
    public TiposPrimitivos getTipo() {
        return tipo;
    }

    /** Ativa ou desativa o modo de criação de retas.
     * @param ativo {@code true} para ativar o modo
     */
    public void setModoReta(boolean ativo) {
        setTipo(ativo ? TiposPrimitivos.RETA : TiposPrimitivos.NENHUM);
    }

    /** Verifica se o modo de reta está ativo.
     * @return {@code true} se o modo de reta estiver ativo
     */
    public boolean getModoReta() {
        return tipo == TiposPrimitivos.RETA;
    }

    /** Ativa ou desativa o modo de criação de círculos.
     * @param ativo {@code true} para ativar o modo
     */
    public void setModoCirculo(boolean ativo) {
        setTipo(ativo ? TiposPrimitivos.CIRCULO : TiposPrimitivos.NENHUM);
    }

    /** Verifica se o modo de círculo está ativo.
     * @return {@code true} se o modo de círculo estiver ativo
     */
    public boolean getModoCirculo() {
        return tipo == TiposPrimitivos.CIRCULO;
    }

    /** Define a cor usada somente nos novos primitivos.
     * @param corAtual nova cor
     * @throws IllegalArgumentException se a cor for nula
     */
    public void setCorAtual(Color corAtual) {
        if (corAtual == null) {
            throw new IllegalArgumentException("A cor nao pode ser nula");
        }
        this.corAtual = corAtual;
    }

    /** Retorna a cor usada nos novos primitivos.
     * @return cor atual
     */
    public Color getCorAtual() {
        return corAtual;
    }

    /** Define a espessura usada somente nos novos primitivos.
     * @param espessuraAtual nova espessura em pixels
     * @throws IllegalArgumentException se a espessura for menor que um
     */
    public void setEspessuraAtual(int espessuraAtual) {
        if (espessuraAtual < 1) {
            throw new IllegalArgumentException("A espessura deve ser maior ou igual a 1");
        }
        this.espessuraAtual = espessuraAtual;
    }

    /** Retorna a espessura usada nos novos primitivos.
     * @return espessura atual
     */
    public int getEspessuraAtual() {
        return espessuraAtual;
    }

    /** Define o algoritmo usado somente nos novos círculos.
     * @param algoritmoCirculo novo algoritmo
     * @throws IllegalArgumentException se o algoritmo for nulo
     */
    public void setAlgoritmoCirculo(AlgoritmoCirculo algoritmoCirculo) {
        if (algoritmoCirculo == null) {
            throw new IllegalArgumentException("O algoritmo nao pode ser nulo");
        }
        this.algoritmoCirculo = algoritmoCirculo;
    }

    /** Retorna o algoritmo usado para novos círculos.
     * @return algoritmo atual
     */
    public AlgoritmoCirculo getAlgoritmoCirculo() {
        return algoritmoCirculo;
    }

    /** Substitui o renderizador e solicita uma nova pintura.
     * @param renderizador novo renderizador
     * @throws IllegalArgumentException se o renderizador for nulo
     */
    public void setRenderizador(RenderizadorPrimitivos renderizador) {
        if (renderizador == null) {
            throw new IllegalArgumentException("O renderizador nao pode ser nulo");
        }
        this.renderizador = renderizador;
        repaint();
    }

    /** Adiciona um primitivo à cena e sua cópia quando o espelhamento está ativo
     * com eixo definido. Valida a cópia antes de alterar a cena.
     * @param primitivo primitivo a adicionar
     * @throws IllegalArgumentException se o primitivo for nulo ou a cópia exceder os limites
     */
    public void adicionarPrimitivo(PrimitivoGrafico primitivo) {
        if (primitivo == null) {
            throw new IllegalArgumentException("O primitivo nao pode ser nulo");
        }
        PrimitivoGrafico copia = null;
        if (espelhamentoAtivo && eixoP2 != null) {
            copia = Espelhamento.refletir(primitivo, eixoP1, eixoP2);
            RenderizadorManual.validar(copia);
        }
        primitivos.add(primitivo);
        primitivosVisiveis.add(primitivo);
        if (copia != null) {
            primitivos.add(copia);
            primitivosVisiveis.add(copia);
        }
        repaint();
    }

    /** Retorna uma visão não modificável dos primitivos da cena.
     * A lista é uma cópia, mas seus elementos são as instâncias armazenadas.
     * @return cópia não modificável da lista de primitivos armazenados
     */
    public List<PrimitivoGrafico> getPrimitivos() {
        return Collections.unmodifiableList(new ArrayList<PrimitivoGrafico>(primitivos));
    }

    /** Retorna a quantidade de primitivos armazenados.
     * @return quantidade de primitivos
     */
    public int getQuantidadePrimitivos() {
        return primitivos.size();
    }

    /** Retorna a quantidade de pontos armazenados.
     * @return quantidade de pontos
     */
    public int getQuantidadePontos() {
        return pontos.size();
    }

    /** Grava JSON e JPEG dos mesmos objetos completos exibidos, na ordem atual.
     * Preserva a memória retida e qualquer construção incompleta.
     * A imagem RGB tem fundo branco, qualidade 0.95 e não inclui sobreposições
     * da interface. O JSON conserva geometria fora da área; o JPEG a recorta.
     * As duas gravações são independentes. Em falha do JPEG, o resultado informa
     * o erro e o JSON permanece salvo; uma imagem anterior pode estar desatualizada.
     * As coordenadas são normalizadas pela largura e pela altura do painel.
     * @param arquivo arquivo de destino
     * @return resultado do JPEG; retornar confirma que o JSON foi salvo
     * @throws IOException se o JSON não puder ser gravado
     * @throws IllegalArgumentException se o arquivo for nulo ou o painel não tiver dimensões válidas
     */
    public ResultadoSalvamento salvarProjeto(Path arquivo) throws IOException {
        if (arquivo == null) throw new IllegalArgumentException("O arquivo e obrigatorio");
        List<PontoGr> pontosArquivo = new ArrayList<PontoGr>(pontosVisiveis);
        List<PrimitivoGrafico> primitivosArquivo = new ArrayList<PrimitivoGrafico>(primitivosVisiveis);
        int largura = getWidth(), altura = getHeight();
        RenderizadorPrimitivos renderizadorArquivo = renderizador;
        PersistenciaProjeto.salvar(arquivo, pontosArquivo, primitivosArquivo, largura, altura);
        BufferedImage imagem = null;
        try {
            imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = imagem.createGraphics();
            try {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, largura, altura);
                desenharCena(g, pontosArquivo, primitivosArquivo, renderizadorArquivo);
            } finally {
                g.dispose();
            }
            ExportacaoJPEG.salvar(NomesProjeto.jpeg(arquivo), imagem);
            return new ResultadoSalvamento(null);
        } catch (IOException | RuntimeException erro) {
            return new ResultadoSalvamento(new IOException("Falha na imagem JPEG: " + erro.getMessage(), erro));
        } finally {
            if (imagem != null) imagem.flush();
        }
    }

    /** Resultado de um JSON gravado; a imagem pode ter falhado separadamente. */
    public static final class ResultadoSalvamento {
        private final IOException erroImagem;

        private ResultadoSalvamento(IOException erroImagem) { this.erroImagem = erroImagem; }

        /** Informa falha da imagem sem invalidar o JSON salvo.
         * @return erro da imagem, ou null se ambos os arquivos foram gravados
         */
        public IOException getErroImagem() { return erroImagem; }
    }

    /** Substitui a cena armazenada e visível pelo conteúdo de um arquivo JSON.
     * Também descarta pontos pendentes e a seleção atual antes de solicitar
     * uma nova pintura.
     * @param arquivo arquivo de origem
     * @throws IOException se o arquivo não puder ser lido ou contiver dados inválidos
     * @throws IllegalArgumentException se o arquivo for nulo ou o painel não tiver dimensões válidas
     */
    public void carregarProjeto(Path arquivo) throws IOException {
        PersistenciaProjeto.Cena cena = PersistenciaProjeto.carregar(
            arquivo, getWidth(), getHeight());
        pontos.clear();
        pontos.addAll(cena.getPontos());
        primitivos.clear();
        primitivos.addAll(cena.getPrimitivos());
        if (eixoP2 == null) eixoP1 = null;
        previaEixo = null;
        pontosPendentes.clear();
        pontoPrevia = null;
        limparSelecao();
        redesenhar();
    }

    /** Informa se existe um ponto ou uma forma visível selecionada.
     * @return {@code true} quando há uma seleção ativa
     */
    public boolean temSelecao() {
        return pontoSelecionado != null || primitivoSelecionado != null;
    }

    /** Remove da cena armazenada e da tela o item selecionado.
     * A seleção é sempre desfeita e uma nova pintura é solicitada.
     * @return {@code true} quando um item foi removido; {@code false} se não
     *         havia item selecionado
     */
    public boolean excluirSelecionado() {
        cancelarReflexaoSelecionada();
        boolean removeu = false;
        if (pontoSelecionado != null) {
            removeu = pontos.remove(pontoSelecionado);
            pontosVisiveis.remove(pontoSelecionado);
        } else if (primitivoSelecionado != null) {
            removeu = primitivos.remove(primitivoSelecionado);
            primitivosVisiveis.remove(primitivoSelecionado);
        }
        limparSelecao();
        repaint();
        return removeu;
    }

    /** Oculta a cena, preservando objetos completos para Redesenhar.
     * Também descarta pontos pendentes, a prévia e a seleção atual.
     * Oculta o guia automático, mantém um eixo completo e cancela um incompleto.
     */
    public void limpar() {
        cancelarReflexaoSelecionada();
        mostrarEixo = false;
        if (eixoP2 == null) eixoP1 = null;
        previaEixo = null;
        pontosVisiveis.clear();
        primitivosVisiveis.clear();
        pontosPendentes.clear();
        pontoPrevia = null;
        limparSelecao();
        repaint();
    }

    /** Redesenha todos os pontos e primitivos armazenados. */
    public void redesenhar() {
        redesenhar(null);
    }

    /** Redesenha os elementos do tipo informado, ou todos se for {@code null}.
     * O filtro {@link TiposPrimitivos#PONTO} exibe os pontos; os demais filtros
     * exibem apenas os primitivos do tipo correspondente.
     * Cancela construções pendentes e só restaura o guia automático completo.
     * @param filtro tipo a redesenhar, ou {@code null} para exibir tudo
     */
    public void redesenhar(TiposPrimitivos filtro) {
        cancelarReflexaoSelecionada();
        pontosPendentes.clear();
        pontoPrevia = null;
        previaEixo = null;
        if (eixoP2 == null) eixoP1 = null;
        mostrarEixo = espelhamentoAtivo && eixoP2 != null;
        limparSelecao();
        primitivosVisiveis.clear();
        pontosVisiveis.clear();
        if (filtro == null) {
            primitivosVisiveis.addAll(primitivos);
            pontosVisiveis.addAll(pontos);
        } else {
            for (PrimitivoGrafico primitivo : primitivos) {
                if (corresponde(primitivo, filtro)) {
                    primitivosVisiveis.add(primitivo);
                }
            }
            if (filtro == TiposPrimitivos.PONTO) {
                pontosVisiveis.addAll(pontos);
            }
        }
        repaint();
    }

    private boolean corresponde(PrimitivoGrafico primitivo, TiposPrimitivos filtro) {
        switch (filtro) {
            case RETA: return primitivo instanceof RetaGrafica;
            case RETANGULO: return primitivo instanceof Retangulo;
            case TRIANGULO: return primitivo instanceof Triangulo;
            case CIRCULO: return primitivo instanceof CirculoGrafico;
            default: return false;
        }
    }

    /** {@inheritDoc}
     * A pintura também inclui uma prévia incompleta e o destaque da seleção.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        desenharCena(g, pontosVisiveis, primitivosVisiveis, renderizador);
        desenharEixo(g);
        desenharPrevia(g);
        desenharSelecao(g);
    }

    private static void desenharCena(Graphics g, List<PontoGr> pontosCena,
            List<PrimitivoGrafico> formasCena, RenderizadorPrimitivos mecanismo) {
        for (PrimitivoGrafico primitivo : formasCena) primitivo.desenhar(g, mecanismo);
        for (PontoGr ponto : pontosCena) ponto.desenharPonto(g);
    }

    /** Processa um clique para criar um ponto, concluir uma forma ou selecionar
     * um elemento, conforme o modo atual.
     * @param e evento de mouse com as coordenadas do clique em pixels
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Ponto ponto = new Ponto(e.getX(), e.getY());
        if (fonteReflexao != null) {
            receberEixoSelecionado(ponto);
            return;
        }
        if (espelhamentoAtivo) mostrarEixo = true;

        if (espelhamentoAtivo && eixoP2 == null && tipo != TiposPrimitivos.SELECAO) {
            if (eixoP1 == null) {
                eixoP1 = ponto;
                previaEixo = ponto;
                msg.setText("Espelhar: clique em p2 da reta");
            } else {
                try {
                    Espelhamento.refletir(ponto, eixoP1, ponto);
                    eixoP2 = ponto;
                    previaEixo = null;
                    msg.setText("Eixo definido. Desenhe " + tipo + " normalmente.");
                } catch (IllegalArgumentException erro) {
                    msg.setText(erro.getMessage() + "; escolha outro p2");
                }
            }
            repaint();
            return;
        }

        if (tipo == TiposPrimitivos.SELECAO) {
            selecionar(ponto);
            return;
        }

        if (tipo == TiposPrimitivos.PONTO) {
            int diametro = Math.max(4, espessuraAtual + 2);
            PontoGr pontoGr = new PontoGr(e.getX(), e.getY(), corAtual,
                       "p" + pontos.size(), diametro);
            PontoGr copia;
            try {
                copia = espelhamentoAtivo ? espelharPonto(pontoGr, eixoP1, eixoP2) : null;
            } catch (IllegalArgumentException erro) {
                msg.setText(erro.getMessage());
                return;
            }
            pontos.add(pontoGr);
            pontosVisiveis.add(pontoGr);
            if (copia != null) {
                pontos.add(copia);
                pontosVisiveis.add(copia);
            }
            msg.setText("Ponto criado em (" + e.getX() + ", " + e.getY() + ")");
            repaint();
            return;
        }

        if (tipo == TiposPrimitivos.NENHUM) {
            msg.setText("Selecione um primitivo antes de clicar");
            return;
        }

        pontosPendentes.add(ponto);
        int necessarios = tipo.getQuantidadePontos();
        if (pontosPendentes.size() == necessarios) {
            try {
                criarPrimitivoPendente();
                msg.setText(tipo + " criado. Selecione novos pontos.");
            } catch (IllegalArgumentException erro) {
                msg.setText(erro.getMessage());
            }
            pontosPendentes.clear();
            pontoPrevia = null;
        } else {
            if (pontosPendentes.size() == 1 && aceitaPrevia(tipo)) {
                pontoPrevia = ponto;
            }
            msg.setText(tipo + ": ponto " + pontosPendentes.size() + " de " + necessarios);
        }
        repaint();
    }

    private PontoGr espelharPonto(PontoGr original, Ponto p1, Ponto p2) {
        Ponto refletido = Espelhamento.refletir(original, p1, p2);
        if (refletido.getX() < Integer.MIN_VALUE || refletido.getX() > Integer.MAX_VALUE
                || refletido.getY() < Integer.MIN_VALUE || refletido.getY() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Coordenada fora do limite de rasterizacao");
        }
        PontoGr copia = new PontoGr(0, 0, original.getCorPto(),
            original.getNomePto(), original.getDiametro());
        copia.setX(refletido.getX());
        copia.setY(refletido.getY());
        copia.setCorNomePto(original.getCorNomePto());
        return copia;
    }

    private void desenharEixo(Graphics g) {
        if (fonteReflexao != null) {
            desenharGuia(g, reflexaoP1, reflexaoPrevia, false);
            return;
        }
        Ponto fim = eixoP2 != null ? eixoP2 : previaEixo;
        if (!mostrarEixo || !espelhamentoAtivo || eixoP1 == null || fim == null) return;
        desenharGuia(g, eixoP1, fim, eixoP2 != null);
    }

    private void desenharGuia(Graphics g, Ponto inicio, Ponto fim, boolean completa) {
        if (inicio == null || fim == null) return;
        Graphics2D eixo = (Graphics2D)g.create();
        eixo.setColor(Color.GRAY);
        eixo.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
            10, new float[] {6, 4}, 0));
        double dx = fim.getX() - inicio.getX(), dy = fim.getY() - inicio.getY();
        double comprimento = Math.hypot(dx, dy);
        if (completa && comprimento > 0) {
            double alcance = getWidth() + getHeight();
            dx *= alcance / comprimento;
            dy *= alcance / comprimento;
            eixo.drawLine(inteiro(inicio.getX() - dx), inteiro(inicio.getY() - dy),
                inteiro(inicio.getX() + dx), inteiro(inicio.getY() + dy));
        } else {
            eixo.drawLine(inteiro(inicio.getX()), inteiro(inicio.getY()),
                inteiro(fim.getX()), inteiro(fim.getY()));
        }
        eixo.dispose();
    }

    private void criarPrimitivoPendente() {
        EstiloReta estilo = new EstiloReta(corAtual, espessuraAtual);
        Ponto p1 = pontosPendentes.get(0);
        Ponto p2 = pontosPendentes.get(1);

        if (tipo == TiposPrimitivos.TRIANGULO) {
            adicionarPrimitivo(new Triangulo(p1, p2, pontosPendentes.get(2), estilo));
        } else {
            adicionarPrimitivo(criarPrimitivoDoisPontos(tipo, p1, p2, estilo));
        }
    }

    private PrimitivoGrafico criarPrimitivoDoisPontos(TiposPrimitivos tipo,
            Ponto p1, Ponto p2, EstiloReta estilo) {
        switch (tipo) {
            case RETA: return new RetaGrafica(p1, p2, estilo);
            case RETANGULO: return new Retangulo(p1, p2, estilo);
            case CIRCULO: return new CirculoGrafico(p1, p2, estilo, algoritmoCirculo);
            default: throw new IllegalStateException(
                "Tipo sem construcao por dois pontos: " + tipo);
        }
    }

    private boolean aceitaPrevia(TiposPrimitivos tipo) {
        return tipo == TiposPrimitivos.RETA || tipo == TiposPrimitivos.RETANGULO
            || tipo == TiposPrimitivos.CIRCULO;
    }

    private void desenharPrevia(Graphics g) {
        if (pontoPrevia == null || pontosPendentes.size() != 1 || !aceitaPrevia(tipo)) {
            return;
        }
        EstiloReta estilo = new EstiloReta(corAtual, espessuraAtual);
        criarPrimitivoDoisPontos(tipo, pontosPendentes.get(0), pontoPrevia, estilo)
            .desenhar(g, renderizador);
    }

    private void selecionar(Ponto ponto) {
        limparSelecao();
        for (int i = pontosVisiveis.size() - 1; i >= 0; i--) {
            PontoGr candidato = pontosVisiveis.get(i);
            if (candidato.calcularDistancia(ponto)
                    <= candidato.getDiametro() / 2.0 + MARGEM_SELECAO) {
                pontoSelecionado = candidato;
                break;
            }
        }
        for (int i = primitivosVisiveis.size() - 1;
                pontoSelecionado == null && primitivoSelecionado == null && i >= 0; i--) {
            PrimitivoGrafico candidato = primitivosVisiveis.get(i);
            if (contem(candidato, ponto)) {
                primitivoSelecionado = candidato;
            }
        }
        msg.setText(temSelecao() ? "Primitivo selecionado" : "Nenhum primitivo encontrado");
        repaint();
    }

    private boolean contem(PrimitivoGrafico primitivo, Ponto ponto) {
        double margem = MARGEM_SELECAO + primitivo.getEspessura() / 2.0;
        if (primitivo instanceof RetaGrafica) {
            RetaGrafica reta = (RetaGrafica)primitivo;
            return distanciaSegmento(ponto, reta.getP1(), reta.getP2()) <= margem;
        }
        if (primitivo instanceof CirculoGrafico) {
            CirculoGrafico circulo = (CirculoGrafico)primitivo;
            return circulo.getCentro().calcularDistancia(ponto) <= circulo.getRaio() + margem;
        }
        if (primitivo instanceof Retangulo) {
            Retangulo retangulo = (Retangulo)primitivo;
            List<Ponto> v = retangulo.getVertices();
            return contemTriangulo(ponto, v.get(0), v.get(1), v.get(2), margem)
                || contemTriangulo(ponto, v.get(0), v.get(2), v.get(3), margem);
        }
        if (primitivo instanceof Triangulo) {
            List<Ponto> vertices = ((Triangulo)primitivo).getVertices();
            return contemTriangulo(ponto, vertices.get(0), vertices.get(1), vertices.get(2), margem);
        }
        return false;
    }

    private double distanciaSegmento(Ponto ponto, Ponto inicio, Ponto fim) {
        double dx = fim.getX() - inicio.getX();
        double dy = fim.getY() - inicio.getY();
        if (dx == 0 && dy == 0) {
            return ponto.calcularDistancia(inicio);
        }
        double proporcao = ((ponto.getX() - inicio.getX()) * dx
            + (ponto.getY() - inicio.getY()) * dy) / (dx * dx + dy * dy);
        proporcao = Math.max(0, Math.min(1, proporcao));
        return ponto.calcularDistancia(new Ponto(
            inicio.getX() + proporcao * dx, inicio.getY() + proporcao * dy));
    }

    private boolean contemTriangulo(Ponto ponto, Ponto p1, Ponto p2, Ponto p3,
                                     double margem) {
        double d1 = lado(ponto, p1, p2);
        double d2 = lado(ponto, p2, p3);
        double d3 = lado(ponto, p3, p1);
        boolean degenerado = Math.abs(lado(p1, p2, p3)) < 0.000001;
        boolean dentro = !degenerado && (!(d1 < 0 || d2 < 0 || d3 < 0)
            || !(d1 > 0 || d2 > 0 || d3 > 0));
        return dentro || distanciaSegmento(ponto, p1, p2) <= margem
            || distanciaSegmento(ponto, p2, p3) <= margem
            || distanciaSegmento(ponto, p3, p1) <= margem;
    }

    private double lado(Ponto ponto, Ponto p1, Ponto p2) {
        return (ponto.getX() - p2.getX()) * (p1.getY() - p2.getY())
            - (p1.getX() - p2.getX()) * (ponto.getY() - p2.getY());
    }

    private void desenharSelecao(Graphics g) {
        if (!temSelecao()) {
            return;
        }
        Graphics2D selecao = (Graphics2D)g.create();
        selecao.setColor(Color.ORANGE);
        selecao.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
            10, new float[] {6, 4}, 0));
        if (pontoSelecionado != null) {
            int raio = pontoSelecionado.getDiametro() / 2 + 4;
            selecao.drawOval(inteiro(pontoSelecionado.getX()) - raio,
                inteiro(pontoSelecionado.getY()) - raio, raio * 2, raio * 2);
        } else if (primitivoSelecionado instanceof RetaGrafica) {
            RetaGrafica reta = (RetaGrafica)primitivoSelecionado;
            selecao.drawLine(inteiro(reta.getP1().getX()), inteiro(reta.getP1().getY()),
                inteiro(reta.getP2().getX()), inteiro(reta.getP2().getY()));
        } else if (primitivoSelecionado instanceof Retangulo) {
            Retangulo retangulo = (Retangulo)primitivoSelecionado;
            Polygon poligono = new Polygon();
            for (Ponto vertice : retangulo.getVertices()) {
                poligono.addPoint(inteiro(vertice.getX()), inteiro(vertice.getY()));
            }
            selecao.drawPolygon(poligono);
        } else if (primitivoSelecionado instanceof Triangulo) {
            List<Ponto> vertices = ((Triangulo)primitivoSelecionado).getVertices();
            Polygon poligono = new Polygon();
            for (Ponto vertice : vertices) {
                poligono.addPoint(inteiro(vertice.getX()), inteiro(vertice.getY()));
            }
            selecao.drawPolygon(poligono);
        } else if (primitivoSelecionado instanceof CirculoGrafico) {
            CirculoGrafico circulo = (CirculoGrafico)primitivoSelecionado;
            int raio = inteiro(circulo.getRaio());
            selecao.drawOval(inteiro(circulo.getCentro().getX()) - raio,
                inteiro(circulo.getCentro().getY()) - raio, raio * 2, raio * 2);
        }
        selecao.dispose();
    }

    private int inteiro(double valor) {
        return (int)Math.round(valor);
    }

    private void limparSelecao() {
        pontoSelecionado = null;
        primitivoSelecionado = null;
    }

    /** Atualiza a coordenada da prévia após o primeiro ponto de uma forma.
     * @param e evento de movimento com a posição atual do mouse em pixels
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (fonteReflexao != null) {
            reflexaoPrevia = new Ponto(e.getX(), e.getY());
            repaint();
            return;
        }
        if (espelhamentoAtivo && eixoP2 == null && tipo != TiposPrimitivos.SELECAO) {
            msg.setText(eixoP1 == null ? "Espelhar: clique em p1 da reta"
                : "Espelhar: clique em p2 da reta");
            previaEixo = new Ponto(e.getX(), e.getY());
            repaint();
            return;
        }
        msg.setText("(" + e.getX() + ", " + e.getY() + ")");
        if (pontosPendentes.size() == 1 && aceitaPrevia(tipo)) {
            pontoPrevia = new Ponto(e.getX(), e.getY());
            repaint();
        }
    }

    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseClicked(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }
    @Override public void mouseDragged(MouseEvent e) { mouseMoved(e); }
}
