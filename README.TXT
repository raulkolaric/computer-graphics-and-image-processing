PRIMITIVOS GRAFICOS
===================

Projeto Java/BlueJ para criar e redesenhar pontos, retas, retângulos,
triângulos e círculos a partir de cliques do mouse.

Uso
---
1. Execute App.
2. Escolha o primitivo na barra.
3. Informe os pontos pelo mouse:
   - reta, retângulo e círculo: dois pontos;
   - triângulo: três pontos;
   - ponto: um ponto.
4. Escolha a cor e informe a espessura antes de criar o primitivo.
5. Para círculos, escolha entre equação reduzida, paramétrico ou simetria.
6. Ao informar o primeiro ponto de uma reta, retângulo ou círculo, mova o mouse
   para visualizar o elástico antes do segundo clique.
7. Escolha Selecionar, clique em um ponto ou primitivo e use Excluir selecionado
   para removê-lo da cena.
8. Use Redesenhar para reconstruir a cena armazenada em memória.
9. Use Salvar projeto para gravar a cena em `projeto-anterior.json`. Ao abrir a
   aplicação, Recarregar projeto fica habilitado se esse arquivo existir; ele
   restaura pontos, formas, coordenadas proporcionais à área de desenho, cor,
   espessura e algoritmo do círculo no formato JSON da atividade.

Arquitetura
-----------
- Reta e Circulo representam a geometria matemática.
- RetaGrafica e CirculoGrafico acrescentam estilo e renderização.
- Retangulo e Triangulo são compostos exclusivamente por RetaGrafica.
- PainelDesenho armazena uma lista dinâmica de PrimitivoGrafico.
- PersistenciaProjeto grava e lê a cena no formato JSON, sem dependência externa.
- RenderizadorPrimitivos isola o mecanismo de desenho. Atualmente a
  implementação manual usa a equação reduzida da reta e algoritmos manuais
  de círculo. Uma implementação de biblioteca pode ser injetada no painel.

Compilação e testes
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
