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
package org.ysb33r.gradle.olifant.compatibility.testing.internal

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.olifant.ProgressLogger
import org.ysb33r.gradle.olifant.internal.logging.DownloadProgressLogger
import spock.lang.Specification

class DownloadProgressLoggerSpec extends Specification {

    Project project = ProjectBuilder.builder().build()

    def "Instantiate a progress logger which hooks into the Gradle progress logger"() {
        when:
        ProgressLogger logger = new DownloadProgressLogger(project,'foo')

        then:
        noExceptionThrown()

        when:
        logger.started()

        then:
        noExceptionThrown()

        when:
        logger.log('bar')

        then:
        noExceptionThrown()

        when:
        logger.completed()

        then:
        noExceptionThrown()
    }
}