/*-
 * #%L
 * Users Service
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
package com.bbva.labs.hyperscale.contract_testing.users.app

import java.util.concurrent.CompletionStage

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import com.bbva.labs.hyperscale.contract_testing.users.rest.UserService

import scala.concurrent.Future
import scala.compat.java8.FutureConverters.toJava

class WebServer(port: Int) {
    def start(): Future[Unit] = {
        serverBinding = Http().bindAndHandle(UserService.route, "0.0.0.0", port)
        serverBinding.map(_ => println(s"Started web server on port $port"))
    }

    def startJava(): CompletionStage[Unit] = toJava(start())

    def stop(): Future[Unit] = {
        serverBinding.flatMap(_.unbind()).map(_ => println("Closing web server")).map(_ => Unit)
    }

    def stopJava(): CompletionStage[Unit] = toJava(stop())

    private var serverBinding: Future[ServerBinding] = null
    private implicit val system = ActorSystem("users")
    private implicit val materializer = ActorMaterializer()
    private implicit val executionContext = system.dispatcher
}
