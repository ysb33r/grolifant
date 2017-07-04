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
import org.ysb33r.gradle.olifant.OperatingSystem
import org.ysb33r.gradle.olifant.exec.AbstractScriptExecSpec
import org.ysb33r.gradle.olifant.exec.ResolvedExecutable
import spock.lang.Specification

/**
 *
 * @since
 */
class AbstractToolScriptExecSpecSpec extends Specification {

    static final Boolean IS_WINDOWS = OperatingSystem.current().isWindows()

    static
    // tag::example-exec-spec[]
    class PerlScriptExecSpec extends AbstractScriptExecSpec {
        PerlScriptExecSpec(Project project,Object exe) {
            super(project)
            setExecutable(exe ?: 'perl')
        }
    }
    // end::example-exec-spec[]

    Project project = ProjectBuilder.builder().build()
    PerlScriptExecSpec testExecSpec = new PerlScriptExecSpec(project,null)

    void 'Configuring a specification'() {

        File python = IS_WINDOWS ? new File(project.projectDir,'/path/to/python.exe') : new File('/path/to/python.exe')

        // Use ResolvedExecutable in this test as it allows a different code path to be tested in the base class.
        ResolvedExecutable wheresIsPython = new ResolvedExecutable() {
            @Override
            File getExecutable() {
                python
            }
        }

        when:
        testExecSpec.configure {
            executable wheresIsPython
            // tag::script-examples[]
            script 'install.py'       // <1>
            scriptArgs = ['aye']      // <2>
            scriptArgs 'cee',{'dee'}  // <3>
            // end::script-examples[]
        }

        then:
        testExecSpec.getCommandLine() == [
                python.absolutePath,
                'install.py',
                'aye', 'cee', 'dee'
        ]

        testExecSpec.ignoreExitValue == false
        testExecSpec.standardInput == System.in
        testExecSpec.standardOutput == System.out
        testExecSpec.errorOutput == System.err
        testExecSpec.workingDir == project.file('.')

    }

    void 'Lazy-evaluate scipt '() {
        when:
        testExecSpec.script = {'install'}

        then:
        testExecSpec.script == 'install'
    }


}
