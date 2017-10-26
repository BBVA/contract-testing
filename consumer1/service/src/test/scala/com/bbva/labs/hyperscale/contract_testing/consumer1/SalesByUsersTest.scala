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

import com.bbva.labs.hyperscale.contract_testing.SalesByUsers
import com.pyruby.stubserver.{StubMethod, StubServer}
import org.json4s.{JArray, JObject, JString}
import org.json4s.JsonDSL.WithBigDecimal._
import org.json4s.jackson.JsonMethods._
import org.scalamock.scalatest.MockFactory

import scala.concurrent.duration._
import scala.language.postfixOps
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}

import scala.concurrent.Await

class SalesByUsersTest extends FlatSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach with MockFactory {

    override def beforeAll(): Unit = {
        serverUser.start()
        serverSales.start()
    }

    override def afterAll(): Unit = {
        serverUser.stop()
        serverSales.stop()
    }

    override def afterEach(): Unit = {
        serverUser.clearExpectations()
        serverUser.clearExpectations()
    }

    "When executing the consumer1" should "call to users and sales service" in {
        val users = JArray(List("user1"))
        val sales = JArray(List("sales1"))
        serverUser.expect(StubMethod.get("/api/users")).thenReturn(200, "application/json", compact(render(users)))
        serverSales.expect(StubMethod.get("/api/sales")).thenReturn(200, "application/json", compact(render(sales)))

        val userSales = JArray(List(JString("userSales")))
        val mergeMock = mockFunction[JArray, JArray, JArray]
        mergeMock expects(*, *) returns userSales anyNumberOfTimes()
        Await.result(service(mergeMock).execute(), 3 seconds)

        serverUser.verify()
    }

    it should "joint both result" in {
        val users = JArray(List("user1"))
        val sales = JArray(List("sales1"))
        serverUser.expect(StubMethod.get("/api/users")).thenReturn(200, "application/json", compact(render(users)))
        serverSales.expect(StubMethod.get("/api/sales")).thenReturn(200, "application/json", compact(render(sales)))

        val userSales = JArray(List(JString("userSales")))
        val mergeMock = mockFunction[JArray, JArray, JArray]
        mergeMock expects(users, sales) returns userSales once()
        Await.result(service(mergeMock).execute(), 3 seconds) shouldBe userSales

        serverUser.verify()
    }

    private val encoding = "UTF-8"
    private val serverUserPort = 8081
    private val serverSalesPort = 8082
    private val serverUser: StubServer = new StubServer(serverUserPort)
    private val serverSales: StubServer = new StubServer(serverSalesPort)

    private def service(mergeFunction: ((JArray, JArray) => JArray)) = new SalesByUsers(s"http://localhost:${serverUser.getLocalPort}", s"http://localhost:${serverSales.getLocalPort}") {
        override def merge(users: JArray, sales: JArray): JArray = mergeFunction(users, sales)
    }
}
