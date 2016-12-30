package org.ysb33r.gradle.olifant

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

/** Dealing with URIs
 *
 */
@CompileStatic
class UriUtils {

    /** Attempts to convert object to a URI. Will hand
     *
     * @param uriThingy Anything that could be converted to a URI
     * @return URI object
     */
    static URI urize(final Object uriThingy) {
        switch(uriThingy) {
            case URI:
                return (URI)uriThingy
            case Closure:
                return urize(((Closure)uriThingy).call())
            default:
                if (uriThingy.metaClass.respondsTo(uriThingy,'toURI')) {
                    return convertWithNativeUriConversion(uriThingy)
                } else {
                    return urize(StringUtils.stringize(uriThingy))
                }
        }
    }

    @CompileDynamic
    private static URI convertWithNativeUriConversion(Object uriThingy) {
        uriThingy.toURI()
    }
}
