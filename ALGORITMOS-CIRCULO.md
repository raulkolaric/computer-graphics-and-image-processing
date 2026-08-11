# Comparacao dos algoritmos de circulo

O projeto oferece tres algoritmos manuais selecionaveis na interface.
Todos recebem centro, raio, cor e espessura e produzem apenas pixels; nenhum
deles delega o desenho da circunferencia a `Graphics.drawOval`.

## Equacao reduzida

A partir de `(x - xc)^2 + (y - yc)^2 = r^2`, o algoritmo percorre coordenadas
inteiras em cada eixo e calcula a outra coordenada com raiz quadrada. Sao
desenhadas as duas solucoes simetricas. A segunda passagem, invertendo os
eixos, reduz lacunas nas regioes mais inclinadas.

- Vantagem: corresponde diretamente a equacao matematica do circulo.
- Custo: executa raizes quadradas e pode calcular o mesmo pixel mais de uma vez.

## Equacoes parametricas

Percorre o angulo de `0` a `2*pi` e calcula
`x = xc + r*cos(theta)` e `y = yc + r*sin(theta)`. O passo angular varia com
o raio para manter pontos vizinhos proximos na grade de pixels.

- Vantagem: algoritmo curto, geral e facil de relacionar com trigonometria.
- Custo: seno e cosseno sao operacoes mais caras e o arredondamento gera
  repeticoes ou lacunas se o passo for escolhido de forma inadequada.

## Simetria em oito octantes

O algoritmo do ponto medio calcula somente o primeiro octante com somas e
subtracoes inteiras. Cada ponto obtido e refletido para os outros sete
octantes.

- Vantagem: evita trigonometria e raiz quadrada no laco; em geral e a opcao
  mais eficiente e produz uma circunferencia rasterizada continua.
- Custo: e menos direto de deduzir a partir da equacao original.

Para desenho interativo, a simetria em oito octantes e o padrao do projeto.
As outras duas opcoes permanecem disponiveis para comparacao didatica.
