package com.bbva.labs.hyperscale.contract_testing.users.verify_contracts;

/*-
 * #%L
 * Users Contract Testing
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

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.TestTarget;
import com.bbva.labs.hyperscale.contract_testing.users.app.WebServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


@RunWith(PactRunner.class)
@Provider("users")
@PactBroker(host = "${PACT_BROKER_HOST}", port = "${PACT_BROKER_PORT}")
public class UsersContractsVerifierTest {
    public UsersContractsVerifierTest() throws MalformedURLException {
        target = new HttpTarget(new URL(System.getenv().getOrDefault("SERVICE_URL", "http://127.0.0.1:3000")));
    }

    @BeforeClass
    public static void setUpWebServer() throws ExecutionException, InterruptedException {
        webServer.startJava().toCompletableFuture().get();
    }

    @AfterClass
    public static void tearDownWebServer() throws ExecutionException, InterruptedException {
        webServer.stopJava().toCompletableFuture().get();
    }

    @TestTarget
    public final HttpTarget target;
    private static WebServer webServer = new WebServer(3000);
}
