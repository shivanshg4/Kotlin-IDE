package com.shivansh.ktlab.data.repository

object TutorialsRepository {
    fun getTutorials(): List<TutorialLesson> {
        return listOf(
            TutorialLesson(
                id = "lesson_1",
                title = "1. Introduction to Kotlin",
                summary = "Overview of Kotlin language syntax and execution.",
                content = "Kotlin is a concise, safe, and modern programming language. Every program starts execution in the main() function or runs top-level expressions directly. You can use println() to print text to the output window.",
                codeSnippet = """
                    fun main() {
                        println("Hello from Kotlin Tutorial!")
                    }
                """.trimIndent()
            ),
            TutorialLesson(
                id = "lesson_2",
                title = "2. Variables (val vs var)",
                summary = "Understanding immutable (val) and mutable (var) declarations.",
                content = "Use 'val' for read-only (immutable) variables whose values cannot be changed once assigned. Use 'var' for mutable variables that can be reassigned later in your code.",
                codeSnippet = """
                    fun main() {
                        val language = "Kotlin"
                        var version = 1.0
                        println("Language: ${'$'}language, Version: ${'$'}version")
                        version = 2.0
                        println("Updated Version: ${'$'}version")
                    }
                """.trimIndent()
            ),
            TutorialLesson(
                id = "lesson_3",
                title = "3. Data Types",
                summary = "Explore basic types: Int, Double, String, Boolean.",
                content = "Kotlin has rich built-in primitive types. Numbers can be integers (Int) or decimals (Double). Text is held in String, and logic in Boolean (true/false).",
                codeSnippet = """
                    fun main() {
                        val count: Int = 42
                        val price: Double = 19.99
                        val title: String = "KtLab IDE"
                        val isActive: Boolean = true
                        println("Count: ${'$'}count, Price: ${'$'}price, Active: ${'$'}isActive")
                    }
                """.trimIndent()
            ),
            TutorialLesson(
                id = "lesson_4",
                title = "4. Operators",
                summary = "Arithmetic, comparison, and assignment operators.",
                content = "Perform math using +, -, *, /, and %. Compare values with ==, !=, <, >, <=, >=. Assign values with =, +=, -=, *=, /=.",
                codeSnippet = """
                    fun main() {
                        var a = 10
                        var b = 3
                        println("Sum: ${'$'}{a + b}")
                        println("Remainder: ${'$'}{a % b}")
                        println("Is a > b? ${'$'}{a > b}")
                    }
                """.trimIndent()
            ),
            TutorialLesson(
                id = "lesson_5",
                title = "5. Control Flow (if/else)",
                summary = "Conditional branching with if and else statements.",
                content = "Use 'if' to evaluate logical conditions. You can chain 'else if' and 'else' branches to handle multiple cases.",
                codeSnippet = """
                    fun main() {
                        val score = 85
                        if (score >= 90) {
                            println("Grade: A")
                        } else if (score >= 80) {
                            println("Grade: B")
                        } else {
                            println("Grade: C")
                        }
                    }
                """.trimIndent()
            ),
            TutorialLesson(
                id = "lesson_6",
                title = "6. Loops (while & for)",
                summary = "Iterating over code blocks using loops and ranges.",
                content = "While loops repeat code while a condition is true. For loops iterate through ranges like 1..5 or collections.",
                codeSnippet = """
                    fun main() {
                        println("For loop 1..3:")
                        for (i in 1..3) {
                            println("i = ${'$'}i")
                        }
                    }
                """.trimIndent()
            ),
            TutorialLesson(
                id = "lesson_7",
                title = "7. Functions",
                summary = "Organizing reusable blocks of code with 'fun'.",
                content = "Functions group logic into reusable blocks. Functions accept parameters with specified types and can return values.",
                codeSnippet = """
                    fun square(x: Int): Int {
                        return x * x
                    }

                    fun main() {
                        val result = square(6)
                        println("Square of 6 is: ${'$'}result")
                    }
                """.trimIndent()
            ),
            TutorialLesson(
                id = "lesson_8",
                title = "8. Strings & Interpolation",
                summary = "Formatting strings with variables and expressions.",
                content = "String templates allow you to insert variable values using ${'$'}variable or expression results using ${'$'}{expression} directly inside quotes.",
                codeSnippet = """
                    fun main() {
                        val item = "Book"
                        val quantity = 3
                        val priceEach = 12
                        println("Ordered ${'$'}quantity x ${'$'}item(s) for a total of $${'$'}{quantity * priceEach}")
                    }
                """.trimIndent()
            ),
            TutorialLesson(
                id = "lesson_9",
                title = "9. Math Operations",
                summary = "Using max, min, abs, and sqrt built-in functions.",
                content = "KtLab provides math helpers like max(a, b), min(a, b), abs(x), and sqrt(x) directly for quick math computations.",
                codeSnippet = """
                    fun main() {
                        val x = -16
                        val y = 9
                        println("Abs of ${'$'}x: ${'$'}{abs(x)}")
                        println("Sqrt of ${'$'}y: ${'$'}{sqrt(y)}")
                        println("Max of 12 and 20: ${'$'}{max(12, 20)}")
                    }
                """.trimIndent()
            ),
            TutorialLesson(
                id = "lesson_10",
                title = "10. Lists & Collections",
                summary = "Creating and accessing lists with listOf().",
                content = "Lists hold ordered sequences of elements. Use listOf(...) to create lists and access elements with square brackets [index] or .size.",
                codeSnippet = """
                    fun main() {
                        val fruits = listOf("Apple", "Banana", "Cherry")
                        println("Total fruits: ${'$'}{fruits.size}")
                        println("First fruit: ${'$'}{fruits[0]}")
                    }
                """.trimIndent()
            ),
            TutorialLesson(
                id = "lesson_11",
                title = "11. Scope & Blocks",
                summary = "Block scope and variable visibility inside braces {}.",
                content = "Variables declared inside curly braces {} exist only within that block scope and are destroyed when the block ends.",
                codeSnippet = """
                    fun main() {
                        val outer = 100
                        if (outer > 50) {
                            val inner = 200
                            println("Inside block: outer=${'$'}outer, inner=${'$'}inner")
                        }
                    }
                """.trimIndent()
            ),
            TutorialLesson(
                id = "lesson_12",
                title = "12. Returning Values",
                summary = "Using return statements inside custom functions.",
                content = "Use the 'return' keyword inside a function to immediately exit the function and pass back a result value.",
                codeSnippet = """
                    fun isEven(n: Int): Boolean {
                        if (n % 2 == 0) return true
                        return false
                    }

                    fun main() {
                        println("Is 8 even? ${'$'}{isEven(8)}")
                    }
                """.trimIndent()
            ),
            TutorialLesson(
                id = "lesson_13",
                title = "13. Recursive Functions",
                summary = "Functions calling themselves to solve sub-problems.",
                content = "Recursion happens when a function calls itself. Always include a base condition to prevent infinite loops.",
                codeSnippet = """
                    fun sumUpTo(n: Int): Int {
                        if (n <= 1) return n
                        return n + sumUpTo(n - 1)
                    }

                    fun main() {
                        println("Sum 1..5 = ${'$'}{sumUpTo(5)}")
                    }
                """.trimIndent()
            ),
            TutorialLesson(
                id = "lesson_14",
                title = "14. Logic & Conditions",
                summary = "Combining boolean conditions with && and ||.",
                content = "Use && (AND) when all conditions must be true, and || (OR) when at least one condition must be true.",
                codeSnippet = """
                    fun main() {
                        val age = 22
                        val hasLicense = true
                        if (age >= 18 && hasLicense) {
                            println("Eligible to drive!")
                        }
                    }
                """.trimIndent()
            ),
            TutorialLesson(
                id = "lesson_15",
                title = "15. Program Structure",
                summary = "Understanding overall Kotlin program organization.",
                content = "Kotlin programs consist of functions, variable declarations, and control flow blocks working together seamlessly.",
                codeSnippet = """
                    fun welcome(user: String) {
                        println("Welcome ${'$'}user to KtLab IDE!")
                    }

                    fun main() {
                        val name = "Kotlin Learner"
                        welcome(name)
                    }
                """.trimIndent()
            )
        )
    }
}
