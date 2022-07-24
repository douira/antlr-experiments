package io.github.douira.glsl_transformer.ast.query;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.test_util.TestWithASTTransformer;

public class RootTest extends TestWithASTTransformer {
  @Test
  void testRenameAll() {
    Root.indexSeparateTrees(register -> {
      var a = new Identifier("a");
      var b = new Identifier("b");
      register.apply(a);
      register.apply(b);
      var root = a.getRoot();
      var index = root.identifierIndex;
      assertTrue(index.has("a"));
      assertTrue(index.has("b"));
      assertFalse(index.has("c"));
      root.rename("a", "c");
      assertDoesNotThrow(() -> root.rename("foo", "c"));
      assertFalse(index.has("a"));
      assertTrue(index.has("b"));
      assertTrue(index.has("c"));
    });
  }

  @Test
  void testReplaceAll() {
    transformer.setTransformation((tree, root) -> {
      root.process("foo",
          id -> id.getParent().replaceByAndDelete(
              transformer.parseExpression(tree, "bam + spam")));
    });
    assertTransform(
        "int x = bam + spam + bar + fooo; ",
        "int x = foo + bar + fooo;");
  }

  @Test
  void testReplaceAllMultiple() {
    transformer.setTransformation((tree, root) -> {
      root.process(root.identifierIndex.prefixQueryFlat("f"),
          id -> id.getParent().replaceByAndDelete(
              transformer.parseExpression(tree, "bam + spam")));
    });
    assertTransform(
        "int x = bam + spam + bar + bam + spam; ",
        "int x = foo + bar + fan;");
  }

  @Test
  void testNullReplaceStream() {
    transformer.setTransformation((tree, root) -> {
      root.process(Stream.of((Identifier) null), id -> {
      });
    });
    assertDoesNotThrow(() -> transformer.transform(""));
  }

  @Test
  void testReplaceAllReferenceExpressionsPrefix() {
    transformer.setTransformation((tree, root) -> {
      root.replaceReferenceExpressions(transformer,
          root.identifierIndex.prefixQueryFlat("f"),
          "bam + spam");
    });
    assertTransform(
        "int foo = bam + spam + bar + bam + spam; ",
        "int foo = foo + bar + fan;");
  }

  @Test
  void testReplaceAllReferenceExpressionsExact() {
    transformer.setTransformation((tree, root) -> {
      root.replaceReferenceExpressions(transformer,
          "foo",
          "bam + spam");
    });
    assertTransform(
        "int foo = bam + spam + bar + fan; ",
        "int foo = foo + bar + fan;");
  }
}
