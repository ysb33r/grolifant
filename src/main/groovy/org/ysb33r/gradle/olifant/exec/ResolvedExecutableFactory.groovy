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
package org.ysb33r.gradle.olifant.exec

import groovy.transform.CompileStatic

/** Creates a {@link ResolvedExecutable}.
 *
 * @since 0.3
 */
@CompileStatic
interface ResolvedExecutableFactory {

    /** Creates {@link ResolvedExecutable} from a specific input.
     *
     * @param from An object that can be used to resolved an executable. It is up to the implementation to decide whether the
     *   object is of an appropriate type.
     * @return The resolved executable.
     */
    ResolvedExecutable build(Map<String,Object> options,Object from)
}
