package tests;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JLabel;
import circulo.AlgoritmoCirculo;
import circulo.CirculoGrafico;
import ponto.Ponto;
import reta.RetaGrafica;
import ui.PainelDesenho;
import ui.TiposPrimitivos;

/** Verifica a preservação da sobreposição ao salvar e abrir. */
public class TestaOrdem {
    /** Executa a regressão de ordem.
     * @param args argumentos não utilizados
     * @throws Exception se um arquivo temporário não puder ser processado
     */
    public static void main(String[] args) throws Exception {
        PainelDesenho painel = new PainelDesenho(new JLabel(), TiposPrimitivos.NENHUM);
        painel.setSize(100, 100);
        painel.adicionarPrimitivo(new CirculoGrafico(new Ponto(50, 50), new Ponto(70, 50),
            Color.RED, 1, AlgoritmoCirculo.SIMETRIA_OCTANTES));
        painel.adicionarPrimitivo(new RetaGrafica(new Ponto(70, 0), new Ponto(70, 99), Color.BLUE, 1));
        Path arquivo = Files.createTempFile("ordem-", ".json");
        try {
            verificarPixel(painel);
            painel.salvarProjeto(arquivo);
            String json = Files.readString(arquivo);
            painel.carregarProjeto(arquivo);
            verificarPixel(painel);
            for (String ordem : new String[] {"0", "-1", "2"}) {
                Files.writeString(arquivo, json.replace("\"ordem\": 1", "\"ordem\": " + ordem));
                try {
                    painel.carregarProjeto(arquivo);
                    throw new AssertionError("Ordem invalida aceita");
                } catch (java.io.IOException esperado) { }
            }
        } finally { Files.deleteIfExists(arquivo); }
        System.out.println("TestaOrdem: todos os testes passaram");
    }

    private static void verificarPixel(PainelDesenho painel) {
        BufferedImage imagem = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics g = imagem.getGraphics();
        painel.paint(g);
        g.dispose();
        if (imagem.getRGB(70, 50) != Color.BLUE.getRGB())
            throw new AssertionError("Reta deixou de cobrir o circulo");
    }
}
