package io.github.manamiproject.modb.test

import java.io.BufferedReader
import java.lang.ClassLoader.getSystemResourceAsStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.fail

/**
 * Lets you access a file in _src/test/resources_ as [Path].
 *
 * **Example**:
 *
 * For _src/test/resources/file.txt_ you can call
 * ```
 * val file = testResource("file.txt")
 * ```
 * For _src/test/resources/dir/subdir/file.txt_ you can call
 * ```
 * val file = testResource("dir/subdir/file.txt")
 * ```
 *
 * @since 1.0.0
 * @see loadTestResource
 * @return [Path] object of the given file.
 */
fun testResource(path: String): Path {
    val resource = ClassLoader.getSystemResource(path)?.toURI() ?: throw IllegalStateException("Path [$path] not found.")

    return Paths.get(resource)
}

/**
 * Reads the content of a file from _src/test/resources_ into a [String].
 * Line separators will always be converted to `\n`
 *
 * **Example**:
 *
 * For _src/test/resources/file.txt_ you can call
 * ```
 * val content = loadTestResource("file.txt")
 * ```
 * For _src/test/resources/dir/subdir/file.txt_ you can call
 * ```
 * val content = loadTestResource("dir/subdir/file.txt")
 * ```
 *
 * @since 1.0.0
 * @see testResource
 * @return Content of a file as [String]
 * @throws IllegalStateException If the given path is not a regular file.
 */
fun loadTestResource(path: String): String {
    val file = testResource(path)

    check(Files.exists(file) and Files.isRegularFile(file)) { "[$path] either not exist or is not a file." }

    return getSystemResourceAsStream(path)?.bufferedReader()
            ?.use(BufferedReader::readText)
            ?.replace(System.getProperty("line.separator"), "\n")
            ?: throw IllegalStateException("Unable to load file [$path]")
}

/**
 * Lets a test fail with a message that the invocation shouldn't have happened.
 * @since 1.0.0
 */
fun shouldNotBeInvoked(): Nothing = fail("should not be invoked")