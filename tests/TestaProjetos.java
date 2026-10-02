package tests;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import circulo.AlgoritmoCirculo;
import circulo.CirculoGrafico;
import persistencia.PersistenciaProjeto;
import ponto.Ponto;
import reta.RetaGrafica;
import retangulo.Retangulo;
import triangulo.Triangulo;
import ui.PainelDesenho;
import ui.TiposPrimitivos;

/** Regressões de memória retida, subconjuntos exibidos e salvamento completo. */
public class TestaProjetos {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try { testar(); } catch (Exception erro) { throw new AssertionError(erro); }
        });
        System.out.println("TestaProjetos: OK");
    }

    private static void testar() throws Exception {
        testarNomes();
        Path pasta = Files.createTempDirectory("projetos-");
        Path a = pasta.resolve("A.json"), b = pasta.resolve("B.json");
        try {
            PainelDesenho p = painel();
            adicionar(p, Color.BLACK, 10);
            p.salvarProjeto(a);
            p.limpar();
            adicionar(p, Color.RED, 40);
            p.salvarProjeto(b);
            verificar(b, 0, Color.RED);
            assert p.getQuantidadePrimitivos() == 2 : "salvar nao poda memoria";
            p.salvarProjeto(a);
            verificar(a, 0, Color.RED);
            p.redesenhar();
            p.redesenhar();
            p.salvarProjeto(a);
            p.salvarProjeto(b);
            verificar(a, 0, Color.BLACK, Color.RED);
            verificar(b, 0, Color.BLACK, Color.RED);
            adicionar(p, Color.BLUE, 70);
            p.salvarProjeto(a);
            verificar(a, 0, Color.BLACK, Color.RED, Color.BLUE);
            p.carregarProjeto(b);
            assert p.getQuantidadePrimitivos() == 2 : "abrir substitui memoria";
            p.salvarProjeto(b);
            p.salvarProjeto(a);
            verificar(a, 0, Color.BLACK, Color.RED);
            p.setTipo(TiposPrimitivos.SELECAO);
            clicar(p, 10, 10);
            assert p.excluirSelecionado();
            p.redesenhar();
            p.salvarProjeto(a);
            verificar(a, 0, Color.RED);

            p.setTipo(TiposPrimitivos.PONTO);
            clicar(p, 150, 150);
            p.adicionarPrimitivo(new Retangulo(new Ponto(80, 80), new Ponto(90, 90), Color.BLUE, 3));
            p.adicionarPrimitivo(new Triangulo(new Ponto(100, 100), new Ponto(110, 100),
                new Ponto(105, 110), Color.GREEN, 2));
            p.adicionarPrimitivo(new CirculoGrafico(new Ponto(120, 120), new Ponto(130, 120),
                Color.MAGENTA, 4, AlgoritmoCirculo.PARAMETRICO));
            for (TiposPrimitivos filtro : TiposPrimitivos.values()) {
                p.redesenhar(filtro);
                p.salvarProjeto(a);
                PersistenciaProjeto.Cena cena = PersistenciaProjeto.carregar(a, 200, 200);
                assert cena.getPontos().size() == (filtro == TiposPrimitivos.PONTO ? 1 : 0);
                int esperado = filtro == TiposPrimitivos.RETA || filtro == TiposPrimitivos.RETANGULO
                    || filtro == TiposPrimitivos.TRIANGULO || filtro == TiposPrimitivos.CIRCULO ? 1 : 0;
                assert cena.getPrimitivos().size() == esperado : filtro;
                if (filtro == TiposPrimitivos.CIRCULO) {
                    CirculoGrafico c = (CirculoGrafico)cena.getPrimitivos().get(0);
                    assert c.getAlgoritmo() == AlgoritmoCirculo.PARAMETRICO && c.getEspessura() == 4;
                    assert Math.abs(c.getRaio() - 10) < 1e-8;
                }
                assert p.getQuantidadePrimitivos() == 4 && p.getQuantidadePontos() == 1;
            }
            p.limpar();
            p.salvarProjeto(a);
            verificar(a, 0);
            p.redesenhar();
            p.salvarProjeto(a);
            verificar(a, 1, Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA);
            p.setTipo(TiposPrimitivos.RETA);
            clicar(p, 5, 5);
            p.salvarProjeto(b);
            verificar(b, 1, Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA);
            try { p.salvarProjeto(pasta); throw new AssertionError("escrita deveria falhar"); }
            catch (IOException esperado) { }
            assert p.getQuantidadePrimitivos() == 4;
            clicar(p, 15, 5);
            assert p.getQuantidadePrimitivos() == 5 : "salvar preserva construcao incompleta";
            clicar(p, 30, 30);
            p.redesenhar();
            clicar(p, 40, 40);
            assert p.getQuantidadePrimitivos() == 5 : "redesenhar cancela previa";
            Files.writeString(b, "{invalido}");
            try { p.carregarProjeto(b); throw new AssertionError("importacao deveria falhar"); }
            catch (IOException esperado) { }
            clicar(p, 50, 50);
            assert p.getQuantidadePrimitivos() == 6 : "importacao invalida preserva interacao";
            p.carregarProjeto(a);
            assert p.getQuantidadePrimitivos() == 4;
        } finally {
            try (java.util.stream.Stream<Path> arquivos = Files.list(pasta)) {
                for (Path arquivo : (Iterable<Path>)arquivos::iterator) Files.delete(arquivo);
            }
            Files.delete(pasta);
        }
    }

    private static void testarNomes() throws Exception {
        Path pasta = Files.createTempDirectory("nomes-projeto-");
        try {
            Path ausente = pasta.resolve("ausente");
            assert persistencia.NomesProjeto.proximo(ausente).getFileName().toString().equals("Projeto 1.json");
            assert !Files.exists(ausente) : "sugerir nao cria diretorio";
            assert persistencia.NomesProjeto.proximo(pasta).getFileName().toString().equals("Projeto 1.json");
            for (String nome : new String[] {"Projeto 1.json", "pRoJeTo 4.JPEG", "Projeto 7.JPG",
                    "Outro 90.json", "Projeto -9.json", "Projeto 8x.json", "Projeto 0.json", "Projeto 09.txt"})
                Files.writeString(pasta.resolve(nome), "");
            Path sugestao = persistencia.NomesProjeto.proximo(pasta);
            assert sugestao.getFileName().toString().equals("Projeto 8.json") : sugestao;
            assert sugestao.equals(persistencia.NomesProjeto.proximo(pasta)) : "sem contador de sessao";
            assert persistencia.NomesProjeto.conflitos(sugestao).isEmpty();
            Files.writeString(persistencia.NomesProjeto.jpeg(sugestao), "imagem orfa");
            assert persistencia.NomesProjeto.conflitos(sugestao).size() == 1 : "reconsulta colisao tardia";
            Files.writeString(sugestao, "");
            assert persistencia.NomesProjeto.conflitos(sugestao).size() == 2;
            assert persistencia.NomesProjeto.proximo(pasta).getFileName().toString().equals("Projeto 9.json");
            for (String nome : new String[] {"copia.JSON", "copia.jpeg", "copia.JPG", "copia"})
                assert persistencia.NomesProjeto.json(pasta.resolve(nome)).equals(pasta.resolve("copia.json"));
            Files.writeString(pasta.resolve("Projeto 9223372036854775807.json"), "");
            assert persistencia.NomesProjeto.proximo(pasta).getFileName().toString()
                .equals("Projeto 9223372036854775808.json") : "sem overflow de long";
        } finally {
            try (java.util.stream.Stream<Path> arquivos = Files.list(pasta)) {
                for (Path arquivo : (Iterable<Path>)arquivos::iterator) Files.delete(arquivo);
            }
            Files.delete(pasta);
        }
    }
    private static PainelDesenho painel() {
        PainelDesenho p = new PainelDesenho(new JLabel(), TiposPrimitivos.NENHUM);
        p.setSize(200, 200);
        return p;
    }
    private static void clicar(PainelDesenho p, int x, int y) {
        p.mousePressed(new MouseEvent(p, MouseEvent.MOUSE_PRESSED, 0, 0, x, y, 1, false));
    }
    private static void adicionar(PainelDesenho p, Color cor, int x) {
        p.adicionarPrimitivo(new RetaGrafica(new Ponto(x, x), new Ponto(x + 5, x + 5), cor, 1));
    }
    private static void verificar(Path arquivo, int pontos, Color... cores) throws Exception {
        PersistenciaProjeto.Cena cena = PersistenciaProjeto.carregar(arquivo, 200, 200);
        assert cena.getPontos().size() == pontos;
        assert cena.getPrimitivos().size() == cores.length : arquivo;
        for (int i = 0; i < cores.length; i++) assert cena.getPrimitivos().get(i).getCor().equals(cores[i]);
    }
}
