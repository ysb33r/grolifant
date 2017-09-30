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
