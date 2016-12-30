package org.ysb33r.gradle.olifant

import groovy.transform.CompileStatic

/** Dealing with URIs
 *
 */
@CompileStatic
class UriUtils {
    /** Attempts to convert object to a URI
     *
     * @param uriThingy Anything that could be converted to a URI
     * @return URI object
     */
    static URI urize(final Object uriThingy) {
        switch(uriThingy) {
            case URI:
                return (URI)uriThingy
            case File:
                return ((File)uriThingy).toURI()
            case String:
                return ((String)uriThingy).toURI()
            case Closure:
                return urize(((Closure)uriThingy).call())
            default:
                return urize(StringUtils.stringize(uriThingy))
        }
    }
}
