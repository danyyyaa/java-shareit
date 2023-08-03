package ru.practicum.shareit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class LoggingAspectTests {

    private final LoggingAspect loggingAspect = new LoggingAspect();

    @Test
    void testLog() throws Throwable {
        String methodName = "methodName";
        Object[] arguments = new Object[]{1, "test"};
        Object returnValue = "Result";

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature methodSignature = mock(MethodSignature.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(arguments);
        when(joinPoint.proceed()).thenReturn(returnValue);

        Object result = loggingAspect.log(joinPoint);

        assertEquals(returnValue, result);

        verify(joinPoint).getSignature();
        verify(methodSignature).getName();
        verify(joinPoint).getArgs();
        verify(joinPoint).proceed();
    }

    @Test
    void testLogWithException() throws Throwable {
        String methodName = "methodName";
        Object[] arguments = new Object[]{1, "test"};
        Exception exception = new RuntimeException("Test exception");

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature methodSignature = mock(MethodSignature.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(arguments);
        when(joinPoint.proceed()).thenThrow(exception);

        try {
            loggingAspect.log(joinPoint);
        } catch (Exception e) {
            assertEquals(exception, e);
        }

        verify(joinPoint).getSignature();
        verify(methodSignature).getName();
        verify(joinPoint).getArgs();
        verify(joinPoint).proceed();
    }
}
