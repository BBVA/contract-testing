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

import org.json4s.JsonAST.JArray
import org.json4s.JsonDSL.WithBigDecimal._
import org.json4s._

object mergeByUser extends ((JArray, JArray) => JArray) {
    override def apply(users: JArray, sales: JArray): JArray = {
        val userSales: Map[String, Int] = sales.children.foldLeft(Map[String, Int]())((map, sale) => {
            val JString(userId) = sale \ "user-id"
            val purchases: JArray = (sale \ "purchases").asInstanceOf[JArray]
            map + ((userId, map.getOrElse(userId, 0) + sumSales(purchases)))
        })

        JArray(users.children.map(user => {
            val JString(userId) = user \ "id"
            val JString(address) = user \ "address"

            val total = userSales.get(userId)
                .map(total => JString(total + " EUR"))
                .getOrElse(JNothing)

            val userTotal = ("user-id" -> userId) ~
                ("address" -> address) ~
                ("total" -> total)
            userTotal
        })
            .filter(_ \ "total" != JNothing))
    }

    private def sumSales(sales: JArray): Int = {
        sales.children.map(user => {
            val JString(price) = user \ "price"
            parseEuro(price)
        }).sum
    }

    private def parseEuro(money: String): Int = {
        val pattern = "(\\d+) EUR".r
        val pattern(amount) = money
        amount.toInt
    }
}
