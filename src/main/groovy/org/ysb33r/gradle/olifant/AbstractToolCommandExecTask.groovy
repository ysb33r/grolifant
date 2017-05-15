package org.ysb33r.gradle.olifant

import groovy.transform.CompileStatic
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/** A abstract task type for executing binaries that take a command as well as a set of command arguments.
 *
 * @since 0.3
 */
@CompileStatic
abstract class AbstractToolCommandExecTask<T extends AbstractToolCommandExecSpec> extends AbstractExecTask<AbstractToolCommandExecTask<T>,T> {

    /** The command used in this specification as a String.
     *
     * @return Command
     */
    @Input
    String getCommand() {
        toolExecSpec.getCommand()
    }

    /** Set the command to use.
     *
     * @param cmd Anything that can be resolved via {@link StringUtils.stringize(Object)}
     */
    void setCommand(Object cmd) {
        toolExecSpec.setCommand(cmd)
    }

    /** Set the command to use.
     *
     * @param cmd Anything that can be resolved via {@link org.ysb33r.gradle.olifant.StringUtils.stringize(Object)}
     */
    void command(Object cmd) {
        setCommand(cmd)
    }

    /** Replace the command-specific arguments with a new set.
     *
     * @param args New list of command-specific arguments
     */
    void setCmdArgs(Iterable<?> args) {
        toolExecSpec.setCmdArgs(args)
    }

    /** Add more command-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void cmdArgs(Iterable<?> args) {
        toolExecSpec.cmdArgs(args)
    }

    /** Add more command-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void cmdArgs(Object... args) {
        toolExecSpec.cmdArgs(args)
    }

    /** Any arguments specific to the command in use
     *
     * @return Arguments to the command. Can be empty, but never null.
     */
    @Optional
    @Input
    List<String> getCmdArgs() {
        toolExecSpec.getCmdArgs()
    }

    private T getToolExecSpec() {
        (T)(super.execSpec)
    }
}
