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
import org.gradle.process.ProcessForkOptions
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.olifant.exec.AbstractCommandExecSpec
import org.ysb33r.gradle.olifant.MapUtils
import org.ysb33r.gradle.olifant.StringUtils
import spock.lang.Specification

class AbstractCommandExecSpecSpec extends Specification {

    static
    // tag::example-exec-spec[]
    class GitExecSpec extends AbstractCommandExecSpec {
        GitExecSpec(Project project,Object exe) {
            super(project)
            setExecutable (exe ?: 'git')
        }
    }
    // end::example-exec-spec[]

    Project project = ProjectBuilder.builder().build()
    GitExecSpec testExecSpec = new GitExecSpec(project,null)

    void 'Configuring a specification'() {
        when:
        testExecSpec.configure {

            // tag::declarative[]
            ignoreExitValue true  // <1>
            standardOutput System.out  // <2>
            standardInput System.in    // <3>
            errorOutput System.err     // <4>
            workingDir '.'     // <5>
            // end::declarative[]

            // tag::environment[]
            environment = [ foo : 'bar']               // <1>
            environment foo2 : 'bar2', foo3 : {'bar3'} // <2>
            environment 'foo4', 'bar4'   // <3>
            // end::environment[]

            // tag::executable[]
            executable {'/path/to/exe'}         // <1>
            exeArgs = [ 'first', 'second' ]     // <2>
            exeArgs 'third', {'fourth'}  // <3>
            // end::executable[]

            // tag::command[]
            command 'install'         // <1>
            cmdArgs = [ 'aye', 'bee'] // <2>
            cmdArgs 'cee', {'dee'}    // <3>
            // end::command[]
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
        MapUtils.stringizeValues(testExecSpec.environment) == [ foo : 'bar', foo2 : 'bar2', foo3 : 'bar3', foo4 : 'bar4']

    }

    void 'Lazy-evaluate command'() {
        when:
        testExecSpec.command = {'install'}

        then:
        testExecSpec.command == 'install'
    }

    void 'Copying process fork options'() {
        setup:
        ProcessForkOptions target = new ProcessForkOptions() {

            @Override
            String getExecutable() {
                StringUtils.stringize(exe)
            }

            // Override in 4.0
            void setExecutable(String s) {
                this.exe = s
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

            // Override in 4.0
            void setWorkingDir(File f) {
                this.wd = f
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



