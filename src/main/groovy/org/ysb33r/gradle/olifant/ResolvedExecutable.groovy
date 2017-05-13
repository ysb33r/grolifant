package org.ysb33r.gradle.olifant

import groovy.transform.CompileStatic

/** Holds a reference to an executable that will only be made available when explicitly called.
 *
 * This makes it possibly to resolve executables which only become available after packages have been
 * downlaoded.
 *
 * @since 0.3
 */
@CompileStatic
interface ResolvedExecutable {

    /** Location of a tool executable.
     *
     * @return Full path to the tool executable
     */
    File getExecutable()
}