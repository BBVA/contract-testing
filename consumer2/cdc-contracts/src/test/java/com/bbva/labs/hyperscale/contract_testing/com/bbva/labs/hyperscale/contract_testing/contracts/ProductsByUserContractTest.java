package com.bbva.labs.hyperscale.contract_testing.com.bbva.labs.hyperscale.contract_testing.contracts;

/*-
 * #%L
 * Consumer2 contracts
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

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactSpecVersion;
import au.com.dius.pact.model.RequestResponsePact;
import com.bbva.labs.hyperscale.contract_testing.ProductsByUsers;
import org.junit.Rule;
import org.junit.Test;
import scala.concurrent.Await;
import scala.concurrent.duration.FiniteDuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProductsByUserContractTest {
    private static Integer usersPort = 1234;
    private static Integer salesPort = 4321;

    @Rule
    public PactProviderRuleMk2 mockUsersProvider = new PactProviderRuleMk2("users", "localhost", usersPort, PactSpecVersion.V2, this);

    @Rule
    public PactProviderRuleMk2 mockSalesProvider = new PactProviderRuleMk2("sales", "localhost", salesPort, PactSpecVersion.V2, this);

    @Pact(provider = "users", consumer = "consumer2")
    public RequestResponsePact createUsersFragment(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        DslPart body = PactDslJsonArray.arrayEachLike()
            .stringType("id", "user-1")
            .stringType("email", "email-1")
            .closeObject();
        return builder
            .uponReceiving("Requesting users with email")
            .path("/api/users")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(headers)
            .body(body)
            .toPact();
    }

    @Pact(provider = "sales", consumer = "consumer2")
    public RequestResponsePact createSalesFragment(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        DslPart body = PactDslJsonArray.arrayEachLike()
            .stringType("user-id", "user-1")
            .eachLike("purchases")
            .stringType("type", "Cloth");
        return builder
            .uponReceiving("Requesting sales with products")
            .path("/api/sales")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(headers)
            .body(body)
            .toPact();
    }

    @Test
    @PactVerification({"users", "sales"})
    public void mergeSalesByUsers() throws Exception {
        Await.result(new ProductsByUsers("http://localhost:" + usersPort, "http://localhost:" + salesPort).execute(), new FiniteDuration(3, TimeUnit.SECONDS));
    }
}
