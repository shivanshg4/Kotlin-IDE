package com.shivansh.ktlab.interp

enum class TokenType {
    // Keywords
    VAL, VAR, FUN, IF, ELSE, WHILE, FOR, IN, RETURN, TRUE, FALSE, NULL, BREAK, CONTINUE,
    WHEN, UNTIL, STEP, TO,

    // Identifiers & Literals
    IDENTIFIER, INT_LITERAL, LONG_LITERAL, DOUBLE_LITERAL, STRING_LITERAL,

    // Operators
    PLUS, MINUS, STAR, SLASH, PERCENT,
    ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN, STAR_ASSIGN, SLASH_ASSIGN,
    EQ, NEQ, LT, GT, LTE, GTE,
    AND, OR, NOT,
    RANGE, // ..
    INC, DEC, // ++, --

    // Delimiters
    LPAREN, RPAREN,
    LBRACE, RBRACE,
    LBRACKET, RBRACKET,
    COMMA, DOT, COLON, SEMICOLON, ARROW,

    // Special
    NEWLINE, EOF
}
