package tests;
import ui.*;
import quadrado.Retangulo;
import ponto.Ponto;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.nio.file.*;
import javax.swing.JLabel;
public class TestaEdicao {
 static void evento(PainelDesenho p,int tipo,int x,int y) {
  MouseEvent e=new MouseEvent(p,tipo,0,0,x,y,1,false,MouseEvent.BUTTON1);
  if(tipo==MouseEvent.MOUSE_PRESSED)p.mousePressed(e);
  else if(tipo==MouseEvent.MOUSE_DRAGGED)p.mouseDragged(e);
  else p.mouseReleased(e);
 }
 public static void main(String[] args) throws Exception {
  PainelDesenho p=new PainelDesenho(new JLabel(),TiposPrimitivos.SELECAO);
  p.setSize(200,200);
  p.adicionarPrimitivo(new Retangulo(new Ponto(20,20),new Ponto(80,80),Color.BLUE,3));
  evento(p,501,50,50); evento(p,501,80,80); evento(p,506,130,110); evento(p,502,130,110);
  Retangulo r=(Retangulo)p.getPrimitivos().get(0);
  if(r.getCanto2().getX()!=130 || r.getCanto2().getY()!=110 || !r.getCor().equals(Color.BLUE))
   throw new AssertionError("Redimensionamento");
  Path json=Files.createTempFile("edicao-", ".json"), png=Files.createTempFile("edicao-", ".png");
  try {
   p.salvarProjeto(json);
   p.carregarProjeto(json);
   r=(Retangulo)p.getPrimitivos().get(0);
   if(r.getCanto2().getX()!=130)throw new AssertionError("Persistencia");
   evento(p,501,50,50); evento(p,501,20,20); evento(p,506,10,10); evento(p,502,10,10);
   p.exportarPng(png);
   java.awt.image.BufferedImage imagem=javax.imageio.ImageIO.read(png.toFile());
   if(imagem.getWidth()!=200 || imagem.getRGB(10,50)!=Color.BLUE.getRGB()
      || imagem.getRGB(12,12)!=Color.WHITE.getRGB())throw new AssertionError("PNG");
   System.out.println("TestaEdicao: redimensionamento, recarga e PNG aprovados");
  } finally {Files.deleteIfExists(json);Files.deleteIfExists(png);}
 }
}
