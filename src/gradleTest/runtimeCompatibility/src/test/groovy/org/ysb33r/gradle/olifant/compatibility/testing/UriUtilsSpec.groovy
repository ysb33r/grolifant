package org.ysb33r.gradle.olifant.compatibility.testing

import org.ysb33r.gradle.olifant.UriUtils
import spock.lang.Specification


class UriUtilsSpec extends Specification {

    def 'Converting simple items to URIs'() {
        expect:
        // tag::urize[]
        UriUtils.urize( 'ftp://foo/bar' )        == new URI('ftp://foo/bar')
        UriUtils.urize( new File('/foo.bar') )   == new File('/foo.bar').toURI()
        UriUtils.urize( {'ftp://foo/bar'} )      == new URI('ftp://foo/bar')
        UriUtils.urize( {new File('/foo.bar')} ) == new File('/foo.bar').toURI()
        // end::urize[]
    }
}