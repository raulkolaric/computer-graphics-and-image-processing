# Contributing

Thanks for helping out. This is a small teaching project, so the process is light. The conventions are strict in a few places, and they are easy to miss.

By taking part you agree to follow the [Code of Conduct](CODE_OF_CONDUCT.md).

## Getting set up

You need JDK 11 or later and Git. Nothing else.

```bash
git clone https://github.com/raulkolaric/computer-graphics-and-image-processing.git
cd computer-graphics-and-image-processing
javac --release 11 -Xlint:all -d out *.java */*.java
```

Run the app with `java -cp out App`. Never commit `out/`, `.class` files, `.DS_Store`, or editor state. BlueJ rewrites `package.bluej` whenever you open the project. Leave it out of your commits unless your change is really about BlueJ metadata.

## Two languages, two rules

This is the rule people trip over most.

| Domain | Language |
| --- | --- |
| Source identifiers, Javadoc, code comments, UI text | **Brazilian Portuguese** |
| Branches, commits, PRs, issues, and repository docs | **American English** |

Don't mix the two within a domain.

**Code (Portuguese).**
- Name every type, method, field, variable, test class, and fixture in Portuguese, without diacritics: `quantidadePontos`, `calcularDistancia`, `RenderizadorPrimitivos`. Don't add English synonyms such as `pointCount` beside the Portuguese model.
- Use `PascalCase` for types, `camelCase` for members, and `UPPER_SNAKE_CASE` for constants. Spell names out. Don't abbreviate unless the abbreviation is standard in the domain.
- Names forced by Java or a library (`main`, `toString`, `paintComponent`) stay as they are.
- Javadoc and comments use correct accents. UI strings in the existing app are Portuguese too.

**Repository text (American English).** Use `color`, `behavior`, `center`, and `canceled`, never the British spellings. This covers branch names, commit messages, PR and issue text, and the files in the repository root such as this one.

## Code style

- Prefer the smallest change that fits the existing design.
- Keep the project compilable with plain `javac`. Don't add a build system or a dependency for something the JDK already does.
- Keep the layering: geometry never imports Swing, and primitives delegate pixel work to `RenderizadorPrimitivos`. Reuse `EstiloReta`, `PrimitivoGrafico`, and `RenderizadorPrimitivos` instead of adding parallel abstractions.
- Swing components and event wiring live in `ui.Gui`. Scene state and mouse input live in `ui.PainelDesenho`. Keep toolbars non-floatable.
- Style belongs to a shape when it is created. Redraw must never depend on the toolbar's current settings.
- Validate arguments at public boundaries and throw `IllegalArgumentException`.
- Document every public or protected type and member in Javadoc. Describe the contract: units, ranges, `null` handling, side effects, and thread requirements. Don't restate the declaration. Use `@param`, `@return`, and `@throws`.
- Comments explain *why*, one precise sentence at the smallest block they apply to. No commented-out code and no bare `TODO`.
- When a change affects the visible workflow, update `README.TXT` (Portuguese). If it changes something described in `README.md`, update that too.

## Tests

Tests are executable classes in `tests/` that use `assert`. There is no framework, so don't add one. Add the smallest check that proves the behavior, preferably headless against domain objects or `PainelDesenho`.

```bash
for t in TestaProjetos TestaEspelhamento TestaPrimitivos TestaImportacao TestaOrdem TestaEscalaCirculo; do
  java -ea -Djava.awt.headless=true -cp out tests.$t
done
```

```bash
git diff --check
```

Run all of them before you open a PR. A new test class needs a `main` and must be added to the loop above in both this file and `README.md`.

Existing compiler warnings are known. Don't mix warning cleanup into an unrelated change.

## Workflow

1. Open or find an issue for anything non-trivial and say you are taking it.
2. Branch from an up-to-date `main`: `git switch -c fix/short-description origin/main`. Use American English, lowercase, and hyphens. (AI-assisted branches use a `codex/` prefix.)
3. Make one logical change per commit. Put a feature or fix and its focused tests in the same commit.
4. Stage files by name, not with `git add -A`, and check `git status` and `git diff --staged` first.
5. Push and open a PR against `main`. Say what changed, why, and which checks you ran. Mention anything you couldn't run.
6. Respond to review. Keep follow-ups small, and don't force-push over a review in progress without saying so.

If `main` has moved and your branch no longer applies cleanly, port the behavior onto a fresh branch by hand. Don't copy obsolete structure wholesale.

### Commit messages

[Conventional Commits](https://www.conventionalcommits.org/): `<type>(<scope>): <imperative description>`, with no trailing period.

Types: `feat`, `fix`, `refactor`, `test`, `docs`, `build`, `ci`, `chore`. Useful scopes include `gui`, `rendering`, `graphics`, `persistence`, and `readme`.

```text
fix(rendering): handle extreme line coordinates

Reject lines longer than the step limit before rasterizing so a bad
file cannot freeze the interface.

Changes:
- renderizacao/RenderizadorManual.java
  - Validate step count before plotting.

Validation:
- all six regression programs under -ea
```

For non-trivial commits, add a short explanation, a `Changes:` list, and a `Validation:` list. Only list checks you actually ran. Use `!` and a `BREAKING CHANGE:` footer only for genuine breaking changes.

## Reporting bugs and suggesting features

Open a GitHub issue in American English. For a bug, include:

- what you did, what you expected, and what happened;
- your JDK version (`java -version`) and operating system;
- the project JSON if the bug involves opening or saving a file, and a screenshot if it is visual.

For a feature, describe the problem first and the proposed behavior second. Small features that fit the existing layers are much easier to accept than new subsystems.

## Questions

If something here is unclear, or you aren't sure a change is wanted, open an issue and ask before you invest time.
