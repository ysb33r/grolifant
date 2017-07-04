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
import org.gradle.process.ExecResult
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.olifant.exec.AbstractCommandExecSpec
import org.ysb33r.gradle.olifant.exec.ExecSpecInstantiator
import org.ysb33r.gradle.olifant.ExtensionUtils
import org.ysb33r.gradle.olifant.OperatingSystem
import spock.lang.Specification


class ExecProjectExtensionSpec extends Specification {

    static final File TESTDIST_DIR = new File(System.getProperty('COMPAT_TEST_RESOURCES_DIR') ?: 'src/gradleTest/runtimeCompatibility/src/test/resources').absoluteFile
    static final String toolExt = OperatingSystem.current().windows ? 'cmd' : 'sh'

    static
    // tag::example-exec-spec[]
    class GitExecSpec extends AbstractCommandExecSpec {
        GitExecSpec(Project project) {
            super(project)
            setExecutable('git')
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