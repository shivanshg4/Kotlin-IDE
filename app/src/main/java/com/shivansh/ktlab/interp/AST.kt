package com.shivansh.ktlab.interp

sealed interface ASTNode {
    val line: Int
}

data class Param(val name: String, val type: String? = null)

data class WhenEntry(
    val conditions: List<Expr>, // Empty for else branch
    val isElse: Boolean,
    val body: List<Stmt>,
    val exprValue: Expr? = null
)

sealed interface Stmt : ASTNode {
    data class VarDecl(
        val name: String,
        val isMutable: Boolean,
        val typeName: String?,
        val initializer: Expr,
        override val line: Int
    ) : Stmt

    data class FunDecl(
        val name: String,
        val params: List<Param>,
        val returnType: String?,
        val body: List<Stmt>,
        override val line: Int
    ) : Stmt

    data class If(
        val condition: Expr,
        val thenBranch: List<Stmt>,
        val elseBranch: List<Stmt>?,
        override val line: Int
    ) : Stmt

    data class WhenStmt(
        val subject: Expr?,
        val entries: List<WhenEntry>,
        override val line: Int
    ) : Stmt

    data class While(
        val condition: Expr,
        val body: List<Stmt>,
        override val line: Int
    ) : Stmt

    data class For(
        val variableName: String,
        val iterable: Expr,
        val body: List<Stmt>,
        override val line: Int
    ) : Stmt

    data class Return(
        val value: Expr?,
        override val line: Int
    ) : Stmt

    data class Break(override val line: Int) : Stmt
    data class Continue(override val line: Int) : Stmt

    data class Block(
        val statements: List<Stmt>,
        override val line: Int
    ) : Stmt

    data class Expression(
        val expr: Expr,
        override val line: Int
    ) : Stmt
}

sealed interface Expr : ASTNode {
    data class Literal(val value: Any?, override val line: Int) : Expr
    data class StringTemplate(val parts: List<Expr>, override val line: Int) : Expr
    data class Variable(val name: String, override val line: Int) : Expr
    data class Assign(val name: String, val operator: TokenType, val value: Expr, override val line: Int) : Expr
    data class Binary(val left: Expr, val operator: TokenType, val right: Expr, override val line: Int) : Expr
    data class Unary(val operator: TokenType, val right: Expr, val isPrefix: Boolean = true, override val line: Int) : Expr
    data class Call(val callee: Expr, val args: List<Expr>, val trailingLambda: LambdaExpr? = null, override val line: Int) : Expr
    data class IndexGet(val target: Expr, val index: Expr, override val line: Int) : Expr
    data class IndexSet(val target: Expr, val index: Expr, val value: Expr, override val line: Int) : Expr
    data class MemberAccess(val target: Expr, val memberName: String, override val line: Int) : Expr
    data class Range(val start: Expr, val end: Expr, val isUntil: Boolean = false, val step: Expr? = null, override val line: Int) : Expr
    data class IfExpr(
        val condition: Expr,
        val thenBranch: List<Stmt>,
        val elseBranch: List<Stmt>?,
        val thenExpr: Expr? = null,
        val elseExpr: Expr? = null,
        override val line: Int
    ) : Expr
    data class WhenExpr(
        val subject: Expr?,
        val entries: List<WhenEntry>,
        override val line: Int
    ) : Expr
    data class ListLiteral(val elements: List<Expr>, override val line: Int) : Expr
    data class LambdaExpr(
        val params: List<String>,
        val body: List<Stmt>,
        val singleExpr: Expr? = null,
        override val line: Int
    ) : Expr
}
