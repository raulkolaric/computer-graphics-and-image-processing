package tests;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JLabel;
import ponto.Ponto;
import reta.RetaGrafica;
import ui.PainelDesenho;
import ui.TiposPrimitivos;

/** Verifica que projetos inválidos não substituem a cena aberta. */
public class TestaImportacao {
    /** Executa as verificações de importação.
     * @param args argumentos não utilizados
     * @throws Exception se um arquivo temporário não puder ser processado
     */
    public static void main(String[] args) throws Exception {
        String original = Files.readString(Path.of("tests/fixtures/exemplo.json"));
        PainelDesenho painel = new PainelDesenho(new JLabel(), TiposPrimitivos.NENHUM);
        painel.setSize(900, 540);
        RetaGrafica existente = new RetaGrafica(new Ponto(1, 1), new Ponto(10, 10), Color.BLUE, 1);
        painel.adicionarPrimitivo(existente);
        Path arquivo = Files.createTempFile("importacao-", ".json");
        try {
            for (String invalido : new String[] {
                    original.replaceFirst("0.669", "3000.669"),
                    original.replaceFirst("0.643", "1000.643"),
                    original.replaceFirst("0.508", "3000.508"),
                    original.replace("0.744", "4000.744"),
                    original.replace("\"esp\": 12", "\"esp\": 0"),
                    original.replace("\"esp\": 50", "\"esp\": -1")}) {
                Files.writeString(arquivo, invalido);
                try {
                    painel.carregarProjeto(arquivo);
                    throw new AssertionError("Projeto invalido aceito");
                } catch (IOException esperado) {
                    if (painel.getQuantidadePrimitivos() != 1 || painel.getPrimitivos().get(0) != existente)
                        throw new AssertionError("Cena anterior alterada");
                }
            }
            for (String numero : new String[] {"1.62e-1", "1.62E-1", "162e-3", "0.0162e+1"}) {
                Files.writeString(arquivo, original.replaceFirst("0.162", numero));
                var cena = persistencia.PersistenciaProjeto.carregar(arquivo, 900, 540);
                if (cena.getPontos().get(0).getX() != 146) throw new AssertionError("Expoente incorreto");
            }
            for (String numero : new String[] {"1e", "1e+", "01", "1.", "1e309"}) {
                Files.writeString(arquivo, original.replaceFirst("0.162", numero));
                try {
                    painel.carregarProjeto(arquivo);
                    throw new AssertionError("Numero invalido aceito: " + numero);
                } catch (IOException esperado) { }
            }
        } finally {
            Files.deleteIfExists(arquivo);
        }
        System.out.println("TestaImportacao: todos os testes passaram");
    }
}
