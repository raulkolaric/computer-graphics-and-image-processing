package tests;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JLabel;
import ponto.Ponto;
import reta.RetaGrafica;
import ui.PainelDesenho;
import ui.TiposPrimitivos;

/** Verifica que Limpar separa os projetos salvos e abertos. */
public class TestaProjetos {
    public static void main(String[] args) throws Exception {
        Path a = Files.createTempFile("projeto-a-", ".json");
        Path b = Files.createTempFile("projeto-b-", ".json");
        try {
            PainelDesenho painel = new PainelDesenho(new JLabel(), TiposPrimitivos.NENHUM);
            painel.setSize(200, 200);
            painel.adicionarPrimitivo(new RetaGrafica(
                new Ponto(1, 1), new Ponto(10, 10), Color.BLACK, 1));
            painel.salvarProjeto(a);
            painel.limpar();
            painel.adicionarPrimitivo(new RetaGrafica(
                new Ponto(20, 20), new Ponto(30, 30), Color.RED, 1));
            painel.salvarProjeto(b);

            painel.carregarProjeto(a);
            verificar(painel, Color.BLACK);
            painel.carregarProjeto(b);
            verificar(painel, Color.RED);
            painel.carregarProjeto(a);
            verificar(painel, Color.BLACK);
            System.out.println("TestaProjetos: todos os testes passaram");
        } finally {
            Files.deleteIfExists(a);
            Files.deleteIfExists(b);
        }
    }

    private static void verificar(PainelDesenho painel, Color cor) {
        if (painel.getQuantidadePrimitivos() != 1
                || !painel.getPrimitivos().get(0).getCor().equals(cor)) {
            throw new AssertionError("Projeto aberto contém formas de outro projeto");
        }
    }
}
