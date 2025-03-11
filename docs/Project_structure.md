# Project structure

The project itself consists of two parts:
1. CLI implementation
2. Testing environment

### CLI implementation
This part of the project contains implementation of CLI interface.

Structure diagram:
![structure diagram](resources/CLI_component_diagram.jpg)

Class diagram:
![class diagram](resources/CLI_class_diagram.jpg)

Following central classes are intended to be implemented:
1. __Shell__ - entry point and orchestrator for the entire system
2. __Parser__ - responsible for input tokenization and construction of command expression
3. __Executor__ - responsible for executing the command expression in accordance with set priority/rules 
4. __Command__ - interface for simulated commands/utilities like _grep_ and _echo_; will be implemented by various classes such as _EchoCommand_.


### Testing environment
Implemented with the __JUnit__ __framework__, this part of the project contains unit & integration tests used to verify correctness of main part. 
__JUnit__ __framework__ was chosen because of familiarity of both contributors with it. __Mockito__ was chosen to simplify mock object creation.

