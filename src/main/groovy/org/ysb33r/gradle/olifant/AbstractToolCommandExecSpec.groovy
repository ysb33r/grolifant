package org.ysb33r.gradle.olifant

import org.gradle.process.ProcessForkOptions

/** Tool execution specification aimed at script-line tools which takes a script as one of the arguments.
 *
 * @since 0.3
 */
abstract class AbstractToolCommandExecSpec extends AbstractToolExecSpec {

    /** The script used in this specification as a String.
     *
     * @return Command if set (else null).
     */
    String getCommand() {
        if(this.command == null) {
            null
        } else {
            StringUtils.stringize(this.executable)
        }
    }

    /** Set the command to use.
     *
     * @param cmd Anything that can be resolved via {@link StringUtils.stringize(Object)}
     */
    void setCommand(Object cmd) {
        this.command = cmd
    }

    /** Set the command to use.
     *
     * @param cmd Anything that can be resolved via {@link org.ysb33r.gradle.olifant.StringUtils.stringize(Object)}
     */
    void command(Object cmd) {
        this.executable = cmd
    }

    /** Replace the command-specific arguments with a new set.
     *
     * @param args New list of command-specific arguments
     */
    void setCmdArgs(Iterable<?> args) {
        setInstructionArgs(args)
    }

    /** Add more command-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void cmdArgs(Iterable<?> args) {
        instructionArgs(args)
    }

    /** Add more command-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void cmdArgs(Object... args) {
        instructionArgs(args)
    }

    /** Any arguments specific to the command.
     *
     * @return Arguments to the commands. Can be empty, but never null.
     */
    List<String> getCmdArgs() {
        getInstructionsArgs()
    }

    protected AbstractToolCommandExecSpec() {
        super()
    }

    /** A specific instruction passed to a tool.
     *
     * * Instruction can be empty or null, which means that by default implementation {@link #getToolInstructionArgs} will be ignored.
     *
     * @return Instruction as string
     */
    @Override
    protected String getToolInstruction() {
        getCommand()
    }

    private Object command
}
