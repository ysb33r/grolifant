package org.ysb33r.gradle.olifant

import org.gradle.api.Project

/** Tool execution specification aimed at script-line tools which takes a script as one of the arguments.
 *
 * @since 0.3
 */
abstract class AbstractScriptExecSpec extends AbstractToolExecSpec {

    /** The script used in this specification as a String.
     *
     * @return Command if set (else null).
     */
    String getScript() {
        if(this.script == null) {
            null
        } else {
            StringUtils.stringize(this.executable)
        }
    }

    /** Set the executable to use.
     *
     * @param cmd Anything that can be resolved via {@link StringUtils.stringize(Object)}
     */
    void setScript(Object cmd) {
        this.script = cmd
    }

    /** Set the executable to use.
     *
     * @param exe Anything that can be resolved via {@link org.ysb33r.gradle.olifant.StringUtils.stringize(Object)}
     */
    void script(Object cmd) {
        this.executable = cmd
    }

    /** Replace the script-specific arguments with a new set.
     *
     * @param args New list of tool-specific arguments
     */
    void setScriptArgs(Iterable<?> args) {
        setInstructionArgs(args)
    }

    /** Add more script-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void scriptArgs(Iterable<?> args) {
        instructionArgs(args)
    }

    /** Add more script-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void scriptArgs(Object... args) {
        instructionArgs(args)
    }

    /** Any arguments specific to the script.
     *
     * @return Arguments to the commands. Can be empty, but never null.
     */
    List<String> getScriptArgs() {
        getInstructionsArgs()
    }

    /** Construct class and attach it to specific project.
     *
     * @param project Project this exec spec is attached.
     */
    protected AbstractScriptExecSpec(Project project) {
        super(project)
    }

    /** A specific instruction passed to a tool.
     *
     * * Instruction can be empty or null, which means that by default implementation {@link #getToolInstructionArgs} will be ignored.
     *
     * @return Instruction as string
     */
    @Override
    protected String getToolInstruction() {
        getScript()
    }

    private Object script
}
