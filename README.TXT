PRIMITIVOS GRAFICOS
===================

Projeto Java/BlueJ para criar e redesenhar pontos, retas, retangulos,
triangulos e circulos a partir de cliques do mouse.

Uso
---
1. Execute App.
2. Escolha o primitivo na barra.
3. Informe os pontos pelo mouse:
   - reta, retangulo e circulo: dois pontos;
   - triangulo: tres pontos;
   - ponto: um ponto.
4. Escolha a cor e a espessura antes de criar o primitivo.
5. Para circulos, escolha entre equacao reduzida, parametrico ou simetria.
6. Use Redesenhar para reconstruir a cena armazenada em memoria.

Arquitetura
-----------
- Reta e Circulo representam a geometria matematica.
- RetaGrafica e CirculoGrafico acrescentam estilo e renderizacao.
- Retangulo e Triangulo sao compostos exclusivamente por RetaGrafica.
- PainelDesenho armazena uma lista dinamica de PrimitivoGrafico.
- RenderizadorPrimitivos isola o mecanismo de desenho. Atualmente a
  implementacao manual usa a equacao reduzida da reta e algoritmos manuais
  de circulo. Uma implementacao de biblioteca pode ser injetada no painel.

Compilacao e testes
-------------------
  javac *.java
  java -ea -Djava.awt.headless=true TestaPrimitivos

Para abrir a interface:
  java App
