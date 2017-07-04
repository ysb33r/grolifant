/*
 * ============================================================================
 * (C) Copyright Schalk W. Cronje 2016 - 2017
 *
 * This software is licensed under the Apache License 2.0
 * See http://www.apache.org/licenses/LICENSE-2.0 for license details
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 * ============================================================================
 */
package org.ysb33r.gradle.olifant.compatibility.testing

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.olifant.exec.AbstractScriptExecSpec
import org.ysb33r.gradle.olifant.exec.AbstractCommandExecSpec
import org.ysb33r.gradle.olifant.exec.AbstractCommandExecTask
import org.ysb33r.gradle.olifant.exec.AbstractScriptExecTask
import org.ysb33r.gradle.olifant.OperatingSystem
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission

class AbstractExecTaskSpec extends Specification {

    static final File TESTDIST_DIR = new File(System.getProperty('COMPAT_TEST_RESOURCES_DIR') ?: 'src/gradleTest/runtimeCompatibility/src/test/resources').absoluteFile
    static final String toolExt = OperatingSystem.current().windows ? 'cmd' : 'sh'
    static final File scriptToPass = new File(TESTDIST_DIR,'mycmd.' + toolExt)

    void setupSpec() {
        // This code is here to work around the case that execute permissions are lost when GradleTest copies files.
        if(!OperatingSystem.current().windows) {
            Path scriptPath = scriptToPass.toPath()
            Set perms = Files.getPosixFilePermissions(scriptPath)
            perms.add(PosixFilePermission.OWNER_EXECUTE)
            Files.setPosixFilePermissions(scriptPath,perms)
        }
    }

    static
    // tag::example-tool-exec-spec[]
    class MyCmdExecSpec extends AbstractCommandExecSpec {
        MyCmdExecSpec(Project project,File defaultBinary) {
            super(project)
            setExecutable(defaultBinary)
        }
    }
    // end::example-tool-exec-spec[]

    static
    // tag::example-tool-exec-type[]
    class MyCmdExec extends AbstractCommandExecTask< MyCmdExecSpec > {
        MyCmdExec() {
            super()
            // end::example-tool-exec-type[]
            setToolExecutable(AbstractExecTaskSpec.scriptToPass)
            // tag::example-tool-exec-type[]
        }

        @Override
        MyCmdExecSpec createExecSpec(Project project) {
            new MyCmdExecSpec(project,new File('/bin/mycmd'))
        }
    }
    // end::example-tool-exec-type[]

    static
    class MyScriptExecSpec extends AbstractScriptExecSpec {
        MyScriptExecSpec(Project project,File defaultBinary) {
            super(project)
            setExecutable(defaultBinary)
        }
    }

    static
    class MyScriptExec extends AbstractScriptExecTask< MyScriptExecSpec > {
        MyScriptExec() {
            super()
            setToolExecutable(scriptToPass)
        }

        @Override
        MyScriptExecSpec createExecSpec(Project project) {
            new MyScriptExecSpec(project,new File('/bin/mycmd'))
        }
    }

    Project project = ProjectBuilder.builder().build()

    def 'Instantiate exec-type task'() {
        setup:
        project.tasks.create('mycmd', MyCmdExec) {
            command 'install'
            cmdArgs 'some.pkg'
        }

        when:
        project.evaluate()
        project.tasks.mycmd.execute()

        then:
        project.tasks.mycmd.execResult != null

    }

    def 'Instantiate script-type task'() {
        setup:
        project.tasks.create('scriptor', MyScriptExec) {
            script 'install.py'
            scriptArgs 'a','b'
        }

        when:
        project.evaluate()
        project.tasks.scriptor.execute()

        then:
        project.tasks.scriptor.execResult != null
    }
}