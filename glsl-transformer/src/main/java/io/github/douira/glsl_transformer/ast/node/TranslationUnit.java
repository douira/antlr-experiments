package io.github.douira.glsl_transformer.ast.node;

import java.util.*;
import java.util.stream.Stream;

import io.github.douira.glsl_transformer.ast.node.basic.ListASTNode;
import io.github.douira.glsl_transformer.ast.node.external_declaration.*;
import io.github.douira.glsl_transformer.ast.node.statement.*;
import io.github.douira.glsl_transformer.ast.transform.*;
import io.github.douira.glsl_transformer.ast.traversal.*;

public class TranslationUnit extends ListASTNode<ExternalDeclaration> {
  protected VersionStatement versionStatement;

  public TranslationUnit(VersionStatement versionStatement, Stream<ExternalDeclaration> children) {
    super(children);
    this.versionStatement = setup(versionStatement, this::setVersionStatement);
  }

  public TranslationUnit(Stream<ExternalDeclaration> children) {
    super(children);
  }

  public VersionStatement getVersionStatement() {
    return versionStatement;
  }

  public void setVersionStatement(VersionStatement versionStatement) {
    updateParents(this.versionStatement, versionStatement, this::setVersionStatement);
    this.versionStatement = versionStatement;
  }

  public void injectNode(ASTInjectionPoint injectionPoint, ExternalDeclaration node) {
    children.add(injectionPoint.getInjectionIndex(this), node);
  }

  public void injectNodes(
      ASTInjectionPoint injectionPoint,
      Collection<ExternalDeclaration> nodes) {
    children.addAll(injectionPoint.getInjectionIndex(this), nodes);
  }

  public void parseAndInjectNode(
      ASTParser t,
      ASTInjectionPoint injectionPoint,
      String externalDeclaration) {
    children.add(injectionPoint.getInjectionIndex(this),
        t.parseExternalDeclaration(
            this,
            externalDeclaration));
  }

  public void parseAndInjectNodes(
      ASTParser t,
      ASTInjectionPoint injectionPoint,
      String... externalDeclarations) {
    injectNodes(injectionPoint, t.parseExternalDeclarations(this, externalDeclarations));
  }

  public CompoundStatement getFunctionDefinitionBody(String functionName) {
    return getRoot().identifierIndex.getStream(functionName)
        .map(id -> id.getBranchAncestor(FunctionDefinition.class, FunctionDefinition::getFunctionPrototype))
        .filter(Objects::nonNull).findAny().map(FunctionDefinition::getBody).orElse(null);
  }

  public void prependFunctionBody(String functionName, Statement statement) {
    getFunctionDefinitionBody(functionName).statements.add(0, statement);
  }

  public void prependFunctionBody(String functionName, Collection<Statement> statements) {
    getFunctionDefinitionBody(functionName).statements.addAll(0, statements);
  }

  public void appendFunctionBody(String functionName, Statement statement) {
    getFunctionDefinitionBody(functionName).statements.add(statement);
  }

  public void appendFunctionBody(String functionName, Collection<Statement> statements) {
    getFunctionDefinitionBody(functionName).statements.addAll(statements);
  }

  public void prependMain(Statement statement) {
    prependFunctionBody("main", statement);
  }

  public void prependMain(Collection<Statement> statements) {
    prependFunctionBody("main", statements);
  }

  public void appendMain(Statement statement) {
    appendFunctionBody("main", statement);
  }

  public void appendMain(Collection<Statement> statements) {
    appendFunctionBody("main", statements);
  }

  public void prependMain(ASTParser t, String... statements) {
    prependMain(t.parseStatements(this, statements));
  }

  public void prependMain(ASTParser t, String statement) {
    prependMain(t.parseStatement(this, statement));
  }

  public void appendMain(ASTParser t, String... statements) {
    appendMain(t.parseStatements(this, statements));
  }

  public void appendMain(ASTParser t, String statement) {
    appendMain(t.parseStatement(this, statement));
  }

  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visitTranslationUnit(this);

  }

  @Override
  public void enterNode(ASTListener listener) {
    listener.enterTranslationUnit(this);
  }

  @Override
  public void exitNode(ASTListener listener) {
    listener.exitTranslationUnit(this);
  }
}
