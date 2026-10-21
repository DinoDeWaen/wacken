# Architecture tooling assessment — 21 October 2026

The user approved the standard TASK-233 gate and asked whether ArchUnit or a
better alternative could help. **Use the Groma semantic gate for map integrity;
ArchUnit is the recommended complement for executable Java dependency rules.**
No additional Java test dependency is introduced by this assessment.

| Tool | Useful here | Limits and decision |
| --- | --- | --- |
| Existing pinned Groma plus semantic validator | Source ownership, container placement, role documentation, authored collaborators, task links, scenario resolution and rescan drift | Best fit for the approved TASK-233 scope. Meaning still needs source and browser review. |
| ArchUnit | Import compiled Java classes and enforce dependency, layer, annotation and cycle rules as ordinary Java/JUnit tests | Recommended next complement for domain/application/infrastructure boundary enforcement. Does not validate Groma Markdown ownership, descriptions, task links or scenario claims. |
| JDK jdeps | Class/package/module dependency reports with the existing JDK | Useful diagnostic with no new library, but would need custom enforcement/diagnostic code for the readable rule suite ArchUnit already offers. |
| jQAssistant | Scan multiple artifact types into a graph and evaluate richer constraints/concepts | More graph infrastructure and rule configuration than this small project needs. Consider only if cross-artifact analysis becomes a demonstrated requirement. |

ArchUnit analyzes bytecode, so it can check dependencies without relying on
Groma's Java source scanner resolving external/framework symbols. Appropriate
initial rules would prohibit domain dependencies on application, infrastructure,
Android, Room and network/database clients; prohibit application dependencies on
adapters/Android; and preserve infrastructure dependency direction. Use compiled
main-class locations, exclude test fixtures, assert nonempty imports and include
negative fixtures so a wrong import scope cannot produce a vacuous pass.
Gradle already constrains module compile dependencies; ArchUnit would preserve
the intended rules if future build dependencies expand.

Start with JVM modules whose compiled classes are already available. Android
adapter bytecode/import completeness needs explicit verification before adding
broader UI rules. This would check static dependencies, not prove runtime callback
binding, reachability, unused-code safety, business-rule correctness or a truthful
C4 diagram. Do not infer that every unreferenced class is unused: Android entry
points, generated Room code and reflective/framework wiring require other evidence.

Current repository inspection found only a DomainBoundaryTest that checks the
boundary name. It does not enforce dependency direction. Therefore ArchUnit would
add useful protection, but replacing the approved map gate with it would leave
the demonstrated ownership/island/flow regressions uncovered. No larger tool is
better suited to both problems with less complexity.

Primary sources checked on21 October2026:

- [ArchUnit user guide](https://www.archunit.org/userguide/html/000_Index.html):
  bytecode import, JUnit integration, dependency/layer/cycle rules. The current
  guide identifies version1.5.1; any future adoption should pin a reviewed version.
- [Oracle jdeps reference](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jdeps.html):
  package/class dependency analysis and reporting.
- [jQAssistant manual](https://jqassistant.github.io/jqassistant/current/):
  scanning, graph-backed concepts/constraints and build integration.

