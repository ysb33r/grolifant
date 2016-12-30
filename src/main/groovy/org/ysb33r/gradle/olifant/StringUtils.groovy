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
package org.ysb33r.gradle.olifant

import groovy.transform.CompileStatic
import org.gradle.util.CollectionUtils

/** A collection of utilities for converting to strings.
 *
 *
 */
@CompileStatic
class StringUtils {

    /** Converts most things to a string. Closures will be evaluated as well.
     *
     * @param stringy
     * @return A string object
     */
    static String stringize(final Object stringy) {
        CollectionUtils.stringize([stringy])[0]
    }
}
