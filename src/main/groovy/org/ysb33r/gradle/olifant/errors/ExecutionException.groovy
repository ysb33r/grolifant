package org.ysb33r.gradle.olifant.errors

import groovy.transform.CompileStatic
import org.gradle.api.GradleException

/** A failure occurred running or setting up an execution specification.
 *
 * @since 0.4
 */
@CompileStatic
class ExecutionException extends GradleException implements GrolifantError{
    ExecutionException(String message) {
        super(message)
    }

    ExecutionException(String message, Throwable cause) {
        super(message, cause)
    }
}
