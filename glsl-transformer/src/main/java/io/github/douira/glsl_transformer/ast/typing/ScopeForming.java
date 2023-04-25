package io.github.douira.glsl_transformer.ast.typing;

public class ScopeForming extends Type {
  /**
   * The scope formed by this type. Used for compound statements.
   */
  public final Scope formedScope;

  public ScopeForming(Scope formedScope) {
    this.formedScope = formedScope;
  }
}
