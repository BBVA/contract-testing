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

import org.json4s.JsonAST.JArray
import org.json4s.JsonDSL.WithBigDecimal._
import org.json4s._

object mergeByUser extends ((JArray, JArray) => JArray) {
    override def apply(users: JArray, sales: JArray): JArray = {
        val userProducts: Map[String, List[JValue]] = sales.children.foldLeft(Map[String, List[JValue]]())((map, sale) => {
            val JString(userId) = sale \ "user-id"
            val purchases: JArray = (sale \ "purchases").asInstanceOf[JArray]
            val products: List[JValue] = purchases.children.map(_ \ "type")
            map + ((userId, map.getOrElse(userId, List[JValue]()) ++ products))
        })

        JArray(users.children.map(user => {
            val JString(userId) = user \ "id"
            val JString(email) = user \ "email"

            val products = userProducts.get(userId)
                .map(productList => JArray(productList))
                .getOrElse(JNothing)

            val userTotal = ("user-id" -> userId) ~
                ("email" -> email) ~
                ("products" -> products)
            userTotal
        })
            .filter(_ \ "products" != JNothing))
    }

    private implicit val format = org.json4s.DefaultFormats
}
