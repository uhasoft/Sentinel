/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.uhasoft.guardian.transport.util;

import com.uhasoft.guardian.command.CommandRequest;

/**
 * Util class for HTTP command center.
 *
 * @author Eric Zhao
 */
public final class HttpCommandUtils {

    public static final String REQUEST_TARGET = "command-target";

    public static String getTarget(CommandRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        return request.getMetadata().get(REQUEST_TARGET);
    }

    private HttpCommandUtils() {}
}
