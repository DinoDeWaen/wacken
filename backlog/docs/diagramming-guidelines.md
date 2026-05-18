# Diagramming Guidelines

## Purpose

Use diagrams only when they make the documentation easier to understand.
Diagrams should clarify architecture, workflow, boundaries, or important domain
relationships. They should not repeat obvious text.

## Required Format

- Use Mermaid for diagrams in Markdown.
- Use C4-style Mermaid diagrams for architectural designs where possible.
- Use simple Mermaid flowcharts for workflows, state changes, and process flows.

## When To Add Or Update Diagrams

Add or update a diagram when a task changes:

- Architecture boundaries.
- Package/module structure.
- External systems or integrations.
- Ports/adapters or dependency direction.
- Important workflows.
- Domain relationships that are hard to understand from text alone.

Do not add diagrams for small local implementation details that are clearer in
code.

## C4 Guidance

Prefer C4 diagrams at the right level:

- Context: who or what uses the system.
- Container: major deployable or runtime parts.
- Component: only when component boundaries are stable and useful.

For new applications, the default useful levels are Context and
Container/Hexagonal View. Component diagrams are usually unnecessary until the
design has stable internal modules or components that are worth explaining.

## Agent Completion Rule

When closing a task, mention diagram impact in implementation notes:

- `Diagram impact: updated ...`
- `Diagram impact: none, because ...`
