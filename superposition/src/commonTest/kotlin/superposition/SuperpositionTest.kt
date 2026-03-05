package superposition

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

typealias ErrorMessage = String

class SuperpositionTest {

    object TestMapper : Superposition.Unwrap<ErrorMessage> {
        override fun map(error: Throwable): ErrorMessage {
            return error.message.orEmpty()
        }
    }

    @Test
    fun `success can be handled`() {
        class TestException(output: Any) : Throwable("Unexpected output: $output")

        val output = superposition(TestMapper) {
            "success"
        }
        val value = output.handle { throw TestException(it) }
        assertEquals("success", value)
    }

    @Test
    fun `failure can be recovered`() {
        class TestException(output: Any) : Throwable("Unexpected output: $output")

        val output = superposition(TestMapper) {
            @Suppress("ConstantConditionIf")
            if (false) "" else throw RuntimeException("failure")
        }
        val value = output.recover {
            if (it != "failure")
                throw TestException(it)
            "success"
        }
        assertEquals("success", value)
    }

    @Test
    fun `success value can be mapped`() {
        class TestException(output: Any) : Throwable("Unexpected output: $output")

        val output = superposition(TestMapper) {
            "success"
        }
        val value = flatSuperposition(TestMapper) {
            output.map { it.uppercase() }
        }.handle { throw TestException(it) }
        assertEquals("SUCCESS", value)
    }

    @Suppress("SimplifyBooleanWithConstants", "KotlinConstantConditions")
    @Test
    fun `success can fail`() {
        class TestException(output: Any) : Throwable(output.toString())

        val output = superposition(TestMapper) {
            "success"
        }
        val value = assertFailsWith<TestException> {
            flatSuperposition(TestMapper) {
                output.map { if (1 == 1) error("fail was caught") else it }
            }.handle { throw TestException(it) }
        }
        assertEquals("fail was caught", value.message)
    }

    @Test
    fun `failure is untouched by mapper`() {
        class TestException(message: String) : RuntimeException(message)

        val output = superposition(TestMapper) {
            @Suppress("ConstantConditionIf")
            if (false) "" else throw RuntimeException("failure")
        }
        val result = assertFailsWith<TestException> {
            flatSuperposition(TestMapper) {
                output.map { it.uppercase() }
            }.handle { throw TestException(it) }
        }
        assertEquals("failure", result.message)
    }


}