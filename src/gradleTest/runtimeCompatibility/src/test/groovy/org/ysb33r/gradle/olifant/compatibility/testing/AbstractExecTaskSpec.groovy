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
import org.ysb33r.gradle.olifant.AbstractScriptExecSpec
import org.ysb33r.gradle.olifant.AbstractCommandExecSpec
import org.ysb33r.gradle.olifant.AbstractCommandExecTask
import org.ysb33r.gradle.olifant.OperatingSystem
import spock.lang.Specification

class AbstractExecTaskSpec extends Specification {

    static final File TESTDIST_DIR = new File(System.getProperty('COMPAT_TEST_RESOURCES_DIR') ?: 'src/gradleTest/runtimeCompatibility/src/test/resources').absoluteFile
    static final String toolExt = OperatingSystem.current().windows ? 'cmd' : 'sh'

    static
    // tag::example-tool-exec-spec[]
    class MyCmdExecSpec extends AbstractCommandExecSpec {
        MyCmdExecSpec(Project project,File defaultBinary) {
            super(project)
            executable = defaultBinary
        }
    }
    // end::example-tool-exec-spec[]

    static
    // tag::example-tool-exec-type[]
    class MyCmdExec extends AbstractCommandExecTask< MyCmdExecSpec > {
        MyCmdExec() {
            super()
            // end::example-tool-exec-type[]
            setToolExecutable(new File(TESTDIST_DIR,'mycmd.' + toolExt))
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
            executable = defaultBinary
        }
    }

    static
    class MyScriptExec extends AbstractCommandExecTask< MyScriptExecSpec > {
        MyScriptExec() {
            super()
            setToolExecutable(new File(TESTDIST_DIR,'mycmd.' + toolExt))
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