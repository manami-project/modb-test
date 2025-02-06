[![Tests](https://github.com/manami-project/modb-test/actions/workflows/tests.yml/badge.svg)](https://github.com/manami-project/modb-test/actions/workflows/tests.yml) [![codecov](https://codecov.io/gh/manami-project/modb-test/graph/badge.svg?token=B3WWTBLU6O)](https://codecov.io/gh/manami-project/modb-test) ![jdk21](https://img.shields.io/badge/jdk-21-informational)
# modb-test

> [!WARNING]  
> The content of this repository has been moved to [https://github.com/manami-project/modb-app](https://github.com/manami-project/modb-app)

_[modb](https://github.com/manami-project?tab=repositories&q=modb&type=source)_ stands for _**M**anami **O**ffline **D**ata**B**ase_. Repositories prefixed with this acronym are used to create the [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database).

## What does this lib do?
This lib contains all essential dependencies as well as some convenience functions and classes for creating tests in _modb_ prefixed kotlin projects.

## Features
### essential test dependencies
* junit5
* kotlin-test for junit5
* assertj
* wiremock

### easy access to test resources
```kotlin
// access to a file in src/test/resources
val file: Path = testResource("file.txt") // for src/test/resources/file.txt
val file: Path = testResource("dir/subdir/file.txt") // for src/test/resources/dir/subdir/file.txt

// reading the content of a file in src/test/resources into a String
val fileContent: String = loadTestResource("file.txt") // for src/test/resources/file.txt
val fileContent: String = loadTestResource("dir/subdir/file.txt") // for src/test/resources/dir/subdir/file.txt
```

### temporary directory
Creates a temporary directory and ensures that it will be deleted after test execution even if the test fails due to an exception. 
```kotlin
tempDirectory {
    Files.exist(tempDir)
}
```

### mock server test cases
Create mock server test cases.
```kotlin
internal class SampleTest : MockServerTestCase<WireMockServer> by WireMockServerCreator() {

    @Test
    fun `test case`() {
        serverInstance.stubFor(
            head(urlPathEqualTo("/test")).willReturn(
                    aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withStatus(200)
            )
        )
    }
}
```