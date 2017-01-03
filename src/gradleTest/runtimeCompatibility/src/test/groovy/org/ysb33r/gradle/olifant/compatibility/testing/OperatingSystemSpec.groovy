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

import org.ysb33r.gradle.olifant.OperatingSystem
import spock.lang.IgnoreIf
import spock.lang.Specification


/**
 *
 */
class OperatingSystemSpec extends Specification {

    // tag::init_os[]
    OperatingSystem os = OperatingSystem.current() // <1>
    // end::init_os[]

    def 'toString is a combination of name, version and architecture' () {

        expect:
        os.toString() == os.name + ' ' + os.version + ' ' + os.arch
    }

    def 'Find a file in the system search path'() {

        setup:
        if(os.windows) {
            assert os.path.contains(new File('c:/windows'))
        } else {
            assert os.path.contains(new File('/bin'))
        }

        when:
        // tag::init_os[]
        File findExe = os.findInPath('bash')
        // end::init_os[]

        if(os.windows) {
            findExec = os.findInPath('cmd.exe')
        }

        then:
        findExe != null
    }
}