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
4. Informe a espessura antes de criar o primitivo. Escolha uma cor na paleta ou informe R, G e B (0 a 255).
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
  javac -encoding UTF-8 -d bin *.java */*.java
  java -ea -Djava.awt.headless=true -cp bin tests.TestaPrimitivos

Para abrir a interface:
  java -cp bin App

Autores
-------
- Raul Kolaric
- Liam Lopes
- Rafael Infantini
- Guilherme Coutinho

Interface Paint RGB
-------------------
- Ferramentas agrupadas no topo, tela branca sobre bancada cinza e barra de estado.
- Paleta com 20 cores, campos RGB e amostra da cor atual com codigo hexadecimal.
- A cor inicial e preta. Cada desenho guarda sua propria cor.
- Em figuras de varios cliques, cor e espessura sao as selecionadas no ultimo clique.
- Limpar tela apenas oculta os desenhos; Redesenhar restaura todos ou o tipo escolhido.
- Os algoritmos manuais, espessura, rotulos dos pontos e geometria foram preservados.
- Janela inicial de 1100 x 720; minimo de 1000 x 520.
- Java 8 ou superior, sem dependencias externas. No BlueJ, abra package.bluej
  na pasta principal, compile e execute App.main.
- TestaPrimitivos inclui regressao RGB para os cinco tipos de desenho e os tres
  algoritmos de circulo, troca de cor, limpeza e redesenho filtrado.

Arquivos alterados: App.java, ui/Gui.java, ui/PainelDesenho.java,
tests/TestaPrimitivos.java e README.TXT.
