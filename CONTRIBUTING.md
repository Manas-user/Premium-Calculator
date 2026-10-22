# Contributing to Premium Calculator

Thank you for contributing to the **Premium Calculator** project! This project adheres to Git branching strategies, automated testing, and CI/CD quality gates.

---

## 1. Git Branching Strategy: `develop` Workflow

We follow a GitFlow-inspired branching model tailored for agile development and college practical evaluation:

```
main (Production / Tagged Releases: v1.0.0)
  ▲
  │ (Pull Request after CI & QA Sign-off)
develop (Integration / Nightly Builds)
  ▲
  ├── feature/calc-formula-tuning
  ├── feature/status-transition-rules
  └── fix/receipt-formatting
```

### Branch Rules:
- **`main`**: Protected release branch. Only updated via pull requests or verified deployment milestones from `develop`.
- **`develop`**: Primary integration branch where new features and bug fixes merge. All CI pipelines run on this branch.
- **`feature/<feature-name>`**: Feature branches branched from `develop` and merged back into `develop`.
- **`fix/<bug-description>`**: Bug fix branches branched from `develop` or `main`.

---

## 2. Commit Message Convention

We follow the **Conventional Commits** specification (`<type>(<scope>): <short description>`):

### Allowed Types:
- `feat`: A new user-facing or backend feature.
- `fix`: A bug fix in calculation logic, API, or UI.
- `docs`: Documentation updates, practical reports, or architecture diagrams.
- `style`: Formatting, missing semicolons, CSS adjustments (no code change).
- `refactor`: Refactoring production code without changing behavior.
- `test`: Adding missing tests or correcting existing tests (JUnit, Mockito, Selenium).
- `ci`: Changes to CI/CD configuration files (Jenkinsfile, GitHub workflows).
- `chore`: Build tasks, dependency bumps, `.gitignore`, Docker/Ansible scripts.

### Examples:
```bash
git commit -m "feat(calc): implement transparent estimate formula for Life and Health"
git commit -m "fix(api): correct Content-Disposition header in receipt download endpoint"
git commit -m "test(unit): add boundary condition tests for coverage amounts"
git commit -m "ci(jenkins): add automated health check stage with retry loop"
git commit -m "docs(report): complete college practical report sections 1 to 20"
```

---

## 3. Local Development & Verification Checklist

Before creating a commit or opening a pull request:
1. Ensure code compiles without warnings:
   ```powershell
   .\mvnw.cmd clean compile
   ```
2. Run all unit and integration tests:
   ```powershell
   .\mvnw.cmd test
   ```
3. Verify test coverage is maintained and all 33 tests pass.
4. Verify application runs locally at `http://localhost:8080`.
5. Check that no secrets, passwords, or temporary files are committed.
