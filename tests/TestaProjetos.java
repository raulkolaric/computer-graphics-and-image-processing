package tests;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JLabel;
import ponto.Ponto;
import reta.RetaGrafica;
import ui.PainelDesenho;
import ui.TiposPrimitivos;

/** Verifica que novos nomes salvam somente formas novas, sem exigir Limpar. */
public class TestaProjetos {
    public static void main(String[] args) throws Exception {
        Path a = Files.createTempFile("projeto-a-", ".json");
        Path b = Files.createTempFile("projeto-b-", ".json");
        Path c = Files.createTempFile("projeto-c-", ".json");
        Path copia = Files.createTempFile("projeto-copia-", ".json");
        try {
            PainelDesenho painel = new PainelDesenho(new JLabel(), TiposPrimitivos.NENHUM);
            painel.setSize(200, 200);
            adicionar(painel, Color.BLACK, 1);
            painel.salvarProjeto(a);
            adicionar(painel, Color.RED, 20);
            painel.salvarProjeto(b);
            adicionar(painel, Color.BLUE, 40);
            painel.salvarProjeto(b); // Atualiza B sem incorporar A.
            adicionar(painel, Color.GREEN, 60);
            painel.salvarProjeto(c);
            painel.salvarProjeto(a); // Voltar a A preserva seus objetos.

            verificar(a, Color.BLACK);
            verificar(b, Color.RED, Color.BLUE);
            verificar(c, Color.GREEN);
            painel.carregarProjeto(b);
            painel.salvarProjeto(copia); // Primeira gravação após abrir é uma cópia completa.
            verificar(copia, Color.RED, Color.BLUE);
            painel.limpar();
            adicionar(painel, Color.MAGENTA, 80);
            painel.salvarProjeto(c);
            verificar(c, Color.MAGENTA);
            verificar(a, Color.BLACK);
            System.out.println("TestaProjetos: todos os testes passaram");
        } finally {
            Files.deleteIfExists(a);
            Files.deleteIfExists(b);
            Files.deleteIfExists(c);
            Files.deleteIfExists(copia);
        }
    }

    private static void adicionar(PainelDesenho painel, Color cor, int x) {
        painel.adicionarPrimitivo(new RetaGrafica(
            new Ponto(x, x), new Ponto(x + 5, x + 5), cor, 1));
    }

    private static void verificar(Path arquivo, Color... cores) throws Exception {
        PainelDesenho painel = new PainelDesenho(new JLabel(), TiposPrimitivos.NENHUM);
        painel.setSize(200, 200);
        painel.carregarProjeto(arquivo);
        if (painel.getQuantidadePrimitivos() != cores.length) {
            throw new AssertionError("Projeto contém formas de outro arquivo: " + arquivo);
        }
        for (int i = 0; i < cores.length; i++) {
            if (!painel.getPrimitivos().get(i).getCor().equals(cores[i])) {
                throw new AssertionError("Projeto contém forma inesperada: " + arquivo);
            }
        }
    }
}
