# Java Code Style Guide

## Indentation & Spacing
- 4 spaces per indentation level
- Braces on the same line as declarations
- One blank line between methods
- No trailing whitespace

## Naming Conventions
- Classes: PascalCase (e.g., `ChatServlet`)
- Methods/variables: camelCase (e.g., `getConnection`)
- Constants: UPPER_SNAKE_CASE (e.g., `DB_URL`)
- Private fields: prefix with `this.` if needed for clarity

## Function/Class Organization
- One top-level class per file
- Group related methods together (e.g., validation, DB access)
- Private helper classes should be nested

## Comment Styles
- JavaDoc for all public/protected classes and methods
- Inline comments for non-obvious logic
- Avoid commented-out code in main branches

## Import Patterns
- No wildcard imports; import only what is used
- Group imports: standard library, third-party, project-specific

## Automation
- **Linting:** [Checkstyle](https://checkstyle.org/) using `checkstyle.xml` (Google Java Style)
- **Formatting:** [Spotless Maven Plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven) with Google Java Format
- **Pre-commit:** [pre-commit](https://pre-commit.com/) runs Checkstyle, Spotless, and whitespace checks

## How to Use
1. Install pre-commit: `pip install pre-commit`
2. Run `pre-commit install` in the repo root
3. On commit, code will be auto-checked and formatted
4. To manually check/format: `mvn spotless:check` or `mvn spotless:apply`

## References
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Checkstyle Docs](https://checkstyle.org/)
- [Spotless Maven Plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven) 