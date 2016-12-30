package org.ysb33r.gradle.olifant.internal

import groovy.transform.CompileStatic
import org.gradle.util.GradleVersion

/**
 *
 */
@CompileStatic
class LegacyLevel {
    static final boolean PRE_2_2 = GradleVersion.current() < GradleVersion.version('2.2')
}
