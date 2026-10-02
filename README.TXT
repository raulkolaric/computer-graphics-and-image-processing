PRIMITIVOS GRAFICOS
===================

Projeto Java/BlueJ para criar e redesenhar pontos, retas, retângulos,
triângulos e círculos a partir de cliques do mouse.

Uso
---
1. Execute App. A janela padrão tem 900 x 600 pixels, com ações de arquivo
   em uma barra própria.
2. Escolha Ponto, Reta, Retangulo, Triangulo ou Circulo. Informe um ponto para
   Ponto, três para Triangulo e dois para as demais formas.
3. Escolha a cor, espessura e algoritmo do círculo antes de desenhar.
   Após o primeiro clique de uma reta, retângulo ou círculo, mova o mouse
   para ver a prévia. Apenas formas concluídas entram no projeto salvo.
4. Use Selecionar e clique no objeto. Excluir selecionado remove esse objeto
   tanto da tela quanto da memória; Redesenhar não o recupera. Original e
   cópia espelhada podem ser excluídos independentemente.

Limpar e Redesenhar
------------------
Limpar oculta os objetos completos e o eixo de reflexão, preservando a geometria
em memória. Cancela formas incompletas, prévias, seleção e reflexão da seleção.
Mantém ferramenta, cor, espessura, algoritmo e estado do botão Espelhar.
Um eixo automático completo permanece definido; um incompleto é cancelado,
reiniciando em p1. A tela fica inicialmente vazia, inclusive sem guias.

Redesenhar reconstrói a tela a partir da memória, com Todos ou o filtro escolhido,
sem duplicar objetos e preservando a ordem. Cancela construções incompletas.
O guia só reaparece se Espelhar estiver ativo com um eixo completo (também pode
reaparecer na próxima interação de desenho). Redesenhar não lê arquivos JSON.

Abrir projeto com sucesso substitui a memória e a tela. Fechar a aplicação
perde os objetos retidos apenas em memória; somente o conteúdo salvo permanece.
Não há desfazer nem aviso novo ao fechar.

Espelhamento
------------
- Espelhar: ative o botão, clique em dois pontos distintos p1/p2 para definir
  a reta e desenhe normalmente. Cada objeto novo inclui uma cópia refletida.
  A ferramenta continua ativa; o eixo serve para criações posteriores e trocas
  de ferramenta. Desativar cancela a entrada pendente; reativar solicita novo eixo.
- Espelhar selecionado: selecione um objeto visível e pressione a ação. Clique
  em p1/p2 para criar exatamente uma cópia, preservando o original. Serve para
  pontos, todas as formas, objetos importados e cópias anteriores. Não altera
  o eixo automático nem reflete a cópia automaticamente outra vez.
  Um p2 coincidente ou inválido não insere nada: escolha outro p2 ou cancele.
  Esc, troca de ferramenta, Limpar, Redesenhar, abertura bem-sucedida, alternância
  do botão Espelhar ou exclusão da fonte cancelam a operação.
Ambos preservam estilo, rótulos de pontos, espessura e algoritmo do círculo.
Os eixos podem ser verticais, horizontais ou diagonais.

Salvar, Salvar como e Abrir projeto
----------------------------------
Salvar projeto abre o seletor com o último destino bem-sucedido para atualização.
Na primeira gravação, sugere Projeto i.json. Salvar como abre o mesmo seletor
com o próximo nome numerado, editável, sem apagar o arquivo anterior ou a memória.
Após gravar o JSON, inclusive em Salvar como, o destino escolhido passa a ser
lembrado. Cancelar ou falhar antes de gravar o JSON preserva o destino anterior.

O seletor começa em saves/. A sugestão usa 1 em uma pasta vazia e, depois, um
número acima do maior Projeto i.json/.jpeg/.jpg existente, ignorando diferenças
de maiúsculas e sem preencher lacunas. É recalculada na pasta escolhida, inclusive
após reiniciar a aplicação. Nomes não relacionados e sufixos malformados são
ignorados; números grandes não sofrem overflow. A extensão final é .json.
Abrir/cancelar o seletor não cria saves/: a pasta só é criada ao gravar.

A sugestão não reserva o caminho. Antes de gravar, uma confirmação relaciona
todos os destinos existentes que seriam substituídos: JSON e seu .jpeg.
Uma imagem existente exige confirmação mesmo sem JSON. Atualizar o mesmo projeto
não gera um novo número automaticamente.

Toda gravação substitui o JSON pela coleção de objetos completos exibidos naquele
momento. O nome e as gravações anteriores não mudam esse conteúdo. Salvar não
apaga objetos retidos, não muda o filtro e não conclui nem cancela formas pendentes.
Um objeto parcialmente fora da área ou coberto por outro conserva sua geometria
inteira no JSON. Uma tela vazia produz um projeto vazio válido.

Exemplos:
  A -> Limpar -> B -> salvar                       grava B; A continua na memória
  A -> salvar -> B -> salvar novamente             grava A+B
  A -> Limpar -> Redesenhar Todos -> salvar        grava A
  A+B -> excluir A -> Redesenhar -> salvar         grava B
  Redesenhar Circulo -> salvar                     grava apenas círculos exibidos
  Limpar -> salvar                                grava projeto vazio
  Salvar A.json -> Salvar como B.json              grava o mesmo desenho em ambos
  Abrir A.json -> salvar A.json -> salvar B.json   grava o mesmo desenho em B
Após o primeiro exemplo, Redesenhar Todos recupera A+B. Abrir o arquivo salvo
com B substitui a memória por B.

Cada gravação também atualiza uma imagem .jpeg de mesmo nome ao lado do JSON,
por exemplo saves/Projeto 1.json e saves/Projeto 1.jpeg. Usa a mesma cena completa,
ordem e dimensões da área de desenho, fundo branco RGB e qualidade JPEG 0.95.
A imagem é recortada às suas dimensões; não contém prévias, seleção nem guias.
O código reutiliza o renderizador manual e desenha pontos depois das formas.

JSON e JPEG são gravações independentes, sem substituição atômica conjunta.
Se o JSON for salvo e o JPEG falhar, o JSON permanece salvo e seu destino é
lembrado. A interface informa a falha da imagem: um JPEG anterior pode estar
desatualizado. Verifique a mensagem antes de usar a imagem exportada.

Abrir projeto permite escolher um JSON existente. Valida o arquivo completo e
os limites de rasterização antes de substituir a cena e cancelar a interação
pendente. Não espelha novamente os objetos importados. Arquivo inválido ou diálogo
cancelado preserva a cena e o destino lembrado. Um arquivo deve conter somente
um objeto JSON raiz. Nos círculos, centro é o ponto central e raio é um ponto
da circunferência (não um vetor).

Persistência e compatibilidade
------------------------------
A escala é aplicada ao abrir o arquivo; redimensionar uma janela já aberta não
altera a escala da cena atual.

- Retângulos incluem p3 e p4 (cantos adjacentes a p1) para preservar orientação
  após espelhamento. p1 e p2 continuam sendo cantos opostos; arquivos antigos
  com apenas esses dois pontos permanecem compatíveis.
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
- Arquivos legados de versão 1 com coordenadas absolutas continuam legíveis.
- O leitor aceita números em notação científica, como 1.62e-1, dentro do formato
  de projeto suportado; não implementa toda a especificação JSON.

Exemplo do professor
-------------------
`saves/exemplo.json` contém uma reconstrução aproximada dos círculos
medida a partir da imagem de referência. O original está preservado em
`tests/fixtures/exemplo.json`; os outros primitivos não foram alterados.
`tests/fixtures/projeto-anterior.json` preserva um projeto anterior para regressão.
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
- TransfGeometricaComposta e Matrizes adaptam o exemplo do professor: translação,
  rotação, reflexão no eixo X e transformações inversas. atan2 permite retas
  verticais; pontos coincidentes são rejeitados.
- Reta e Circulo representam a geometria matemática.
- RetaGrafica e CirculoGrafico acrescentam estilo e renderização.
- Retangulo, no pacote retangulo, e Triangulo são compostos exclusivamente por
  RetaGrafica.
- PainelDesenho retém objetos completos e mantém referências aos objetos exibidos.
- NomesProjeto examina nomes e conflitos; ExportacaoJPEG codifica a imagem companheira.
- PersistenciaProjeto grava e lê a cena no formato JSON, sem dependência externa.
- RenderizadorPrimitivos isola o mecanismo de desenho. Atualmente a
  implementação manual usa a equação reduzida da reta e algoritmos manuais
  de círculo. Uma implementação de biblioteca pode ser injetada no painel.

Compilação e testes
-------------------
Requer JDK 11 ou superior (a persistência usa Files.readString e a GUI usa Path.of).

  javac --release 11 -Xlint:all -d .docs/bin *.java */*.java
  java -ea -Djava.awt.headless=true -cp .docs/bin tests.TestaProjetos
  java -ea -Djava.awt.headless=true -cp .docs/bin tests.TestaEspelhamento
  java -ea -Djava.awt.headless=true -cp .docs/bin tests.TestaPrimitivos
  java -ea -Djava.awt.headless=true -cp .docs/bin tests.TestaImportacao
  java -ea -Djava.awt.headless=true -cp .docs/bin tests.TestaOrdem
  java -ea -Djava.awt.headless=true -cp .docs/bin tests.TestaEscalaCirculo

Os seis programas acima são regressões com assertions. TestaProjetos também
verifica nomes numerados, imagens JPEG e falhas parciais. TestaPonto e TestaReta
são demonstrações adicionais. Compilar com --release 11 verifica APIs/bytecode;
o runtime usado depende do JDK instalado. No BlueJ, abra o projeto, compile
as classes e execute App.main; não são necessárias dependências externas.

Para abrir a interface:
  java -cp .docs/bin App

Autores
-------
- Raul Kolaric
- Liam Lopes
- Rafael Infantini
- Guilherme Coutinho
