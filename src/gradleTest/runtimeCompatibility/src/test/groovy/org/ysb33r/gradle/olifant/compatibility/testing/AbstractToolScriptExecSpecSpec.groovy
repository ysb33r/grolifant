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
import org.ysb33r.gradle.olifant.ResolvedExecutable
import spock.lang.Specification

/**
 *
 * @since
 */
class AbstractToolScriptExecSpecSpec extends Specification {

    static
    // tag::example-exec-spec[]
    class PerlScriptExecSpec extends AbstractScriptExecSpec {
        PerlScriptExecSpec(Project project,Object exe) {
            super(project)
            executable = exe ?: 'perl'
        }
    }
    // end::example-exec-spec[]

    Project project = ProjectBuilder.builder().build()
    PerlScriptExecSpec testExecSpec = new PerlScriptExecSpec(project,null)

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
            // tag::script-examples[]
            script 'install.py'       // <1>
            scriptArgs = ['aye']      // <2>
            scriptArgs 'cee',{'dee'}  // <3>
            // end::script-examples[]
        }

        then:
        testExecSpec.getCommandLine() == [
                '/path/to/python.exe',
                'install.py',
                'aye', 'cee', 'dee'
        ]

        testExecSpec.ignoreExitValue == false
        testExecSpec.standardInput == null
        testExecSpec.standardOutput == null
        testExecSpec.errorOutput == null
        testExecSpec.workingDir == project.file('.')

    }

    void 'Lazy-evaluate scipt '() {
        when:
        testExecSpec.script = {'install'}

        then:
        testExecSpec.script == 'install'
    }


}
