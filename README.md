# Javacino

Java-based command line interface simulation.

## Table of Contents
- [Description](#description)
- [Dependencies](#dependencies)
- [Getting Started](#getting-started)
- [Design Document](#design-document)
- [Project structure](#project-structure)
- [License](#license)

## Description

Javacino is a Java-based implementation of a Command-Line Interface (CLI) designed to provide a modular and extensible framework for executing commands. The project includes both the core CLI implementation and a robust testing environment to ensure reliability and correctness.

## Dependencies
Following libraries are used:
- [JUnit](https://junit.org/junit5/) - core testing framework.

- [Mockito](https://site.mockito.org/) - mocking framework to simplify mock object creation for testing.

- [picocli](https://picocli.info/) - framework for creating Command Line Interface apps for JVM.

## Getting Started
Clone the repository:

```
git clone git@github.com:MaksNazarov/Javacino.git
cd javacino
```

This project uses Gradle as its build tool. To build the project, ensure you have Gradle installed on your system. If not, you can download it from [here](https://gradle.org/install/).

```
./gradlew run
```

This will compile the source code, run tests.

## Design document
For project architecture description & decision motivation insights see [here](docs/DesignDocument.md).

## Project structure

See [here](docs/Project_structure.md).

For some motivation behind dependencies choice & implementation decisions, see [here](docs/WhyWeDidThis.md).

## License

MIT, for more info see [here](LICENSE).