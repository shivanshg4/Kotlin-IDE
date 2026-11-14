package com.shivansh.ktlab.interp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

data class ExecutionResult(
    val output: String,
    val resultValue: String?,
    val executionTimeMs: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

class KotlinInterpreter {
    fun execute(code: String): ExecutionResult {
        val startTime = System.currentTimeMillis()
        if (code.isBlank()) {
            return ExecutionResult(
                output = "",
                resultValue = null,
                executionTimeMs = 0,
                isSuccess = true
            )
        }

        return runBlocking(Dispatchers.Default) {
            try {
                withTimeout(5000L) {
                    val lexer = Lexer(code)
                    val tokens = lexer.tokenize()
                    val parser = Parser(tokens)
                    val statements = parser.parseProgram()
                    val globalEnv = Environment()
                    val evaluator = Evaluator()

                    val result = evaluator.evaluate(statements, globalEnv)
                    val executionTimeMs = System.currentTimeMillis() - startTime

                    ExecutionResult(
                        output = evaluator.outputBuffer.toString(),
                        resultValue = result?.toString(),
                        executionTimeMs = executionTimeMs,
                        isSuccess = true
                    )
                }
            } catch (e: TimeoutCancellationException) {
                val executionTimeMs = System.currentTimeMillis() - startTime
                ExecutionResult(
                    output = "",
                    resultValue = null,
                    executionTimeMs = executionTimeMs,
                    isSuccess = false,
                    errorMessage = "Execution timed out (exceeded 5 seconds)"
                )
            } catch (e: Exception) {
                val executionTimeMs = System.currentTimeMillis() - startTime
                ExecutionResult(
                    output = "",
                    resultValue = null,
                    executionTimeMs = executionTimeMs,
                    isSuccess = false,
                    errorMessage = e.message ?: "Runtime error occurred during execution"
                )
            }
        }
    }
}
