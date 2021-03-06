package com.landoop.jdbc4

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import java.sql.SQLException
import java.util.*

class LsqlDriverTest : WordSpec() {
  init {
    "LsqlDriver" should {
      "return required and optional properties for connections" {
        val props = LsqlDriver().getPropertyInfo("any", Properties())
        props.map { it.name }.toSet() shouldBe setOf("user", "password", "weakssl")
      }
      "set user and password to required" {
        val props = LsqlDriver().getPropertyInfo("any", Properties())
        props.first { it.name == "user" }.required shouldBe true
        props.first { it.name == "password" }.required shouldBe true
      }
      "set weakssl as optional" {
        val props = LsqlDriver().getPropertyInfo("any", Properties())
        props.first { it.name == "weakssl" }.required shouldBe false
      }
      "accept valid single host url" {
        LsqlDriver().acceptsURL("jdbc:lsql:kafka:http://localhost:3030") shouldBe true
      }
      "accept valid multiple host url" {
        LsqlDriver().acceptsURL("jdbc:lsql:kafka:http://localhost:3030,http://localhost:3031") shouldBe true
      }
      "parse parameters from url" {
        val (url, props) = LsqlDriver().parseUrl("jdbc:lsql:kafka:http://localhost:3030,http://localhost:3031?user=admin&wibble=wobble")
        url shouldBe "jdbc:lsql:kafka:http://localhost:3030,http://localhost:3031"
        props["user"] shouldBe "admin"
        props["wibble"] shouldBe "wobble"
      }
      "reject invalid url" {
        LsqlDriver().acceptsURL("jdbc:qqqqq") shouldBe false
      }
      "throw return null for connect when the url is invalid" {
        LsqlDriver().connect("jdbc:wibble", Properties()) shouldBe null
      }
      "throw for connection when the url is null" {
        shouldThrow<SQLException> {
          LsqlDriver().connect(null, Properties())
        }
      }
      "require each url to be http or https" {
        shouldThrow<SQLException> {
          LsqlDriver().connect("jdbc:lsql:kafka:httpq://localhost:3030", Properties())
        }
      }
    }
  }
}