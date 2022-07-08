package io.github.douira.glsl_transformer.ast.node.expression.binary;

import io.github.douira.glsl_transformer.ast.node.expression.Expression;
import io.github.douira.glsl_transformer.ast.traversal.*;

public class BitwiseXorAssignmentExpression extends BinaryExpression {
  public BitwiseXorAssignmentExpression(Expression left, Expression right) {
    super(left, right);
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.BITWISE_XOR_ASSIGNMENT;
  }

  @Override
  public <R> R expressionAccept(ASTVisitor<R> visitor) {
    return visitor.visitBitwiseXorAssignmentExpression(this);
  }

  @Override
  public void enterNode(ASTListener listener) {
    super.enterNode(listener);
    listener.enterBitwiseXorAssignmentExpression(this);
  }

  @Override
  public void exitNode(ASTListener listener) {
    super.exitNode(listener);
    listener.exitBitwiseXorAssignmentExpression(this);
  }
}
