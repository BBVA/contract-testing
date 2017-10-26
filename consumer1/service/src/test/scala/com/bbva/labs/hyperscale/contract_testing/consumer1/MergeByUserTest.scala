/*-
 * #%L
 * Consumer1
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
        val address1 = "address-1"
        val user2 = "user-2"
        val address2 = "address-2"
        val price1 = "10 EUR"
        val price2 = "17 EUR"

        val users = JArray(List(
            ("id" -> user1) ~ ("address" -> address1),
            ("id" -> user2) ~ ("address" -> address2)))
        val sales: JArray = JArray(List(
            ("user-id" -> user1) ~ ("purchases" -> List("price" -> price1)),
            ("user-id" -> user2) ~ ("purchases" -> List("price" -> price2))
        ))

        val expectedResult: JArray = JArray(List(
            ("user-id" -> user1) ~
                ("address" -> address1) ~
                ("total" -> price1),
            ("user-id" -> user2) ~
                ("address" -> address2) ~
                ("total" -> price2)
        ))
        val merged = mergeByUser(users, sales)
        val Diff(changed, added, deleted) = merged.diff(expectedResult)
        changed shouldBe JNothing
        added shouldBe JNothing
        deleted shouldBe JNothing
    }

    "Given a list of users and a list of sales (two sales by user)" should "be join by the user id" in {
        val user1 = "user-1"
        val address1 = "address-1"
        val user2 = "user-2"
        val address2 = "address-2"
        val price11: String = "10 EUR"
        val price12: String = "22 EUR"
        val price21: String = "13 EUR"
        val price22: String = "3 EUR"

        val users = JArray(List(
            ("id" -> user1) ~ ("address" -> address1),
            ("id" -> user2) ~ ("address" -> address2)))
        val sales: JArray = JArray(List(
            ("user-id" -> user1) ~ ("purchases" -> List("price" -> price11, "price" -> price12)),
            ("user-id" -> user2) ~ ("purchases" -> List("price" -> price21, "price" -> price22))
        ))

        val expectedResult: JArray = JArray(List(
            ("user-id" -> user1) ~
                ("address" -> address1) ~
                ("total" -> "32 EUR"),
            ("user-id" -> user2) ~
                ("address" -> address2) ~
                ("total" -> "16 EUR")
        ))
        val merged = mergeByUser(users, sales)
        val Diff(changed, added, deleted) = merged.diff(expectedResult)

        changed shouldBe JNothing
        added shouldBe JNothing
        deleted shouldBe JNothing

    }

    "Given a list of users and a list of sales (one user with one sale in two different moments)" should "be join by the user id" in {
        val user1 = "user-1"
        val address1 = "address-1"
        val price11: String = "10 EUR"
        val price12: String = "22 EUR"

        val users = JArray(List(("id" -> user1) ~ ("address" -> address1)))
        val sales: JArray = JArray(List(
            ("user-id" -> user1) ~ ("purchases" -> List("price" -> price11)),
            ("user-id" -> user1) ~ ("purchases" -> List("price" -> price12))
        ))

        val expectedResult: JArray = JArray(List(
            ("user-id" -> user1) ~
                ("address" -> address1) ~
                ("total" -> "32 EUR")
        ))
        val merged = mergeByUser(users, sales)
        val Diff(changed, added, deleted) = merged.diff(expectedResult)

        changed shouldBe JNothing
        added shouldBe JNothing
        deleted shouldBe JNothing

    }

    "Given a list of users and a list of sales (with no matching users)" should "be return an empty list" in {
        val user1 = "user-1"
        val address1 = "address-1"
        val user2 = "user-2"
        val address2 = "address-2"
        val user3 = "user-3"
        val price1 = "10 EUR"
        val price2 = "7 EUR"

        val users = JArray(List(
            ("id" -> user1) ~ ("address" -> address1),
            ("id" -> user2) ~ ("address" -> address2)))
        val sales: JArray = JArray(List(
            ("user-id" -> user3) ~ ("purchases" -> List("price" -> price1, "price" -> price2))
        ))

        val merged = mergeByUser(users, sales)
        merged shouldBe JArray(List())
    }
}
