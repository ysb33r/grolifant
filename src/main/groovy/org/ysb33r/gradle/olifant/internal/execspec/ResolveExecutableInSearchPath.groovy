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
package org.ysb33r.gradle.olifant.internal.execspec

import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.ysb33r.gradle.olifant.OperatingSystem
import org.ysb33r.gradle.olifant.ResolvedExecutable
import org.ysb33r.gradle.olifant.ResolvedExecutableFactory
import org.ysb33r.gradle.olifant.StringUtils

/** Resolves executable by search the system path.
 *
 * @since 0.3
 */
@CompileStatic
class ResolveExecutableInSearchPath implements ResolvedExecutableFactory {

    static final ResolveExecutableInSearchPath INSTANCE = new ResolveExecutableInSearchPath()

    /** Creates {@link ResolvedExecutable} from a specific input.
     *
     * @param options Ignored.
     * @param lazyPath Any object that can be resolved to a string using {@link org.ysb33r.gradle.olifant.StringUtils#stringize(Object)}.
     * @return The resolved executable.
     */
    @Override
    ResolvedExecutable build(Map<String,Object> options,Object lazyPath) {
        return new ResolvedExecutable() {
            @Override
            File getExecutable() {
                final String path = StringUtils.stringize(lazyPath)
                final File foundPath = OS.findInPath(path)

                if(foundPath == null) {
                    throw new GradleException("Cannot locate '${path}' in system search path")
                }

                foundPath
            }
        }
    }

    private static final OperatingSystem OS = OperatingSystem.current()
}
