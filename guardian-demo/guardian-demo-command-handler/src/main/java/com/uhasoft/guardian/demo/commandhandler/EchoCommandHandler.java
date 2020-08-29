/*
 * Copyright 1999-2019 Alibaba Group Holding Ltd.
 *
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
 */
package com.uhasoft.guardian.demo.commandhandler;

import com.uhasoft.guardian.command.CommandHandler;
import com.uhasoft.guardian.command.CommandRequest;
import com.uhasoft.guardian.command.CommandResponse;
import com.uhasoft.guardian.command.annotation.CommandMapping;

/**
 * This class is a demo shows how to create and register a customized CommandHandler.
 *
 * <ul>
 * <li>1. Create a class which implements the {@link CommandHandler} SPI interface</li>
 * <li>2. Use a {@link CommandMapping} to specify the url and desc of your CommandHandler</li>
 * <li>3. Implement your own {@code handle} method </li>
 * <li>4. Add your CommandHandler in {@code com.uhasoft.guardian.command.CommandHandler} file which is stored in
 * {@code resources/META-INF/services/} directory </li>
 * </ul>
 *
 * @author houyi
 */
@CommandMapping(name = "echo", desc = "echo command for demo")
public class EchoCommandHandler implements CommandHandler<String> {

    @Override
    public CommandResponse<String> handle(CommandRequest request) {
        String name = request.getParam("name");
        if (name == null || name.trim().length() == 0) {
            return CommandResponse.ofSuccess("Tell us what's your name by submit a name parameter");
        }
        return CommandResponse.ofSuccess("Hello: " + name);
    }

}
