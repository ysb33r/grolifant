package org.ysb33r.gradle.olifant

import groovy.transform.CompileStatic
import org.gradle.api.GradleException

/** Methods for dealing with closures
 *
 * @since 0.3
 */
@CompileStatic
class ClosureUtils {

    /** Configure this item using a closure
     *
     * @param item Item to configure
     * @param cfg Configurating closure to use.
     */
    static void configureItem(Object item,Closure cfg) {
        Closure runner = (Closure)(cfg.clone())
        runner.delegate = item
        if(runner.maximumNumberOfParameters == 0) {
            runner()
        } else if(runner.maximumNumberOfParameters > 1) {
            throw new GradleException("Cannot use this closure for configuration as it has more than one input parameter")
        } else {
            runner(item)
        }
    }
}
