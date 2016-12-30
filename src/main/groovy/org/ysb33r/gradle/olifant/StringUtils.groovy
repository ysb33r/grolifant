package org.ysb33r.gradle.olifant

import groovy.transform.CompileStatic
import org.gradle.util.CollectionUtils

/** A collection of utilities for converting to strings.
 *
 *
 */
@CompileStatic
class StringUtils {

    /** Converts most things to a string. Closures will be evaluated as well.
     *
     * @param stringy
     * @return A string object
     */
    static String stringize(final Object stringy) {
        CollectionUtils.stringize([stringy])[0]
    }
}
