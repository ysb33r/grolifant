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
package org.ysb33r.gradle.olifant.internal.logging

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.ysb33r.gradle.olifant.BaseProgressLogger
import org.ysb33r.gradle.olifant.ProgressLogger

/**
 * <p> This was built from an idea conceived by Michel Kremer - {@link https://github.com/michel-kraemer/gradle-download-task/blob/master/src/main/java/de/undercouch/gradle/tasks/download/internal/ProgressLoggerWrapper.java}.
 *
 * @since 0.4
 */
@CompileStatic
class DownloadProgressLogger implements ProgressLogger {

    DownloadProgressLogger(final Project project,final String text) {
        this.logger = findFactoryFor(project,findLoggerFactory()).invokeMethod('newOperation',this.class)
        if(this.logger) {
            configureLogger(text)
        } else {
            project.logger.debug( "Could not create a progress logger for ${text} - no progress feedback will be provided")
        }
    }

    @Override
    void log(final String text) {
        this.logger?.invokeMethod('progress',text)
    }

    @Override
    void started() {
        startLogging()
    }

    @Override
    void completed() {
        stopLogging()
    }

    @CompileDynamic()
    private void startLogging() {
        this.logger?.started()
    }

    @CompileDynamic()
    private void stopLogging() {
        this.logger?.completed()
    }

    @CompileDynamic
    private void configureLogger(final String text) {
        this.logger.invokeMethod('setDescription',text)
        this.logger.invokeMethod('setLoggingHeader',text)
    }

    private Object logger

    private static Object findFactoryFor(Project project, Class clazz) {
        if(clazz!=null) {
            ((ProjectInternal)project).services.get(clazz)
        } else {
            null
        }
    }

    private static Class findLoggerFactory() {
        try {
            Class.forName('org.gradle.internal.logging.progress.ProgressLoggerFactory')
        } catch (ClassNotFoundException e1) {
            try {
                Class.forName('org.gradle.logging.ProgressLoggerFactory')
            } catch (ClassNotFoundException e2) {
                null
            }
        }
    }
}
