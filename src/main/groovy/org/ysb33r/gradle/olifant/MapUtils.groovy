package org.ysb33r.gradle.olifant

import groovy.transform.CompileStatic

/** Various utilities dealing with key-value pairs.
 *
 * @since 0.3
 */
@CompileStatic
class MapUtils {

    /** Evaluates a map of objects to a map of strings.
     *
     * Anything value that can be evaluated by {@link StringUtils.stringize(final Object stringy)} is
     * evaluted
     *
     * @param properties Map that will be evaluated
     * @return Converted {@code Map<String,String>}
     */
    static Map<String,String> stringizeValues(Map<String,Object> properties) {
        (Map<String,String>)(properties.collectEntries { String key,Object value ->
            [ (key) : StringUtils.stringize(value) ]
        })
    }
}
