package com.shivansh.ktlab.interp

class Lexer(private val source: String) {
    private var start = 0
    private var current = 0
    private var line = 1
    private var column = 1
    private var tokenStartColumn = 1

    private val keywords = mapOf(
        "val" to TokenType.VAL,
        "var" to TokenType.VAR,
        "fun" to TokenType.FUN,
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
        "while" to TokenType.WHILE,
        "for" to TokenType.FOR,
        "in" to TokenType.IN,
        "return" to TokenType.RETURN,
        "true" to TokenType.TRUE,
        "false" to TokenType.FALSE,
        "null" to TokenType.NULL,
        "break" to TokenType.BREAK,
        "continue" to TokenType.CONTINUE,
        "when" to TokenType.WHEN,
        "until" to TokenType.UNTIL,
        "step" to TokenType.STEP,
        "to" to TokenType.TO
    )

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        while (!isAtEnd()) {
            start = current
            tokenStartColumn = column
            val c = advance()
            when (c) {
                ' ', '\r', '\t' -> {
                    // Ignore whitespace
                }
                '\n' -> {
                    tokens.add(Token(TokenType.NEWLINE, "\n", null, line, tokenStartColumn))
                    line++
                    column = 1
                }
                '(' -> addToken(tokens, TokenType.LPAREN)
                ')' -> addToken(tokens, TokenType.RPAREN)
                '{' -> addToken(tokens, TokenType.LBRACE)
                '}' -> addToken(tokens, TokenType.RBRACE)
                '[' -> addToken(tokens, TokenType.LBRACKET)
                ']' -> addToken(tokens, TokenType.RBRACKET)
                ',' -> addToken(tokens, TokenType.COMMA)
                ';' -> addToken(tokens, TokenType.SEMICOLON)
                ':' -> addToken(tokens, TokenType.COLON)
                '%' -> addToken(tokens, TokenType.PERCENT)
                '+' -> {
                    when {
                        match('+') -> addToken(tokens, TokenType.INC)
                        match('=') -> addToken(tokens, TokenType.PLUS_ASSIGN)
                        else -> addToken(tokens, TokenType.PLUS)
                    }
                }
                '-' -> {
                    when {
                        match('-') -> addToken(tokens, TokenType.DEC)
                        match('=') -> addToken(tokens, TokenType.MINUS_ASSIGN)
                        match('>') -> addToken(tokens, TokenType.ARROW)
                        else -> addToken(tokens, TokenType.MINUS)
                    }
                }
                '*' -> {
                    if (match('=')) addToken(tokens, TokenType.STAR_ASSIGN)
                    else addToken(tokens, TokenType.STAR)
                }
                '/' -> {
                    if (match('/')) {
                        // Line comment
                        while (peek() != '\n' && !isAtEnd()) advance()
                    } else if (match('*')) {
                        // Block comment
                        while (!isAtEnd()) {
                            if (peek() == '*' && peekNext() == '/') {
                                advance() // consume '*'
                                advance() // consume '/'
                                break
                            }
                            if (peek() == '\n') {
                                line++
                                column = 1
                            }
                            advance()
                        }
                    } else if (match('=')) {
                        addToken(tokens, TokenType.SLASH_ASSIGN)
                    } else {
                        addToken(tokens, TokenType.SLASH)
                    }
                }
                '!' -> {
                    if (match('=')) addToken(tokens, TokenType.NEQ)
                    else addToken(tokens, TokenType.NOT)
                }
                '=' -> {
                    if (match('=')) addToken(tokens, TokenType.EQ)
                    else addToken(tokens, TokenType.ASSIGN)
                }
                '<' -> {
                    if (match('=')) addToken(tokens, TokenType.LTE)
                    else addToken(tokens, TokenType.LT)
                }
                '>' -> {
                    if (match('=')) addToken(tokens, TokenType.GTE)
                    else addToken(tokens, TokenType.GT)
                }
                '&' -> {
                    if (match('&')) addToken(tokens, TokenType.AND)
                    else throw RuntimeException("Syntax error at line $line, col $tokenStartColumn: Unexpected character '&'")
                }
                '|' -> {
                    if (match('|')) addToken(tokens, TokenType.OR)
                    else throw RuntimeException("Syntax error at line $line, col $tokenStartColumn: Unexpected character '|'")
                }
                '.' -> {
                    if (match('.')) {
                        addToken(tokens, TokenType.RANGE)
                    } else {
                        addToken(tokens, TokenType.DOT)
                    }
                }
                '"' -> {
                    if (peek() == '"' && peekNext() == '"') {
                        advance() // consume second quote
                        advance() // consume third quote
                        rawString(tokens)
                    } else {
                        string(tokens)
                    }
                }
                else -> {
                    if (c.isDigit()) {
                        number(tokens)
                    } else if (c.isLetter() || c == '_') {
                        identifier(tokens)
                    } else {
                        throw RuntimeException("Syntax error at line $line, col $tokenStartColumn: Unexpected character '$c'")
                    }
                }
            }
        }
        tokens.add(Token(TokenType.EOF, "", null, line, column))
        return tokens
    }

    private fun isAtEnd(): Boolean = current >= source.length

    private fun advance(): Char {
        val c = source[current++]
        column++
        return c
    }

    private fun peek(): Char = if (isAtEnd()) '\u0000' else source[current]

    private fun peekNext(): Char = if (current + 1 >= source.length) '\u0000' else source[current + 1]

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false
        current++
        column++
        return true
    }

    private fun addToken(tokens: MutableList<Token>, type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line, tokenStartColumn))
    }

    private fun string(tokens: MutableList<Token>) {
        val sb = StringBuilder()
        var braceDepth = 0
        var inTemplateExpr = false

        while (!isAtEnd()) {
            val c = peek()
            if (c == '"' && !inTemplateExpr) {
                break
            }
            if (c == '\n') {
                line++
                column = 1
            }
            if (c == '\\') {
                advance()
                when (peek()) {
                    'n' -> sb.append('\n')
                    't' -> sb.append('\t')
                    'r' -> sb.append('\r')
                    '"' -> sb.append('"')
                    '\\' -> sb.append('\\')
                    '$' -> sb.append('$')
                    else -> sb.append(peek())
                }
                advance()
            } else if (c == '$' && peekNext() == '{') {
                sb.append(advance()) // '$'
                sb.append(advance()) // '{'
                braceDepth++
                inTemplateExpr = true
            } else if (inTemplateExpr && c == '{') {
                sb.append(advance())
                braceDepth++
            } else if (inTemplateExpr && c == '}') {
                sb.append(advance())
                braceDepth--
                if (braceDepth == 0) {
                    inTemplateExpr = false
                }
            } else {
                sb.append(advance())
            }
        }

        if (isAtEnd()) {
            throw RuntimeException("Syntax error at line $line: Unterminated string literal")
        }

        advance() // consume closing quote
        val strValue = sb.toString()
        addToken(tokens, TokenType.STRING_LITERAL, strValue)
    }

    private fun rawString(tokens: MutableList<Token>) {
        val sb = StringBuilder()
        while (!isAtEnd()) {
            if (peek() == '"' && peekNext() == '"' && current + 2 < source.length && source[current + 2] == '"') {
                advance() // 1
                advance() // 2
                advance() // 3
                break
            }
            if (peek() == '\n') {
                line++
                column = 1
            }
            sb.append(advance())
        }
        addToken(tokens, TokenType.STRING_LITERAL, sb.toString())
    }

    private fun number(tokens: MutableList<Token>) {
        while (peek().isDigit()) advance()

        // Check for fractional part
        if (peek() == '.' && peekNext().isDigit()) {
            advance() // consume '.'
            while (peek().isDigit()) advance()
            val text = source.substring(start, current)
            addToken(tokens, TokenType.DOUBLE_LITERAL, text.toDouble())
            return
        }

        // Check for Long suffix 'L' or 'l'
        if (peek() == 'L' || peek() == 'l') {
            advance() // consume 'L'
            val numText = source.substring(start, current - 1)
            addToken(tokens, TokenType.LONG_LITERAL, numText.toLong())
            return
        }

        val text = source.substring(start, current)
        try {
            val intVal = text.toInt()
            addToken(tokens, TokenType.INT_LITERAL, intVal)
        } catch (_: NumberFormatException) {
            val longVal = text.toLong()
            addToken(tokens, TokenType.LONG_LITERAL, longVal)
        }
    }

    private fun identifier(tokens: MutableList<Token>) {
        while (peek().isLetterOrDigit() || peek() == '_') advance()
        val text = source.substring(start, current)
        val type = keywords[text] ?: TokenType.IDENTIFIER
        when (type) {
            TokenType.TRUE -> addToken(tokens, TokenType.TRUE, true)
            TokenType.FALSE -> addToken(tokens, TokenType.FALSE, false)
            TokenType.NULL -> addToken(tokens, TokenType.NULL, null)
            else -> addToken(tokens, type)
        }
    }
}
