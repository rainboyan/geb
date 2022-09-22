/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.gradle.lambdatest

import geb.gradle.ToStringProviderValue
import geb.gradle.cloud.CloudBrowsersExtension
import groovy.transform.InheritConstructors
import org.gradle.api.Action
import org.gradle.api.tasks.Nested

@InheritConstructors(constructorAnnotations = true)
abstract class LambdaTestExtension extends CloudBrowsersExtension {

    final String providerName = "lambdatest"

    @Nested
    abstract LambdaTestTunnelOps getTunnelOps()

    @Nested
    abstract LambdaTestAccount getAccount()

    void tunnelOps(Action<? super LambdaTestTunnelOps> action) {
        action.execute(tunnelOps)
    }

    void account(Action<? super LambdaTestAccount> action) {
        action.execute(account)
    }

    protected void addExtensions() {
        super.addExtensions()

        task { test ->
            test.environment(
                (LambdaTestAccount.USER_ENV_VAR): new ToStringProviderValue(account.username),
                (LambdaTestAccount.ACCESS_KEY_ENV_VAR): new ToStringProviderValue(account.accessKey),
                (LambdaTestTunnelOps.TUNNEL_NAME_ENV_VAR): new ToStringProviderValue(tunnelOps.tunnelName)
            )
        }

        tunnelOps.username.convention(account.username)
        tunnelOps.accessKey.convention(account.accessKey)
    }
}
