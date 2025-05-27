# Design Document

## 1. Introduction
Javacino is a Java-based command-line interface (CLI) framework designed for modularity, extensibility, and testability. It provides a structured architecture for executing commands, supporting custom command implementations and robust testing.

This document provides insights into architecture, design decisions and motivations behind the project.

Scope:

## 2. Stakeholders
| STAKEHOLDER | CONCERNS |
|-|-|
| Developers | Modularity, extensibility|
| End Users | Reliability, usability, correctness, safety|

## 3. Architectural Drivers
### 1. Key Functional Requirements
Javacino should:
* be start-able from console on Unix-based systems with JVM support.
* support a variety of shell-like commands, including but not limited to, _cat_, _echo_, _exit_, _wc_, _pwd_. Full list of commands will be provided & updated in additional file. 
* be able to pass commands to outer shell for invocation.
* specifically support the _exit_ command to terminate the Javacino session gracefully.

### 2. Non-functional requirements
* Modularity: Javacino components must be insulated to ease up independent modification.
* Extensibility: New commands should be implementable via the __Command__ interface without core code changes.

### 3. Testability
Javacino should contain unit/integration tests.

## 4. Architecture Decisions
Several important architectural decisions should be highlighted.

1. __Command__ interface:
* Decision: all command/utility implementations share a common interface with overloadeable execute() method which returns exit code of the command.
* Rationale: simplifies storing & execution of complex expressions; exit code can be used as ''boolean variable'' (0 as True, others as False) with logical operators.

2. __Parser__ output format:
* Decision: Parser output has a form of postfix notation with pipe operator |, logical operators && and ||. The other elements are Command interface instances.
* Rationale: simplifies expression storage, is quick to implement using stack interfaces, is easier to parse compared to Solution Tree.

3. Pipe operator support
* Decision: Javacino supports the pipe (|) operator for chaining commands.
* Rationale: Pipes allow users to combine multiple commands, where the output of one command becomes the input of the next. This is a fundamental feature of Unix-like shells. 

4. Global vars support
* Decision: ShellContext class is created to interact with both Parser and Executor
* Rationale: global vars set() operation is a command, to be processed by Executor, and get() operation should be run before command call to decrease the risks of substitution being late.

* Decision: global vars do not support pipes.
* Rationale: doing so will seriously alter existing relationship b/w Parser and Executor.

4. Testing Frameworks:
* Decisions: use JUnit + Mockito.
* Rationale: Familiarity, robust mocking capabilities for isolated testing.

## 5.Viewpoints and Views
### 5.1 Module viewpoint

### 5.2 Component viewpoint
Purpose: Describe runtime interactions

Interactions:
1. Shell reads input -> Invokes Parser.
2. Parser tokenizes input -> passes it to Executor as postfix notation of Command instances.
3. Executor parses the notation -> executes commands in correct order & handles input/output transfer

### 5.3 Deployment Viewpoint
Purpose: define deployment configuration.
* Javacino will be packaged as a standalone JAR with dependencies. Source version available from Github will contain tests.