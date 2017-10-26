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
package com.bbva.labs.hyperscale.contract_testing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import org.json4s.JArray
import org.json4s.jackson.JsonMethods._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class ProductsByUsers(usersHost: String, salesHost: String) {
    def execute(): Future[JArray] = {
        val usersFuture: Future[JArray] = get(s"$usersHost/api/users")
        val salesFuture: Future[JArray] = get(s"$salesHost/api/sales")

        for {
            users <- usersFuture
            sales <- salesFuture
        } yield merge(users, sales)
    }

    private def get[T](uri: String): Future[T] = {
        val eventualEventualString: Future[String] = Http().singleRequest(HttpRequest(uri = uri)).flatMap(_.entity.toStrict(5 seconds).map(_.data.decodeString("UTF-8")))
        eventualEventualString.map(parse(_)).map(_.asInstanceOf[T])
    }

    protected def merge(users: JArray, sales: JArray): JArray = mergeByUser(users, sales)

    private implicit val system = ActorSystem()
    private implicit val materializer = ActorMaterializer()
}
