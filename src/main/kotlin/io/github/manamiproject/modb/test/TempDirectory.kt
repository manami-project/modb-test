package io.github.manamiproject.modb.test

import kotlinx.coroutines.runBlocking
import java.io.Closeable
import java.nio.file.Files
import java.nio.file.Path

/**
 * Helper class for [tempDirectory]
 * @since 1.0.0
 * @see tempDirectory
 */
public class TempDirectory(prefix: String = "modb_") : Closeable {

    public val tempDir: Path = Files.createTempDirectory(prefix)

    override fun close() {
        tempDir.toFile().deleteRecursively()
    }
}

/**
 * Creates a temporary directory and ensures that it is removed after test execution even if an exception occurred.
 * The temporary directory is prefixed with _modb__.
 *
 * **Example**
 *
 * ```
 * tempDirectory {
 *      println(Files.exist(tempDir))
 * }
 * ```
 *
 * @since 1.0.0
 */
@Deprecated("Use coroutine instead", ReplaceWith(""))
public fun tempDirectory(action: TempDirectory.() -> Unit): Unit = tempDirectorySuspendable { action.invoke(this) }

/**
 * Creates a temporary directory and ensures that it is removed after test execution even if an exception occurred.
 * The temporary directory is prefixed with _modb__.
 * You can access the created temporary directry within [action] using the variable `tempDir`.
 *
 * **Example**
 *
 * ```
 * tempDirectorySuspendable {
 *      println(Files.exist(tempDir))
 * }
 * ```
 *
 * @since 1.4.0
 * @param action Can take any action including suspend functions.
 */
public fun tempDirectorySuspendable(action: suspend TempDirectory.() -> Unit) {
    val tempDir = TempDirectory()

    tempDir.use {
        runBlocking {
            it.action()
        }
    }
}