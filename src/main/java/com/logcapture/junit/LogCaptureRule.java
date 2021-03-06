package com.logcapture.junit;

import ch.qos.logback.classic.Logger;
import com.logcapture.LogCapture;
import com.logcapture.assertion.ExpectedLoggingMessage;
import com.logcapture.infrastructure.logback.StubAppender;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.LoggerFactory;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;

public class LogCaptureRule implements MethodRule, TestRule {

  private final String loggerName;
  private StubAppender logAppender;

  public LogCaptureRule() {
    this(ROOT_LOGGER_NAME);
  }

  public LogCaptureRule(String loggerName) {
    this.loggerName = loggerName;
  }

  @Override
  public Statement apply(Statement base, Description description) {
    return apply(base, null, null);
  }

  @Override
  public Statement apply(Statement base, FrameworkMethod method, Object target) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        logAppender = new StubAppender();
        Logger root = (Logger) LoggerFactory.getLogger(loggerName);

        root.addAppender(logAppender);
        try {
          base.evaluate();
        } finally {
          root.detachAppender(logAppender);
        }
      }
    };
  }

  public LogCaptureRule logged(ExpectedLoggingMessage expectedLoggingMessage) {
    new LogCapture<>(logAppender.events(), null).logged(expectedLoggingMessage);
    return this;
  }
}
