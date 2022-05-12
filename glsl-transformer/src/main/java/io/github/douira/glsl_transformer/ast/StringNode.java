package io.github.douira.glsl_transformer.ast;

/**
 * String node provides a terminal node with arbitrary contents. This is useful
 * for inserting strings into the printed code such as comments.
 * 
 * Only use this terminal node for non-parsed tokens like comments, whitespace
 * or preprocessor directives. For all other syntax features, use the local
 * roots system. Parse tree search doesn't work inside string nodes as they are
 * not parsed.
 * 
 * @see ASTNode
 */
public class StringNode extends UnparsableASTNode {
  private final String content;
  private final boolean doNewlineInsertion;

  /**
   * Creates a new string node with the given string content.
   * 
   * @param content The string to create a token for
   */
  public StringNode(String content) {
    if (content == null) {
      throw new IllegalArgumentException("String node content must not be null!");
    }
    this.content = content;
    this.doNewlineInsertion = true;
  }

  /**
   * Creates a new string node with the given string content and a newline
   * insertion flag.
   * 
   * @param content            The string to create a token for
   * @param doNewlineInsertion Whether to insert a newline before each group of
   *                           unparsable AST nodes in the printer
   */
  public StringNode(String content, boolean doNewlineInsertion) {
    this.content = content;
    this.doNewlineInsertion = doNewlineInsertion;
  }

  @Override
  protected String getPrinted() {
    return getContent();
  }

  @Override
  public boolean doNewlineInsertion() {
    return doNewlineInsertion;
  }

  /**
   * Returns the string node's content.
   * 
   * @return The content
   */
  protected String getContent() {
    return content;
  }
}
