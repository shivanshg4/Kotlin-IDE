package com.shivansh.ktlab.interp

class ReturnException(val value: Any?) : Exception()
class BreakException : Exception()
class ContinueException : Exception()

class Evaluator {
    val outputBuffer = StringBuilder()
    private var stepCount = 0
    private val maxSteps = 100_000

    private fun tickStep(line: Int) {
        stepCount++
        if (stepCount > maxSteps) {
            throw RuntimeException("Runtime error at line $line: Step limit exceeded (maximum $maxSteps steps)")
        }
    }

    fun evaluate(statements: List<Stmt>, globalEnv: Environment): Any? {
        var lastResult: Any? = null
        for (stmt in statements) {
            lastResult = evaluateStmt(stmt, globalEnv)
        }

        // If top-level execution defined a 'main' function and didn't invoke it explicitly, call main()
        val mainFun = globalEnv.getFun("main")
        if (mainFun != null) {
            lastResult = callUserFunction(mainFun, emptyList(), globalEnv)
        }

        return lastResult
    }

    fun evaluateStmt(stmt: Stmt, env: Environment): Any? {
        tickStep(stmt.line)
        when (stmt) {
            is Stmt.VarDecl -> {
                val value = evaluateExpr(stmt.initializer, env)
                env.define(stmt.name, value, stmt.isMutable)
                return value
            }
            is Stmt.FunDecl -> {
                env.defineFun(stmt.name, stmt)
                return null
            }
            is Stmt.If -> {
                val cond = isTruthy(evaluateExpr(stmt.condition, env))
                return if (cond) {
                    evaluateBlock(stmt.thenBranch, env)
                } else if (stmt.elseBranch != null) {
                    evaluateBlock(stmt.elseBranch, env)
                } else {
                    null
                }
            }
            is Stmt.WhenStmt -> {
                val subjectVal = stmt.subject?.let { evaluateExpr(it, env) }
                for (entry in stmt.entries) {
                    if (entry.isElse) {
                        return evaluateBlock(entry.body, env)
                    } else {
                        var matched = false
                        for (condExpr in entry.conditions) {
                            if (stmt.subject != null) {
                                val condVal = evaluateExpr(condExpr, env)
                                if (isEqual(subjectVal, condVal)) {
                                    matched = true
                                    break
                                }
                            } else {
                                if (isTruthy(evaluateExpr(condExpr, env))) {
                                    matched = true
                                    break
                                }
                            }
                        }
                        if (matched) {
                            return evaluateBlock(entry.body, env)
                        }
                    }
                }
                return null
            }
            is Stmt.While -> {
                while (isTruthy(evaluateExpr(stmt.condition, env))) {
                    try {
                        evaluateBlock(stmt.body, env)
                    } catch (e: BreakException) {
                        break
                    } catch (e: ContinueException) {
                        continue
                    }
                }
                return null
            }
            is Stmt.For -> {
                val iterableVal = evaluateExpr(stmt.iterable, env)
                val items = when (iterableVal) {
                    is IntRange -> iterableVal.toList()
                    is LongRange -> iterableVal.toList()
                    is Iterable<*> -> iterableVal.toList()
                    is Array<*> -> iterableVal.toList()
                    is String -> iterableVal.map { it.toString() }
                    else -> throw RuntimeException("Runtime error at line ${stmt.line}: Cannot iterate over ${iterableVal?.javaClass?.simpleName ?: "null"}")
                }
                for (item in items) {
                    val childEnv = Environment(env)
                    childEnv.define(stmt.variableName, item, isMutable = false)
                    try {
                        evaluateBlock(stmt.body, childEnv)
                    } catch (e: BreakException) {
                        break
                    } catch (e: ContinueException) {
                        continue
                    }
                }
                return null
            }
            is Stmt.Return -> {
                val valToReturn = stmt.value?.let { evaluateExpr(it, env) }
                throw ReturnException(valToReturn)
            }
            is Stmt.Break -> throw BreakException()
            is Stmt.Continue -> throw ContinueException()
            is Stmt.Block -> return evaluateBlock(stmt.statements, env)
            is Stmt.Expression -> return evaluateExpr(stmt.expr, env)
        }
    }

    private fun evaluateBlock(statements: List<Stmt>, env: Environment): Any? {
        val childEnv = Environment(env)
        var lastVal: Any? = null
        for (s in statements) {
            lastVal = evaluateStmt(s, childEnv)
        }
        return lastVal
    }

    fun evaluateExpr(expr: Expr, env: Environment): Any? {
        tickStep(expr.line)
        return when (expr) {
            is Expr.Literal -> expr.value
            is Expr.StringTemplate -> {
                val sb = StringBuilder()
                for (part in expr.parts) {
                    val res = evaluateExpr(part, env)
                    sb.append(res?.toString() ?: "null")
                }
                sb.toString()
            }
            is Expr.Variable -> env.get(expr.name, expr.line)
            is Expr.Assign -> {
                val rightVal = evaluateExpr(expr.value, env)
                val currentVal = env.get(expr.name, expr.line)
                val finalVal = when (expr.operator) {
                    TokenType.ASSIGN -> rightVal
                    TokenType.PLUS_ASSIGN -> addValues(currentVal, rightVal, expr.line)
                    TokenType.MINUS_ASSIGN -> subtractValues(currentVal, rightVal, expr.line)
                    TokenType.STAR_ASSIGN -> multiplyValues(currentVal, rightVal, expr.line)
                    TokenType.SLASH_ASSIGN -> divideValues(currentVal, rightVal, expr.line)
                    else -> throw RuntimeException("Runtime error at line ${expr.line}: Unsupported assignment operator ${expr.operator}")
                }
                env.assign(expr.name, finalVal, expr.line)
                finalVal
            }
            is Expr.Binary -> {
                when (expr.operator) {
                    TokenType.PLUS -> addValues(evaluateExpr(expr.left, env), evaluateExpr(expr.right, env), expr.line)
                    TokenType.MINUS -> subtractValues(evaluateExpr(expr.left, env), evaluateExpr(expr.right, env), expr.line)
                    TokenType.STAR -> multiplyValues(evaluateExpr(expr.left, env), evaluateExpr(expr.right, env), expr.line)
                    TokenType.SLASH -> divideValues(evaluateExpr(expr.left, env), evaluateExpr(expr.right, env), expr.line)
                    TokenType.PERCENT -> moduloValues(evaluateExpr(expr.left, env), evaluateExpr(expr.right, env), expr.line)
                    TokenType.EQ -> isEqual(evaluateExpr(expr.left, env), evaluateExpr(expr.right, env))
                    TokenType.NEQ -> !isEqual(evaluateExpr(expr.left, env), evaluateExpr(expr.right, env))
                    TokenType.LT -> compareValues(evaluateExpr(expr.left, env), evaluateExpr(expr.right, env), expr.line) < 0
                    TokenType.LTE -> compareValues(evaluateExpr(expr.left, env), evaluateExpr(expr.right, env), expr.line) <= 0
                    TokenType.GT -> compareValues(evaluateExpr(expr.left, env), evaluateExpr(expr.right, env), expr.line) > 0
                    TokenType.GTE -> compareValues(evaluateExpr(expr.left, env), evaluateExpr(expr.right, env), expr.line) >= 0
                    TokenType.AND -> isTruthy(evaluateExpr(expr.left, env)) && isTruthy(evaluateExpr(expr.right, env))
                    TokenType.OR -> isTruthy(evaluateExpr(expr.left, env)) || isTruthy(evaluateExpr(expr.right, env))
                    else -> throw RuntimeException("Runtime error at line ${expr.line}: Unsupported binary operator ${expr.operator}")
                }
            }
            is Expr.Unary -> {
                val valRight = evaluateExpr(expr.right, env)
                when (expr.operator) {
                    TokenType.MINUS -> negateValue(valRight, expr.line)
                    TokenType.PLUS -> valRight
                    TokenType.NOT -> !isTruthy(valRight)
                    TokenType.INC -> {
                        if (expr.right is Expr.Variable) {
                            val cur = env.get(expr.right.name, expr.line)
                            val next = addValues(cur, 1, expr.line)
                            env.assign(expr.right.name, next, expr.line)
                            if (expr.isPrefix) next else cur
                        } else throw RuntimeException("Runtime error at line ${expr.line}: Operand must be a variable for ++")
                    }
                    TokenType.DEC -> {
                        if (expr.right is Expr.Variable) {
                            val cur = env.get(expr.right.name, expr.line)
                            val next = subtractValues(cur, 1, expr.line)
                            env.assign(expr.right.name, next, expr.line)
                            if (expr.isPrefix) next else cur
                        } else throw RuntimeException("Runtime error at line ${expr.line}: Operand must be a variable for --")
                    }
                    else -> throw RuntimeException("Runtime error at line ${expr.line}: Unsupported unary operator ${expr.operator}")
                }
            }
            is Expr.Range -> {
                val startVal = (evaluateExpr(expr.start, env) as Number).toInt()
                val endVal = (evaluateExpr(expr.end, env) as Number).toInt()
                val stepVal = expr.step?.let { (evaluateExpr(it, env) as Number).toInt() } ?: 1

                val finalEnd = if (expr.isUntil) endVal - 1 else endVal
                if (stepVal != 1) {
                    (startVal..finalEnd step stepVal)
                } else {
                    startVal..finalEnd
                }
            }
            is Expr.IfExpr -> {
                val cond = isTruthy(evaluateExpr(expr.condition, env))
                if (cond) {
                    if (expr.thenExpr != null) {
                        evaluateExpr(expr.thenExpr, env)
                    } else {
                        evaluateBlock(expr.thenBranch, env)
                    }
                } else if (expr.elseBranch != null || expr.elseExpr != null) {
                    if (expr.elseExpr != null) {
                        evaluateExpr(expr.elseExpr, env)
                    } else if (expr.elseBranch != null) {
                        evaluateBlock(expr.elseBranch, env)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            is Expr.WhenExpr -> {
                val subjectVal = expr.subject?.let { evaluateExpr(it, env) }
                for (entry in expr.entries) {
                    if (entry.isElse) {
                        return if (entry.exprValue != null) evaluateExpr(entry.exprValue, env) else evaluateBlock(entry.body, env)
                    } else {
                        var matched = false
                        for (condExpr in entry.conditions) {
                            if (expr.subject != null) {
                                val condVal = evaluateExpr(condExpr, env)
                                if (condVal is IntRange || condVal is Iterable<*>) {
                                    val itemToMatch = subjectVal
                                    if (condVal is IntRange && itemToMatch is Number && condVal.contains(itemToMatch.toInt())) {
                                        matched = true
                                        break
                                    }
                                } else if (isEqual(subjectVal, condVal)) {
                                    matched = true
                                    break
                                }
                            } else {
                                if (isTruthy(evaluateExpr(condExpr, env))) {
                                    matched = true
                                    break
                                }
                            }
                        }
                        if (matched) {
                            return if (entry.exprValue != null) evaluateExpr(entry.exprValue, env) else evaluateBlock(entry.body, env)
                        }
                    }
                }
                null
            }
            is Expr.ListLiteral -> {
                val list = ArrayList<Any?>()
                for (e in expr.elements) {
                    list.add(evaluateExpr(e, env))
                }
                list
            }
            is Expr.LambdaExpr -> expr
            is Expr.IndexGet -> {
                val target = evaluateExpr(expr.target, env)
                val indexVal = evaluateExpr(expr.index, env)
                when (target) {
                    is List<*> -> {
                        val idx = (indexVal as Number).toInt()
                        target[idx]
                    }
                    is Map<*, *> -> target[indexVal]
                    is Array<*> -> {
                        val idx = (indexVal as Number).toInt()
                        target[idx]
                    }
                    is String -> {
                        val idx = (indexVal as Number).toInt()
                        target[idx].toString()
                    }
                    else -> throw RuntimeException("Runtime error at line ${expr.line}: Cannot index target of type ${target?.javaClass?.simpleName}")
                }
            }
            is Expr.IndexSet -> {
                val target = evaluateExpr(expr.target, env)
                val indexVal = evaluateExpr(expr.index, env)
                val value = evaluateExpr(expr.value, env)
                when (target) {
                    is MutableList<*> -> {
                        val idx = (indexVal as Number).toInt()
                        @Suppress("UNCHECKED_CAST")
                        (target as MutableList<Any?>)[idx] = value
                        value
                    }
                    is MutableMap<*, *> -> {
                        @Suppress("UNCHECKED_CAST")
                        (target as MutableMap<Any?, Any?>)[indexVal] = value
                        value
                    }
                    else -> throw RuntimeException("Runtime error at line ${expr.line}: Target is not a mutable list or mutable map")
                }
            }
            is Expr.MemberAccess -> {
                val target = evaluateExpr(expr.target, env)
                when (expr.memberName) {
                    "size", "length" -> {
                        when (target) {
                            is List<*> -> target.size
                            is Map<*, *> -> target.size
                            is Array<*> -> target.size
                            is String -> target.length
                            else -> throw RuntimeException("Runtime error at line ${expr.line}: Property '${expr.memberName}' not found on target")
                        }
                    }
                    else -> throw RuntimeException("Runtime error at line ${expr.line}: Property '${expr.memberName}' not found")
                }
            }
            is Expr.Call -> evaluateCall(expr, env)
        }
    }

    private fun evaluateCall(expr: Expr.Call, env: Environment): Any? {
        val args = expr.args.map { evaluateExpr(it, env) }

        if (expr.callee is Expr.MemberAccess) {
            val target = evaluateExpr(expr.callee.target, env)
            val memberName = expr.callee.memberName
            return callMemberMethod(target, memberName, args, expr.trailingLambda, env, expr.line)
        }

        if (expr.callee is Expr.Variable) {
            val funcName = expr.callee.name

            // Check built-in functions
            when (funcName) {
                "println" -> {
                    if (args.isEmpty()) {
                        outputBuffer.append("\n")
                    } else {
                        outputBuffer.append(args.joinToString(" ")).append("\n")
                    }
                    return null
                }
                "print" -> {
                    outputBuffer.append(args.joinToString(" "))
                    return null
                }
                "readLine" -> {
                    return ""
                }
                "listOf", "arrayOf" -> {
                    return ArrayList(args)
                }
                "mutableListOf" -> {
                    return ArrayList(args)
                }
                "mapOf" -> {
                    val map = LinkedHashMap<Any?, Any?>()
                    for (arg in args) {
                        if (arg is Pair<*, *>) {
                            map[arg.first] = arg.second
                        }
                    }
                    return map
                }
                "Pair" -> {
                    if (args.size == 2) {
                        return Pair(args[0], args[1])
                    }
                }
                "max" -> {
                    if (args.size == 2) {
                        val a = (args[0] as Number).toDouble()
                        val b = (args[1] as Number).toDouble()
                        val maxVal = kotlin.math.max(a, b)
                        return if (args[0] is Int && args[1] is Int) maxVal.toInt() else if (args[0] is Long || args[1] is Long) maxVal.toLong() else maxVal
                    }
                }
                "min" -> {
                    if (args.size == 2) {
                        val a = (args[0] as Number).toDouble()
                        val b = (args[1] as Number).toDouble()
                        val minVal = kotlin.math.min(a, b)
                        return if (args[0] is Int && args[1] is Int) minVal.toInt() else if (args[0] is Long || args[1] is Long) minVal.toLong() else minVal
                    }
                }
                "abs" -> {
                    if (args.size == 1) {
                        val n = args[0] as Number
                        return when (n) {
                            is Int -> kotlin.math.abs(n)
                            is Long -> kotlin.math.abs(n)
                            else -> kotlin.math.abs(n.toDouble())
                        }
                    }
                }
                "sqrt" -> {
                    if (args.size == 1) {
                        val n = (args[0] as Number).toDouble()
                        return kotlin.math.sqrt(n)
                    }
                }
                "toInt" -> if (args.isNotEmpty()) return (args[0] as Number).toInt()
                "toLong" -> if (args.isNotEmpty()) return (args[0] as Number).toLong()
                "toDouble" -> if (args.isNotEmpty()) return (args[0] as Number).toDouble()
                "toString" -> if (args.isNotEmpty()) return args[0]?.toString() ?: "null"
            }

            // Check custom function
            val userFun = env.getFun(funcName)
            if (userFun != null) {
                return callUserFunction(userFun, args, env)
            }

            throw RuntimeException("Runtime error at line ${expr.line}: Undefined function '$funcName'")
        }

        throw RuntimeException("Runtime error at line ${expr.line}: Invalid function call target")
    }

    private fun callMemberMethod(
        target: Any?,
        method: String,
        args: List<Any?>,
        trailingLambda: Expr.LambdaExpr?,
        env: Environment,
        line: Int
    ): Any? {
        when (target) {
            is MutableList<*> -> {
                @Suppress("UNCHECKED_CAST")
                val list = target as MutableList<Any?>
                when (method) {
                    "add" -> {
                        if (args.size == 1) {
                            list.add(args[0])
                        } else if (args.size == 2) {
                            list.add((args[0] as Number).toInt(), args[1])
                        }
                        return true
                    }
                    "removeAt" -> return list.removeAt((args[0] as Number).toInt())
                    "contains" -> return list.contains(args.getOrNull(0))
                    "get" -> return list[(args[0] as Number).toInt()]
                    "set" -> { list[(args[0] as Number).toInt()] = args.getOrNull(1); return null }
                    "joinToString" -> return list.joinToString(args.getOrNull(0)?.toString() ?: ", ")
                    "forEach" -> {
                        val lambda = trailingLambda ?: (args.firstOrNull() as? Expr.LambdaExpr)
                        if (lambda != null) {
                            for (item in list) {
                                invokeLambda(lambda, listOf(item), env)
                            }
                            return null
                        }
                    }
                    "map" -> {
                        val lambda = trailingLambda ?: (args.firstOrNull() as? Expr.LambdaExpr)
                        if (lambda != null) {
                            val newList = ArrayList<Any?>()
                            for (item in list) {
                                newList.add(invokeLambda(lambda, listOf(item), env))
                            }
                            return newList
                        }
                    }
                    "filter" -> {
                        val lambda = trailingLambda ?: (args.firstOrNull() as? Expr.LambdaExpr)
                        if (lambda != null) {
                            val newList = ArrayList<Any?>()
                            for (item in list) {
                                val res = invokeLambda(lambda, listOf(item), env)
                                if (isTruthy(res)) newList.add(item)
                            }
                            return newList
                        }
                    }
                }
            }
            is List<*> -> {
                when (method) {
                    "contains" -> return target.contains(args.getOrNull(0))
                    "get" -> return target[(args[0] as Number).toInt()]
                    "joinToString" -> return target.joinToString(args.getOrNull(0)?.toString() ?: ", ")
                    "forEach" -> {
                        val lambda = trailingLambda ?: (args.firstOrNull() as? Expr.LambdaExpr)
                        if (lambda != null) {
                            for (item in target) {
                                invokeLambda(lambda, listOf(item), env)
                            }
                            return null
                        }
                    }
                    "map" -> {
                        val lambda = trailingLambda ?: (args.firstOrNull() as? Expr.LambdaExpr)
                        if (lambda != null) {
                            val newList = ArrayList<Any?>()
                            for (item in target) {
                                newList.add(invokeLambda(lambda, listOf(item), env))
                            }
                            return newList
                        }
                    }
                    "filter" -> {
                        val lambda = trailingLambda ?: (args.firstOrNull() as? Expr.LambdaExpr)
                        if (lambda != null) {
                            val newList = ArrayList<Any?>()
                            for (item in target) {
                                val res = invokeLambda(lambda, listOf(item), env)
                                if (isTruthy(res)) newList.add(item)
                            }
                            return newList
                        }
                    }
                }
            }
            is Map<*, *> -> {
                when (method) {
                    "get" -> return target[args.getOrNull(0)]
                    "contains", "containsKey" -> return target.containsKey(args.getOrNull(0))
                    "forEach" -> {
                        val lambda = trailingLambda ?: (args.firstOrNull() as? Expr.LambdaExpr)
                        if (lambda != null) {
                            for ((k, v) in target) {
                                invokeLambda(lambda, listOf(Pair(k, v)), env)
                            }
                            return null
                        }
                    }
                }
            }
            is String -> {
                when (method) {
                    "uppercase", "toUpperCase" -> return target.uppercase()
                    "lowercase", "toLowerCase" -> return target.lowercase()
                    "contains" -> return target.contains(args.getOrNull(0)?.toString() ?: "")
                    "startsWith" -> return target.startsWith(args.getOrNull(0)?.toString() ?: "")
                    "endsWith" -> return target.endsWith(args.getOrNull(0)?.toString() ?: "")
                    "substring" -> {
                        val start = (args[0] as Number).toInt()
                        val end = if (args.size > 1) (args[1] as Number).toInt() else target.length
                        return target.substring(start, end)
                    }
                    "toInt" -> return target.trim().toInt()
                    "toLong" -> return target.trim().toLong()
                    "toDouble" -> return target.trim().toDouble()
                    "split" -> {
                        val delim = args.getOrNull(0)?.toString() ?: " "
                        return ArrayList(target.split(delim))
                    }
                    "replace" -> {
                        val oldVal = args.getOrNull(0)?.toString() ?: ""
                        val newVal = args.getOrNull(1)?.toString() ?: ""
                        return target.replace(oldVal, newVal)
                    }
                }
            }
            is Number -> {
                when (method) {
                    "toInt" -> return target.toInt()
                    "toLong" -> return target.toLong()
                    "toDouble" -> return target.toDouble()
                    "toString" -> return target.toString()
                }
            }
        }
        throw RuntimeException("Runtime error at line $line: Method '$method' not supported on ${target?.javaClass?.simpleName}")
    }

    private fun invokeLambda(lambda: Expr.LambdaExpr, args: List<Any?>, env: Environment): Any? {
        val lambdaEnv = Environment(env)
        for (i in lambda.params.indices) {
            val paramName = lambda.params[i]
            val argVal = args.getOrNull(i)
            lambdaEnv.define(paramName, argVal, isMutable = false)
        }

        if (lambda.singleExpr != null) {
            return evaluateExpr(lambda.singleExpr, lambdaEnv)
        }

        return try {
            evaluateBlock(lambda.body, lambdaEnv)
        } catch (e: ReturnException) {
            e.value
        }
    }

    private fun callUserFunction(funDecl: Stmt.FunDecl, args: List<Any?>, env: Environment): Any? {
        val callEnv = Environment(env)
        for (i in funDecl.params.indices) {
            val param = funDecl.params[i]
            val argVal = args.getOrNull(i)
            callEnv.define(param.name, argVal, isMutable = false)
        }

        try {
            return evaluateBlock(funDecl.body, callEnv)
        } catch (e: ReturnException) {
            return e.value
        }
    }

    private fun isTruthy(value: Any?): Boolean {
        if (value == null) return false
        if (value is Boolean) return value
        if (value is Number) return value.toDouble() != 0.0
        if (value is String) return value.isNotEmpty()
        return true
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        if (a == null || b == null) return false
        if (a is Number && b is Number) {
            return a.toDouble() == b.toDouble()
        }
        return a == b
    }

    private fun compareValues(a: Any?, b: Any?, line: Int): Int {
        if (a is Number && b is Number) {
            return a.toDouble().compareTo(b.toDouble())
        }
        if (a is String && b is String) {
            return a.compareTo(b)
        }
        throw RuntimeException("Runtime error at line $line: Cannot compare $a and $b")
    }

    private fun addValues(a: Any?, b: Any?, line: Int): Any {
        if (a is String || b is String) {
            return "${a ?: "null"}${b ?: "null"}"
        }
        if (a is Number && b is Number) {
            if (a is Double || b is Double) {
                return a.toDouble() + b.toDouble()
            }
            if (a is Long || b is Long) {
                return a.toLong() + b.toLong()
            }
            return a.toInt() + b.toInt()
        }
        throw RuntimeException("Runtime error at line $line: Cannot add $a and $b")
    }

    private fun subtractValues(a: Any?, b: Any?, line: Int): Any {
        if (a is Number && b is Number) {
            if (a is Double || b is Double) {
                return a.toDouble() - b.toDouble()
            }
            if (a is Long || b is Long) {
                return a.toLong() - b.toLong()
            }
            return a.toInt() - b.toInt()
        }
        throw RuntimeException("Runtime error at line $line: Cannot subtract $b from $a")
    }

    private fun multiplyValues(a: Any?, b: Any?, line: Int): Any {
        if (a is Number && b is Number) {
            if (a is Double || b is Double) {
                return a.toDouble() * b.toDouble()
            }
            if (a is Long || b is Long) {
                return a.toLong() * b.toLong()
            }
            return a.toInt() * b.toInt()
        }
        throw RuntimeException("Runtime error at line $line: Cannot multiply $a and $b")
    }

    private fun divideValues(a: Any?, b: Any?, line: Int): Any {
        if (a is Number && b is Number) {
            if (b.toDouble() == 0.0) {
                throw RuntimeException("Runtime error at line $line: Division by zero")
            }
            if (a is Double || b is Double) {
                return a.toDouble() / b.toDouble()
            }
            if (a is Long || b is Long) {
                return a.toLong() / b.toLong()
            }
            return a.toInt() / b.toInt()
        }
        throw RuntimeException("Runtime error at line $line: Cannot divide $a by $b")
    }

    private fun moduloValues(a: Any?, b: Any?, line: Int): Any {
        if (a is Number && b is Number) {
            if (b.toLong() == 0L) {
                throw RuntimeException("Runtime error at line $line: Modulo by zero")
            }
            if (a is Long || b is Long) {
                return a.toLong() % b.toLong()
            }
            return a.toInt() % b.toInt()
        }
        throw RuntimeException("Runtime error at line $line: Cannot calculate modulo of $a % $b")
    }

    private fun negateValue(a: Any?, line: Int): Any {
        if (a is Number) {
            if (a is Double) return -a.toDouble()
            if (a is Long) return -a.toLong()
            return -a.toInt()
        }
        throw RuntimeException("Runtime error at line $line: Cannot negate non-number $a")
    }
}
