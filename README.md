# TestaPontoGr — Graphical Primitives

A small Java/Swing desktop application for drawing **points, lines, rectangles, triangles, and circles** from mouse clicks. Every line and circle is rasterized *by hand*, pixel by pixel, rather than with `Graphics.drawLine` or `drawOval`. It was built for a computer graphics course at PUC and is meant to be read as much as run.

- Draw, select, delete, clear, and redraw primitives.
- Mirror shapes across a line you define with the mouse, using the composed-transformation matrices from the course.
- Save and reopen a scene as JSON. Each save also writes a JPEG image next to it.
- Three circle algorithms to compare: reduced equation, sine/cosine, and 8-octant midpoint.
- Plain JDK only: no build system and no dependencies.

> The user interface, source identifiers, and Javadoc are in Brazilian Portuguese. This file and the other repository documents are in English. See [CONTRIBUTING.md](CONTRIBUTING.md) for the rationale. The full Portuguese user guide is [README.TXT](README.TXT).

## Quick start

You need **JDK 11 or later**. Anything newer works; the tests pass on JDK 26.

```bash
javac --release 11 -d out *.java */*.java
```

```bash
java -cp out App
```

The window opens at 900 × 600 pixels. In BlueJ, open the project folder, compile, and run `App.main`.

## Using the app

1. Pick a tool: **Ponto**, **Reta**, **Retangulo**, **Triangulo**, or **Circulo**.
2. Pick a color, thickness, and (for circles) an algorithm. These apply to *new* shapes only. Existing shapes keep the style they were created with.
3. Click on the canvas. A point takes 1 click, a triangle 3, and every other shape 2. After the first click, move the mouse to see a live preview. Only completed shapes enter the project.

| Control | What it does |
| --- | --- |
| **Selecionar** | Click a shape to select it. |
| **Excluir selecionado** | Removes the selected shape from the screen *and* from memory. |
| **Limpar** | Hides everything but keeps the shapes in memory. |
| **Redesenhar** | Rebuilds the canvas from memory, optionally filtered by shape type. |
| **Espelhar** | While on, click two distinct points to define an axis. Every new shape then gets a mirrored copy. |
| **Espelhar selecionado** | Click p1 and p2 to add one mirrored copy of the selected shape. Esc cancels. |
| **Salvar projeto / Salvar como** | Write the scene to `saves/Projeto N.json` plus a matching `.jpeg`. |
| **Abrir projeto** | Validate a JSON file, then replace the current scene. |

*Clear* and *Redraw* work on an in-memory store, and a save writes only what is currently on screen. The README.TXT section "Limpar e Redesenhar" has worked examples of what each sequence saves.

## How it works

### Layers

```text
App ─▶ ui (Gui, PainelDesenho)
         │
         ▼
      graphical primitives ─▶ geometry
         │
         ▼
      RenderizadorPrimitivos  ◀── RenderizadorManual
```

| Package | Responsibility |
| --- | --- |
| `ponto` | `Ponto` is a mathematical point. `PontoGr` is a drawable, optionally labeled point. |
| `reta`, `circulo` | `Reta` and `Circulo` hold pure geometry. `RetaGrafica` and `CirculoGrafico` add style and drawing. |
| `retangulo`, `triangulo` | Shapes composed entirely of `RetaGrafica` edges. |
| `renderizacao` | The renderer interface, the manual rasterizer, mirroring, and the 3×3 matrix helpers. |
| `persistencia` | JSON read/write, numbered file names, and JPEG export. |
| `ui` | Swing controls (`Gui`), plus scene state and mouse input (`PainelDesenho`). |

Two rules keep the design clean. Geometry never depends on Swing. Graphical primitives hand pixel work to a `RenderizadorPrimitivos` instead of drawing themselves. Because the renderer is injected into `PainelDesenho`, tests can render without opening a window.

### Rasterization

`RenderizadorManual` plots individual pixels.

- **Lines** use the reduced equation `y = m·x + b`. It steps along x when `|Δx| ≥ |Δy|` and along y otherwise, so steep lines have no gaps. Vertical lines are a special case.
- **Circles** can use any of three algorithms, chosen per circle:
  - `EQUACAO_REDUZIDA` solves `y = yc ± √(r² − (x − xc)²)`.
  - `PARAMETRICO` sweeps `x = xc + r·cos θ`, `y = yc + r·sin θ`.
  - `SIMETRIA_OCTANTES` is the midpoint algorithm. It computes one octant and mirrors it eight ways.
- Lines are capped at 2,000,000 steps and circles at a radius of 100,000. Extreme input fails with an `IllegalArgumentException` instead of freezing the UI.

### Mirroring

`Espelhamento` reflects any point across an arbitrary axis by composing three transformations with `Matrizes` and `TransfGeometricaComposta`: translate the axis to the origin, rotate it onto the X axis, reflect across X, then apply the inverses. The rotation uses `atan2`, so vertical axes work, and coincident axis points are rejected. A mirrored rectangle keeps four vertices, so its orientation survives the reflection.

### Scene lifecycle

`PainelDesenho` keeps two collections: *stored* (memory) and *visible* (on screen). New shapes go into both. *Clear* empties only the visible set, *Redraw* restores it from the stored set, and *Delete* removes from both.

## File formats

A project is a single JSON object with a `figura` key that groups shapes by type (`ponto`, `reta`, `triangulo`, `retangulo`, `circulo`). Coordinates are **normalized**, stored as fractions of the canvas width and height, so a scene reopens at any window size. Scaling happens when a file is opened; resizing a window later does not rescale the scene.

```json
{
  "figura": {
    "reta": [
      {
        "p1": { "x": 0.669, "y": 0.434 },
        "p2": { "x": 0.669, "y": 0.484 },
        "cor": { "r": 79, "g": 56, "b": 7 },
        "esp": 12,
        "id": "reta_1"
      }
    ]
  }
}
```

(Abridged. See [`saves/exemplo.json`](saves/exemplo.json) for a full file.)

Compatibility notes:

- **`ordem`**: new saves number every shape so overlap is preserved. If any shape has it, all must, with unique integers from 0 to *n − 1*. Without it, the order is lines, triangles, rectangles, circles. Points are always drawn last.
- **`raioRelativo`**: a circle's pixel radius divided by `min(width, height)`. This keeps circles circular when the canvas aspect ratio changes. The older `raio` field (a point on the circumference) is still written and still used when `raioRelativo` is absent.
- **Rectangles** store `p3` and `p4` in addition to the opposite corners `p1` and `p2`. Older two-point files still load.
- **Legacy v1** files with absolute coordinates are still readable.
- The JSON reader is hand-written and project-specific. It accepts scientific notation such as `1.62e-1` but is **not** a general JSON parser.
- A save writes the `.json` first and then the `.jpeg` (white background, quality 0.95), with no shared transaction. If only the image fails, the JSON stays saved and the UI warns that the old JPEG may be stale.

## Tests

The tests are plain Java programs that use `assert`, so run them with `-ea`. There is no JUnit.

```bash
for t in TestaProjetos TestaEspelhamento TestaPrimitivos TestaImportacao TestaOrdem TestaEscalaCirculo; do
  java -ea -Djava.awt.headless=true -cp out tests.$t
done
```

Each prints an `OK` or "passed" line and exits non-zero on failure. `TestaPonto` and `TestaReta` are small demos, not regression tests. Fixtures live in `tests/fixtures/`.

## Project layout

```text
App.java             entry point (default package, kept for BlueJ)
ponto/ reta/ circulo/ retangulo/ triangulo/   geometry and graphical primitives
renderizacao/        rasterizer, mirroring, matrices
persistencia/        JSON, file naming, JPEG export
ui/                  Swing GUI and scene state
tests/               assertion-based regression programs
saves/               default project folder, created on first save
README.TXT           full user guide, in Portuguese
```

## Contributing

Contributions are welcome. Read [CONTRIBUTING.md](CONTRIBUTING.md) first, especially the naming rules, which differ from typical Java style. All participants are expected to follow the [Code of Conduct](CODE_OF_CONDUCT.md).

## Authors

Raul Kolaric, Liam Lopes, Rafael Infantini, and Guilherme Coutinho.

## License

[MIT](LICENSE) © 2026 Raul Kolarić
