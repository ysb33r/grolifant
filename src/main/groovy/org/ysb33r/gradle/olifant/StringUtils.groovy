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

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.util.CollectionUtils
import org.ysb33r.gradle.olifant.internal.LegacyLevel

/** A collection of utilities for converting to strings.
 *
 *
 */
@CompileStatic
class StringUtils {

    /** Converts most things to a string. Closures are evaluated as well.
     *
     * @param stringy An object that can be converted to a string or a closure that
     *   can be evaluated to something that can be converted to a string.
     * @return A string object
     */
    static String stringize(final Object stringy) {
        if(LegacyLevel.PRE_2_2) {
            if (stringy instanceof Closure) {
                StringUtils.stringize_legacy([((Closure)stringy).call()])[0]
            } else {
                StringUtils.stringize_legacy([stringy])[0]
            }
        } else {
            if (stringy instanceof Closure) {
                CollectionUtils.stringize([((Closure)stringy).call()])[0]
            } else {
                CollectionUtils.stringize([stringy])[0]
            }
        }
    }

    /** Converts a collection of most things to a list of strings. Closures are evaluated as well.
     *
     * @param Iterable list of objects that can be converetd to strings, including closure that can be evaluated
     *   into objects that can be converted to strings.
     * @return A list of strings
     */
    static List<String> stringize(final Iterable<?> stringyThings) {
        List<Object> collection = []
        for( Object item in stringyThings) {
            if(item instanceof Closure) {
                collection.add(StringUtils.stringize(item))
            } else {
                collection.add(item)
            }
        }
        if(LegacyLevel.PRE_2_2) {
            stringize_legacy(collection)
        } else {
            (List<String>)CollectionUtils.stringize(collection)
        }
    }

    @CompileDynamic
    private static List<String> stringize_legacy(Iterable<?> stringyList) {
        List<String> collection = []
        CollectionUtils.stringize(stringyList,collection)
        collection
    }
}
