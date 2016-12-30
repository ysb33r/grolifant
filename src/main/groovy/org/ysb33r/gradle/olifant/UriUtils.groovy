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

/** Dealing with URIs
 *
 */
@CompileStatic
class UriUtils {

    /** Attempts to convert object to a URI.
     *
     * Closures can be passed and will be evaluated. Result will then be converted to a URI.
     *
     * @param uriThingy Anything that could be converted to a URI
     * @return URI object
     */
    static URI urize(final Object uriThingy) {
        switch(uriThingy) {
            case URI:
                return (URI)uriThingy
            case Closure:
                return urize(((Closure)uriThingy).call())
            default:
                if (uriThingy.metaClass.respondsTo(uriThingy,'toURI')) {
                    return convertWithNativeUriMethod(uriThingy)
                } else {
                    return urize(StringUtils.stringize(uriThingy))
                }
        }
    }

    /** Helper method to call toURI method on object
     *
     * @param uriThingy Object to convert to URI
     * @return URI
     */
    @CompileDynamic
    private static URI convertWithNativeUriMethod(Object uriThingy) {
        uriThingy.toURI()
    }
}
