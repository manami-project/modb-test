package io.github.manamiproject.modb.test

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
public fun tempDirectory(action: TempDirectory.() -> Unit) {
    val tempDir = TempDirectory()

    tempDir.use {
        it.action()
    }
}