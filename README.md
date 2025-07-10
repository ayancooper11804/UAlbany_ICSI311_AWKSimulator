# AWK Language Simulator, Fall 2023
This project was developed as part of my *Principles of Programming Languages* course. The goal was to simulate core functionality of the AWK programming language — a scripting language used for pattern scanning and processing — in order to better understand how programming languages are structured and interpreted.
## Project Overview
This simulator replicates key behaviors of AWK by implementing:
Lexer: Tokenizes input strings into meaningful components.
Parser: Analyzes tokens to create a structured representation (AST).
Interpreter: Executes parsed input in a manner similar to AWK.
It handles a variety of input operations and mimics real-world AWK use cases, helping us explore aspects like syntax design, evaluation rules, and execution flow.
## Testing
Unit tests were written to verify correct behavior under multiple scenarios, ensuring that the interpreter properly processed AWK-style code across a variety of cases.
## Notes
This project was designed for educational purposes.
The codebase includes multiple helper classes and subcomponents (some minimal), reflecting the modular design of interpreters.
AWK was chosen specifically to examine dynamic scripting languages and contrast them with static counterparts.
