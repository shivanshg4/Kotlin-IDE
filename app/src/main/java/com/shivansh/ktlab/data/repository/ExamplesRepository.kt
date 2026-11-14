package com.shivansh.ktlab.data.repository

object ExamplesRepository {
    fun getExamples(): List<ExampleProgram> {
        return listOf(
            ExampleProgram(
                id = "hello_world",
                title = "1. Hello World",
                description = "Classic beginner program to print messages to output.",
                category = "Basics",
                code = """
                    fun main() {
                        println("Hello, World!")
                        println("Welcome to KtLab - Offline Kotlin IDE")
                    }
                """.trimIndent()
            ),
            ExampleProgram(
                id = "variables",
                title = "2. Variables & Types",
                description = "Demonstrates val, var, Int, Double, Long, String, Boolean.",
                category = "Basics",
                code = """
                    fun main() {
                        val name: String = "Kotlin Learner"
                        var score: Int = 100
                        val pi: Double = 3.14159
                        val isOnline: Boolean = true
                        println("Name: ${'$'}name")
                        println("Score: ${'$'}score")
                        println("Pi: ${'$'}pi, Online: ${'$'}isOnline")
                    }
                """.trimIndent()
            ),
            ExampleProgram(
                id = "if_and_when",
                title = "3. If & When Expressions",
                description = "Conditional branching with if-else and when expressions.",
                category = "Control Flow",
                code = """
                    fun main() {
                        val score = 85
                        val grade = when (score) {
                            90, 100 -> "A"
                            85 -> "B"
                            else -> "C"
                        }
                        val status = if (score >= 60) "Passed" else "Failed"
                        println("Grade: ${'$'}grade, Status: ${'$'}status")
                    }
                """.trimIndent()
            ),
            ExampleProgram(
                id = "loops_ranges",
                title = "4. Loops & Ranges",
                description = "Iterating over ranges, until, step, and while loops.",
                category = "Control Flow",
                code = """
                    fun main() {
                        println("Range 1..5:")
                        for (i in 1..5) {
                            print("${'$'}i ")
                        }
                        println()
                        
                        println("Until 1 until 5 step 2:")
                        for (i in 1 until 5 step 2) {
                            print("${'$'}i ")
                        }
                        println()
                    }
                """.trimIndent()
            ),
            ExampleProgram(
                id = "custom_functions",
                title = "5. Custom Functions",
                description = "Declaring functions with parameters and return values.",
                category = "Functions",
                code = """
                    fun greet(name: String) {
                        println("Hello, ${'$'}name! Welcome.")
                    }

                    fun add(a: Int, b: Int): Int {
                        return a + b
                    }

                    fun main() {
                        greet("Alice")
                        val sum = add(20, 30)
                        println("Sum: ${'$'}sum")
                    }
                """.trimIndent()
            ),
            ExampleProgram(
                id = "lists_maps",
                title = "6. Lists & Maps",
                description = "Creating, indexing, and accessing elements in lists and maps.",
                category = "Collections",
                code = """
                    fun main() {
                        val fruits = listOf("Apple", "Banana", "Cherry")
                        println("Fruits size: ${'$'}{fruits.size}")
                        println("First fruit: ${'$'}{fruits[0]}")
                        
                        val ages = mapOf("Alice" to 25, "Bob" to 30)
                        println("Alice's age: ${'$'}{ages["Alice"]}")
                    }
                """.trimIndent()
            ),
            ExampleProgram(
                id = "recursion",
                title = "7. Recursion",
                description = "Functions invoking themselves to solve sub-problems.",
                category = "Algorithms",
                code = """
                    fun sumUpTo(n: Int): Int {
                        if (n <= 1) return n
                        return n + sumUpTo(n - 1)
                    }

                    fun main() {
                        val result = sumUpTo(5)
                        println("Sum 1..5 is: ${'$'}result")
                    }
                """.trimIndent()
            ),
            ExampleProgram(
                id = "fibonacci",
                title = "8. Fibonacci Sequence",
                description = "Generating the Fibonacci sequence using a loop.",
                category = "Algorithms",
                code = """
                    fun main() {
                        val n = 8
                        var a = 0
                        var b = 1
                        println("First ${'$'}n Fibonacci numbers:")
                        for (i in 1..n) {
                            print("${'$'}a ")
                            val next = a + b
                            a = b
                            b = next
                        }
                        println()
                    }
                """.trimIndent()
            ),
            ExampleProgram(
                id = "prime_check",
                title = "9. Prime Number Check",
                description = "Checking if an integer is a prime number.",
                category = "Algorithms",
                code = """
                    fun isPrime(n: Int): Boolean {
                        if (n <= 1) return false
                        for (i in 2..(n - 1)) {
                            if (n % i == 0) return false
                        }
                        return true
                    }

                    fun main() {
                        val num = 17
                        println("Is ${'$'}num prime? ${'$'}{isPrime(num)}")
                    }
                """.trimIndent()
            ),
            ExampleProgram(
                id = "fizzbuzz",
                title = "10. FizzBuzz Algorithm",
                description = "Classic programming problem: multiples of 3, 5, and both.",
                category = "Algorithms",
                code = """
                    fun main() {
                        for (i in 1..15) {
                            if (i % 3 == 0 && i % 5 == 0) {
                                println("FizzBuzz")
                            } else if (i % 3 == 0) {
                                println("Fizz")
                            } else if (i % 5 == 0) {
                                println("Buzz")
                            } else {
                                println(i)
                            }
                        }
                    }
                """.trimIndent()
            ),
            ExampleProgram(
                id = "string_templates",
                title = "11. String Templates",
                description = "Formatting string variables and math expression templates.",
                category = "Strings",
                code = """
                    fun main() {
                        val item = "Book"
                        val price = 12
                        val qty = 3
                        println("Item: ${'$'}item")
                        println("Total: $${'$'}{price * qty}")
                    }
                """.trimIndent()
            ),
            ExampleProgram(
                id = "factorial",
                title = "12. Factorial Calculation",
                description = "Recursive calculation of n! (n factorial).",
                category = "Algorithms",
                code = """
                    fun factorial(n: Int): Int {
                        if (n <= 1) return 1
                        return n * factorial(n - 1)
                    }

                    fun main() {
                        val num = 5
                        println("${'$'}num! = ${'$'}{factorial(num)}")
                    }
                """.trimIndent()
            ),
            ExampleProgram(
                id = "math_calc",
                title = "13. Math Calculations",
                description = "Built-in math utilities: max, min, sqrt, abs.",
                category = "Math",
                code = """
                    fun main() {
                        println("Max(20, 50): ${'$'}{max(20, 50)}")
                        println("Min(20, 50): ${'$'}{min(20, 50)}")
                        println("Sqrt(16): ${'$'}{sqrt(16)}")
                        println("Abs(-15): ${'$'}{abs(-15)}")
                    }
                """.trimIndent()
            ),
            ExampleProgram(
                id = "higher_order_functions",
                title = "14. Higher-Order Functions",
                description = "Transforming and filtering collections with forEach, map, filter.",
                category = "Collections",
                code = """
                    fun main() {
                        val numbers = listOf(1, 2, 3, 4, 5, 6)
                        val doubled = numbers.map { it * 2 }
                        val evens = doubled.filter { it % 4 == 0 }
                        println("Filtered: ${'$'}{evens.joinToString(", ")}")
                    }
                """.trimIndent()
            ),
            ExampleProgram(
                id = "star_pattern",
                title = "15. Star Pattern Generator",
                description = "Nested loops generating a right-angled triangle pattern.",
                category = "Patterns",
                code = """
                    fun main() {
                        val rows = 5
                        for (i in 1..rows) {
                            var line = ""
                            for (j in 1..i) {
                                line = line + "* "
                            }
                            println(line)
                        }
                    }
                """.trimIndent()
            )
        )
    }
}
