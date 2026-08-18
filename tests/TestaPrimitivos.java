package tests;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Queue;
import javax.swing.JLabel;

import circulo.AlgoritmoCirculo;
import circulo.CirculoGrafico;
import ponto.Ponto;
import quadrado.Retangulo;
import renderizacao.FiguraPontos;
import renderizacao.RenderizadorManual;
import renderizacao.RenderizadorPrimitivos;
import reta.Reta;
import reta.RetaGrafica;
import triangulo.Triangulo;
import ui.PainelDesenho;
import ui.TiposPrimitivos;

/** Testes de regressao executaveis sem interface grafica. */
public class TestaPrimitivos {
    private static final RenderizadorManual RENDERIZADOR = new RenderizadorManual();

    public static void main(String[] args) {
        testarRetaMatematica();
        testarDirecoesDaReta();
        testarPrimitivosCompostos();
        testarCirculos();
        testarArmazenamentoERedesenho();
        testarEntradaMouse();
        testarTrocaDeRenderizador();
        System.out.println("TestaPrimitivos: todos os testes passaram");
    }

    private static void testarRetaMatematica() {
        Reta reta = new Reta(2, 5, 6, 13);
        verificar(Math.abs(reta.calcularM() - 2.0) < 0.000001, "coeficiente m");
        verificar(Math.abs(reta.calcularB() - 1.0) < 0.000001, "coeficiente b");

        Reta vertical = new Reta(4, 1, 4, 20);
        verificar(vertical.isVertical(), "deteccao de reta vertical");
        boolean rejeitouM = false;
        try {
            vertical.calcularM();
        } catch (IllegalStateException esperado) {
            rejeitouM = true;
        }
        verificar(rejeitouM, "reta vertical nao deve expor m infinito");
        verificar(new Reta(3, 3, 3, 3).isDegenerada(), "reta degenerada");

        BufferedImage fracionaria = novaImagem();
        Graphics g = fracionaria.getGraphics();
        new RetaGrafica(new Ponto(10.4, 10.4), new Ponto(50.4, 31.6), Color.PINK, 1)
            .desenhar(g, RENDERIZADOR);
        g.dispose();
        verificarCor(fracionaria, 10, 10, Color.PINK, "inicio fracionario arredondado");
        verificarCor(fracionaria, 50, 32, Color.PINK, "fim fracionario arredondado");
    }

    private static void testarDirecoesDaReta() {
        int[][] segmentos = {
            {10, 10, 50, 10}, {50, 12, 10, 12},
            {14, 10, 14, 50}, {16, 50, 16, 10},
            {20, 20, 55, 45}, {55, 45, 20, 20},
            {55, 20, 20, 45}, {20, 45, 55, 20},
            {60, 10, 65, 55}, {70, 55, 75, 10},
            {80, 30, 80, 30}
        };

        for (int[] s : segmentos) {
            BufferedImage imagem = novaImagem();
            Graphics g = imagem.getGraphics();
            RetaGrafica reta = new RetaGrafica(
                new Ponto(s[0], s[1]), new Ponto(s[2], s[3]), Color.RED, 1);
            reta.desenhar(g, RENDERIZADOR);
            g.dispose();
            verificarCor(imagem, s[0], s[1], Color.RED, "inicio da reta");
            verificarCor(imagem, s[2], s[3], Color.RED, "fim da reta");
            verificar(conectados(imagem, s[0], s[1], s[2], s[3], Color.RED),
                "reta continua entre os extremos");
        }

        BufferedImage grossa = novaImagem();
        Graphics g = grossa.getGraphics();
        new RetaGrafica(new Ponto(10, 70), new Ponto(80, 70), Color.BLUE, 5)
            .desenhar(g, RENDERIZADOR);
        g.dispose();
        verificarCor(grossa, 40, 68, Color.BLUE, "espessura da reta");

        boolean rejeitouExtrema = false;
        try {
            new RetaGrafica(new Ponto(0, 0), new Ponto(Integer.MAX_VALUE, 0),
                Color.BLACK, 1).desenhar(novaImagem().getGraphics(), RENDERIZADOR);
        } catch (IllegalArgumentException esperado) {
            rejeitouExtrema = true;
        }
        verificar(rejeitouExtrema, "reta extrema e rejeitada sem travar o rasterizador");
    }

    private static void testarPrimitivosCompostos() {
        Retangulo retangulo = new Retangulo(
            new Ponto(60, 50), new Ponto(20, 10), Color.MAGENTA, 3);
        verificar(retangulo.getRetas().size() == 4, "quatro retas no retangulo");
        for (RetaGrafica reta : retangulo.getRetas()) {
            verificar(reta.getCor().equals(Color.MAGENTA), "cor das retas do retangulo");
            verificar(reta.getEspessura() == 3, "espessura das retas do retangulo");
        }

        Triangulo triangulo = new Triangulo(new Ponto(10, 80), new Ponto(50, 20),
            new Ponto(90, 80), Color.GREEN, 2);
        verificar(triangulo.getRetas().size() == 3, "tres retas no triangulo");

        BufferedImage imagem = novaImagem();
        Graphics g = imagem.getGraphics();
        retangulo.desenhar(g, RENDERIZADOR);
        triangulo.desenhar(g, RENDERIZADOR);
        g.dispose();
        verificarCor(imagem, 20, 10, Color.MAGENTA, "canto do retangulo");
        verificarCor(imagem, 50, 20, Color.GREEN, "vertice do triangulo");
    }

    private static void testarCirculos() {
        for (AlgoritmoCirculo algoritmo : AlgoritmoCirculo.values()) {
            BufferedImage imagem = novaImagem();
            Graphics g = imagem.getGraphics();
            CirculoGrafico circulo = new CirculoGrafico(new Ponto(50, 50),
                new Ponto(70, 50), Color.ORANGE, 1, algoritmo);
            circulo.desenhar(g, RENDERIZADOR);
            g.dispose();
            verificarCor(imagem, 70, 50, Color.ORANGE, algoritmo + " direita");
            verificarCor(imagem, 30, 50, Color.ORANGE, algoritmo + " esquerda");
            verificarCor(imagem, 50, 70, Color.ORANGE, algoritmo + " abaixo");
            verificarCor(imagem, 50, 30, Color.ORANGE, algoritmo + " acima");
            verificar(contarPixels(imagem, Color.ORANGE) > 80,
                algoritmo + " produz uma circunferencia, nao apenas pontos cardinais");
            if (algoritmo == AlgoritmoCirculo.SIMETRIA_OCTANTES) {
                verificarSimetria(imagem, 50, 50, Color.ORANGE);
            }
        }

        BufferedImage imagem = novaImagem();
        Graphics g = imagem.getGraphics();
        new CirculoGrafico(new Ponto(40, 40), new Ponto(40, 40), Color.CYAN, 1,
            AlgoritmoCirculo.SIMETRIA_OCTANTES).desenhar(g, RENDERIZADOR);
        g.dispose();
        verificarCor(imagem, 40, 40, Color.CYAN, "circulo de raio zero");

        boolean rejeitouRaioExtremo = false;
        try {
            new CirculoGrafico(new Ponto(0, 0), new Ponto(100_001, 0), Color.BLACK, 1,
                AlgoritmoCirculo.PARAMETRICO).desenhar(
                    novaImagem().getGraphics(), RENDERIZADOR);
        } catch (IllegalArgumentException esperado) {
            rejeitouRaioExtremo = true;
        }
        verificar(rejeitouRaioExtremo, "circulo extremo e rejeitado sem travar");
    }

    private static void testarArmazenamentoERedesenho() {
        PainelDesenho painel = new PainelDesenho(new JLabel(), TiposPrimitivos.NENHUM);
        painel.setSize(120, 120);
        for (int i = 0; i < 120; i++) {
            int y = 1 + (i % 100);
            painel.adicionarPrimitivo(new RetaGrafica(
                new Ponto(1, y), new Ponto(110, y), Color.BLACK, 1));
        }
        verificar(painel.getQuantidadePrimitivos() == 120, "lista dinamica de primitivos");

        BufferedImage primeira = novaImagem();
        Graphics g1 = primeira.getGraphics();
        painel.paint(g1);
        g1.dispose();
        BufferedImage segunda = novaImagem();
        Graphics g2 = segunda.getGraphics();
        painel.redesenhar();
        painel.paint(g2);
        g2.dispose();
        verificar(checksum(primeira) == checksum(segunda), "redesenho deterministico da cena");
    }

    private static void testarEntradaMouse() {
        PainelDesenho painel = new PainelDesenho(new JLabel(), TiposPrimitivos.RETA);
        clicar(painel, 10, 10);
        verificar(painel.getQuantidadePrimitivos() == 0, "reta aguarda o segundo ponto");
        clicar(painel, 30, 30);
        verificar(painel.getQuantidadePrimitivos() == 1, "reta criada com dois pontos");

        painel.setTipo(TiposPrimitivos.TRIANGULO);
        clicar(painel, 20, 20);
        clicar(painel, 40, 20);
        verificar(painel.getQuantidadePrimitivos() == 1, "triangulo aguarda tres pontos");
        clicar(painel, 30, 50);
        verificar(painel.getQuantidadePrimitivos() == 2, "triangulo criado");

        painel.setTipo(TiposPrimitivos.CIRCULO);
        clicar(painel, 60, 60);
        painel.setTipo(TiposPrimitivos.RETANGULO);
        clicar(painel, 5, 5);
        verificar(painel.getQuantidadePrimitivos() == 2,
            "troca de modo descarta ponto pendente");
        clicar(painel, 15, 15);
        verificar(painel.getQuantidadePrimitivos() == 3, "retangulo criado");
    }

    private static void testarTrocaDeRenderizador() {
        ContadorRenderizador contador = new ContadorRenderizador();
        Retangulo retangulo = new Retangulo(new Ponto(1, 1), new Ponto(10, 10),
            Color.BLACK, 1);
        retangulo.desenhar(novaImagem().getGraphics(), contador);
        verificar(contador.retas == 4, "retangulo delega ao renderizador injetado");

        CirculoGrafico circulo = new CirculoGrafico(new Ponto(5, 5), new Ponto(8, 5),
            Color.BLACK, 1, AlgoritmoCirculo.PARAMETRICO);
        circulo.desenhar(novaImagem().getGraphics(), contador);
        verificar(contador.circulos == 1, "circulo delega ao renderizador injetado");

        FiguraPontos.setRenderizador(contador);
        FiguraPontos.desenharReta(novaImagem().getGraphics(),
            new Reta(1, 1, 5, 5));
        verificar(contador.retas == 5, "fachada legada permite trocar o renderizador");
        FiguraPontos.setRenderizador(RENDERIZADOR);
    }

    private static void clicar(PainelDesenho painel, int x, int y) {
        painel.mousePressed(new MouseEvent(painel, MouseEvent.MOUSE_PRESSED,
            System.currentTimeMillis(), 0, x, y, 1, false));
    }

    private static BufferedImage novaImagem() {
        return new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
    }

    private static long checksum(BufferedImage imagem) {
        long resultado = 17;
        for (int y = 0; y < imagem.getHeight(); y++) {
            for (int x = 0; x < imagem.getWidth(); x++) {
                resultado = resultado * 31 + imagem.getRGB(x, y);
            }
        }
        return resultado;
    }

    private static boolean conectados(BufferedImage imagem, int xInicio, int yInicio,
                                       int xFim, int yFim, Color cor) {
        boolean[][] visitado = new boolean[imagem.getHeight()][imagem.getWidth()];
        Queue<int[]> fila = new ArrayDeque<int[]>();
        fila.add(new int[] {xInicio, yInicio});
        visitado[yInicio][xInicio] = true;

        while (!fila.isEmpty()) {
            int[] atual = fila.remove();
            if (atual[0] == xFim && atual[1] == yFim) {
                return true;
            }
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    int x = atual[0] + dx;
                    int y = atual[1] + dy;
                    if ((dx != 0 || dy != 0) && x >= 0 && y >= 0
                            && x < imagem.getWidth() && y < imagem.getHeight()
                            && !visitado[y][x] && imagem.getRGB(x, y) == cor.getRGB()) {
                        visitado[y][x] = true;
                        fila.add(new int[] {x, y});
                    }
                }
            }
        }
        return false;
    }

    private static int contarPixels(BufferedImage imagem, Color cor) {
        int total = 0;
        for (int y = 0; y < imagem.getHeight(); y++) {
            for (int x = 0; x < imagem.getWidth(); x++) {
                if (imagem.getRGB(x, y) == cor.getRGB()) {
                    total++;
                }
            }
        }
        return total;
    }

    private static void verificarSimetria(BufferedImage imagem, int xc, int yc, Color cor) {
        for (int y = 0; y < imagem.getHeight(); y++) {
            for (int x = 0; x < imagem.getWidth(); x++) {
                if (imagem.getRGB(x, y) != cor.getRGB()) {
                    continue;
                }
                int dx = x - xc;
                int dy = y - yc;
                int[][] reflexos = {
                    {xc + dx, yc + dy}, {xc - dx, yc + dy},
                    {xc + dx, yc - dy}, {xc - dx, yc - dy},
                    {xc + dy, yc + dx}, {xc - dy, yc + dx},
                    {xc + dy, yc - dx}, {xc - dy, yc - dx}
                };
                for (int[] reflexo : reflexos) {
                    verificarCor(imagem, reflexo[0], reflexo[1], cor,
                        "simetria nos oito octantes");
                }
            }
        }
    }

    private static void verificarCor(BufferedImage imagem, int x, int y, Color esperada,
                                      String descricao) {
        verificar(imagem.getRGB(x, y) == esperada.getRGB(), descricao);
    }

    private static void verificar(boolean condicao, String descricao) {
        if (!condicao) {
            throw new AssertionError("Falhou: " + descricao);
        }
    }

    private static class ContadorRenderizador implements RenderizadorPrimitivos {
        int retas;
        int circulos;

        @Override
        public void desenharReta(Graphics g, RetaGrafica reta) {
            retas++;
        }

        @Override
        public void desenharCirculo(Graphics g, CirculoGrafico circulo) {
            circulos++;
        }
    }
}
