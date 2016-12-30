package org.ysb33r.gradle.grolifant.compatibility.testing

import org.ysb33r.gradle.olifant.StringUtils
import spock.lang.Specification

class StringUtilsSpec extends Specification {

    def 'Convert a single string'() {
        expect:
        StringUtils.stringize('foo') == 'foo'
        StringUtils.stringize(new File('foo')) == 'foo'
        StringUtils.stringize({'foo'}) == 'foo'
    }
}