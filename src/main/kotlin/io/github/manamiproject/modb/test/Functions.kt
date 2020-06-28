package io.github.manamiproject.modb.test

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
 * testResource("file.txt")
 * ```
 * For _src/test/resources/dir/subdir/file.txt_ you can call
 * ```
 * testResource("dir/subdir/file.txt")
 * ```
 *
 * @since 1.0.0
 * @see loadTestResource
 * @return [Path] object of the given file.
 */
fun testResource(path: String): Path {
    val resource = TempDirectory::class.java.classLoader.getResource(path) ?: throw IllegalStateException("Path [$path] not found.")

    return Paths.get(resource.toURI())
}

/**
 * Reads the content of a file from _src/test/resources_ into a [String]
 *
 * **Example**:
 *
 * For _src/test/resources/file.txt_ you can call
 * ```
 * testResource("file.txt")
 * ```
 * For _src/test/resources/dir/subdir/file.txt_ you can call
 * ```
 * testResource("dir/subdir/file.txt")
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

    return Files.readAllLines(file).joinToString("\n")
}

/**
 * Lets a test fail with a message that the invocation shouldn't have happened.
 * @since 1.0.0
 */
fun shouldNotBeInvoked(): Nothing = fail("should not be invoked")