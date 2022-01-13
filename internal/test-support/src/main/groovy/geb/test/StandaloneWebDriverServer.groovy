/*
 * Copyright 2021 the original author or authors.
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
package geb.test

import org.openqa.selenium.grid.commands.Standalone
import org.openqa.selenium.grid.config.CompoundConfig
import org.openqa.selenium.grid.config.Config
import org.openqa.selenium.grid.config.MapConfig
import org.openqa.selenium.grid.server.Server
import org.openqa.selenium.net.PortProber
import org.openqa.selenium.remote.Browser

import static groovy.json.JsonOutput.toJson
import static org.openqa.selenium.remote.CapabilityType.BROWSER_NAME

class StandaloneWebDriverServer {

    @Delegate(includes = ["getUrl"], interfaces = false)
    private final Server<? extends Server> seleniumServer = new Standalone().asServer(
        new CompoundConfig(
            new MapConfig([
                server: [
                    host: "localhost",
                    port: PortProber.findFreePort()
                ],
                node: [
                    "detect-drivers": false
                ]
            ]),
            new Config() {
                @Override
                Set<String> getSectionNames() {
                    ["docker"]
                }

                @Override
                Set<String> getOptions(String section) {
                    section == "docker" ? ["configs"] : []
                }

                @Override
                Optional<List<String>> getAll(String section, String option) {
                    def all = section == "docker" && option == "configs" ?
                        [
                            "selenium/standalone-chrome:96.0.4664.110",
                            toJson((BROWSER_NAME): Browser.CHROME.browserName())
                        ] :
                        null
                    Optional.ofNullable(all)
                }
            }
        )
    )

    StandaloneWebDriverServer() {
        seleniumServer.start()
    }

    void close() {
        seleniumServer.stop()
    }
}
