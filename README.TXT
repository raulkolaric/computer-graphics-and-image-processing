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
4. Escolha a cor e informe a espessura antes de criar o primitivo.
5. Para circulos, escolha entre equacao reduzida, parametrico ou simetria.
6. Ao informar o primeiro ponto de uma reta, retangulo ou circulo, mova o mouse
   para visualizar o elastico antes do segundo clique.
7. Escolha Selecionar, clique em um primitivo e use Excluir selecionado para
   remove-lo da cena.
8. Use Redesenhar para reconstruir a cena armazenada em memoria.
9. Use Salvar projeto para gravar a cena em `projeto-anterior.json`. Ao abrir a
   aplicacao, Recarregar projeto fica habilitado se esse arquivo existir; ele
   restaura pontos, formas, coordenadas proporcionais a area de desenho, cor,
   espessura e algoritmo do circulo no formato JSON da atividade.

Arquitetura
-----------
- Reta e Circulo representam a geometria matematica.
- RetaGrafica e CirculoGrafico acrescentam estilo e renderizacao.
- Retangulo e Triangulo sao compostos exclusivamente por RetaGrafica.
- PainelDesenho armazena uma lista dinamica de PrimitivoGrafico.
- PersistenciaProjeto grava e le a cena no formato JSON, sem dependencia externa.
- RenderizadorPrimitivos isola o mecanismo de desenho. Atualmente a
  implementacao manual usa a equacao reduzida da reta e algoritmos manuais
  de circulo. Uma implementacao de biblioteca pode ser injetada no painel.

Compilacao e testes
-------------------
  javac -d bin *.java */*.java
  java -ea -Djava.awt.headless=true -cp bin tests.TestaPrimitivos

Para abrir a interface:
  java -cp bin App

Autores
-------
- Raul Kolaric
- Liam Lopes
- Rafael Infantini
- Guilherme Coutinho
