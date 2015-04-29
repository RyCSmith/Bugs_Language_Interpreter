# Bugs_Language_Interpreter
Overview: Interpreter for Bugs Programming Language

Design: The Bugs Language is a Turing complete programming language designed by Dr. Dave Matuszek at the University of Pennsylvania. The primary purpose of the language is to create digital drawings. Some helper files (TreeParser, Token, SyntaxException) were provided by Professor Matuszek. All implementation completed by Ryan Smith.

Use: The Bugs interpreter accepts Bugs language files as .txt. files. It recognizes proper syntax and parses the file into an syntax tree that can be used to execute the program. It uses the Tree API I wrote (also available on this account) for the Tree data structure. After parsing it synchronizes all drawings within a program and allows the user to control execution. To use, launch the .jar executable and choose load from the file menu. Sample Bugs programs are available in the bugs progs folder (all written by Professor Matuszek). After loading click Run to execute the program.

Other info: Javadocs included. Video demonstration of the interpreter can be seen at ryancharlessmith.com.