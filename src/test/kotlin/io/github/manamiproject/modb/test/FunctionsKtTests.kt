package io.github.manamiproject.modb.test

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.opentest4j.AssertionFailedError
import java.nio.file.Files

internal class FunctionsKtTests {

    @Test
    fun `shouldNotBeInvoked() throws exception with specific message` () {
        // when
        val result = assertThrows<AssertionFailedError> {
            shouldNotBeInvoked()
        }

        // then
        assertThat(result).hasMessage("should not be invoked")
    }

    @Nested
    inner class TestResourceTests {

        @Test
        fun `test resource in root directory`() {
            // given
            val path = "test-file.txt"

            // when
            val result = testResource("test_resource_tests/$path")

            // then
            assertThat(result).exists()
            assertThat(result).isRegularFile()
            assertThat(result.fileName.toString()).endsWith(path)
        }

        @Test
        fun `test resource in subdirectory`() {
            // given
            val filename = "other-test-file.txt"

            // when
            val result = testResource("test_resource_tests/subdirectory/$filename")

            // then
            assertThat(result).exists()
            assertThat(result).isRegularFile()
            assertThat(result.fileName.toString()).endsWith(filename)
        }

        @Test
        fun `grants access to a directory`() {
            // when
            val result = testResource("test_resource_tests")

            // then
            assertThat(result).exists()
            assertThat(result).isDirectory()
            assertThat(Files.list(result)).hasSize(2)
        }

        @Test
        fun `throws an exception if the the given path does not exist`() {
            val path = "non-existent-file.txt"

            // when
            val result = assertThrows<IllegalStateException> {
                testResource(path)
            }

            // then
            assertThat(result).hasMessage("Path [$path] not found.")
        }
    }

    @Nested
    inner class LoadTestResourceTests {

        @Test
        fun `load test resource from root directory`() {
            // when
            val result = loadTestResource("test_resource_tests/test-file.txt")

            // then
            assertThat(result).isEqualTo("File in\n\nroot directory.")
        }

        @Test
        fun `load test resource from subdirectory`() {
            // when
            val result = loadTestResource("test_resource_tests/subdirectory/other-test-file.txt")

            // then
            assertThat(result).isEqualTo("File in\nsubdirectory.")
        }

        @Test
        fun `throws an exception if the the given path is a directory`() {
            val path = "test_resource_tests"

            // when
            val result = assertThrows<IllegalStateException> {
                loadTestResource(path)
            }

            // then
            assertThat(result).hasMessage("[$path] either not exist or is not a file.")
        }

        @Test
        fun `throws an exception if the the given path does not exist`() {
            val path = "non-existent-file.txt"

            // when
            val result = assertThrows<IllegalStateException> {
                loadTestResource(path)
            }

            // then
            assertThat(result).hasMessage("Path [$path] not found.")
        }
    }
}