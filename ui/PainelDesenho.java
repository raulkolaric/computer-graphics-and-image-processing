package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

import circulo.AlgoritmoCirculo;
import circulo.CirculoGrafico;
import ponto.Ponto;
import ponto.PontoGr;
import quadrado.Retangulo;
import renderizacao.PrimitivoGrafico;
import renderizacao.RenderizadorManual;
import renderizacao.RenderizadorPrimitivos;
import reta.EstiloReta;
import reta.RetaGrafica;
import triangulo.Triangulo;

/** Painel que recebe pontos pelo mouse, armazena a cena e a redesenha. */
public class PainelDesenho extends JPanel implements MouseListener, MouseMotionListener {
    private static final long serialVersionUID = 1L;

    private final JLabel msg;
    private final List<PontoGr> pontos = new ArrayList<PontoGr>();
    private final List<PrimitivoGrafico> primitivos = new ArrayList<PrimitivoGrafico>();
    private final List<Ponto> pontosPendentes = new ArrayList<Ponto>();

    private TiposPrimitivos tipo;
    private RenderizadorPrimitivos renderizador;
    private Color corAtual = Color.BLACK;
    private int espessuraAtual = 1;
    private AlgoritmoCirculo algoritmoCirculo = AlgoritmoCirculo.SIMETRIA_OCTANTES;

    /** Cria um painel usando o renderizador manual padrão.
     * @param msg etiqueta usada para exibir mensagens
     * @param tipo tipo de primitivo inicialmente selecionado
     */
    public PainelDesenho(JLabel msg, TiposPrimitivos tipo) {
        this(msg, tipo, new RenderizadorManual());
    }

    /** Cria um painel com o renderizador informado.
     * @param msg etiqueta usada para exibir mensagens
     * @param tipo tipo de primitivo inicialmente selecionado
     * @param renderizador renderizador dos primitivos
     */
    public PainelDesenho(JLabel msg, TiposPrimitivos tipo,
                          RenderizadorPrimitivos renderizador) {
        if (msg == null || tipo == null || renderizador == null) {
            throw new IllegalArgumentException("Mensagem, tipo e renderizador sao obrigatorios");
        }
        this.tipo = tipo;
        this.msg = msg;
        this.renderizador = renderizador;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /** Seleciona o tipo de primitivo e limpa pontos pendentes.
     * @param tipo novo tipo de primitivo
     */
    public void setTipo(TiposPrimitivos tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo nao pode ser nulo");
        }
        this.tipo = tipo;
        pontosPendentes.clear();
        msg.setText("Modo: " + tipo);
    }

    /** @return tipo de primitivo selecionado */
    public TiposPrimitivos getTipo() {
        return tipo;
    }

    /** Ativa ou desativa o modo de criação de retas.
     * @param ativo {@code true} para ativar o modo
     */
    public void setModoReta(boolean ativo) {
        setTipo(ativo ? TiposPrimitivos.RETA : TiposPrimitivos.NENHUM);
    }

    /** @return {@code true} se o modo de reta estiver ativo */
    public boolean getModoReta() {
        return tipo == TiposPrimitivos.RETA;
    }

    /** Ativa ou desativa o modo de criação de círculos.
     * @param ativo {@code true} para ativar o modo
     */
    public void setModoCirculo(boolean ativo) {
        setTipo(ativo ? TiposPrimitivos.CIRCULO : TiposPrimitivos.NENHUM);
    }

    /** @return {@code true} se o modo de círculo estiver ativo */
    public boolean getModoCirculo() {
        return tipo == TiposPrimitivos.CIRCULO;
    }

    /** Define a cor usada nos novos primitivos.
     * @param corAtual nova cor
     */
    public void setCorAtual(Color corAtual) {
        if (corAtual == null) {
            throw new IllegalArgumentException("A cor nao pode ser nula");
        }
        this.corAtual = corAtual;
    }

    /** @return cor usada nos novos primitivos */
    public Color getCorAtual() {
        return corAtual;
    }

    /** Define a espessura usada nos novos primitivos.
     * @param espessuraAtual nova espessura em pixels
     */
    public void setEspessuraAtual(int espessuraAtual) {
        if (espessuraAtual < 1) {
            throw new IllegalArgumentException("A espessura deve ser maior ou igual a 1");
        }
        this.espessuraAtual = espessuraAtual;
    }

    /** @return espessura usada nos novos primitivos */
    public int getEspessuraAtual() {
        return espessuraAtual;
    }

    /** Define o algoritmo usado para novos círculos.
     * @param algoritmoCirculo novo algoritmo
     */
    public void setAlgoritmoCirculo(AlgoritmoCirculo algoritmoCirculo) {
        if (algoritmoCirculo == null) {
            throw new IllegalArgumentException("O algoritmo nao pode ser nulo");
        }
        this.algoritmoCirculo = algoritmoCirculo;
    }

    /** @return algoritmo usado para novos círculos */
    public AlgoritmoCirculo getAlgoritmoCirculo() {
        return algoritmoCirculo;
    }

    /** Substitui o renderizador e solicita uma nova pintura.
     * @param renderizador novo renderizador
     */
    public void setRenderizador(RenderizadorPrimitivos renderizador) {
        if (renderizador == null) {
            throw new IllegalArgumentException("O renderizador nao pode ser nulo");
        }
        this.renderizador = renderizador;
        repaint();
    }

    /** Adiciona um primitivo à cena e solicita uma nova pintura.
     * @param primitivo primitivo a adicionar
     */
    public void adicionarPrimitivo(PrimitivoGrafico primitivo) {
        if (primitivo == null) {
            throw new IllegalArgumentException("O primitivo nao pode ser nulo");
        }
        primitivos.add(primitivo);
        repaint();
    }

    /** @return visão não modificável dos primitivos da cena */
    public List<PrimitivoGrafico> getPrimitivos() {
        return Collections.unmodifiableList(new ArrayList<PrimitivoGrafico>(primitivos));
    }

    /** @return quantidade de primitivos armazenados */
    public int getQuantidadePrimitivos() {
        return primitivos.size();
    }

    /** @return quantidade de pontos armazenados */
    public int getQuantidadePontos() {
        return pontos.size();
    }

    /** Remove pontos, primitivos e seleções pendentes da cena. */
    public void limpar() {
        pontos.clear();
        primitivos.clear();
        pontosPendentes.clear();
        repaint();
    }

    /** Solicita que o painel seja redesenhado. */
    public void redesenhar() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (PrimitivoGrafico primitivo : primitivos) {
            primitivo.desenhar(g, renderizador);
        }
        for (PontoGr ponto : pontos) {
            ponto.desenharPonto(g);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Ponto ponto = new Ponto(e.getX(), e.getY());

        if (tipo == TiposPrimitivos.PONTO) {
            int diametro = Math.max(4, espessuraAtual + 2);
            pontos.add(new PontoGr(e.getX(), e.getY(), corAtual,
                       "p" + pontos.size(), diametro));
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
            criarPrimitivoPendente();
            pontosPendentes.clear();
            msg.setText(tipo + " criado. Selecione novos pontos.");
        } else {
            msg.setText(tipo + ": ponto " + pontosPendentes.size() + " de " + necessarios);
        }
        repaint();
    }

    private void criarPrimitivoPendente() {
        EstiloReta estilo = new EstiloReta(corAtual, espessuraAtual);
        Ponto p1 = pontosPendentes.get(0);
        Ponto p2 = pontosPendentes.get(1);

        switch (tipo) {
            case RETA:
                primitivos.add(new RetaGrafica(p1, p2, estilo));
                break;
            case RETANGULO:
                primitivos.add(new Retangulo(p1, p2, estilo));
                break;
            case TRIANGULO:
                primitivos.add(new Triangulo(p1, p2, pontosPendentes.get(2), estilo));
                break;
            case CIRCULO:
                primitivos.add(new CirculoGrafico(p1, p2, estilo, algoritmoCirculo));
                break;
            default:
                throw new IllegalStateException("Tipo sem construcao por multiplos pontos: " + tipo);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        msg.setText("(" + e.getX() + ", " + e.getY() + ")");
    }

    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseClicked(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }
    @Override public void mouseDragged(MouseEvent e) { }
}
