package tests;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import circulo.AlgoritmoCirculo;
import circulo.CirculoGrafico;
import persistencia.PersistenciaProjeto;
import ponto.Ponto;
import renderizacao.PrimitivoGrafico;

/** Verifica que a escala não depende da direção do ponto de raio. */
public class TestaEscalaCirculo {
    /** Executa a regressão de escala e compatibilidade.
     * @param args argumentos não utilizados
     * @throws Exception se um arquivo temporário não puder ser processado
     */
    public static void main(String[] args) throws Exception {
        List<PrimitivoGrafico> circulos = new ArrayList<PrimitivoGrafico>();
        for (Ponto p : new Ponto[] {new Ponto(70, 50), new Ponto(50, 70), new Ponto(62, 66)}) {
            circulos.add(new CirculoGrafico(new Ponto(50, 50), p,
                Color.RED, 1, AlgoritmoCirculo.SIMETRIA_OCTANTES));
        }
        Path arquivo = Files.createTempFile("escala-", ".json");
        try {
            PersistenciaProjeto.salvar(arquivo, Collections.emptyList(), circulos, 100, 100);
            String json = Files.readString(arquivo);
            for (int[] tamanho : new int[][] {{200, 100}, {100, 200}, {200, 200}}) {
                Files.writeString(arquivo, json);
                var cena = PersistenciaProjeto.carregar(arquivo, tamanho[0], tamanho[1]);
                double esperado = 0.2 * Math.min(tamanho[0], tamanho[1]);
                for (PrimitivoGrafico forma : cena.getPrimitivos()) {
                    CirculoGrafico c = (CirculoGrafico)forma;
                    if (Math.abs(c.getRaio() - esperado) > 0.000001)
                        throw new AssertionError("Escala depende do segundo clique");
                    if (c.getCentro().getX() != tamanho[0] / 2.0 || c.getCentro().getY() != tamanho[1] / 2.0)
                        throw new AssertionError("Centro perdeu a proporcao");
                }
                PersistenciaProjeto.salvar(arquivo, cena.getPontos(), cena.getPrimitivos(), tamanho[0], tamanho[1]);
                var reaberta = PersistenciaProjeto.carregar(arquivo, 100, 100);
                if (Math.abs(((CirculoGrafico)reaberta.getPrimitivos().get(0)).getRaio() - 20) > 0.000001)
                    throw new AssertionError("Raio mudou apos salvar novamente");
            }
            for (String invalido : new String[] {"-1", "\"texto\"", "1000000"}) {
                Files.writeString(arquivo, json.replace("\"raioRelativo\": 0.2", "\"raioRelativo\": " + invalido));
                try {
                    PersistenciaProjeto.carregar(arquivo, 100, 100);
                    throw new AssertionError("Raio invalido aceito");
                } catch (java.io.IOException esperado) { }
            }
            Files.writeString(arquivo, json.replace(", \"raioRelativo\": 0.2", ""));
            if (((CirculoGrafico)PersistenciaProjeto.carregar(arquivo, 100, 100).getPrimitivos().get(0)).getRaio() != 20)
                throw new AssertionError("Arquivo anterior deixou de abrir");
        } finally { Files.deleteIfExists(arquivo); }
        System.out.println("TestaEscalaCirculo: todos os testes passaram");
    }
}
