package tests;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import circulo.AlgoritmoCirculo;
import circulo.CirculoGrafico;
import ponto.Ponto;
import renderizacao.Espelhamento;
import renderizacao.PrimitivoGrafico;
import reta.RetaGrafica;
import retangulo.Retangulo;
import triangulo.Triangulo;
import ui.PainelDesenho;
import ui.TiposPrimitivos;

/** Execute com -ea e -Djava.awt.headless=true. */
public class TestaEspelhamento {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try { testar(); } catch (Exception erro) { throw new AssertionError(erro); }
        });
        System.out.println("Espelhamento: OK");
    }

    private static void testar() throws Exception {
        Ponto ponto = new Ponto(20, 30);
        igual(Espelhamento.refletir(ponto, new Ponto(0, 10), new Ponto(50, 10)), 20, -10);
        igual(Espelhamento.refletir(ponto, new Ponto(10, 0), new Ponto(10, 50)), 0, 30);
        igual(Espelhamento.refletir(ponto, new Ponto(0, 0), new Ponto(50, 50)), 30, 20);
        Ponto p1 = new Ponto(10, 15), p2 = new Ponto(90, 55);
        Ponto refletido = Espelhamento.refletir(ponto, p1, p2);
        igual(Espelhamento.refletir(refletido, p1, p2), 20, 30);
        Ponto invertido = Espelhamento.refletir(ponto, p2, p1);
        igual(invertido, refletido.getX(), refletido.getY());
        igual(Espelhamento.refletir(p1, p1, p2), 10, 15);
        for (Ponto invalido : Arrays.asList(p1, new Ponto(Double.NaN, 0))) {
            boolean rejeitou = false;
            try { Espelhamento.refletir(ponto, p1, invalido); }
            catch (IllegalArgumentException esperado) { rejeitou = true; }
            assert rejeitou : "reta invalida";
        }

        List<PrimitivoGrafico> formas = Arrays.asList(
            new RetaGrafica(new Ponto(20, 20), new Ponto(40, 20), Color.BLUE, 3),
            new Triangulo(new Ponto(20, 20), new Ponto(40, 20), new Ponto(30, 40), Color.BLUE, 3),
            new Retangulo(new Ponto(20, 20), new Ponto(40, 40), Color.BLUE, 3),
            new CirculoGrafico(new Ponto(30, 30), new Ponto(40, 30), Color.BLUE, 3,
                AlgoritmoCirculo.PARAMETRICO));
        Path arquivo = Files.createTempFile("espelhamento-", ".json");
        try {
            for (PrimitivoGrafico original : formas) {
                JLabel msg = new JLabel();
                PainelDesenho painel = new PainelDesenho(msg, TiposPrimitivos.ESPELHAMENTO);
                painel.setSize(160, 160);
                painel.adicionarPrimitivo(original);
                clicar(painel, 30, original instanceof RetaGrafica ? 20 : 30);
                assert painel.temSelecao();
                clicar(painel, 70, 30);
                clicar(painel, 70, 30);
                assert painel.getQuantidadePrimitivos() == 1 : "reta degenerada nao cria copia";
                assert painel.temSelecao();
                clicar(painel, 100, 90);
                assert painel.getQuantidadePrimitivos() == 2;
                assert painel.getPrimitivos().get(0) == original;
                assert !painel.temSelecao();
                PrimitivoGrafico copia = painel.getPrimitivos().get(1);
                assert copia.getClass() == original.getClass();
                assert copia.getCor().equals(Color.BLUE) && copia.getEspessura() == 3;
                if (copia instanceof Retangulo) {
                    List<Ponto> v = ((Retangulo)original).getVertices();
                    List<Ponto> c = ((Retangulo)copia).getVertices();
                    for (int i = 0; i < 4; i++) {
                        Ponto esperado = Espelhamento.refletir(v.get(i), new Ponto(70, 30), new Ponto(100, 90));
                        igual(c.get(i), esperado.getX(), esperado.getY());
                    }
                    assert Math.abs(c.get(0).getY() - c.get(1).getY()) > 1 : "retangulo orientado";
                }
                if (copia instanceof CirculoGrafico) {
                    assert ((CirculoGrafico)copia).getAlgoritmo() == AlgoritmoCirculo.PARAMETRICO;
                    assert Math.abs(((CirculoGrafico)copia).getRaio() - 10) < 1e-8;
                }
                BufferedImage antes = pintar(painel);
                painel.limpar();
                painel.redesenhar();
                assert Arrays.equals(pixels(antes), pixels(pintar(painel))) : "copia armazenada e redesenhada";
                painel.salvarProjeto(arquivo);
                painel.carregarProjeto(arquivo);
                assert Arrays.equals(pixels(antes), pixels(pintar(painel))) : "persistencia da copia";
                Ponto centroCopia = Espelhamento.refletir(new Ponto(30, 30), new Ponto(70, 30), new Ponto(100, 90));
                painel.setTipo(TiposPrimitivos.SELECAO);
                Ponto alvo = original instanceof RetaGrafica
                    ? Espelhamento.refletir(new Ponto(30, 20), new Ponto(70, 30), new Ponto(100, 90)) : centroCopia;
                clicar(painel, (int)Math.round(alvo.getX()), (int)Math.round(alvo.getY()));
                assert painel.excluirSelecionado() : "copia selecionavel";
                assert painel.getQuantidadePrimitivos() == 1;
                if (copia instanceof Retangulo) {
                    List<Ponto> restante = ((Retangulo)painel.getPrimitivos().get(0)).getVertices();
                    igual(restante.get(0), 20, 20);
                }
            }
            PainelDesenho painel = new PainelDesenho(new JLabel(), TiposPrimitivos.PONTO);
            painel.setSize(160, 160);
            painel.setCorAtual(Color.RED);
            clicar(painel, 20, 30);
            painel.setTipo(TiposPrimitivos.ESPELHAMENTO);
            clicar(painel, 20, 30);
            clicar(painel, 70, 0);
            painel.setTipo(TiposPrimitivos.RETA);
            assert painel.getQuantidadePontos() == 1 : "cancelamento";
            painel.setTipo(TiposPrimitivos.ESPELHAMENTO);
            clicar(painel, 20, 30);
            clicar(painel, 70, 0);
            clicar(painel, 70, 100);
            assert painel.getQuantidadePontos() == 2;
            clicar(painel, 20, 30);
            clicar(painel, 70, 0);
            painel.redesenhar();
            clicar(painel, 20, 30);
            clicar(painel, 70, 0);
            assert painel.getQuantidadePontos() == 2 : "redesenhar cancela a reta pendente";
            painel.setTipo(TiposPrimitivos.NENHUM);
            assert pintar(painel).getRGB(120, 30) == Color.RED.getRGB();
            painel.salvarProjeto(arquivo);
            painel.carregarProjeto(arquivo);
            assert painel.getQuantidadePontos() == 2;
        } finally { Files.deleteIfExists(arquivo); }
    }

    private static void igual(Ponto p, double x, double y) {
        assert Math.abs(p.getX() - x) < 1e-8 && Math.abs(p.getY() - y) < 1e-8 : p;
    }
    private static void clicar(PainelDesenho p, int x, int y) {
        p.mousePressed(new MouseEvent(p, MouseEvent.MOUSE_PRESSED, 0, 0, x, y, 1, false));
    }
    private static BufferedImage pintar(PainelDesenho p) {
        BufferedImage imagem = new BufferedImage(160, 160, BufferedImage.TYPE_INT_RGB);
        Graphics g = imagem.getGraphics();
        p.paint(g);
        g.dispose();
        return imagem;
    }
    private static int[] pixels(BufferedImage imagem) {
        return imagem.getRGB(0, 0, 160, 160, null, 0, 160);
    }
}
