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
import org.gradle.api.Project
import org.ysb33r.gradle.olifant.internal.logging.DownloadProgressLogger

/** Various utilities related to logging.
 *
 * @since 0.4
 */
@CompileStatic
class LogUtils {

    /** Creates an instance of a progress logger that coul dbe use to log progress to console
     *
     * @param project Project that this logger will be attached to.
     * @param text Set description and logging header.
     * @return A basic progress logger
     */
    static ProgressLogger createProgressLogger(final Project project, final String text) {
        new DownloadProgressLogger(project,text)
    }

}
