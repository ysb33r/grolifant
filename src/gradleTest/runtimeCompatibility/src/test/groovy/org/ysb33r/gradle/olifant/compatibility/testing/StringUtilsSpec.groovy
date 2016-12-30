package org.ysb33r.gradle.olifant.compatibility.testing

import org.ysb33r.gradle.olifant.StringUtils
import spock.lang.Specification

class StringUtilsSpec extends Specification {

    def 'Convert a single string'() {
        expect:
        // tag::stringize_string[]
        StringUtils.stringize( 'foo' )           == 'foo'
        StringUtils.stringize( new File('foo') ) == 'foo'
        StringUtils.stringize( {'foo'} )         == 'foo'
        // end::stringize_string[]
    }

    def 'Convert a list of strings'() {
        expect:
        // tag::stringize_collection[]
        StringUtils.stringize(['foo1',new File('foo2'),{'foo3'}]) == ['foo1','foo2','foo3']
        // end::stringize_collection[]
    }
}