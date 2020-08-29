package com.uhasoft.guardian;

import com.uhasoft.guardian.context.ContextTestUtil;
import com.uhasoft.guardian.context.ContextUtil;
import com.uhasoft.guardian.util.function.Predicate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Carpenter Lee
 */
public class TracerTest extends Tracer {

    @Before
    public void setUp() {
        ContextTestUtil.cleanUpContext();
        ContextTestUtil.resetContextMap();
    }

    @After
    public void tearDown() {
        ContextTestUtil.cleanUpContext();
        ContextTestUtil.resetContextMap();
    }

    @Test
    public void testTraceWhenContextSizeExceedsThreshold() {
        int i = 0;
        for (; i < Constants.MAX_CONTEXT_NAME_SIZE; i++) {
            ContextUtil.enter("test-context-" + i);
            ContextUtil.exit();
        }

        try {
            ContextUtil.enter("test-context-" + i);
            throw new RuntimeException("test");
        } catch (Exception e) {
            trace(e);
        } finally {
            ContextUtil.exit();
        }
    }

    @Test
    public void setExceptionsToTrace() {
        ignoreClasses = null;
        traceClasses = null;
        setExceptionsToTrace(TraceException.class, TraceException2.class);
        Assert.assertTrue(shouldTrace(new TraceException2()));
        Assert.assertTrue(shouldTrace(new TraceExceptionSub()));
        Assert.assertFalse(shouldTrace(new Exception()));
    }

    @Test
    public void setExceptionPredicate() {

        Predicate<Throwable> throwablePredicate = new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) {
                if (throwable instanceof TraceException) {
                    return true;
                } else if (throwable instanceof IgnoreException) {
                    return false;
                }
                return false;
            }
        };
        setExceptionPredicate(throwablePredicate);
        Assert.assertTrue(shouldTrace(new TraceException()));
        Assert.assertFalse(shouldTrace(new IgnoreException()));
    }

    @Test
    public void setExceptionsToIgnore() {
        ignoreClasses = null;
        traceClasses = null;
        setExceptionsToIgnore(IgnoreException.class, IgnoreException2.class);
        Assert.assertFalse(shouldTrace(new IgnoreException()));
        Assert.assertFalse(shouldTrace(new IgnoreExceptionSub()));
        Assert.assertTrue(shouldTrace(new Exception()));
    }

    @Test
    public void testBoth() {
        ignoreClasses = null;
        traceClasses = null;
        setExceptionsToTrace(TraceException.class, TraceException2.class, BothException.class);
        setExceptionsToIgnore(IgnoreException.class, IgnoreException2.class, BothException.class);
        Assert.assertFalse(shouldTrace(new IgnoreException()));
        Assert.assertFalse(shouldTrace(new BothException()));
        Assert.assertTrue(shouldTrace(new TraceException()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull() {
        setExceptionsToTrace(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull1() {
        setExceptionsToTrace(TraceException.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull2() {
        setExceptionsToIgnore(IgnoreException.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull3() {
        setExceptionPredicate(null);
    }

    private class TraceException extends Exception {}

    private class TraceException2 extends Exception {}

    private class TraceExceptionSub extends TraceException {}

    private class IgnoreException extends Exception {}

    private class IgnoreException2 extends Exception {}

    private class IgnoreExceptionSub extends IgnoreException {}

    private class BothException extends Exception {}
}