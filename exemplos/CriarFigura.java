package exemplos;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import ui.PainelDesenho;
import ui.TiposPrimitivos;

/** Gera uma figura usando os mesmos eventos de desenho e persistencia da GUI. */
public class CriarFigura {
    private static PainelDesenho painel;
    public static void main(String[] args) throws Exception {
        Path destino = Path.of(args.length == 0 ? "exemplos" : args[0]);
        Files.createDirectories(destino);
        SwingUtilities.invokeAndWait(() -> {
            try {
                painel = new PainelDesenho(new JLabel(), TiposPrimitivos.NENHUM);
                painel.setSize(960, 640);
                painel.setEspessuraAtual(3);
                forma(TiposPrimitivos.RETA, 0x43865C, 65, 505, 895, 505);
                forma(TiposPrimitivos.RETANGULO, 0x356A91, 255, 285, 615, 505);
                forma(TiposPrimitivos.TRIANGULO, 0xC45B47, 215, 285, 435, 120, 655, 285);
                forma(TiposPrimitivos.RETANGULO, 0xA87946, 400, 365, 475, 505);
                forma(TiposPrimitivos.CIRCULO, 0xA87946, 457, 438, 461, 438);
                forma(TiposPrimitivos.RETANGULO, 0x449FAE, 295, 330, 365, 395);
                forma(TiposPrimitivos.RETANGULO, 0x449FAE, 510, 330, 580, 395);
                forma(TiposPrimitivos.RETA, 0x449FAE, 330, 330, 330, 395);
                forma(TiposPrimitivos.RETA, 0x449FAE, 295, 362, 365, 362);
                forma(TiposPrimitivos.RETA, 0x449FAE, 545, 330, 545, 395);
                forma(TiposPrimitivos.RETA, 0x449FAE, 510, 362, 580, 362);
                forma(TiposPrimitivos.CIRCULO, 0xE5AD32, 765, 135, 808, 135);
                for (int i = 0; i < 12; i++) {
                    double t = i * Math.PI / 6;
                    forma(TiposPrimitivos.RETA, 0xE5AD32,
                        765 + (int)Math.round(55 * Math.cos(t)), 135 + (int)Math.round(55 * Math.sin(t)),
                        765 + (int)Math.round(70 * Math.cos(t)), 135 + (int)Math.round(70 * Math.sin(t)));
                }
                forma(TiposPrimitivos.RETANGULO, 0xA87946, 755, 392, 779, 505);
                forma(TiposPrimitivos.TRIANGULO, 0x43865C, 685, 400, 767, 260, 849, 400);
                forma(TiposPrimitivos.TRIANGULO, 0x43865C, 700, 330, 767, 220, 834, 330);
                forma(TiposPrimitivos.PONTO, 0xC45B47, 140, 487);
                forma(TiposPrimitivos.PONTO, 0xC45B47, 175, 487);
                forma(TiposPrimitivos.PONTO, 0xC45B47, 210, 487);
                Path json = destino.resolve("figura-casa.json");
                painel.salvarProjeto(json);
                // A imagem e renderizada do JSON recarregado para comprovar a correspondencia.
                PainelDesenho recarregado = new PainelDesenho(new JLabel(), TiposPrimitivos.NENHUM);
                recarregado.setSize(960, 640);
                recarregado.carregarProjeto(json);
                recarregado.exportarPng(destino.resolve("figura-casa.png"));
                BufferedImage imagem = new BufferedImage(960, 640, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = imagem.createGraphics();
                recarregado.paint(g);
                g.dispose();
                if (!ImageIO.write(imagem, "jpeg", destino.resolve("figura-casa.jpeg").toFile())) {
                    throw new IllegalStateException("Codificador JPEG indisponivel");
                }
                System.out.println("JSON e JPEG criados: " + recarregado.getQuantidadePrimitivos()
                    + " formas e " + recarregado.getQuantidadePontos() + " pontos.");
            } catch (Exception erro) {
                throw new RuntimeException(erro);
            }
        });
    }
    private static void forma(TiposPrimitivos tipo, int rgb, int... coordenadas) {
        painel.setTipo(tipo);
        painel.setCorAtual(new Color(rgb));
        for (int i = 0; i < coordenadas.length; i += 2) {
            painel.mousePressed(new MouseEvent(painel, MouseEvent.MOUSE_PRESSED,
                0, 0, coordenadas[i], coordenadas[i + 1], 1, false, MouseEvent.BUTTON1));
        }
    }
}

