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
        testarSelecao();
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
                TiposPrimitivos tipo = original instanceof RetaGrafica ? TiposPrimitivos.RETA
                    : original instanceof Triangulo ? TiposPrimitivos.TRIANGULO
                    : original instanceof Retangulo ? TiposPrimitivos.RETANGULO : TiposPrimitivos.CIRCULO;
                PainelDesenho painel = new PainelDesenho(msg, tipo);
                painel.setSize(160, 160);
                painel.setCorAtual(Color.BLUE);
                painel.setEspessuraAtual(3);
                painel.setAlgoritmoCirculo(AlgoritmoCirculo.PARAMETRICO);
                painel.setEspelhamento(true);
                assert painel.getTipo() == tipo : "toggle preserva ferramenta";
                clicar(painel, 70, 30);
                clicar(painel, 70, 30);
                assert painel.getQuantidadePrimitivos() == 0 : "eixo degenerado nao cria forma";
                clicar(painel, 100, 90);
                assert painel.getQuantidadePrimitivos() == 0 : "eixo nao entra na cena";
                if (tipo == TiposPrimitivos.CIRCULO) {
                    clicar(painel, 30, 30);
                    clicar(painel, 40, 30);
                } else {
                    clicar(painel, 20, 20);
                    clicar(painel, 40, tipo == TiposPrimitivos.RETANGULO ? 40 : 20);
                    if (tipo == TiposPrimitivos.TRIANGULO) clicar(painel, 30, 40);
                }
                assert painel.getQuantidadePrimitivos() == 2 : "criacao automatica";
                assert painel.getTipo() == tipo;
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
            painel.setEspelhamento(true);
            clicar(painel, 70, 0);
            painel.setTipo(TiposPrimitivos.PONTO);
            clicar(painel, 70, 100);
            assert painel.getQuantidadePontos() == 1 : "ativar nao espelha pontos antigos";
            clicar(painel, 20, 30);
            assert painel.getQuantidadePontos() == 3;
            clicar(painel, 25, 50);
            assert painel.getQuantidadePontos() == 5 : "eixo reutilizado";
            assert pintar(painel).getRGB(120, 30) == Color.RED.getRGB();
            painel.salvarProjeto(arquivo);
            painel.carregarProjeto(arquivo);
            assert painel.getQuantidadePontos() == 5 : "abrir nao duplica novamente";
            painel.setEspelhamento(false);
            clicar(painel, 30, 60);
            assert painel.getQuantidadePontos() == 6 : "desativado cria so original";
            painel.setTipo(TiposPrimitivos.RETA);
            clicar(painel, 10, 10);
            painel.setEspelhamento(true);
            clicar(painel, 70, 0);
            painel.setEspelhamento(false);
            clicar(painel, 20, 20);
            assert painel.getQuantidadePrimitivos() == 0 : "toggle cancela forma e eixo incompletos";
            clicar(painel, 40, 20);
            assert painel.getQuantidadePrimitivos() == 1;
            painel.setEspelhamento(true);
            clicar(painel, 0, 70);
            clicar(painel, 100, 70);
            clicar(painel, 10, 10);
            clicar(painel, 20, 10);
            assert painel.getQuantidadePrimitivos() == 3 : "novo eixo reutilizado";
            RetaGrafica ultima = (RetaGrafica)painel.getPrimitivos().get(2);
            igual(ultima.getP1(), 10, 130);
            igual(ultima.getP2(), 20, 130);
        } finally { Files.deleteIfExists(arquivo); Files.deleteIfExists(persistencia.NomesProjeto.jpeg(arquivo)); }
    }

    private static void testarSelecao() throws Exception {
        Path arquivo = Files.createTempFile("selecao-reflexao-", ".json");
        try {
            for (TiposPrimitivos tipo : Arrays.asList(TiposPrimitivos.PONTO, TiposPrimitivos.RETA,
                    TiposPrimitivos.RETANGULO, TiposPrimitivos.TRIANGULO, TiposPrimitivos.CIRCULO)) {
                PainelDesenho p = new PainelDesenho(new JLabel(), tipo);
                p.setSize(160, 160);
                p.setCorAtual(Color.BLUE);
                p.setEspessuraAtual(3);
                p.setAlgoritmoCirculo(AlgoritmoCirculo.PARAMETRICO);
                assert !p.espelharSelecionado() : "sem selecao nao entra no modo";
                clicar(p, 20, 20);
                if (tipo != TiposPrimitivos.PONTO) clicar(p, 40, 20);
                if (tipo == TiposPrimitivos.TRIANGULO) clicar(p, 30, 40);
                p.salvarProjeto(arquivo);
                p.carregarProjeto(arquivo); // A operação também funciona com objetos importados.
                p.setEspelhamento(true);
                clicar(p, 70, 0);
                clicar(p, 70, 100);
                p.setTipo(TiposPrimitivos.SELECAO);
                clicar(p, 20, 20);
                assert p.espelharSelecionado();
                clicar(p, 0, 60);
                clicar(p, 0, 60);
                assert p.getQuantidadePontos() + p.getQuantidadePrimitivos() == 1;
                clicar(p, 100, 60);
                assert p.getQuantidadePontos() + p.getQuantidadePrimitivos() == 2 : "uma unica copia";
                p.salvarProjeto(arquivo);
                persistencia.PersistenciaProjeto.Cena cena =
                    persistencia.PersistenciaProjeto.carregar(arquivo, 160, 160);
                if (tipo == TiposPrimitivos.PONTO) {
                    igual(cena.getPontos().get(1), 20, 100);
                    assert cena.getPontos().get(0).getNomePto().equals(cena.getPontos().get(1).getNomePto());
                    assert cena.getPontos().get(1).getDiametro() == 5;
                } else {
                    PrimitivoGrafico copia = p.getPrimitivos().get(1);
                    assert copia.getCor().equals(Color.BLUE) && copia.getEspessura() == 3;
                    if (copia instanceof CirculoGrafico)
                        assert ((CirculoGrafico)copia).getAlgoritmo() == AlgoritmoCirculo.PARAMETRICO;
                }
                p.limpar();
                BufferedImage vazia = pintar(p);
                assert vazia.getRGB(70, 80) == p.getBackground().getRGB() : "limpar oculta guia";
                p.redesenhar();
                p.redesenhar();
                assert p.getQuantidadePontos() + p.getQuantidadePrimitivos() == 2;
                clicar(p, 20, 100);
                assert p.espelharSelecionado() : "copia pode ser fonte";
                clicar(p, 0, 0);
                assert p.excluirSelecionado();
                clicar(p, 100, 100);
                assert p.getQuantidadePontos() + p.getQuantidadePrimitivos() == 1 : "deletar fonte cancela";
                p.redesenhar();
                p.setTipo(TiposPrimitivos.PONTO);
                clicar(p, 30, 120);
                assert p.getQuantidadePontos() + p.getQuantidadePrimitivos() == 3 : "automatico independente";
                p.salvarProjeto(arquivo);
                assert persistencia.PersistenciaProjeto.carregar(arquivo, 160, 160).getPontos()
                    .stream().anyMatch(pt -> Math.abs(pt.getX() - 110) < 1e-8 && Math.abs(pt.getY() - 120) < 1e-8);
            }
            for (String cancelamento : Arrays.asList("escape", "ferramenta", "limpar", "abrir")) {
                PainelDesenho p = new PainelDesenho(new JLabel(), TiposPrimitivos.PONTO);
                p.setSize(160, 160);
                clicar(p, 20, 20);
                p.salvarProjeto(arquivo);
                p.setTipo(TiposPrimitivos.SELECAO);
                clicar(p, 20, 20);
                assert p.espelharSelecionado();
                clicar(p, 50, 0);
                switch (cancelamento) {
                    case "escape": p.getActionForKeyStroke(javax.swing.KeyStroke.getKeyStroke("ESCAPE"))
                        .actionPerformed(new java.awt.event.ActionEvent(p, 0, "escape")); break;
                    case "ferramenta": p.setTipo(TiposPrimitivos.SELECAO); break;
                    case "limpar": p.limpar(); break;
                    case "abrir": p.carregarProjeto(arquivo); break;
                    default: throw new AssertionError(cancelamento);
                }
                clicar(p, 50, 100);
                assert p.getQuantidadePontos() == 1 : cancelamento;
            }
            PainelDesenho p = new PainelDesenho(new JLabel(), TiposPrimitivos.PONTO);
            p.setSize(160, 160);
            p.setEspelhamento(true);
            clicar(p, 10, 0);
            p.limpar();
            clicar(p, 70, 0);
            clicar(p, 70, 100);
            clicar(p, 20, 30);
            assert p.getQuantidadePontos() == 2 : "eixo incompleto reinicia em p1";
            assert pintar(p).getRGB(120, 30) == Color.BLACK.getRGB();
        } finally { Files.deleteIfExists(arquivo); Files.deleteIfExists(persistencia.NomesProjeto.jpeg(arquivo)); }
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
