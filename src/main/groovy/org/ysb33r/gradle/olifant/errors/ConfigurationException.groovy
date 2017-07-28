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
package org.ysb33r.gradle.olifant.errors

import groovy.transform.CompileStatic
import org.gradle.api.GradleException

/** A failure has occurred when downloading a distribution of an external tool or SDK.
 *
 * @since 0.4
 */
@CompileStatic
class ConfigurationException extends GradleException implements GrolifantError {

    ConfigurationException(String message) {
        super(message)
    }

    ConfigurationException(String message, Throwable cause) {
        super(message, cause)
    }
}
