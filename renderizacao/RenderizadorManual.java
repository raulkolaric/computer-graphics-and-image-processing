package renderizacao;

import java.awt.Graphics;

import circulo.CirculoGrafico;
import reta.Reta;
import reta.RetaGrafica;

/** Rasterizador que desenha os primitivos ponto a ponto. */
public class RenderizadorManual implements RenderizadorPrimitivos {
    private static final long MAX_PASSOS_RETA = 2_000_000L;
    private static final int MAX_RAIO = 100_000;

    /** Cria um renderizador manual. */
    public RenderizadorManual() {
    }

    /** Desenha uma reta sem usar {@code Graphics.drawLine}.
     * @param g superfície de desenho
     * @param reta reta a desenhar
     */
    @Override
    public void desenharReta(Graphics g, RetaGrafica reta) {
        g.setColor(reta.getCor());

        int x1 = converterCoordenada(reta.getP1().getX());
        int y1 = converterCoordenada(reta.getP1().getY());
        int x2 = converterCoordenada(reta.getP2().getX());
        int y2 = converterCoordenada(reta.getP2().getY());
        validarQuantidadePassos(x1, x2);
        validarQuantidadePassos(y1, y2);

        if (x1 == x2) {
            int inicio = Math.min(y1, y2);
            int fim = Math.max(y1, y2);
            for (long y = inicio; y <= fim; y++) {
                plotar(g, x1, (int)y, reta.getEspessura());
            }
            return;
        }

        // A grade grafica usa coordenadas inteiras; a equacao e calculada
        // entre os extremos ja convertidos para pixels.
        Reta retaRaster = new Reta(x1, y1, x2, y2);
        double m = retaRaster.calcularM();
        double b = retaRaster.calcularB();
        long deltaX = Math.abs((long)x2 - x1);
        long deltaY = Math.abs((long)y2 - y1);

        if (deltaX >= deltaY) {
            int inicio = Math.min(x1, x2);
            int fim = Math.max(x1, x2);
            for (long x = inicio; x <= fim; x++) {
                int y = (int)Math.round(m * x + b);
                plotar(g, (int)x, y, reta.getEspessura());
            }
        } else {
            int inicio = Math.min(y1, y2);
            int fim = Math.max(y1, y2);
            for (long y = inicio; y <= fim; y++) {
                int x = (int)Math.round((y - b) / m);
                plotar(g, x, (int)y, reta.getEspessura());
            }
        }
    }

    /** Desenha um círculo usando o algoritmo configurado no objeto.
     * @param g superfície de desenho
     * @param circulo círculo a desenhar
     */
    @Override
    public void desenharCirculo(Graphics g, CirculoGrafico circulo) {
        g.setColor(circulo.getCor());
        int xc = converterCoordenada(circulo.getCentro().getX());
        int yc = converterCoordenada(circulo.getCentro().getY());
        double raioCalculado = circulo.getRaio();
        if (!Double.isFinite(raioCalculado) || raioCalculado > MAX_RAIO) {
            throw new IllegalArgumentException("Raio fora do limite de rasterizacao");
        }
        int raio = (int)Math.round(raioCalculado);
        validarLimitesCirculo(xc, yc, raio);

        if (raio == 0) {
            plotar(g, xc, yc, circulo.getEspessura());
            return;
        }

        switch (circulo.getAlgoritmo()) {
            case EQUACAO_REDUZIDA:
                desenharCirculoEquacao(g, xc, yc, raio, circulo.getEspessura());
                break;
            case PARAMETRICO:
                desenharCirculoParametrico(g, xc, yc, raio, circulo.getEspessura());
                break;
            case SIMETRIA_OCTANTES:
                desenharCirculoSimetria(g, xc, yc, raio, circulo.getEspessura());
                break;
            default:
                throw new IllegalStateException("Algoritmo de circulo desconhecido");
        }
    }

    private void desenharCirculoEquacao(Graphics g, int xc, int yc, int raio, int espessura) {
        long raioQuadrado = (long)raio * raio;
        for (int x = -raio; x <= raio; x++) {
            int y = (int)Math.round(Math.sqrt(raioQuadrado - (long)x * x));
            plotar(g, xc + x, yc + y, espessura);
            plotar(g, xc + x, yc - y, espessura);
        }
        for (int y = -raio; y <= raio; y++) {
            int x = (int)Math.round(Math.sqrt(raioQuadrado - (long)y * y));
            plotar(g, xc + x, yc + y, espessura);
            plotar(g, xc - x, yc + y, espessura);
        }
    }

    private void desenharCirculoParametrico(Graphics g, int xc, int yc, int raio,
                                             int espessura) {
        double passo = 1.0 / raio;
        for (double theta = 0; theta < Math.PI * 2; theta += passo) {
            int x = xc + (int)Math.round(raio * Math.cos(theta));
            int y = yc + (int)Math.round(raio * Math.sin(theta));
            plotar(g, x, y, espessura);
        }
        plotar(g, xc + raio, yc, espessura);
        plotar(g, xc - raio, yc, espessura);
        plotar(g, xc, yc + raio, espessura);
        plotar(g, xc, yc - raio, espessura);
    }

    private void desenharCirculoSimetria(Graphics g, int xc, int yc, int raio,
                                          int espessura) {
        int x = 0;
        int y = raio;
        int decisao = 1 - raio;
        while (x <= y) {
            plotarOctantes(g, xc, yc, x, y, espessura);
            x++;
            if (decisao < 0) {
                decisao += 2 * x + 1;
            } else {
                y--;
                decisao += 2 * (x - y) + 1;
            }
        }
    }

    private void plotarOctantes(Graphics g, int xc, int yc, int x, int y, int espessura) {
        plotar(g, xc + x, yc + y, espessura);
        plotar(g, xc - x, yc + y, espessura);
        plotar(g, xc + x, yc - y, espessura);
        plotar(g, xc - x, yc - y, espessura);
        plotar(g, xc + y, yc + x, espessura);
        plotar(g, xc - y, yc + x, espessura);
        plotar(g, xc + y, yc - x, espessura);
        plotar(g, xc - y, yc - x, espessura);
    }

    private void plotar(Graphics g, int x, int y, int espessura) {
        int deslocamento = espessura / 2;
        long esquerda = (long)x - deslocamento;
        long topo = (long)y - deslocamento;
        if (esquerda < Integer.MIN_VALUE || topo < Integer.MIN_VALUE
                || esquerda > Integer.MAX_VALUE || topo > Integer.MAX_VALUE) {
            return;
        }
        g.fillRect((int)esquerda, (int)topo, espessura, espessura);
    }

    private int converterCoordenada(double coordenada) {
        if (!Double.isFinite(coordenada)
                || coordenada < Integer.MIN_VALUE || coordenada > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Coordenada fora do limite de rasterizacao");
        }
        return (int)Math.round(coordenada);
    }

    private void validarQuantidadePassos(int inicio, int fim) {
        if (Math.abs((long)fim - inicio) > MAX_PASSOS_RETA) {
            throw new IllegalArgumentException("Reta excede o limite de rasterizacao");
        }
    }

    private void validarLimitesCirculo(int xc, int yc, int raio) {
        if ((long)xc - raio < Integer.MIN_VALUE || (long)xc + raio > Integer.MAX_VALUE
                || (long)yc - raio < Integer.MIN_VALUE || (long)yc + raio > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Circulo excede o limite de rasterizacao");
        }
    }
}
