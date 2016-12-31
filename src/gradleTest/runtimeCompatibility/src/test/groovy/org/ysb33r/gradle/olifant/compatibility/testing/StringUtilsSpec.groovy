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

import org.ysb33r.gradle.olifant.StringUtils
import spock.lang.Specification

class StringUtilsSpec extends Specification {

    def 'Convert a single string'() {
        expect:
        // tag::stringize_string[]
        StringUtils.stringize( 'foo' )           == 'foo'
        StringUtils.stringize( new File('foo') ) == 'foo'
        StringUtils.stringize( {'foo'} )         == 'foo'
        // end::stringize_string[]
    }

    def 'Convert a list of strings'() {
        expect:
        // tag::stringize_collection[]
        StringUtils.stringize(['foo1',new File('foo2'),{'foo3'}]) == ['foo1','foo2','foo3']
        // end::stringize_collection[]
    }
}