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
