/*-
 * #%L
 * Sales Service
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
package com.bbva.labs.hyperscale.contract_testing.sales.rest

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshaller
import org.json4s.{JArray, _}
import org.json4s.jackson.JsonMethods._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, Matchers}

class SalesServiceTest extends FunSpec with Matchers with ScalatestRouteTest with MockFactory {

    describe("When requesting Get Sales") {
        it("should return 200") {
            Get("/api/sales") ~> SalesService.route ~> check {
                status.intValue() shouldEqual 200
            }
        }

        it("should return a json array") {
            Get("/api/sales") ~> SalesService.route ~> check {
                responseAs[JValue] shouldBe a[JArray]
            }
        }
    }

    private implicit val um: Unmarshaller[HttpEntity, JValue] = {
        Unmarshaller.byteStringUnmarshaller.mapWithCharset { (data, charset) =>
            parse(new String(data.toArray, "UTF-8"))
        }
    }
}
