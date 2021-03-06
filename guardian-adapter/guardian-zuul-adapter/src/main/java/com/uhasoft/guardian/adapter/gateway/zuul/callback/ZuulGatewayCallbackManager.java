/*
 * Copyright 1999-2019 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uhasoft.guardian.adapter.gateway.zuul.callback;

import com.uhasoft.guardian.util.AssertUtil;

/**
 * @author Eric Zhao
 * @since 1.6.0
 */
public final class ZuulGatewayCallbackManager {

    private static volatile RequestOriginParser originParser = new DefaultRequestOriginParser();

    public static RequestOriginParser getOriginParser() {
        return originParser;
    }

    public static void setOriginParser(RequestOriginParser originParser) {
        AssertUtil.notNull(originParser, "originParser cannot be null");
        ZuulGatewayCallbackManager.originParser = originParser;
    }

    private ZuulGatewayCallbackManager() {}
}
