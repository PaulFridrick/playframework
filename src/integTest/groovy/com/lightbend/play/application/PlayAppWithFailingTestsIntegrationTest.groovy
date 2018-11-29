package com.lightbend.play.application

import com.lightbend.play.AbstractIntegrationTest
import com.lightbend.play.fixtures.app.PlayApp
import com.lightbend.play.fixtures.app.WithFailingTestsApp
import com.lightbend.play.fixtures.test.JUnitXmlTestExecutionResult
import org.gradle.testkit.runner.BuildResult

import static com.lightbend.play.fixtures.PlayCoverage.DEFAULT_PLAY_VERSION

class PlayAppWithFailingTestsIntegrationTest extends AbstractIntegrationTest {

    PlayApp playApp = new WithFailingTestsApp(DEFAULT_PLAY_VERSION)

    def setup() {
        playApp.writeSources(projectDir)
    }

    def "reports failing run play app tests"() {
        when:
        BuildResult result = buildAndFail("test")
        then:

        result.output.contains """
FailingApplicationSpec > failingTest FAILED
    java.lang.AssertionError at FailingApplicationSpec.scala:23
"""

        result.output.contains """
FailingIntegrationSpec > failingTest FAILED
    java.lang.AssertionError at FailingIntegrationSpec.scala:23
"""
        result.output.contains("6 tests completed, 2 failed")
        result.output.contains("There were failing tests.")

        def testResult = new JUnitXmlTestExecutionResult(projectDir)
        testResult.assertTestClassesExecuted("ApplicationSpec", "IntegrationSpec", "FailingApplicationSpec", "FailingIntegrationSpec")
        testResult.testClass("ApplicationSpec").assertTestCount(2, 0, 0)
        testResult.testClass("IntegrationSpec").assertTestCount(1, 0, 0)
        testResult.testClass("FailingIntegrationSpec").assertTestCount(1, 1, 0)
        testResult.testClass("FailingApplicationSpec").assertTestCount(2, 1, 0)
    }
}