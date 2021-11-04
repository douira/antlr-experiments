# GLSL Parsing with ANTLR4

This is a work in progress experiment on parsing, transforming and re-printing GLSL code using ANTLR4. Try it out with `gradle run`. It will parse a GLSL file from `resources` and print the corresponding parse tree.

## Credit

This repo is based on https://github.com/gabriele-tomassetti/antlr-mega-tutorial which is part of the nice [Java Setup section of the ANTLR Mega Tutorial](https://tomassetti.me/antlr-mega-tutorial/#java-setup).

## Goals

All of these should be fulfilled at the same time.

- Print out the entire original input and preserve line numbers.
- Find certain syntax structures, delete them and replace them with others
- Print out the modified input and also preserve line numbers.

### An example

One program transformation job that this project wants to achieve is the following. (from coderbot on Discord)

Match all declarations of the form:

`layout(location = 0) in vec4 Name;` or `layout (location = 0) attribute vec4 NAME;`

Then:

1. delete that declaration
2. replace all references to that attribute / input with a function call to iris_getModelSpaceVertexPosition()
3. Add the function iris_getModelSpaceVertexPosition() with a specified payload
4. Add a different vertex attribute declaration called iris_Position
   Assuming:
5. there are no functions already called iris_getModelSpaceVertexPosition and there are no vertex attributes with the name iris_Position - if any are found, flag an error
