/*-
 * #%L
 * Consumer2
 * %%
 * Copyright (C) 2017 Banco Bilbao Vizcaya Argentaria, S.A.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.bbva.labs.hyperscale.contract_testing.consumer1

import com.bbva.labs.hyperscale.contract_testing.mergeByUser
import org.json4s.JsonAST.JArray
import org.json4s.JsonDSL.WithBigDecimal._
import org.json4s._
import org.scalatest.{FlatSpec, Matchers}

import scala.language.postfixOps

class MergeByUserTest extends FlatSpec with Matchers {
    "Given a list of users and a list of sales (one sale by user)" should "be join by the user id" in {
        val user1 = "user-1"
        val email1 = "address-1"
        val user2 = "user-2"
        val email2 = "address-2"
        val type1 = "type1"
        val type2 = "type2"

        val users = JArray(List(
            ("id" -> user1) ~ ("email" -> email1),
            ("id" -> user2) ~ ("email" -> email2)))
        val sales: JArray = JArray(List(
            ("user-id" -> user1) ~ ("purchases" -> List("type" -> type1)),
            ("user-id" -> user2) ~ ("purchases" -> List("type" -> type2))
        ))

        val expectedResult: JArray = JArray(List(
            ("user-id" -> user1) ~
                ("email" -> email1) ~
                ("products" -> List(type1)),
            ("user-id" -> user2) ~
                ("email" -> email2) ~
                ("products" -> List(type2))
        ))
        val merged = mergeByUser(users, sales)
        val Diff(changed, added, deleted) = merged.diff(expectedResult)
        changed shouldBe JNothing
        added shouldBe JNothing
        deleted shouldBe JNothing
    }

    "Given a list of users and a list of sales (two sales by user)" should "be join by the user id" in {
        val user1 = "user-1"
        val email1 = "address-1"
        val user2 = "user-2"
        val email2 = "address-2"
        val type11: String = "type11"
        val type12: String = "type12"
        val type21: String = "type21"
        val type22: String = "type22"

        val users = JArray(List(
            ("id" -> user1) ~ ("email" -> email1),
            ("id" -> user2) ~ ("email" -> email2)))
        val sales: JArray = JArray(List(
            ("user-id" -> user1) ~ ("purchases" -> List("type" -> type11, "type" -> type12)),
            ("user-id" -> user2) ~ ("purchases" -> List("type" -> type21, "type" -> type22))
        ))

        val expectedResult: JArray = JArray(List(
            ("user-id" -> user1) ~
                ("email" -> email1) ~
                ("products" -> List(type11, type12)),
            ("user-id" -> user2) ~
                ("email" -> email2) ~
                ("products" -> List(type21, type22))
        ))
        val merged = mergeByUser(users, sales)
        val Diff(changed, added, deleted) = merged.diff(expectedResult)

        changed shouldBe JNothing
        added shouldBe JNothing
        deleted shouldBe JNothing

    }

    "Given a list of users and a list of sales (one user with one sale in two different moments)" should "be join by the user id" in {
        val user1 = "user-1"
        val email1 = "address-1"
        val type11: String = "type11"
        val type12: String = "type12"

        val users = JArray(List(("id" -> user1) ~ ("email" -> email1)))
        val sales: JArray = JArray(List(
            ("user-id" -> user1) ~ ("purchases" -> List("type" -> type11)),
            ("user-id" -> user1) ~ ("purchases" -> List("type" -> type12))
        ))

        val expectedResult: JArray = JArray(List(
            ("user-id" -> user1) ~
                ("email" -> email1) ~
                ("products" -> List(type11, type12))
        ))
        val merged = mergeByUser(users, sales)
        val Diff(changed, added, deleted) = merged.diff(expectedResult)

        changed shouldBe JNothing
        added shouldBe JNothing
        deleted shouldBe JNothing

    }

    "Given a list of users and a list of sales (with no matching users)" should "be return an empty list" in {
        val user1 = "user-1"
        val email1 = "address-1"
        val user2 = "user-2"
        val email2 = "address-2"
        val user3 = "user-3"
        val type1 = "type1"
        val type2 = "type2"

        val users = JArray(List(
            ("id" -> user1) ~ ("email" -> email1),
            ("id" -> user2) ~ ("email" -> email2)))
        val sales: JArray = JArray(List(
            ("user-id" -> user3) ~ ("purchases" -> List("type" -> type1, "type" -> type2))
        ))

        val merged = mergeByUser(users, sales)
        merged shouldBe JArray(List())
    }
}
