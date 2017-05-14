package org.ysb33r.gradle.olifant.compatibility.testing

import org.gradle.api.Project
import org.gradle.process.ExecResult
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.olifant.AbstractToolCommandExecSpec
import org.ysb33r.gradle.olifant.ExecSpecInstantiator
import org.ysb33r.gradle.olifant.ExtensionUtils
import org.ysb33r.gradle.olifant.OperatingSystem
import org.ysb33r.gradle.olifant.internal.execspec.ExecProjectExtension
import spock.lang.Specification


class ExecProjectExtensionSpec extends Specification {

    static final File TESTDIST_DIR = new File(System.getProperty('COMPAT_TEST_RESOURCES_DIR') ?: 'src/gradleTest/runtimeCompatibility/src/test/resources').absoluteFile
    static final String toolExt = OperatingSystem.current().windows ? 'cmd' : 'sh'

    static
    // tag::example-exec-spec[]
    class GitExecSpec extends AbstractToolCommandExecSpec {
        GitExecSpec(Project project) {
            super(project)
            executable = 'git'
        }
    }
    // end::example-exec-spec[]

    Project project = ProjectBuilder.builder().build()

    void 'Add execution specification to project as extension'() {

        when:
        // tag::adding-extension[]
        ExtensionUtils.addExecProjectExtension('gitexec', project, { Project project ->
            new GitExecSpec(project)
        } as ExecSpecInstantiator<GitExecSpec>) // <1>
        // end::adding-extension[]

        then:
        project.extensions.extraProperties.get('gitexec')
    }

    void 'Run executable as extension'() {

        setup:
        ExtensionUtils.addExecProjectExtension('myrunner', project, { Project project ->
            new GitExecSpec(project)
        } as ExecSpecInstantiator<GitExecSpec>)

        OutputStream output = new ByteArrayOutputStream()

        when:
        Closure configurator = {
            command 'install'
            standardOutput output
            executable new File( TESTDIST_DIR, "mycmd.${toolExt}" )
        }

        ExecResult result = project.myrunner configurator

        then:
        result.exitValue == 0
        output.toString().startsWith('install')


    }
}