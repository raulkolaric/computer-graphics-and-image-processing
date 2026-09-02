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
import java.util.Locale;
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

/** Lê e grava a cena no formato JSON proposto para a atividade. */
public final class PersistenciaProjeto {
    private PersistenciaProjeto() { }

    /** Grava a cena usando coordenadas na escala informada.
     * @param arquivo arquivo de destino
     * @param pontos pontos armazenados
     * @param primitivos formas armazenadas
     * @param largura largura da área de desenho em pixels
     * @param altura altura da área de desenho em pixels
     * @throws IOException se o arquivo não puder ser gravado
     * @throws IllegalArgumentException se algum argumento for inválido
     */
    public static void salvar(Path arquivo, List<PontoGr> pontos,
                              List<PrimitivoGrafico> primitivos,
                              int largura, int altura) throws IOException {
        validarArgumentos(arquivo, pontos, primitivos, largura, altura);
        StringBuilder json = new StringBuilder("{\n  \"figura\": {\n");
        json.append("    \"ponto\": [");
        for (int i = 0; i < pontos.size(); i++) {
            if (i > 0) json.append(',');
            PontoGr ponto = pontos.get(i);
            json.append("\n      {").append(pontoJson(ponto, largura, altura))
                .append(", \"cor\": ").append(corJson(ponto.getCorPto()))
                .append(", \"esp\": ").append(ponto.getDiametro())
                .append(", \"id\": \"ponto_").append(i + 1).append("\"")
                .append(", \"nome\": \"").append(escapar(ponto.getNomePto()))
                .append("\"}");
        }
        json.append("\n    ],\n");
        adicionarLista(json, "reta", RetaGrafica.class, primitivos, largura, altura);
        json.append(",\n");
        adicionarLista(json, "triangulo", Triangulo.class, primitivos, largura, altura);
        json.append(",\n");
        adicionarLista(json, "retangulo", Retangulo.class, primitivos, largura, altura);
        json.append(",\n");
        adicionarLista(json, "circulo", CirculoGrafico.class, primitivos, largura, altura);
        json.append("\n  }\n}\n");
        Files.write(arquivo, json.toString().getBytes(StandardCharsets.UTF_8));
    }

    /** Grava a cena sem normalizar as coordenadas.
     * @param arquivo arquivo de destino
     * @param pontos pontos armazenados
     * @param primitivos formas armazenadas
     * @throws IOException se o arquivo não puder ser gravado
     */
    public static void salvar(Path arquivo, List<PontoGr> pontos,
                              List<PrimitivoGrafico> primitivos) throws IOException {
        salvar(arquivo, pontos, primitivos, 1, 1);
    }

    /** Carrega uma cena e converte coordenadas relativas para pixels.
     * Também aceita o formato absoluto usado pela primeira versão da persistência.
     * @param arquivo arquivo de origem
     * @param largura largura da área de desenho em pixels
     * @param altura altura da área de desenho em pixels
     * @return cena carregada
     * @throws IOException se o arquivo não puder ser lido ou contiver dados inválidos
     */
    public static Cena carregar(Path arquivo, int largura, int altura) throws IOException {
        if (arquivo == null) {
            throw new IllegalArgumentException("O arquivo e obrigatorio");
        }
        validarDimensoes(largura, altura);
        Object raiz = new LeitorJson(Files.readString(arquivo, StandardCharsets.UTF_8)).ler();
        Map<String, Object> projeto = objeto(raiz, "O JSON deve conter um objeto raiz");
        if (projeto.containsKey("figura")) {
            return carregarFigura(objeto(projeto.get("figura"), "Figura invalida"),
                largura, altura);
        }
        return carregarLegado(projeto);
    }

    /** Carrega uma cena sem alterar a escala das coordenadas.
     * @param arquivo arquivo de origem
     * @return cena carregada
     * @throws IOException se o arquivo não puder ser lido ou contiver dados inválidos
     */
    public static Cena carregar(Path arquivo) throws IOException {
        return carregar(arquivo, 1, 1);
    }

    private static Cena carregarFigura(Map<String, Object> figura,
            int largura, int altura) throws IOException {
        List<PontoGr> pontos = new ArrayList<PontoGr>();
        for (Object item : lista(figura.get("ponto"), "ponto")) {
            Map<String, Object> dado = objeto(item, "Ponto invalido");
            Ponto p = pontoRelativo(dado, largura, altura);
            String nome = dado.get("nome") instanceof String ? (String)dado.get("nome") : "";
            pontos.add(new PontoGr((int)Math.round(p.getX()), (int)Math.round(p.getY()),
                cor(dado), nome, numero(dado.get("esp"), "esp")));
        }

        List<PrimitivoGrafico> primitivos = new ArrayList<PrimitivoGrafico>();
        for (Object item : lista(figura.get("reta"), "reta")) {
            Map<String, Object> dado = objeto(item, "Reta invalida");
            EstiloReta estilo = estilo(dado);
            primitivos.add(new RetaGrafica(pontoRelativo(dado.get("p1"), largura, altura),
                pontoRelativo(dado.get("p2"), largura, altura), estilo));
        }
        for (Object item : lista(figura.get("triangulo"), "triangulo")) {
            Map<String, Object> dado = objeto(item, "Triangulo invalido");
            EstiloReta estilo = estilo(dado);
            primitivos.add(new Triangulo(pontoRelativo(dado.get("p1"), largura, altura),
                pontoRelativo(dado.get("p2"), largura, altura),
                pontoRelativo(dado.get("p3"), largura, altura), estilo));
        }
        for (Object item : lista(figura.get("retangulo"), "retangulo")) {
            Map<String, Object> dado = objeto(item, "Retangulo invalido");
            primitivos.add(new Retangulo(pontoRelativo(dado.get("p1"), largura, altura),
                pontoRelativo(dado.get("p2"), largura, altura), estilo(dado)));
        }
        for (Object item : lista(figura.get("circulo"), "circulo")) {
            Map<String, Object> dado = objeto(item, "Circulo invalido");
            AlgoritmoCirculo algoritmo = AlgoritmoCirculo.SIMETRIA_OCTANTES;
            if (dado.get("algoritmo") instanceof String) {
                try {
                    algoritmo = AlgoritmoCirculo.valueOf((String)dado.get("algoritmo"));
                } catch (IllegalArgumentException erro) {
                    throw new IOException("Algoritmo de circulo invalido", erro);
                }
            }
            primitivos.add(new CirculoGrafico(
                pontoRelativo(dado.get("centro"), largura, altura),
                pontoRelativo(dado.get("raio"), largura, altura), estilo(dado), algoritmo));
        }
        return new Cena(pontos, primitivos);
    }

    private static Cena carregarLegado(Map<String, Object> projeto) throws IOException {
        if (numero(projeto.get("versao"), "versao") != 1) {
            throw new IOException("Versao de projeto nao suportada");
        }
        List<PontoGr> pontos = new ArrayList<PontoGr>();
        for (Object item : lista(projeto.get("pontos"), "pontos")) {
            Map<String, Object> dado = objeto(item, "Ponto invalido");
            Ponto p = ponto(dado);
            pontos.add(new PontoGr((int)Math.round(p.getX()), (int)Math.round(p.getY()),
                corLegada(dado), texto(dado.get("nome"), "nome"),
                numero(dado.get("diametro"), "diametro")));
        }
        List<PrimitivoGrafico> primitivos = new ArrayList<PrimitivoGrafico>();
        for (Object item : lista(projeto.get("primitivos"), "primitivos")) {
            primitivos.add(lerPrimitivo(objeto(item, "Primitivo invalido")));
        }
        return new Cena(pontos, primitivos);
    }

    private static void adicionarLista(StringBuilder json, String nome, Class<?> classe,
            List<PrimitivoGrafico> primitivos, int largura, int altura) {
        json.append("    \"").append(nome).append("\": [");
        int indice = 0;
        for (PrimitivoGrafico primitivo : primitivos) {
            if (!classe.isInstance(primitivo)) {
                continue;
            }
            if (indice > 0) json.append(',');
            indice++;
            json.append("\n      ").append(primitivoJson(
                primitivo, nome + "_" + indice, largura, altura));
        }
        json.append("\n    ]");
    }

    private static String primitivoJson(PrimitivoGrafico primitivo, String id,
            int largura, int altura) {
        String atributos = ", \"cor\": " + corJson(primitivo.getCor())
            + ", \"esp\": " + primitivo.getEspessura() + ", \"id\": \"" + id + "\"";
        if (primitivo instanceof RetaGrafica) {
            RetaGrafica reta = (RetaGrafica)primitivo;
            return "{" + pontoNomeadoJson("p1", reta.getP1(), largura, altura)
                + ", " + pontoNomeadoJson("p2", reta.getP2(), largura, altura)
                + atributos + "}";
        }
        if (primitivo instanceof Retangulo) {
            Retangulo retangulo = (Retangulo)primitivo;
            return "{" + pontoNomeadoJson("p1", retangulo.getCanto1(), largura, altura)
                + ", " + pontoNomeadoJson("p2", retangulo.getCanto2(), largura, altura)
                + atributos + "}";
        }
        if (primitivo instanceof Triangulo) {
            List<Ponto> vertices = ((Triangulo)primitivo).getVertices();
            return "{" + pontoNomeadoJson("p1", vertices.get(0), largura, altura)
                + ", " + pontoNomeadoJson("p2", vertices.get(1), largura, altura)
                + ", " + pontoNomeadoJson("p3", vertices.get(2), largura, altura)
                + atributos + "}";
        }
        if (primitivo instanceof CirculoGrafico) {
            CirculoGrafico circulo = (CirculoGrafico)primitivo;
            return "{" + pontoNomeadoJson("centro", circulo.getCentro(), largura, altura)
                + ", " + pontoNomeadoJson("raio", circulo.getPontoRaio(), largura, altura)
                + atributos + ", \"algoritmo\": \"" + circulo.getAlgoritmo().name() + "\"}";
        }
        throw new IllegalArgumentException("Primitivo nao suportado: " + primitivo.getClass().getName());
    }

    private static PrimitivoGrafico lerPrimitivo(Map<String, Object> dado) throws IOException {
        String tipo = texto(dado.get("tipo"), "tipo");
        List<Object> dadosPontos = lista(dado.get("pontos"), "pontos do primitivo");
        Color cor = corLegada(dado);
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

    private static String pontoNomeadoJson(String nome, Ponto ponto, int largura, int altura) {
        return "\"" + nome + "\": {" + pontoJson(ponto, largura, altura) + "}";
    }
    private static String pontoJson(Ponto ponto, int largura, int altura) {
        double x = ponto.getX() / largura;
        double y = ponto.getY() / altura;
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("As coordenadas devem ser finitas");
        }
        return "\"x\": " + decimal(x) + ", \"y\": " + decimal(y);
    }
    private static String corJson(Color cor) {
        return "{\"r\": " + cor.getRed() + ", \"b\": " + cor.getBlue()
            + ", \"g\": " + cor.getGreen() + "}";
    }
    private static String decimal(double valor) {
        return String.format(Locale.ROOT, "%.6f", valor);
    }
    private static String escapar(String texto) { return texto.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r"); }
    private static void validarArgumentos(Path arquivo, List<PontoGr> pontos,
            List<PrimitivoGrafico> primitivos, int largura, int altura) {
        if (arquivo == null || pontos == null || primitivos == null) {
            throw new IllegalArgumentException("Arquivo e cena sao obrigatorios");
        }
        for (PontoGr ponto : pontos) {
            if (ponto == null) {
                throw new IllegalArgumentException("A cena nao pode conter pontos nulos");
            }
        }
        for (PrimitivoGrafico primitivo : primitivos) {
            if (!(primitivo instanceof RetaGrafica) && !(primitivo instanceof Retangulo)
                    && !(primitivo instanceof Triangulo)
                    && !(primitivo instanceof CirculoGrafico)) {
                throw new IllegalArgumentException("Primitivo nao suportado na persistencia");
            }
        }
        validarDimensoes(largura, altura);
    }
    private static void validarDimensoes(int largura, int altura) {
        if (largura < 1 || altura < 1) {
            throw new IllegalArgumentException("A area de desenho deve ter dimensoes positivas");
        }
    }
    private static EstiloReta estilo(Map<String, Object> dado) throws IOException {
        return new EstiloReta(cor(dado), numero(dado.get("esp"), "esp"));
    }
    @SuppressWarnings("unchecked")
    private static Map<String, Object> objeto(Object valor, String mensagem) throws IOException { if (!(valor instanceof Map)) throw new IOException(mensagem); return (Map<String, Object>)valor; }
    @SuppressWarnings("unchecked")
    private static List<Object> lista(Object valor, String nome) throws IOException { if (!(valor instanceof List)) throw new IOException("Campo obrigatorio invalido: " + nome); return (List<Object>)valor; }
    private static String texto(Object valor, String nome) throws IOException { if (!(valor instanceof String)) throw new IOException("Campo obrigatorio invalido: " + nome); return (String)valor; }
    private static int numero(Object valor, String nome) throws IOException { if (!(valor instanceof Number)) throw new IOException("Campo inteiro invalido: " + nome); double numero = ((Number)valor).doubleValue(); if (!Double.isFinite(numero) || numero != Math.rint(numero) || numero < Integer.MIN_VALUE || numero > Integer.MAX_VALUE) throw new IOException("Campo inteiro invalido: " + nome); return (int)numero; }
    private static Ponto ponto(Object valor) throws IOException { return ponto(objeto(valor, "Coordenada invalida")); }
    private static Ponto ponto(Map<String, Object> dado) throws IOException { Object x = dado.get("x"), y = dado.get("y"); if (!(x instanceof Number) || !(y instanceof Number)) throw new IOException("Coordenadas invalidas"); return new Ponto(((Number)x).doubleValue(), ((Number)y).doubleValue()); }
    private static Ponto pontoRelativo(Object valor, int largura, int altura) throws IOException { return pontoRelativo(objeto(valor, "Coordenada invalida"), largura, altura); }
    private static Ponto pontoRelativo(Map<String, Object> dado, int largura, int altura) throws IOException { Ponto relativo = ponto(dado); double x = relativo.getX() * largura, y = relativo.getY() * altura; if (!Double.isFinite(x) || !Double.isFinite(y) || x < Integer.MIN_VALUE || x > Integer.MAX_VALUE || y < Integer.MIN_VALUE || y > Integer.MAX_VALUE) throw new IOException("Coordenadas invalidas"); return new Ponto(x, y); }
    private static Color cor(Map<String, Object> dado) throws IOException { Map<String, Object> valor = objeto(dado.get("cor"), "Cor invalida"); int r = componente(valor.get("r"), "r"), g = componente(valor.get("g"), "g"), b = componente(valor.get("b"), "b"); return new Color(r, g, b); }
    private static int componente(Object valor, String nome) throws IOException { int resultado = numero(valor, nome); if (resultado < 0 || resultado > 255) throw new IOException("Componente de cor invalido: " + nome); return resultado; }
    private static Color corLegada(Map<String, Object> dado) throws IOException { String valor = texto(dado.get("cor"), "cor"); try { if (!valor.matches("#[0-9A-Fa-f]{8}")) throw new NumberFormatException(); return new Color((int)Long.parseLong(valor.substring(1), 16), true); } catch (NumberFormatException erro) { throw new IOException("Cor invalida", erro); } }

    /** Cena reconstruída a partir de um arquivo JSON. */
    public static final class Cena {
        private final List<PontoGr> pontos;
        private final List<PrimitivoGrafico> primitivos;
        private Cena(List<PontoGr> pontos, List<PrimitivoGrafico> primitivos) { this.pontos = Collections.unmodifiableList(pontos); this.primitivos = Collections.unmodifiableList(primitivos); }
        /** Retorna os pontos carregados.
         * @return lista não modificável de pontos
         */
        public List<PontoGr> getPontos() { return pontos; }
        /** Retorna as formas carregadas.
         * @return lista não modificável de formas
         */
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
