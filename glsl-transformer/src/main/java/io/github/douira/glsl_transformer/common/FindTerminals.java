package io.github.douira.glsl_transformer.common;

import java.util.Collection;
import java.util.function.Consumer;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import io.github.douira.glsl_transformer.GLSLLexer;
import io.github.douira.glsl_transformer.transform.WalkPhase;

public class FindTerminals extends WalkPhase {
  public record Target(String contained, Consumer<Token> action) {
  }

  private Collection<Target> targets;

  public FindTerminals(Collection<Target> targets) {
    this.targets = targets;
  }

  @Override
  public void visitTerminal(TerminalNode node) {
    Token token = node.getSymbol();
    if (token.getType() == GLSLLexer.IDENTIFIER) {
      String text = token.getText();
      for (var target : targets) {
        if (text.contains(target.contained())) {
          target.action().accept(token);
        }
      }
    }
  }
}
