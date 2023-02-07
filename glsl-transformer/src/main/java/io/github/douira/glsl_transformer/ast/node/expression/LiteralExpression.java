package io.github.douira.glsl_transformer.ast.node.expression;

import java.util.Objects;

import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.traversal.*;
import io.github.douira.glsl_transformer.util.NumericType;
import io.github.douira.glsl_transformer.util.NumericType.NumberType;

public class LiteralExpression extends TerminalExpression {
  private NumericType literalType;
  private boolean booleanValue;
  private long integerValue;
  private IntegerFormat integerFormat;
  private double floatingValue;

  public enum IntegerFormat {
    DECIMAL(10),
    HEXADECIMAL(16),
    OCTAL(8);

    public final int radix;

    IntegerFormat(int radix) {
      this.radix = radix;
    }
  }

  private LiteralExpression(
      NumericType literalType,
      boolean booleanValue,
      long integerValue,
      IntegerFormat integerFormat,
      double floatingValue) {
    this.literalType = literalType;
    this.booleanValue = booleanValue;
    this.integerValue = integerValue;
    this.integerFormat = integerFormat;
    this.floatingValue = floatingValue;
  }

  public LiteralExpression(boolean booleanValue) {
    setBoolean(booleanValue);
  }

  public LiteralExpression(NumericType literalType, long integerValue) {
    setInteger(literalType, integerValue);
  }

  public LiteralExpression(NumericType literalType, long integerValue, IntegerFormat integerFormat) {
    setInteger(literalType, integerValue, integerFormat);
  }

  public LiteralExpression(NumericType literalType, double floatingValue) {
    setFloating(literalType, floatingValue);
  }

  private void validateLiteralType(NumericType type) {
    if (type == null) {
      throw new NullPointerException("Literal type cannot be null!");
    }
    if (!type.isScalar()) {
      throw new IllegalArgumentException("Literal type must be a scalar!");
    }
  }

  public Number getNumber() {
    var bitDepth = literalType.getBitDepth();
    switch (getNumberType()) {
      case BOOLEAN:
        return booleanValue ? 1 : 0;
      case SIGNED_INTEGER:
      case UNSIGNED_INTEGER:
        switch (bitDepth) {
          case 8:
            return Byte.valueOf((byte) integerValue);
          case 16:
            return Short.valueOf((short) integerValue);
          case 32:
            return Integer.valueOf((int) integerValue);
          case 64:
            return Long.valueOf(integerValue);
          default:
            throw new IllegalArgumentException("Unsupported bit depth: " + bitDepth);
        }
      case FLOATING_POINT:
        if (bitDepth == 64) {
          return Double.valueOf(floatingValue);
        }
        return Float.valueOf((float) floatingValue);
      default:
        throw new IllegalArgumentException("Unsupported number type: " + getNumberType());
    }
  }

  public NumericType getNumericType() {
    return literalType;
  }

  public NumericType.NumberType getNumberType() {
    return literalType.getNumberType();
  }

  public boolean getBoolean() {
    return booleanValue;
  }

  public void setBoolean(boolean booleanValue) {
    this.booleanValue = booleanValue;
    this.integerFormat = null;
    this.integerValue = 0;
    this.floatingValue = 0;
    this.literalType = NumericType.BOOL;
  }

  public void changeBoolean(boolean booleanValue) {
    if (!isBoolean()) {
      throw new IllegalStateException("Literal type must be a boolean!");
    }
    this.booleanValue = booleanValue;
  }

  public long getInteger() {
    return integerValue;
  }

  public void setInteger(NumericType integerType, long integerValue, IntegerFormat integerFormat) {
    Objects.requireNonNull(integerFormat, "Integer format cannot be null!");
    validateLiteralType(integerType);
    var numberType = integerType.getNumberType();
    if (numberType != NumberType.SIGNED_INTEGER && numberType != NumberType.UNSIGNED_INTEGER) {
      throw new IllegalArgumentException("Literal type must be an integer!");
    }
    this.integerValue = integerValue;
    this.booleanValue = false;
    this.integerFormat = integerFormat;
    this.floatingValue = 0;
    this.literalType = integerType;
  }

  public void setInteger(NumericType integerType, long integerValue) {
    setInteger(integerType, integerValue, IntegerFormat.DECIMAL);
  }

  public void setInteger(int integerValue) {
    setInteger(NumericType.INT32, integerValue);
  }

  public void changeInteger(long integerValue) {
    if (!isInteger()) {
      throw new IllegalStateException("Literal type must be an integer!");
    }
    this.integerValue = integerValue;
  }

  public IntegerFormat getIntegerFormat() {
    return integerFormat;
  }

  public int getIntegerRadix() {
    return integerFormat.radix;
  }

  public void setIntegerFormat(IntegerFormat integerFormat) {
    if (!isInteger()) {
      throw new IllegalStateException("Literal type must be an integer!");
    }
    this.integerFormat = integerFormat;
  }

  public double getFloating() {
    return floatingValue;
  }

  public void setFloating(NumericType floatingType, double floatingValue) {
    validateLiteralType(floatingType);
    if (floatingType.getNumberType() != NumberType.FLOATING_POINT) {
      throw new IllegalArgumentException("Literal type must be a floating point!");
    }
    this.floatingValue = floatingValue;
    this.booleanValue = false;
    this.integerValue = 0;
    this.integerFormat = null;
    this.literalType = floatingType;
  }

  public void setFloating(float floatingValue) {
    setFloating(NumericType.FLOAT32, floatingValue);
  }

  public void changeFloating(double floatingValue) {
    if (!isFloatingPoint()) {
      throw new IllegalStateException("Literal type must be a floating point!");
    }
    this.floatingValue = floatingValue;
  }

  public boolean isBoolean() {
    return getNumberType() == NumberType.BOOLEAN;
  }

  public boolean isInteger() {
    return getNumberType() == NumberType.SIGNED_INTEGER
        || getNumberType() == NumberType.UNSIGNED_INTEGER;
  }

  public boolean isFloatingPoint() {
    return getNumberType() == NumberType.FLOATING_POINT;
  }

  public boolean isPositive() {
    switch (getNumberType()) {
      case BOOLEAN:
        return booleanValue;
      case SIGNED_INTEGER:
      case UNSIGNED_INTEGER:
        return integerValue > 0l;
      case FLOATING_POINT:
        return floatingValue > 0.0d;
      default:
        throw new IllegalArgumentException("Unsupported number type: " + getNumberType());
    }
  }

  public boolean isNonZero() {
    switch (getNumberType()) {
      case BOOLEAN:
        return true;
      case SIGNED_INTEGER:
      case UNSIGNED_INTEGER:
        return integerValue != 0l;
      case FLOATING_POINT:
        return floatingValue != 0.0d;
      default:
        throw new IllegalArgumentException("Unsupported number type: " + getNumberType());
    }
  }

  public static LiteralExpression getDefaultValue(NumberType numberType) {
    switch (numberType) {
      case BOOLEAN:
        return new LiteralExpression(false);
      case SIGNED_INTEGER:
      case UNSIGNED_INTEGER:
        return new LiteralExpression(NumericType.INT32, 0);
      case FLOATING_POINT:
        return new LiteralExpression(NumericType.FLOAT32, 0.0d);
      default:
        throw new IllegalArgumentException("Unsupported literal type: " + numberType);
    }
  }

  public static LiteralExpression getDefaultValue(NumericType type) {
    return getDefaultValue(type.getNumberType());
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.LITERAL;
  }

  @Override
  public <R> R expressionAccept(ASTVisitor<R> visitor) {
    return visitor.visitLiteralExpression(this);
  }

  @Override
  public void enterNode(ASTListener listener) {
    super.enterNode(listener);
    listener.enterLiteralExpression(this);
  }

  @Override
  public void exitNode(ASTListener listener) {
    super.exitNode(listener);
    listener.exitLiteralExpression(this);
  }

  @Override
  public LiteralExpression clone() {
    return new LiteralExpression(literalType, booleanValue, integerValue, integerFormat, floatingValue);
  }

  @Override
  public LiteralExpression cloneInto(Root root) {
    return (LiteralExpression) super.cloneInto(root);
  }
}
