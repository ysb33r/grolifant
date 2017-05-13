package org.ysb33r.gradle.olifant.compatibility.testing

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.process.BaseExecSpec
import org.gradle.process.ProcessForkOptions
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.olifant.AbstractToolCommandExecSpec
import org.ysb33r.gradle.olifant.ResolvedExecutable
import org.ysb33r.gradle.olifant.StringUtils
import spock.lang.Specification


/**
 *
 * @since
 */
class AbstractToolCommandExecSpecSpec extends Specification {

    static class TestSpec extends AbstractToolCommandExecSpec {
        TestSpec(Project project) {super(project)}
    }

    Project project = ProjectBuilder.builder().build()
    TestSpec testExecSpec = new TestSpec(project)

    void 'Configuring a specification'() {
        when:
        testExecSpec.configure {
            environment = [ foo : 'bar']
            exeArgs = [ 'first', 'second' ]
            cmdArgs = [ 'aye', 'bee']

            ignoreExitValue true
            standardOutput System.out
            standardInput System.in
            errorOutput System.err
            workingDir '.'
            environment foo2 : 'bar2', foo3 : 'bar3'
            environment 'foo4', 'bar4'
            executable {'/path/to/exe'}
            exeArgs 'third', 'fourth'
            command 'install'
            cmdArgs 'cee','dee'
        }

        then:
        testExecSpec.getCommandLine() == [
                '/path/to/exe',
                'first','second','third','fourth',
                'install',
                'aye', 'bee', 'cee', 'dee'
        ]
        testExecSpec.ignoreExitValue == true
        testExecSpec.standardInput == System.in
        testExecSpec.standardOutput == System.out
        testExecSpec.errorOutput == System.err
        testExecSpec.workingDir == project.file('.')

    }

    void 'Copying process fork options'() {
        setup:
        ProcessForkOptions target = new ProcessForkOptions() {

            @Override
            String getExecutable() {
                StringUtils.stringize(exe)
            }

            @Override
            void setExecutable(Object o) {
                this.exe = o
            }

            @Override
            ProcessForkOptions executable(Object o) {
                this.exe = o
                return this
            }

            @Override
            File getWorkingDir() {
                project.file(wd)
            }

            @Override
            void setWorkingDir(Object o) {
                this.wd = o
            }

            @Override
            ProcessForkOptions workingDir(Object o) {
                this.wd = o
                return this
            }

            @Override
            Map<String, Object> getEnvironment() {
                env
            }

            @Override
            void setEnvironment(Map<String, ?> map) {
                env.clear()
                env.putAll map
            }

            @Override
            ProcessForkOptions environment(Map<String, ?> map) {
                env.putAll map
                return this
            }

            @Override
            ProcessForkOptions environment(String s, Object o) {
                env.add s,o
                return this
            }

            @Override
            ProcessForkOptions copyTo(ProcessForkOptions processForkOptions) {
                return null // Not going to use this in test
            }

            private Object exe
            private Object wd
            private Map<String,Object> env = [:]
        }

        when:
        testExecSpec.configure {
            executable '/path/to/exe'
            environment foo : 'bar'
            workingDir  '.'
        }

        testExecSpec.copyTo(target)

        then:
        target.executable == '/path/to/exe'
        target.workingDir == project.file('.')
        target.environment == [ foo : 'bar']
    }
}



//@Override
//ProcessForkOptions copyTo(ProcessForkOptions processForkOptions) {
//    processForkOptions.setEnvironment(this.env)
//    processForkOptions.setWorkingDir(this.workingDir)
//
//    if( !(processForkOptions instanceof AbstractToolExecSpec) && this.executable instanceof ResolvedExecutable) {
//        processForkOptions.setExecutable( ((ResolvedExecutable)(this.executable)).getExecutable() )
//    } else {
//        processForkOptions.setExecutable(this.executable)
//    }
//    return this
//}


