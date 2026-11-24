# AGENTS.md

1. 所有回复一律用纯文本 Markdown；不生成 HTML 页面，不调用 answer-me-with-html 技能；不发截图（用户常在手机端使用 OpenCode）。
2. 交流用中文。
3. 设计与开发以 docs/PLAN.md 为准（架构、已定决策、里程碑）；要改方向先改文档再改代码。

## Agent skills

### Issue tracker

工单用 GitHub Issues（`gh` CLI 操作）。见 docs/agents/issue-tracker.md。

### Triage labels

沿用默认五个 triage 角色标签。见 docs/agents/triage-labels.md。

### Domain docs

单上下文：仓库根一个 GLOSSARY.md + docs/adr/（按需惰性创建）。见 docs/agents/domain.md。
