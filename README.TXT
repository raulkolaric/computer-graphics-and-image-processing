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
9. Use Abrir projeto para escolher um arquivo JSON no explorador de arquivos.
   O explorador começa no diretório de trabalho e, após abrir ou salvar,
   seleciona o último arquivo usado com sucesso.
   Use Salvar projeto para escolher o destino; confirme antes de substituir um
   arquivo existente. Cancelar preserva a cena e o arquivo atual.
   As coordenadas são proporcionais à área de desenho. Nos círculos, centro
   é o ponto central e raio é um ponto da circunferência (não um vetor).
   Um arquivo deve conter somente um objeto JSON raiz.
   A importação valida os dados e os limites de rasterização antes de substituir
   a cena. Um projeto inválido exibe uma mensagem de erro e preserva a cena atual.

Persistência e compatibilidade
------------------------------
A escala é aplicada ao abrir o arquivo; redimensionar uma janela já aberta não
altera a escala da cena atual.

- Novas gravações incluem ordem em cada forma para preservar a sobreposição.
  Se presente em uma forma, esse campo deve existir em todas, com valores inteiros
  únicos de 0 até o total de formas menos 1. Sem o campo, a ordem histórica é
  reta, triângulo, retângulo e círculo. Pontos são desenhados depois das formas.
- Novos círculos incluem raioRelativo: raio em pixels dividido pelo menor valor
  entre largura e altura. Ao abrir, esse valor é multiplicado pela menor dimensão
  atual, independentemente da direção do segundo clique. O campo raio continua
  armazenado como ponto da circunferência para compatibilidade.
- Sem raioRelativo, o raio continua sendo calculado pela distância entre centro
  e raio após converter suas coordenadas para pixels. Arquivos antigos não
  registram a proporção original da área; ela não pode ser recuperada ao abrir.
- O leitor aceita números em notação científica, como 1.62e-1, dentro do formato
  de projeto suportado; não implementa toda a especificação JSON.

Exemplo do professor
-------------------
`exemplo.json` e `projeto-anterior.json` contêm uma reconstrução dos círculos
medida a partir da imagem de referência. O original está preservado em
`tests/fixtures/exemplo.json`; os outros primitivos não foram alterados.
Os dois primeiros círculos originais compartilham um ponto de raio distante;
o centro esperado do terceiro corresponde ao seu campo raio. A hipótese é
que a exportação contém pontos desatualizados ou trocados. O código recebido
apenas imprime os campos e não permite confirmar a origem desse problema.
Na reconstrução, os raios são aproximadamente 179, 152 e 128 pixels em uma
área de 1165 x 694, com espessura 6. Os pontos de raio foram colocados abaixo
dos centros para acompanhar a escala vertical da composição. Esses valores
são estimados, não uma nova interpretação geral do formato JSON.
Os testes verificam círculos aninhados acima da base em três tamanhos,
pixels visíveis e persistência do centro corrigido após salvar e abrir.

Arquitetura
-----------
- Reta e Circulo representam a geometria matemática.
- RetaGrafica e CirculoGrafico acrescentam estilo e renderização.
- Retangulo, no pacote retangulo, e Triangulo são compostos exclusivamente por
  RetaGrafica.
- PainelDesenho armazena uma lista dinâmica de PrimitivoGrafico.
- PersistenciaProjeto grava e lê a cena no formato JSON, sem dependência externa.
- RenderizadorPrimitivos isola o mecanismo de desenho. Atualmente a
  implementação manual usa a equação reduzida da reta e algoritmos manuais
  de círculo. Uma implementação de biblioteca pode ser injetada no painel.

Compilação e testes
-------------------
Requer JDK 11 ou superior (a persistência usa Files.readString e a GUI usa Path.of).

  javac -d bin *.java */*.java
  java -ea -Djava.awt.headless=true -cp bin tests.TestaPrimitivos
  java -ea -Djava.awt.headless=true -cp bin tests.TestaImportacao
  java -ea -Djava.awt.headless=true -cp bin tests.TestaOrdem
  java -ea -Djava.awt.headless=true -cp bin tests.TestaEscalaCirculo

Para abrir a interface:
  java -cp bin App

Autores
-------
- Raul Kolaric
- Liam Lopes
- Rafael Infantini
- Guilherme Coutinho
