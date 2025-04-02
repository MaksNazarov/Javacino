# Why we did this

And why we did that too.
## CLI framework choice

Picocli framework was chosen from the lineup of Apache Commons CLI, args4j, Spring Shell and JCommander.

Main motivations behind the choice are as follows:
1. Not Commons CLI: picocli offers less verbose syntax, automatic args type conversion, better help message, autocompletion feature out of the box.
2. Not JCommander: picocli offers richer syntax specifically for command objects
3. Not args4j: same as Commons CLI.
4. Spring Shell: picocli is much less lightweight; Spring Shell is too big for the project scope.
