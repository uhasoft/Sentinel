/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
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
package com.uhasoft.guardian.adapter.quarkus.jaxrs.deployment;

import com.uhasoft.guardian.Constants;
import com.uhasoft.guardian.adapter.jaxrs.config.SentinelJaxRsConfig;
import com.uhasoft.guardian.adapter.jaxrs.fallback.SentinelJaxRsFallback;
import com.uhasoft.guardian.adapter.jaxrs.request.RequestOriginParser;
import com.uhasoft.guardian.node.ClusterNode;
import com.uhasoft.guardian.node.EntranceNode;
import com.uhasoft.guardian.node.Node;
import com.uhasoft.guardian.slots.block.RuleConstant;
import com.uhasoft.guardian.slots.block.flow.FlowRule;
import com.uhasoft.guardian.slots.block.flow.FlowRuleManager;
import com.uhasoft.guardian.slots.clusterbuilder.ClusterBuilderSlot;
import com.uhasoft.guardian.util.StringUtil;
import io.quarkus.test.QuarkusUnitTest;
import io.restassured.response.Response;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author sea
 */
public class SentinelJaxRsQuarkusAdapterTest {

    private static final String HELLO_STR = "Hello!";

    @RegisterExtension
    static final QuarkusUnitTest TEST = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap
            .create(JavaArchive.class)
                    .addClasses(TestResource.class));

    @AfterEach
    public void cleanUp() {
        FlowRuleManager.loadRules(null);
        ClusterBuilderSlot.resetClusterNodes();
    }

    @Test
    public void testGetHello() {
        String url = "/test/hello";
        String resourceName = "GET:" + url;
        Response response = given().get(url);
        response.then().statusCode(200).body(equalTo(HELLO_STR));

        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        assertNotNull(cn);
        assertEquals(1, cn.passQps(), 0.01);

        String context = "";
        for (Node n : Constants.ROOT.getChildList()) {
            if (n instanceof EntranceNode) {
                String id = ((EntranceNode) n).getId().getName();
                if (url.equals(id)) {
                    context = ((EntranceNode) n).getId().getName();
                }
            }
        }
        assertEquals("", context);
    }

    @Test
    public void testAsyncGetHello() {
        String url = "/test/async-hello";
        String resourceName = "GET:" + url;
        Response response = given().get(url);
        response.then().statusCode(200).body(equalTo(HELLO_STR));

        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        assertNotNull(cn);
        assertEquals(1, cn.passQps(), 0.01);

        String context = "";
        for (Node n : Constants.ROOT.getChildList()) {
            if (n instanceof EntranceNode) {
                String id = ((EntranceNode) n).getId().getName();
                if (url.equals(id)) {
                    context = ((EntranceNode) n).getId().getName();
                }
            }
        }
        assertEquals("", context);
    }

    @Test
    public void testUrlPathParam() {
        String url = "/test/hello/{name}";
        String resourceName = "GET:" + url;

        String url1 = "/test/hello/abc";
        Response response1 = given().get(url1);
        response1.then().statusCode(200).body(equalTo("Hello abc !"));

        String url2 = "/test/hello/def";
        Response response2 = given().get(url2);
        response2.then().statusCode(200).body(equalTo("Hello def !"));

        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        assertNotNull(cn);
        assertEquals(2, cn.passQps(), 0.01);

        assertNull(ClusterBuilderSlot.getClusterNode("GET:" + url1));
        assertNull(ClusterBuilderSlot.getClusterNode("GET:" + url2));
    }

    @Test
    public void testDefaultFallback() {
        String url = "/test/hello";
        String resourceName = "GET:" + url;
        configureRulesFor(resourceName, 0);
        Response response = given().get(url);
        response.then().statusCode(javax.ws.rs.core.Response.Status.TOO_MANY_REQUESTS.getStatusCode())
                .body(equalTo("Blocked by Sentinel (flow limiting)"));

        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        assertNotNull(cn);
        assertEquals(0, cn.passQps(), 0.01);
    }

    @Test
    public void testCustomFallback() {
        String url = "/test/hello";
        String resourceName = "GET:" + url;
        SentinelJaxRsConfig.setJaxRsFallback(new SentinelJaxRsFallback() {
            @Override
            public javax.ws.rs.core.Response fallbackResponse(String route, Throwable cause) {
                return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.OK)
                        .entity("Blocked by Sentinel (flow limiting)")
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            }

            @Override
            public Future<javax.ws.rs.core.Response> fallbackFutureResponse(final String route, final Throwable cause) {
                return new FutureTask<>(new Callable<javax.ws.rs.core.Response>() {
                    @Override
                    public javax.ws.rs.core.Response call() throws Exception {
                        return fallbackResponse(route, cause);
                    }
                });
            }
        });


        configureRulesFor(resourceName, 0);
        Response response = given().get(url);
        response.then().statusCode(javax.ws.rs.core.Response.Status.OK.getStatusCode())
                .body(equalTo("Blocked by Sentinel (flow limiting)"));

        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        assertNotNull(cn);
        assertEquals(0, cn.passQps(), 0.01);
    }

    @Test
    public void testCustomRequestOriginParser() {
        String url = "/test/hello";
        String resourceName = "GET:" + url;

        String limitOrigin = "appB";
        final String headerName = "X-APP";
        configureRulesFor(resourceName, 0, limitOrigin);

        SentinelJaxRsConfig.setRequestOriginParser(new RequestOriginParser() {
            @Override
            public String parseOrigin(ContainerRequestContext request) {
                String origin = request.getHeaderString(headerName);
                return origin != null ? origin : "";
            }
        });

        Response response = given()
                .header(headerName, "appA").get(url);
        response.then().statusCode(200).body(equalTo(HELLO_STR));

        Response blockedResp = given()
                .header(headerName, "appB")
                .get(url);
        blockedResp.then().statusCode(javax.ws.rs.core.Response.Status.TOO_MANY_REQUESTS.getStatusCode())
                .body(equalTo("Blocked by Sentinel (flow limiting)"));

        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        assertNotNull(cn);
        assertEquals(1, cn.passQps(), 0.01);
        assertEquals(1, cn.blockQps(), 0.01);
    }

    @Test
    public void testExceptionMapper() {
        String url = "/test/ex";
        String resourceName = "GET:" + url;
        Response response = given().get(url);
        response.then().statusCode(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).body(equalTo("test exception mapper"));

        ClusterNode cn = ClusterBuilderSlot.getClusterNode(resourceName);
        assertNotNull(cn);
    }

    private void configureRulesFor(String resource, int count) {
        configureRulesFor(resource, count, "default");
    }

    private void configureRulesFor(String resource, int count, String limitApp) {
        FlowRule rule = new FlowRule()
                .setCount(count)
                .setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setResource(resource);
        if (StringUtil.isNotBlank(limitApp)) {
            rule.setLimitApp(limitApp);
        }
        FlowRuleManager.loadRules(Collections.singletonList(rule));
    }
}
