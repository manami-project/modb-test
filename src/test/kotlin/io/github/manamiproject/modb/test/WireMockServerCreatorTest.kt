package io.github.manamiproject.modb.test

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.assertj.core.api.Assertions.assertThat
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK
import java.net.URI
import kotlin.test.Test

internal class WireMockServerCreatorTest : MockServerTestCase<WireMockServer> by WireMockServerCreator() {

    @Test
    fun `successfully starts server`() {
        // given
        serverInstance.stubFor(
                head(urlPathEqualTo("/test")).willReturn(
                        aResponse()
                            .withHeader("Content-Type", "text/plain")
                            .withStatus(200)
                )
        )

        val connection = (URI("http://localhost:$port/test").toURL().openConnection() as HttpURLConnection).apply {
            requestMethod = "HEAD"
        }

        // when
        val responseCode = connection.responseCode

        // then
        assertThat(responseCode).isEqualTo(HTTP_OK)
    }
}