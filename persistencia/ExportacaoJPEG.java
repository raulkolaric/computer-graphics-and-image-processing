package persistencia;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/** Grava uma imagem RGB em JPEG, preservando a imagem anterior em falhas de codificação. */
public final class ExportacaoJPEG {
    private ExportacaoJPEG() { }

    /** Codifica em qualidade 0.95 e só então substitui o destino.
     * Não oferece uma transação conjunta com o arquivo JSON.
     * @param arquivo caminho da imagem .jpeg
     * @param imagem imagem RGB com dimensões positivas
     * @throws IOException se o escritor estiver indisponível ou a gravação falhar
     */
    public static void salvar(Path arquivo, BufferedImage imagem) throws IOException {
        if (imagem == null || imagem.getWidth() <= 0 || imagem.getHeight() <= 0
                || imagem.getType() != BufferedImage.TYPE_INT_RGB)
            throw new IOException("JPEG requer imagem RGB com dimensoes positivas");
        Iterator<ImageWriter> escritores = ImageIO.getImageWritersByFormatName("jpeg");
        if (!escritores.hasNext()) throw new IOException("Escritor JPEG indisponivel");
        ImageWriter escritor = escritores.next();
        Path temporario = null;
        try {
            ImageWriteParam parametros = escritor.getDefaultWriteParam();
            if (!parametros.canWriteCompressed()) throw new IOException("Escritor sem controle de qualidade JPEG");
            parametros.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            parametros.setCompressionQuality(0.95f);
            temporario = Files.createTempFile(arquivo.toAbsolutePath().getParent(), ".imagem-", ".tmp");
            try (ImageOutputStream saida = ImageIO.createImageOutputStream(temporario.toFile())) {
                if (saida == null) throw new IOException("Nao foi possivel abrir a saida JPEG");
                escritor.setOutput(saida);
                escritor.write(null, new IIOImage(imagem, null, null), parametros);
                saida.flush();
            }
            if (Files.size(temporario) == 0) throw new IOException("Escritor produziu JPEG vazio");
            Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            escritor.dispose();
            if (temporario != null) Files.deleteIfExists(temporario);
        }
    }
}
