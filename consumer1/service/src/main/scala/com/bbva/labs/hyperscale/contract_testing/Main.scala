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
package com.bbva.labs.hyperscale.contract_testing

import org.json4s.jackson.JsonMethods._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

object Main {
    def main(args: Array[String]): Unit = {
        val usersHost = sys.env.getOrElse("USERS_HOSTS", "http://users:3000")
        val salesHost = sys.env.getOrElse("SALES_HOSTS", "http://sales:3000")

        val result = Await.result(new SalesByUsers(usersHost, salesHost).execute(), 3 seconds)

        compact(render(result))
    }
}
