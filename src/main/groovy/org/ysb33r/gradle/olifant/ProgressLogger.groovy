package org.ysb33r.gradle.olifant

import groovy.transform.CompileStatic

/** A simple interface for logging progress to stdout.
 *
 */
@CompileStatic
interface ProgressLogger {
    void log(String message)
}