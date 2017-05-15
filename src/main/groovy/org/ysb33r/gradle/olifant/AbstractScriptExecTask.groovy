package org.ysb33r.gradle.olifant

import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/** A abstract task type for executing binaries that take a command as well as a set of command arguments.
 *
 * @since 0.3
 */
@CompileStatic
abstract class AbstractScriptExecTask<T extends AbstractScriptExecSpec> extends AbstractExecTask<AbstractScriptExecTask<T>,T> {

    /** The script used in this specification as a String.
     *
     * @return Script
     */
    @Input
    String getScript() {
        toolExecSpec.getScript()
    }

    /** Set the script to use.
     *
     * @param cmd Anything that can be resolved via {@link StringUtils.stringize(Object)}
     */
    void setScript(Object cmd) {
        toolExecSpec.setScript(cmd)
    }

    /** Set the script to use.
     *
     * @param cmd Anything that can be resolved via {@link org.ysb33r.gradle.olifant.StringUtils.stringize(Object)}
     */
    void script(Object cmd) {
        setScript(cmd)
    }

    /** Replace the script-specific arguments with a new set.
     *
     * @param args New list of script-specific arguments
     */
    void setScriptArgs(Iterable<?> args) {
        toolExecSpec.setScriptArgs(args)
    }

    /** Add more script-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void scriptArgs(Iterable<?> args) {
        toolExecSpec.scriptArgs(args)
    }

    /** Add more script-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void scriptArgs(Object... args) {
        toolExecSpec.scriptArgs(args)
    }

    /** Any arguments specific to the script in use
     *
     * @return Arguments to the script. Can be empty, but never null.
     */
    @Optional
    @Input
    List<String> getScriptArgs() {
        toolExecSpec.getScriptArgs()
    }

    private T getToolExecSpec() {
        (T)(super.execSpec)
    }
}
