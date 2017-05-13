package org.ysb33r.gradle.olifant.compatibility.testing

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.olifant.AbstractScriptExecSpec
import org.ysb33r.gradle.olifant.AbstractToolCommandExecSpec
import org.ysb33r.gradle.olifant.ResolvedExecutable
import spock.lang.Specification

/**
 *
 * @since
 */
class AbstractToolScriptExecSpecSpec extends Specification {

    static class TestSpec extends AbstractScriptExecSpec {
        TestSpec(Project project) {super(project)}
    }

    Project project = ProjectBuilder.builder().build()
    TestSpec testExecSpec = new TestSpec(project)

    void 'Configuring a specification'() {

        // Use ResolvedExecutable in this test as it allows a different code path to be tested in the base class.
        ResolvedExecutable wheresIsPython = new ResolvedExecutable() {
            @Override
            File getExecutable() {
                new File('/path/to/python.exe')
            }
        }

        when:
        testExecSpec.configure {
            executable wheresIsPython
            script 'install.py'
            scriptArgs 'cee','dee'
        }

        then:
        testExecSpec.getCommandLine() == [
                '/path/to/python.exe',
                'install.py',
                'cee', 'dee'
        ]

        testExecSpec.ignoreExitValue == false
        testExecSpec.standardInput == null
        testExecSpec.standardOutput == null
        testExecSpec.errorOutput == null
        testExecSpec.workingDir == project.file('.')

    }
}
