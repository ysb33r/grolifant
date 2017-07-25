package org.ysb33r.gradle.olifant.errors

import groovy.transform.CompileStatic

/** An error occurred trying to configure an execution specification or a resolver for
 * an external tool.
 *
 * @since 0.4
 */
@CompileStatic
class ExecConfigurationException extends ConfigurationException {
    ExecConfigurationException(String message) {
        super(message)
    }

    ExecConfigurationException(String message, Throwable cause) {
        super(message, cause)
    }
}
