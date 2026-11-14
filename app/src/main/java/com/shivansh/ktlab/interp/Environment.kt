package com.shivansh.ktlab.interp

class Environment(val parent: Environment? = null) {
    data class ValueHolder(var value: Any?, val isMutable: Boolean)

    private val values = mutableMapOf<String, ValueHolder>()
    private val functions = mutableMapOf<String, Stmt.FunDecl>()

    fun define(name: String, value: Any?, isMutable: Boolean) {
        values[name] = ValueHolder(value, isMutable)
    }

    fun assign(name: String, value: Any?, line: Int = 1): Boolean {
        if (values.containsKey(name)) {
            val holder = values[name]!!
            if (!holder.isMutable) {
                throw RuntimeException("Runtime error at line $line: Cannot reassign immutable variable '$name' (val)")
            }
            holder.value = value
            return true
        }
        if (parent != null) {
            return parent.assign(name, value, line)
        }
        throw RuntimeException("Runtime error at line $line: Undefined variable '$name'")
    }

    fun get(name: String, line: Int = 1): Any? {
        if (values.containsKey(name)) {
            return values[name]!!.value
        }
        if (parent != null) {
            return parent.get(name, line)
        }
        throw RuntimeException("Runtime error at line $line: Undefined variable '$name'")
    }

    fun contains(name: String): Boolean {
        if (values.containsKey(name)) return true
        return parent?.contains(name) ?: false
    }

    fun defineFun(name: String, funDecl: Stmt.FunDecl) {
        functions[name] = funDecl
    }

    fun getFun(name: String): Stmt.FunDecl? {
        if (functions.containsKey(name)) {
            return functions[name]
        }
        return parent?.getFun(name)
    }
}
