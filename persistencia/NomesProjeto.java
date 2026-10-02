package persistencia;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Nomes e conflitos de projetos, calculados no disco sem criar diretórios. */
public final class NomesProjeto {
    private static final Pattern NUMERO = Pattern.compile("Projeto ([0-9]+)\\.(json|jpeg|jpg)",
        Pattern.CASE_INSENSITIVE);

    private NomesProjeto() { }

    /** Sugere um número acima do maior JSON/JPEG/JPG existente, sem preencher lacunas.
     * @param diretorio pasta escolhida, que pode ainda não existir
     * @return caminho da próxima sugestão JSON
     * @throws IOException se a pasta não puder ser examinada ou o nome for longo demais
     */
    public static Path proximo(Path diretorio) throws IOException {
        BigInteger maior = BigInteger.ZERO;
        if (Files.exists(diretorio)) {
            try (java.nio.file.DirectoryStream<Path> entradas = Files.newDirectoryStream(diretorio)) {
                for (Path entrada : entradas) {
                    Matcher nome = NUMERO.matcher(entrada.getFileName().toString());
                    if (nome.matches()) maior = maior.max(new BigInteger(nome.group(1)));
                }
            }
        }
        String nome = "Projeto " + maior.add(BigInteger.ONE) + ".json";
        if (nome.length() > 250) throw new IOException("Numeracao muito longa; escolha outro nome");
        return diretorio.resolve(nome);
    }

    /** Normaliza extensões conhecidas ou acrescenta .json ao nome editado.
     * @param arquivo destino escolhido
     * @return destino absoluto com extensão .json
     */
    public static Path json(Path arquivo) {
        Path absoluto = arquivo.toAbsolutePath().normalize();
        String nome = absoluto.getFileName().toString();
        String minusculo = nome.toLowerCase(Locale.ROOT);
        for (String extensao : new String[] {".json", ".jpeg", ".jpg"}) {
            if (minusculo.endsWith(extensao)) {
                nome = nome.substring(0, nome.length() - extensao.length());
                break;
            }
        }
        return absoluto.resolveSibling(nome + ".json");
    }

    /** Retorna o nome da imagem companheira, sempre com extensão .jpeg.
     * @param arquivo destino JSON
     * @return destino JPEG
     */
    public static Path jpeg(Path arquivo) {
        Path projeto = json(arquivo);
        String nome = projeto.getFileName().toString();
        return projeto.resolveSibling(nome.substring(0, nome.length() - 5) + ".jpeg");
    }

    /** Reconsulta os dois destinos para uma única confirmação de substituição.
     * @param arquivo destino JSON
     * @return arquivos ou diretórios existentes que seriam substituídos
     */
    public static List<Path> conflitos(Path arquivo) {
        List<Path> existentes = new ArrayList<Path>();
        Path projeto = json(arquivo);
        if (Files.exists(projeto)) existentes.add(projeto);
        Path imagem = jpeg(projeto);
        if (Files.exists(imagem)) existentes.add(imagem);
        return existentes;
    }
}
