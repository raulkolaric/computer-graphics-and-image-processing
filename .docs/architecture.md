# Architecture

## Purpose

TestaPontoGr is a small Java/Swing and BlueJ application for creating and
redrawing graphical primitives from mouse clicks. It supports points, lines,
rectangles, triangles, and circles.

The design separates mathematical geometry, styled graphical primitives,
rendering algorithms, and user-interface state.

## Entry point and UI

- `App` starts Swing on the event-dispatch thread and opens `ui.Gui`.
- `ui.Gui` owns the controls and delegates drawing state to
  `ui.PainelDesenho`.
- `ui.TiposPrimitivos` defines the available modes and the number of clicks
  required to construct each primitive.
- `ui.PainelDesenho` receives mouse input, stores the scene, and asks the
  selected renderer to paint it.

The menu is split into three fixed, non-detachable toolbars:

1. Primitive selection.
2. Color, thickness, and circle algorithm.
3. Redraw filter, redraw, clear, delete, and project open/save actions.

Open and save use a JSON file chooser starting in the working directory. After
a successful operation, it selects the last project file. Saving adds `.json`
when needed and asks before overwriting an existing file. Cancelling leaves
the scene and current project unchanged.

The color chooser intentionally exposes only color swatches. Alternative
color-model tabs and the preview panel are not part of the interface.

## Domain model

- `ponto.Ponto` represents a mathematical point.
- `reta.Reta` and `circulo.Circulo` represent geometry without rendering
  concerns.
- `reta.RetaGrafica` and `circulo.CirculoGrafico` add color, thickness, and
  rendering behavior.
- `retangulo.Retangulo` and `triangulo.Triangulo` are composed of graphical
  lines.
- `ponto.PontoGr` represents a drawable, optionally labelled point.
- `reta.EstiloReta` is the shared immutable value for color and thickness.
- `renderizacao.PrimitivoGrafico` is the common contract for stored shapes.

## Rendering

`renderizacao.RenderizadorPrimitivos` isolates rendering from the primitives.
`renderizacao.RenderizadorManual` supplies the current line and circle
algorithms. A renderer can be injected into `PainelDesenho`, which also makes
rendering behavior testable without opening the GUI.

`renderizacao.FiguraPontos` remains as a legacy facade and delegates to the
configured renderer.

## Scene lifecycle

`PainelDesenho` keeps stored and visible collections separate:

- A newly created primitive is added to both collections.
- `limpar()` clears only the visible scene and pending clicks.
- `redesenhar()` restores stored items.
- `redesenhar(TiposPrimitivos)` restores only the selected primitive type.
- Changing drawing mode discards pending clicks so points from different
  modes cannot be combined accidentally.

Color, thickness, and circle algorithm are creation settings. Changing them
affects new primitives only; stored primitives retain their original style.

## Persistence

`PersistenciaProjeto` builds and validates a candidate scene before
`PainelDesenho.carregarProjeto` replaces the stored and visible collections.
Invalid model values and manual-renderer limits produce `IOException`, which
the GUI displays while preserving the existing scene. The renderer shares its
limit validation with the importer without drawing or iterating over pixels.

The `figura` format groups shapes by type. New saves attach `ordem` to every
shape to preserve their original layering. If any shape has this field, every
shape must have a unique integer from zero through the shape count minus one;
partial, duplicate, or out-of-range orders are rejected. Files without it retain
the historical line, triangle, rectangle, circle order. Points are painted after
all shapes, as in the in-memory scene.

Coordinates are normalized separately by width and height. New circles also
store `raioRelativo`, the pixel radius divided by `min(width, height)`. Loading
multiplies this scalar by the current minimum dimension, so radius scaling no
longer depends on the direction of the second click. `centro` and the historical
circumference point `raio` remain in the file. Without `raioRelativo`, loading
keeps the original two-point calculation after coordinate conversion. Old files
do not record the source aspect ratio, so it cannot be recovered automatically.
The legacy absolute-coordinate version 1 format remains readable.
Scaling is applied when opening a file; resizing an already-open window does
not rescale the current scene.

The project-specific JSON reader accepts scientific notation and rejects
non-finite numbers; it does not implement the complete JSON specification.
The application requires JDK 11 or later. The four runnable regression checks
and compilation commands are listed in `README.TXT`.

## Dependency direction

The intended dependency flow is:

```text
App -> UI -> graphical primitives -> geometry
          -> renderer interface <- manual renderer
```

Geometry must not depend on Swing. Graphical primitives should delegate pixel
generation to the renderer rather than duplicating algorithms.
