package com.landoop.jdbc4

import com.landoop.jdbc4.client.RestClient
import com.landoop.jdbc4.client.domain.Credentials
import fi.iki.elonen.NanoHTTPD
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import javax.net.ssl.SSLHandshakeException

class RestClientTest : WordSpec() {

  class LoginServer : NanoHTTPD(61864) {
    override fun serve(session: IHTTPSession): Response {
      return newFixedLengthResponse("""{"success":true, "token": "wibble"}""".trimIndent())
    }
  }

  private val server = LoginServer()

  override fun beforeSpec(description: Description, spec: Spec) {
    server.makeSecure(NanoHTTPD.makeSSLSocketFactory("/keystore.jks", "password".toCharArray()), null)
    server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
  }

  override fun afterSpec(description: Description, spec: Spec) {
    server.stop()
  }

  init {
    "RestClient" should {
      "support self signed certificates if weak ssl is set to true" {
        val client = RestClient(listOf("https://localhost:61864"), Credentials("any", "any"), true)
        client.token shouldBe "wibble"
      }
      "reject self signed certificates if weak ssl is set to false" {
        shouldThrow<SSLHandshakeException> {
          RestClient(listOf("https://localhost:61864"), Credentials("any", "any"), false)
        }
      }
    }
  }
}