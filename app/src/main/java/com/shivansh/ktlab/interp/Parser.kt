package com.shivansh.ktlab.interp

class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parseProgram(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
        skipNewlinesAndSemicolons()
        while (!isAtEnd()) {
            val stmt = parseStatement()
            if (stmt != null) {
                statements.add(stmt)
            }
            skipNewlinesAndSemicolons()
        }
        return statements
    }

    private fun parseStatement(): Stmt? {
        skipNewlines()
        if (isAtEnd()) return null

        val token = peek()
        return when {
            match(TokenType.VAL) -> parseVarDecl(isMutable = false, token.line)
            match(TokenType.VAR) -> parseVarDecl(isMutable = true, token.line)
            match(TokenType.FUN) -> parseFunDecl(token.line)
            match(TokenType.IF) -> parseIfStatement(token.line)
            match(TokenType.WHEN) -> parseWhenStatement(token.line)
            match(TokenType.WHILE) -> parseWhileStatement(token.line)
            match(TokenType.FOR) -> parseForStatement(token.line)
            match(TokenType.RETURN) -> parseReturnStatement(token.line)
            match(TokenType.BREAK) -> Stmt.Break(token.line)
            match(TokenType.CONTINUE) -> Stmt.Continue(token.line)
            match(TokenType.LBRACE) -> parseBlock(token.line)
            else -> {
                val expr = parseExpression()
                match(TokenType.SEMICOLON)
                Stmt.Expression(expr, expr.line)
            }
        }
    }

    private fun parseVarDecl(isMutable: Boolean, line: Int): Stmt.VarDecl {
        val nameToken = consume(TokenType.IDENTIFIER, "Expected variable name")
        var typeName: String? = null
        if (match(TokenType.COLON)) {
            val typeToken = consume(TokenType.IDENTIFIER, "Expected type name")
            typeName = typeToken.lexeme
        }
        consume(TokenType.ASSIGN, "Expected '=' after variable name/type")
        val initializer = parseExpression()
        return Stmt.VarDecl(nameToken.lexeme, isMutable, typeName, initializer, line)
    }

    private fun parseFunDecl(line: Int): Stmt.FunDecl {
        val nameToken = consume(TokenType.IDENTIFIER, "Expected function name")
        consume(TokenType.LPAREN, "Expected '(' after function name")
        val params = mutableListOf<Param>()
        skipNewlines()
        if (!check(TokenType.RPAREN)) {
            do {
                skipNewlines()
                val pName = consume(TokenType.IDENTIFIER, "Expected parameter name").lexeme
                var pType: String? = null
                if (match(TokenType.COLON)) {
                    pType = consume(TokenType.IDENTIFIER, "Expected parameter type").lexeme
                }
                params.add(Param(pName, pType))
                skipNewlines()
            } while (match(TokenType.COMMA))
        }
        consume(TokenType.RPAREN, "Expected ')' after parameters")

        var returnType: String? = null
        if (match(TokenType.COLON)) {
            returnType = consume(TokenType.IDENTIFIER, "Expected return type").lexeme
        }

        skipNewlines()
        val body = mutableListOf<Stmt>()
        if (match(TokenType.ASSIGN)) {
            val expr = parseExpression()
            body.add(Stmt.Return(expr, expr.line))
        } else {
            consume(TokenType.LBRACE, "Expected '{' or '=' for function body")
            val block = parseBlock(previous().line)
            body.addAll(block.statements)
        }

        return Stmt.FunDecl(nameToken.lexeme, params, returnType, body, line)
    }

    private fun parseIfStatement(line: Int): Stmt {
        consume(TokenType.LPAREN, "Expected '(' after 'if'")
        val condition = parseExpression()
        consume(TokenType.RPAREN, "Expected ')' after if condition")
        skipNewlines()

        val thenBranch = parseBlockOrSingleStmt()
        skipNewlines()

        var elseBranch: List<Stmt>? = null
        if (match(TokenType.ELSE)) {
            skipNewlines()
            elseBranch = parseBlockOrSingleStmt()
        }

        return Stmt.If(condition, thenBranch, elseBranch, line)
    }

    private fun parseWhenStatement(line: Int): Stmt {
        var subject: Expr? = null
        if (match(TokenType.LPAREN)) {
            subject = parseExpression()
            consume(TokenType.RPAREN, "Expected ')' after when subject")
        }
        skipNewlines()
        consume(TokenType.LBRACE, "Expected '{' after when")
        skipNewlinesAndSemicolons()

        val entries = mutableListOf<WhenEntry>()
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            if (match(TokenType.ELSE)) {
                consume(TokenType.ARROW, "Expected '->' after else")
                skipNewlines()
                val branch = parseBlockOrSingleStmt()
                entries.add(WhenEntry(conditions = emptyList(), isElse = true, body = branch))
            } else {
                val conds = mutableListOf<Expr>()
                do {
                    skipNewlines()
                    conds.add(parseExpression())
                    skipNewlines()
                } while (match(TokenType.COMMA))
                consume(TokenType.ARROW, "Expected '->' after when conditions")
                skipNewlines()
                val branch = parseBlockOrSingleStmt()
                entries.add(WhenEntry(conditions = conds, isElse = false, body = branch))
            }
            skipNewlinesAndSemicolons()
        }
        consume(TokenType.RBRACE, "Expected '}' at end of when block")

        return Stmt.WhenStmt(subject, entries, line)
    }

    private fun parseWhileStatement(line: Int): Stmt.While {
        consume(TokenType.LPAREN, "Expected '(' after 'while'")
        val condition = parseExpression()
        consume(TokenType.RPAREN, "Expected ')' after while condition")
        skipNewlines()
        val body = parseBlockOrSingleStmt()
        return Stmt.While(condition, body, line)
    }

    private fun parseForStatement(line: Int): Stmt.For {
        consume(TokenType.LPAREN, "Expected '(' after 'for'")
        val varName = consume(TokenType.IDENTIFIER, "Expected loop variable name").lexeme
        consume(TokenType.IN, "Expected 'in' after loop variable")
        val iterable = parseExpression()
        consume(TokenType.RPAREN, "Expected ')' after for expression")
        skipNewlines()
        val body = parseBlockOrSingleStmt()
        return Stmt.For(varName, iterable, body, line)
    }

    private fun parseReturnStatement(line: Int): Stmt.Return {
        val expr = if (!check(TokenType.NEWLINE) && !check(TokenType.SEMICOLON) && !check(TokenType.RBRACE) && !isAtEnd()) {
            parseExpression()
        } else {
            null
        }
        return Stmt.Return(expr, line)
    }

    private fun parseBlock(line: Int): Stmt.Block {
        val statements = mutableListOf<Stmt>()
        skipNewlinesAndSemicolons()
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            val stmt = parseStatement()
            if (stmt != null) {
                statements.add(stmt)
            }
            skipNewlinesAndSemicolons()
        }
        consume(TokenType.RBRACE, "Expected '}' at end of block")
        return Stmt.Block(statements, line)
    }

    private fun parseBlockOrSingleStmt(): List<Stmt> {
        return if (match(TokenType.LBRACE)) {
            parseBlock(previous().line).statements
        } else {
            val stmt = parseStatement()
            if (stmt != null) listOf(stmt) else emptyList()
        }
    }

    fun parseExpression(): Expr {
        return parseAssignment()
    }

    private fun parseAssignment(): Expr {
        val expr = parseLogicalOr()

        if (match(TokenType.ASSIGN, TokenType.PLUS_ASSIGN, TokenType.MINUS_ASSIGN, TokenType.STAR_ASSIGN, TokenType.SLASH_ASSIGN)) {
            val operatorToken = previous()
            val value = parseAssignment()

            return when (expr) {
                is Expr.Variable -> Expr.Assign(expr.name, operatorToken.type, value, operatorToken.line)
                is Expr.IndexGet -> Expr.IndexSet(expr.target, expr.index, value, operatorToken.line)
                else -> throw RuntimeException("Syntax error at line ${operatorToken.line}: Invalid assignment target")
            }
        }

        return expr
    }

    private fun parseLogicalOr(): Expr {
        var expr = parseLogicalAnd()
        while (match(TokenType.OR)) {
            val operator = previous()
            val right = parseLogicalAnd()
            expr = Expr.Binary(expr, operator.type, right, operator.line)
        }
        return expr
    }

    private fun parseLogicalAnd(): Expr {
        var expr = parseEquality()
        while (match(TokenType.AND)) {
            val operator = previous()
            val right = parseEquality()
            expr = Expr.Binary(expr, operator.type, right, operator.line)
        }
        return expr
    }

    private fun parseEquality(): Expr {
        var expr = parseComparison()
        while (match(TokenType.EQ, TokenType.NEQ)) {
            val operator = previous()
            val right = parseComparison()
            expr = Expr.Binary(expr, operator.type, right, operator.line)
        }
        return expr
    }

    private fun parseComparison(): Expr {
        var expr = parseInfix()
        while (match(TokenType.LT, TokenType.LTE, TokenType.GT, TokenType.GTE)) {
            val operator = previous()
            val right = parseInfix()
            expr = Expr.Binary(expr, operator.type, right, operator.line)
        }
        return expr
    }

    private fun parseInfix(): Expr {
        var expr = parseRange()

        while (true) {
            when {
                match(TokenType.UNTIL) -> {
                    val line = previous().line
                    val right = parseRange()
                    expr = Expr.Range(start = expr, end = right, isUntil = true, line = line)
                }
                match(TokenType.STEP) -> {
                    val line = previous().line
                    val stepVal = parseRange()
                    if (expr is Expr.Range) {
                        expr = expr.copy(step = stepVal)
                    } else {
                        throw RuntimeException("Syntax error at line $line: 'step' must follow a range expression")
                    }
                }
                match(TokenType.TO) -> {
                    val line = previous().line
                    val right = parseRange()
                    expr = Expr.Call(
                        callee = Expr.Variable("Pair", line),
                        args = listOf(expr, right),
                        line = line
                    )
                }
                else -> break
            }
        }

        return expr
    }

    private fun parseRange(): Expr {
        var expr = parseTerm()
        while (match(TokenType.RANGE)) {
            val line = previous().line
            val right = parseTerm()
            expr = Expr.Range(start = expr, end = right, isUntil = false, line = line)
        }
        return expr
    }

    private fun parseTerm(): Expr {
        var expr = parseFactor()
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val operator = previous()
            val right = parseFactor()
            expr = Expr.Binary(expr, operator.type, right, operator.line)
        }
        return expr
    }

    private fun parseFactor(): Expr {
        var expr = parseUnary()
        while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
            val operator = previous()
            val right = parseUnary()
            expr = Expr.Binary(expr, operator.type, right, operator.line)
        }
        return expr
    }

    private fun parseUnary(): Expr {
        if (match(TokenType.NOT, TokenType.MINUS, TokenType.PLUS, TokenType.INC, TokenType.DEC)) {
            val operator = previous()
            val right = parseUnary()
            return Expr.Unary(operator.type, right, isPrefix = true, line = operator.line)
        }
        return parsePostfix()
    }

    private fun parsePostfix(): Expr {
        var expr = parsePrimary()

        while (true) {
            when {
                match(TokenType.LPAREN) -> {
                    val line = previous().line
                    val args = mutableListOf<Expr>()
                    skipNewlines()
                    if (!check(TokenType.RPAREN)) {
                        do {
                            skipNewlines()
                            args.add(parseExpression())
                            skipNewlines()
                        } while (match(TokenType.COMMA))
                    }
                    consume(TokenType.RPAREN, "Expected ')' after call arguments")

                    // Check for trailing lambda: e.g. foo(arg) { ... }
                    var trailingLambda: Expr.LambdaExpr? = null
                    if (match(TokenType.LBRACE)) {
                        trailingLambda = parseLambdaBody(previous().line)
                    }

                    expr = Expr.Call(expr, args, trailingLambda, line)
                }
                match(TokenType.DOT) -> {
                    val memberToken = consume(TokenType.IDENTIFIER, "Expected member name after '.'")
                    var currentMember: Expr = Expr.MemberAccess(expr, memberToken.lexeme, memberToken.line)

                    // Check if member access is followed by arguments or trailing lambda
                    if (match(TokenType.LPAREN)) {
                        val args = mutableListOf<Expr>()
                        skipNewlines()
                        if (!check(TokenType.RPAREN)) {
                            do {
                                skipNewlines()
                                args.add(parseExpression())
                                skipNewlines()
                            } while (match(TokenType.COMMA))
                        }
                        consume(TokenType.RPAREN, "Expected ')' after arguments")

                        var trailingLambda: Expr.LambdaExpr? = null
                        if (match(TokenType.LBRACE)) {
                            trailingLambda = parseLambdaBody(previous().line)
                        }

                        currentMember = Expr.Call(currentMember, args, trailingLambda, memberToken.line)
                    } else if (match(TokenType.LBRACE)) {
                        val trailingLambda = parseLambdaBody(previous().line)
                        currentMember = Expr.Call(currentMember, emptyList(), trailingLambda, memberToken.line)
                    }

                    expr = currentMember
                }
                match(TokenType.LBRACKET) -> {
                    val line = previous().line
                    val index = parseExpression()
                    consume(TokenType.RBRACKET, "Expected ']' after index")
                    expr = Expr.IndexGet(expr, index, line)
                }
                match(TokenType.INC) -> {
                    val line = previous().line
                    expr = Expr.Unary(TokenType.INC, expr, isPrefix = false, line = line)
                }
                match(TokenType.DEC) -> {
                    val line = previous().line
                    expr = Expr.Unary(TokenType.DEC, expr, isPrefix = false, line = line)
                }
                check(TokenType.LBRACE) -> {
                    // Method/Function trailing lambda without parentheses: e.g. list.forEach { println(it) }
                    advance() // consume '{'
                    val lambda = parseLambdaBody(previous().line)
                    expr = Expr.Call(expr, emptyList(), lambda, previous().line)
                }
                else -> break
            }
        }

        return expr
    }

    private fun parsePrimary(): Expr {
        val token = peek()

        if (match(TokenType.TRUE)) return Expr.Literal(true, token.line)
        if (match(TokenType.FALSE)) return Expr.Literal(false, token.line)
        if (match(TokenType.NULL)) return Expr.Literal(null, token.line)

        if (match(TokenType.INT_LITERAL, TokenType.LONG_LITERAL, TokenType.DOUBLE_LITERAL)) {
            return Expr.Literal(previous().literal, token.line)
        }

        if (match(TokenType.STRING_LITERAL)) {
            val str = previous().literal as String
            return parseStringLiteralOrTemplate(str, token.line)
        }

        if (match(TokenType.IDENTIFIER)) {
            return Expr.Variable(previous().lexeme, token.line)
        }

        if (match(TokenType.IF)) {
            val line = token.line
            consume(TokenType.LPAREN, "Expected '(' after 'if'")
            val condition = parseExpression()
            consume(TokenType.RPAREN, "Expected ')' after if condition")
            skipNewlines()

            val thenBranch = parseBlockOrSingleStmt()
            skipNewlines()
            var elseBranch: List<Stmt>? = null
            if (match(TokenType.ELSE)) {
                skipNewlines()
                elseBranch = parseBlockOrSingleStmt()
            }

            var thenExpr: Expr? = null
            var elseExpr: Expr? = null

            if (thenBranch.size == 1 && thenBranch[0] is Stmt.Expression) {
                thenExpr = (thenBranch[0] as Stmt.Expression).expr
            }
            if (elseBranch != null && elseBranch.size == 1 && elseBranch[0] is Stmt.Expression) {
                elseExpr = (elseBranch[0] as Stmt.Expression).expr
            }

            return Expr.IfExpr(condition, thenBranch, elseBranch, thenExpr, elseExpr, line)
        }

        if (match(TokenType.WHEN)) {
            val line = token.line
            var subject: Expr? = null
            if (match(TokenType.LPAREN)) {
                subject = parseExpression()
                consume(TokenType.RPAREN, "Expected ')' after when subject")
            }
            skipNewlines()
            consume(TokenType.LBRACE, "Expected '{' after when")
            skipNewlinesAndSemicolons()

            val entries = mutableListOf<WhenEntry>()
            while (!check(TokenType.RBRACE) && !isAtEnd()) {
                if (match(TokenType.ELSE)) {
                    consume(TokenType.ARROW, "Expected '->' after else")
                    skipNewlines()
                    val branch = parseBlockOrSingleExpr()
                    entries.add(WhenEntry(conditions = emptyList(), isElse = true, body = branch.first, exprValue = branch.second))
                } else {
                    val conds = mutableListOf<Expr>()
                    do {
                        skipNewlines()
                        conds.add(parseExpression())
                        skipNewlines()
                    } while (match(TokenType.COMMA))
                    consume(TokenType.ARROW, "Expected '->' after when conditions")
                    skipNewlines()
                    val branch = parseBlockOrSingleExpr()
                    entries.add(WhenEntry(conditions = conds, isElse = false, body = branch.first, exprValue = branch.second))
                }
                skipNewlinesAndSemicolons()
            }
            consume(TokenType.RBRACE, "Expected '}' at end of when block")

            return Expr.WhenExpr(subject, entries, line)
        }

        if (match(TokenType.LPAREN)) {
            skipNewlines()
            val expr = parseExpression()
            skipNewlines()
            consume(TokenType.RPAREN, "Expected ')' after expression")
            return expr
        }

        if (match(TokenType.LBRACKET)) {
            val line = token.line
            val elements = mutableListOf<Expr>()
            skipNewlines()
            if (!check(TokenType.RBRACKET)) {
                do {
                    skipNewlines()
                    elements.add(parseExpression())
                    skipNewlines()
                } while (match(TokenType.COMMA))
            }
            consume(TokenType.RBRACKET, "Expected ']' after list elements")
            return Expr.ListLiteral(elements, line)
        }

        if (match(TokenType.LBRACE)) {
            val line = previous().line
            return parseLambdaBody(line)
        }

        throw RuntimeException("Syntax error at line ${token.line}, col ${token.column}: Unexpected token '${token.lexeme}'")
    }

    private fun parseLambdaBody(line: Int): Expr.LambdaExpr {
        skipNewlines()
        val params = mutableListOf<String>()

        // Check if explicit parameters are declared: { x, y -> ... } or { it -> ... }
        var isExplicitParams = false
        var peekIndex = current
        while (peekIndex < tokens.size && tokens[peekIndex].type != TokenType.RBRACE && tokens[peekIndex].type != TokenType.EOF) {
            if (tokens[peekIndex].type == TokenType.ARROW) {
                isExplicitParams = true
                break
            }
            if (tokens[peekIndex].type == TokenType.LBRACE) {
                break
            }
            peekIndex++
        }

        if (isExplicitParams) {
            while (!check(TokenType.ARROW) && !isAtEnd()) {
                val p = consume(TokenType.IDENTIFIER, "Expected parameter name in lambda").lexeme
                params.add(p)
                if (!match(TokenType.COMMA)) break
            }
            consume(TokenType.ARROW, "Expected '->' in lambda declaration")
        } else {
            params.add("it")
        }

        val statements = mutableListOf<Stmt>()
        var singleExpr: Expr? = null
        skipNewlinesAndSemicolons()

        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            val stmt = parseStatement()
            if (stmt != null) {
                statements.add(stmt)
            }
            skipNewlinesAndSemicolons()
        }
        consume(TokenType.RBRACE, "Expected '}' at end of lambda")

        if (statements.size == 1 && statements[0] is Stmt.Expression) {
            singleExpr = (statements[0] as Stmt.Expression).expr
        }

        return Expr.LambdaExpr(params, statements, singleExpr, line)
    }

    private fun parseBlockOrSingleExpr(): Pair<List<Stmt>, Expr?> {
        return if (match(TokenType.LBRACE)) {
            val block = parseBlock(previous().line)
            Pair(block.statements, null)
        } else {
            val expr = parseExpression()
            Pair(listOf(Stmt.Expression(expr, expr.line)), expr)
        }
    }

    private fun parseStringLiteralOrTemplate(str: String, line: Int): Expr {
        if (!str.contains('$')) {
            return Expr.Literal(str, line)
        }

        val parts = mutableListOf<Expr>()
        var idx = 0
        val sb = StringBuilder()

        while (idx < str.length) {
            val c = str[idx]
            if (c == '$' && idx + 1 < str.length) {
                if (sb.isNotEmpty()) {
                    parts.add(Expr.Literal(sb.toString(), line))
                    sb.clear()
                }
                idx++ // skip '$'
                if (str[idx] == '{') {
                    idx++ // skip '{'
                    val exprStart = idx
                    var depth = 1
                    while (idx < str.length && depth > 0) {
                        if (str[idx] == '{') depth++
                        else if (str[idx] == '}') depth--
                        if (depth > 0) idx++
                    }
                    val exprStr = str.substring(exprStart, idx)
                    if (idx < str.length && str[idx] == '}') idx++ // skip '}'

                    val subTokens = Lexer(exprStr).tokenize()
                    val subExpr = Parser(subTokens).parseExpression()
                    parts.add(subExpr)
                } else if (str[idx].isLetter() || str[idx] == '_') {
                    val identStart = idx
                    while (idx < str.length && (str[idx].isLetterOrDigit() || str[idx] == '_')) {
                        idx++
                    }
                    val ident = str.substring(identStart, idx)
                    parts.add(Expr.Variable(ident, line))
                } else {
                    sb.append('$')
                }
            } else {
                sb.append(c)
                idx++
            }
        }

        if (sb.isNotEmpty()) {
            parts.add(Expr.Literal(sb.toString(), line))
        }

        return Expr.StringTemplate(parts, line)
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == type
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF

    private fun peek(): Token = tokens[current]

    private fun previous(): Token = tokens[current - 1]

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        val t = peek()
        throw RuntimeException("Syntax error at line ${t.line}, col ${t.column}: $message (got '${t.lexeme}')")
    }

    private fun skipNewlines() {
        while (check(TokenType.NEWLINE)) {
            advance()
        }
    }

    private fun skipNewlinesAndSemicolons() {
        while (check(TokenType.NEWLINE) || check(TokenType.SEMICOLON)) {
            advance()
        }
    }
}
