/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.googlejavaformat.java.javadoc;

/**
 * Javadoc token. Our idea of what constitutes a token is often larger or smaller than what you'd
 * naturally expect. The decision is usually pragmatic rather than theoretical. Most of the details
 * are in {@link JavadocLexer}.
 */
final class Token {
  /**
   * Javadoc token type.
   *
   * <p>The general idea is that every token that requires special handling (extra line breaks,
   * indentation, forcing or forbidding whitespace) from {@link JavadocWriter} gets its own type.
   * But I haven't been super careful about it, so I'd imagine that we could merge or remove some of
   * these if we wanted. (For example, PARAGRAPH_CLOSE_TAG and LIST_ITEM_CLOSE_TAG could share a
   * common IGNORABLE token type. But their corresponding OPEN tags exist, so I've kept the CLOSE
   * tags.)
   *
   * <p>Note, though, that tokens of the same type may still have been handled differently by {@link
   * JavadocLexer} when it created them. For example, LITERAL is used for both plain text and inline
   * tags, even though the two affect the lexer's state differently.
   */
  enum Type {
    /** ∕✱✱ */
    BEGIN_JAVADOC,
    /** ✱∕ */
    END_JAVADOC,
    /** The {@code @foo} that begins a block Javadoc tag like {@code @throws}. */
    FOOTER_JAVADOC_TAG_START,
    LIST_OPEN_TAG,
    LIST_CLOSE_TAG,
    LIST_ITEM_OPEN_TAG,
    LIST_ITEM_CLOSE_TAG,
    HEADER_OPEN_TAG,
    HEADER_CLOSE_TAG,
    PARAGRAPH_OPEN_TAG,
    PARAGRAPH_CLOSE_TAG,
    // TODO(cpovirk): Support <div> (probably identically to <blockquote>).
    BLOCKQUOTE_OPEN_TAG,
    BLOCKQUOTE_CLOSE_TAG,
    PRE_OPEN_TAG,
    PRE_CLOSE_TAG,
    TABLE_OPEN_TAG,
    TABLE_CLOSE_TAG,
    HTML_COMMENT,
    // TODO(cpovirk): Support <hr> (probably a blank line before and after).
    BR_TAG,
    /**
     * Whitespace that is not in a {@code <pre>} or {@code <table>} section. Whitespace includes
     * leading newlines, asterisks, and tabs and spaces. In the output, it is translated to newlines
     * (with leading spaces and asterisks) or spaces.
     */
    WHITESPACE,
    /**
     * A newline in a {@code <pre>} or {@code <table>} section. We preserve user formatting in these
     * sections, including newlines.
     */
    FORCED_NEWLINE,
    /**
     * Anything else: {@code foo}, {@code <b>}, {@code {@code foo}} etc. {@link JavadocLexer}
     * sometimes creates adjacent literal tokens, which it then merges into a single, larger literal
     * token before returning its output.
     *
     * <p>This also includes whitespace in a {@code <pre>} or {@code <table>} section. We preserve
     * user formatting in these sections, including arbitrary numbers of spaces. By treating such
     * whitespace as a literal, we can merge it with adjacent literals, preventing us from
     * autowrapping inside these sections -- and doing so naively, to boot. The wrapped line would
     * have no indentation after "* " or, possibly worse, it might begin with an arbitrary amount of
     * whitespace that didn't fit on the previous line. Of course, by doing this, we're potentially
     * creating lines of more than 100 characters. But it seems fair to call in the humans to
     * resolve such problems.
     */
    LITERAL,
    ;
  }

  private final Type type;
  private final String value;

  Token(Type type, String value) {
    this.type = type;
    this.value = value;
  }

  Type getType() {
    return type;
  }

  String getValue() {
    return value;
  }

  int length() {
    return value.length();
  }

  @Override
  public String toString() {
    return "\n" + getType() + ": " + getValue();
  }
}