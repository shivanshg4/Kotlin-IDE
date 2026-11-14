package com.shivansh.ktlab

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.shivansh.ktlab.data.repository.ExamplesRepository
import com.shivansh.ktlab.data.repository.TutorialsRepository
import com.shivansh.ktlab.interp.KotlinInterpreter
import com.shivansh.ktlab.ui.editor.insertSymbolInTextFieldValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InterpreterTest {

    private val interpreter = KotlinInterpreter()

    @Test
    fun testSymbolInsertionLogic() {
        // 1. Insert pair opening bracket at cursor
        var tfv = TextFieldValue(text = "fun main() ", selection = TextRange(11))
        tfv = insertSymbolInTextFieldValue(tfv, "{")
        assertEquals("fun main() {}", tfv.text)
        assertEquals(TextRange(12), tfv.selection) // Cursor placed between { and }

        // 2. Overtype/skip closing bracket
        tfv = insertSymbolInTextFieldValue(tfv, "}")
        assertEquals("fun main() {}", tfv.text)
        assertEquals(TextRange(13), tfv.selection) // Cursor moved past }

        // 3. Tab insertion (4 spaces)
        tfv = insertSymbolInTextFieldValue(tfv, "Tab")
        assertEquals("fun main() {}    ", tfv.text)
        assertEquals(TextRange(17), tfv.selection)

        // 4. Wrap selected text with pair symbols ()
        tfv = TextFieldValue(text = "val x = 10 + 20", selection = TextRange(8, 15))
        tfv = insertSymbolInTextFieldValue(tfv, "(")
        assertEquals("val x = (10 + 20)", tfv.text)
        assertEquals(TextRange(17), tfv.selection)

        // 5. Standard symbol insertion =
        tfv = TextFieldValue(text = "val a ", selection = TextRange(6))
        tfv = insertSymbolInTextFieldValue(tfv, "=")
        assertEquals("val a =", tfv.text)
        assertEquals(TextRange(7), tfv.selection)
    }

    @Test
    fun testAllExamplesAndTutorialSnippetsExecuteSuccessfully() {
        val examples = ExamplesRepository.getExamples()
        assertEquals(15, examples.size)

        for (example in examples) {
            val result = interpreter.execute(example.code)
            assertTrue("Example failed: '${example.title}'\nError: ${result.errorMessage}", result.isSuccess)
        }

        val tutorials = TutorialsRepository.getTutorials()
        assertEquals(15, tutorials.size)

        for (lesson in tutorials) {
            val result = interpreter.execute(lesson.codeSnippet)
            assertTrue("Tutorial lesson failed: '${lesson.title}'\nError: ${result.errorMessage}", result.isSuccess)
        }
    }

    @Test
    fun testHelloWorld() {
        val code = """
            fun main() {
                println("Hello, World!")
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertTrue(result.isSuccess)
        assertEquals("Hello, World!\n", result.output)
    }

    @Test
    fun testVariablesAndTypes() {
        val code = """
            fun main() {
                val a: Int = 10
                var b: Long = 20L
                val c: Double = 5.5
                val isTrue: Boolean = true
                val name: String = "KtLab"
                b += a
                println("${'$'}name: ${'$'}a ${'$'}b ${'$'}c ${'$'}isTrue")
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertTrue(result.isSuccess)
        assertEquals("KtLab: 10 30 5.5 true\n", result.output)
    }

    @Test
    fun testStringTemplates() {
        val code = """
            fun main() {
                val x = 5
                val y = 10
                println("Sum is ${'$'}{x + y}")
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertTrue(result.isSuccess)
        assertEquals("Sum is 15\n", result.output)
    }

    @Test
    fun testArithmeticAndLogic() {
        val code = """
            fun main() {
                val x = 15 % 4
                val isOk = x == 3 && (10 > 5 || false)
                println("x=${'$'}x, ok=${'$'}isOk")
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertTrue(result.isSuccess)
        assertEquals("x=3, ok=true\n", result.output)
    }

    @Test
    fun testIfElseStatementAndExpression() {
        val code = """
            fun main() {
                val a = 20
                val b = 30
                val maxVal = if (a > b) a else b
                println("Max: ${'$'}maxVal")
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertTrue(result.isSuccess)
        assertEquals("Max: 30\n", result.output)
    }

    @Test
    fun testWhenStatementAndExpression() {
        val code = """
            fun main() {
                val score = 85
                val grade = when (score) {
                    90, 100 -> "A"
                    85 -> "B"
                    else -> "C"
                }
                println("Grade: ${'$'}grade")
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertTrue(result.isSuccess)
        assertEquals("Grade: B\n", result.output)
    }

    @Test
    fun testLoopsAndRanges() {
        val code = """
            fun main() {
                var sum = 0
                for (i in 1..5) {
                    sum += i
                }
                println("Sum: ${'$'}sum")
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertTrue(result.isSuccess)
        assertEquals("Sum: 15\n", result.output)
    }

    @Test
    fun testRecursionFibonacci() {
        val code = """
            fun fibonacci(n: Int): Int {
                if (n <= 1) return n
                return fibonacci(n - 1) + fibonacci(n - 2)
            }
            fun main() {
                println(fibonacci(7))
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertTrue(result.isSuccess)
        assertEquals("13\n", result.output)
    }

    @Test
    fun testRecursionFactorial() {
        val code = """
            fun factorial(n: Int): Int {
                if (n <= 1) return 1
                return n * factorial(n - 1)
            }
            fun main() {
                println(factorial(5))
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertTrue(result.isSuccess)
        assertEquals("120\n", result.output)
    }

    @Test
    fun testListOperations() {
        val code = """
            fun main() {
                val list = mutableListOf("apple", "banana")
                list.add("cherry")
                println(list.size)
                println(list[1])
                println(list.contains("cherry"))
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertTrue(result.isSuccess)
        assertEquals("3\nbanana\ntrue\n", result.output)
    }

    @Test
    fun testMapOperations() {
        val code = """
            fun main() {
                val map = mapOf("a" to 1, "b" to 2)
                println(map.size)
                println(map["a"])
                println(map.contains("b"))
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertTrue(result.isSuccess)
        assertEquals("2\n1\ntrue\n", result.output)
    }

    @Test
    fun testHigherOrderFunctions() {
        val code = """
            fun main() {
                val numbers = listOf(1, 2, 3, 4, 5)
                val doubled = numbers.map { it * 2 }
                val evens = doubled.filter { it % 4 == 0 }
                println(evens.joinToString(", "))
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertTrue(result.isSuccess)
        assertEquals("4, 8\n", result.output)
    }

    @Test
    fun testReadLineReturnsEmptyString() {
        val code = """
            fun main() {
                val input = readLine()
                println("Read: '${'$'}input'")
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertTrue(result.isSuccess)
        assertEquals("Read: ''\n", result.output)
    }

    @Test
    fun testSyntaxErrorWithLineNumber() {
        val code = """
            fun main() {
                val x = 10 +
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertFalse(result.isSuccess)
        assertTrue(result.errorMessage?.contains("line 2") == true || result.errorMessage?.contains("line 3") == true)
    }

    @Test
    fun testRuntimeErrorWithLineNumber() {
        val code = """
            fun main() {
                val a = 10
                val b = 0
                val c = a / b
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertFalse(result.isSuccess)
        assertTrue(result.errorMessage?.contains("line 4") == true)
        assertTrue(result.errorMessage?.contains("Division by zero") == true)
    }

    @Test
    fun testStepLimitSafeguard() {
        val code = """
            fun main() {
                while (true) {
                    // Infinite loop
                }
            }
        """.trimIndent()
        val result = interpreter.execute(code)
        assertFalse(result.isSuccess)
        assertTrue(result.errorMessage?.contains("Step limit exceeded") == true)
    }
}
