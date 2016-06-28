package io.kotlintest

import io.kotlintest.specs.ShouldSpec
import io.kotlintest.specs.StringSpec
import org.junit.runner.Description
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener
import org.junit.runner.notification.RunNotifier
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class KotlinTestJunitRunnerTest : StringSpec() {
  init {
    "should handle throwables" {
      System.setProperty("internal", "true")
      val runner = KTestJUnitRunner(ThrowsThrowable::class.java as Class<TestBase>)
      val n = RunNotifier()
      val latch = CountDownLatch(1)
      n.addListener(object : RunListener() {
        override fun testFailure(failure: Failure?) {
          latch.countDown()
        }
      })
      runner.run(n)
      latch.await(5, TimeUnit.SECONDS) shouldBe true
      System.setProperty("internal", "false")
    }

    "should handle ignored tests" {
      val runner = KTestJUnitRunner(HasIgnoredTest::class.java as Class<TestBase>)
      val n = RunNotifier()
      val latch = CountDownLatch(1)
      n.addListener(object : RunListener() {
        override fun testIgnored(description: Description?) {
          latch.countDown()
        }
      })
      runner.run(n)
      latch.await(5, TimeUnit.SECONDS) shouldBe true
    }
  }
}

class ThrowsThrowable : ShouldSpec() {
  init {
    should("throw throwable") {
      if (System.getProperty("internal") == "true")
        throw Throwable("hello")
    }
  }
}

class HasIgnoredTest : StringSpec() {
  init {
    "ignored test" {
    }.config(ignored = true)
  }
}