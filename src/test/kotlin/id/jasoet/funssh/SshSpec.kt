/*
 * Copyright (C)2018 - Deny Prasetyo <jasoet87@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.jasoet.funssh

import com.jcraft.jsch.ChannelExec
import kotlinx.coroutines.experimental.runBlocking
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldNotBeNullOrBlank
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.util.concurrent.atomic.AtomicInteger

object SshSpec : Spek({

    given("Ssh Extension") {

        on("Executing Remote Command") {
            it("should return command result") {
                runBlocking {
                    val text = createSession(host = SshConfig.host, username = SshConfig.username)
                        .use {
                            val exec = it.createChannel<ChannelExec>()
                            exec.setCommand("ls -alh")
                            exec.setErrStream(System.err)
                            exec.use {
                                exec.inputStream.asString()
                            }
                        }

                    text.shouldNotBeNullOrBlank()
                }
            }

            it("should return command result using helper") {
                runBlocking {
                    val text = "ls -alh".executeRemoteAsString(host = SshConfig.host, username = SshConfig.username)
                    text.shouldNotBeNullOrBlank()
                }
            }

            it("should return line sequence") {
                runBlocking {
                    val pingCount =
                        "ping -c 2 google.com".executeRemote(host = SshConfig.host, username = SshConfig.username) {
                            val counter = AtomicInteger()
                            it.forEachLine {
                                counter.incrementAndGet()
                                it.shouldNotBeNull()
                            }
                            counter.get()
                        }
                    pingCount.shouldBeGreaterThan(5)
                }
            }
        }
    }

})
