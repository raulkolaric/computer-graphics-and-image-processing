package persistencia;

import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import circulo.AlgoritmoCirculo;
import circulo.CirculoGrafico;
import ponto.Ponto;
import ponto.PontoGr;
import quadrado.Retangulo;
import renderizacao.PrimitivoGrafico;
import reta.EstiloReta;
import reta.RetaGrafica;
import triangulo.Triangulo;

/** Le e grava a cena em um JSON independente de bibliotecas externas. */
public final class PersistenciaProjeto {
    private PersistenciaProjeto() { }

    public static void salvar(Path arquivo, List<PontoGr> pontos,
                              List<PrimitivoGrafico> primitivos) throws IOException {
        if (arquivo == null || pontos == null || primitivos == null) {
            throw new IllegalArgumentException("Arquivo e cena sao obrigatorios");
        }
        StringBuilder json = new StringBuilder("{\n  \"versao\": 1,\n  \"pontos\": [");
        for (int i = 0; i < pontos.size(); i++) {
            if (i > 0) json.append(',');
            PontoGr ponto = pontos.get(i);
            json.append("\n    {").append(pontoJson(ponto))
                .append(", \"nome\": \"").append(escapar(ponto.getNomePto()))
                .append("\", \"diametro\": ").append(ponto.getDiametro()).append("}");
        }
        json.append("\n  ],\n  \"primitivos\": [");
        for (int i = 0; i < primitivos.size(); i++) {
            if (i > 0) json.append(',');
            json.append("\n    ").append(primitivoJson(primitivos.get(i)));
        }
        json.append("\n  ]\n}\n");
        Files.write(arquivo, json.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static Cena carregar(Path arquivo) throws IOException {
        Object raiz = new LeitorJson(Files.readString(arquivo, StandardCharsets.UTF_8)).ler();
        Map<String, Object> projeto = objeto(raiz, "O JSON deve conter um objeto raiz");
        if (numero(projeto.get("versao"), "versao") != 1) {
            throw new IOException("Versao de projeto nao suportada");
        }
        List<PontoGr> pontos = new ArrayList<PontoGr>();
        for (Object item : lista(projeto.get("pontos"), "pontos")) {
            Map<String, Object> dado = objeto(item, "Ponto invalido");
            Ponto p = ponto(dado);
            pontos.add(new PontoGr((int)Math.round(p.getX()), (int)Math.round(p.getY()),
                cor(dado), texto(dado.get("nome"), "nome"), numero(dado.get("diametro"), "diametro")));
        }
        List<PrimitivoGrafico> primitivos = new ArrayList<PrimitivoGrafico>();
        for (Object item : lista(projeto.get("primitivos"), "primitivos")) {
            primitivos.add(lerPrimitivo(objeto(item, "Primitivo invalido")));
        }
        return new Cena(pontos, primitivos);
    }

    private static String primitivoJson(PrimitivoGrafico primitivo) {
        String atributos = "\"cor\": \"" + corTexto(primitivo.getCor())
            + "\", \"espessura\": " + primitivo.getEspessura();
        if (primitivo instanceof RetaGrafica) {
            RetaGrafica reta = (RetaGrafica)primitivo;
            return "{\"tipo\": \"reta\", " + pontosJson(reta.getP1(), reta.getP2()) + ", " + atributos + "}";
        }
        if (primitivo instanceof Retangulo) {
            Retangulo retangulo = (Retangulo)primitivo;
            return "{\"tipo\": \"retangulo\", " + pontosJson(retangulo.getCanto1(), retangulo.getCanto2()) + ", " + atributos + "}";
        }
        if (primitivo instanceof Triangulo) {
            List<Ponto> vertices = ((Triangulo)primitivo).getVertices();
            return "{\"tipo\": \"triangulo\", " + pontosJson(vertices.get(0), vertices.get(1), vertices.get(2)) + ", " + atributos + "}";
        }
        if (primitivo instanceof CirculoGrafico) {
            CirculoGrafico circulo = (CirculoGrafico)primitivo;
            return "{\"tipo\": \"circulo\", " + pontosJson(circulo.getCentro(), circulo.getPontoRaio()) + ", " + atributos
                + ", \"algoritmo\": \"" + circulo.getAlgoritmo().name() + "\"}";
        }
        throw new IllegalArgumentException("Primitivo nao suportado: " + primitivo.getClass().getName());
    }

    private static PrimitivoGrafico lerPrimitivo(Map<String, Object> dado) throws IOException {
        String tipo = texto(dado.get("tipo"), "tipo");
        List<Object> dadosPontos = lista(dado.get("pontos"), "pontos do primitivo");
        Color cor = cor(dado);
        EstiloReta estilo = new EstiloReta(cor, numero(dado.get("espessura"), "espessura"));
        if ("reta".equals(tipo) && dadosPontos.size() == 2) return new RetaGrafica(ponto(dadosPontos.get(0)), ponto(dadosPontos.get(1)), estilo);
        if ("retangulo".equals(tipo) && dadosPontos.size() == 2) return new Retangulo(ponto(dadosPontos.get(0)), ponto(dadosPontos.get(1)), estilo);
        if ("triangulo".equals(tipo) && dadosPontos.size() == 3) return new Triangulo(ponto(dadosPontos.get(0)), ponto(dadosPontos.get(1)), ponto(dadosPontos.get(2)), estilo);
        if ("circulo".equals(tipo) && dadosPontos.size() == 2) {
            try { return new CirculoGrafico(ponto(dadosPontos.get(0)), ponto(dadosPontos.get(1)), estilo,
                AlgoritmoCirculo.valueOf(texto(dado.get("algoritmo"), "algoritmo"))); }
            catch (IllegalArgumentException erro) { throw new IOException("Algoritmo de circulo invalido", erro); }
        }
        throw new IOException("Primitivo invalido ou com quantidade de pontos incorreta: " + tipo);
    }

    private static String pontosJson(Ponto... pontos) {
        StringBuilder resultado = new StringBuilder("\"pontos\": [");
        for (int i = 0; i < pontos.length; i++) { if (i > 0) resultado.append(", "); resultado.append("{").append(pontoJson(pontos[i])).append("}"); }
        return resultado.append("]").toString();
    }
    private static String pontoJson(Ponto ponto) { return "\"x\": " + ponto.getX() + ", \"y\": " + ponto.getY() + ", \"cor\": \"" + corTexto(ponto instanceof PontoGr ? ((PontoGr)ponto).getCorPto() : Color.BLACK) + "\""; }
    private static String corTexto(Color cor) { return String.format("#%08X", cor.getRGB()); }
    private static String escapar(String texto) { return texto.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r"); }
    @SuppressWarnings("unchecked")
    private static Map<String, Object> objeto(Object valor, String mensagem) throws IOException { if (!(valor instanceof Map)) throw new IOException(mensagem); return (Map<String, Object>)valor; }
    @SuppressWarnings("unchecked")
    private static List<Object> lista(Object valor, String nome) throws IOException { if (!(valor instanceof List)) throw new IOException("Campo obrigatorio invalido: " + nome); return (List<Object>)valor; }
    private static String texto(Object valor, String nome) throws IOException { if (!(valor instanceof String)) throw new IOException("Campo obrigatorio invalido: " + nome); return (String)valor; }
    private static int numero(Object valor, String nome) throws IOException { if (!(valor instanceof Number) || ((Number)valor).doubleValue() != Math.rint(((Number)valor).doubleValue())) throw new IOException("Campo inteiro invalido: " + nome); return ((Number)valor).intValue(); }
    private static Ponto ponto(Object valor) throws IOException { return ponto(objeto(valor, "Coordenada invalida")); }
    private static Ponto ponto(Map<String, Object> dado) throws IOException { Object x = dado.get("x"), y = dado.get("y"); if (!(x instanceof Number) || !(y instanceof Number)) throw new IOException("Coordenadas invalidas"); return new Ponto(((Number)x).doubleValue(), ((Number)y).doubleValue()); }
    private static Color cor(Map<String, Object> dado) throws IOException { String valor = texto(dado.get("cor"), "cor"); try { if (!valor.matches("#[0-9A-Fa-f]{8}")) throw new NumberFormatException(); return new Color((int)Long.parseLong(valor.substring(1), 16), true); } catch (NumberFormatException erro) { throw new IOException("Cor invalida", erro); } }

    public static final class Cena {
        private final List<PontoGr> pontos;
        private final List<PrimitivoGrafico> primitivos;
        private Cena(List<PontoGr> pontos, List<PrimitivoGrafico> primitivos) { this.pontos = Collections.unmodifiableList(pontos); this.primitivos = Collections.unmodifiableList(primitivos); }
        public List<PontoGr> getPontos() { return pontos; }
        public List<PrimitivoGrafico> getPrimitivos() { return primitivos; }
    }

    /** Pequeno parser JSON para o formato do projeto (objetos, listas, numeros e textos). */
    private static final class LeitorJson {
        private final String fonte; private int posicao;
        LeitorJson(String fonte) { this.fonte = fonte; }
        Object ler() throws IOException { Object valor = valor(); espacos(); if (posicao != fonte.length()) throw erro("Conteudo extra"); return valor; }
        private Object valor() throws IOException { espacos(); if (posicao >= fonte.length()) throw erro("Fim inesperado"); char c = fonte.charAt(posicao); if (c == '{') return objeto(); if (c == '[') return lista(); if (c == '\"') return texto(); if (c == '-' || Character.isDigit(c)) return numero(); throw erro("Valor JSON invalido"); }
        private Map<String, Object> objeto() throws IOException { Map<String, Object> resultado = new LinkedHashMap<String, Object>(); consumir('{'); espacos(); if (proximo('}')) { posicao++; return resultado; } do { espacos(); String chave = texto(); espacos(); consumir(':'); resultado.put(chave, valor()); espacos(); } while (consumirSe(',')); consumir('}'); return resultado; }
        private List<Object> lista() throws IOException { List<Object> resultado = new ArrayList<Object>(); consumir('['); espacos(); if (proximo(']')) { posicao++; return resultado; } do { resultado.add(valor()); espacos(); } while (consumirSe(',')); consumir(']'); return resultado; }
        private String texto() throws IOException { consumir('\"'); StringBuilder resultado = new StringBuilder(); while (posicao < fonte.length()) { char c = fonte.charAt(posicao++); if (c == '\"') return resultado.toString(); if (c == '\\') { if (posicao >= fonte.length()) throw erro("Escape incompleto"); char e = fonte.charAt(posicao++); if (e == 'n') resultado.append('\n'); else if (e == 'r') resultado.append('\r'); else if (e == '\"' || e == '\\' || e == '/') resultado.append(e); else throw erro("Escape invalido"); } else resultado.append(c); } throw erro("Texto nao terminado"); }
        private Number numero() throws IOException { int inicio = posicao; if (proximo('-')) posicao++; while (posicao < fonte.length() && Character.isDigit(fonte.charAt(posicao))) posicao++; if (proximo('.')) { posicao++; while (posicao < fonte.length() && Character.isDigit(fonte.charAt(posicao))) posicao++; } try { return Double.valueOf(fonte.substring(inicio, posicao)); } catch (NumberFormatException erro) { throw erro("Numero invalido"); } }
        private void espacos() { while (posicao < fonte.length() && Character.isWhitespace(fonte.charAt(posicao))) posicao++; }
        private void consumir(char esperado) throws IOException { espacos(); if (!proximo(esperado)) throw erro("Esperado '" + esperado + "'"); posicao++; }
        private boolean consumirSe(char c) { espacos(); if (!proximo(c)) return false; posicao++; return true; }
        private boolean proximo(char c) { return posicao < fonte.length() && fonte.charAt(posicao) == c; }
        private IOException erro(String mensagem) { return new IOException(mensagem + " na posicao " + posicao); }
    }
}
