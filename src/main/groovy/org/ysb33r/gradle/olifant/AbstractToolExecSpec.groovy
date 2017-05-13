package org.ysb33r.gradle.olifant

import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.gradle.process.BaseExecSpec
import org.gradle.process.ProcessForkOptions

/** A base class to aid plugin developers create their own {@link org.gradle.process.ExecSpec} implementations.
 *
 * @since 0.3
 */
@CompileStatic
abstract class AbstractToolExecSpec implements BaseExecSpec {

    /** Determine whether the exit value should be ignored.
     *
     * @param b
     * @return
     */
    @Override
    BaseExecSpec setIgnoreExitValue(boolean b) {
        this.ignoreExitValue = b
        return this
    }

    /** State of exit value monitoring.
     *
     * @return Whether return value shoul dbe ignored.
     */
    @Override
    boolean isIgnoreExitValue() {
        this.ignoreExitValue
    }

    @Override
    BaseExecSpec setStandardInput(InputStream inputStream) {
        this.inputStream = inputStream
        return this
    }

    @Override
    InputStream getStandardInput() {
        this.inputStream
    }

    @Override
    BaseExecSpec setStandardOutput(OutputStream outputStream) {
        this.outputStream = outputStream
        return this
    }

    @Override
    OutputStream getStandardOutput() {
        this.outputStream
    }

    @Override
    BaseExecSpec setErrorOutput(OutputStream outputStream) {
        this.errorStream = outputStream
        return this
    }

    @Override
    OutputStream getErrorOutput() {
        this.errorStream
    }

    /** Obtain the working directory for this process.
     *
     * This call will evaluate the lazily-set working directory for {@link #setWorkingDir}0
     * @return A {@code java.io.File} object.
     */
    @Override
    File getWorkingDir() {
        project.file(this.workingDir)
    }

    /** Set the working directory for the execution.
     *
     * @param workDir Any object that is convertiable using Gradle's {@code project.file}.
     */
    @Override
    void setWorkingDir(Object workDir) {
        this.workingDir = workDir
    }

    /** Set the working directory for the execution.
     *
     * @param workDir Any object that is convertible using Gradle's {@code project.file}.
     * @return This object as {@link org.gradle.process.ProcessForkOptions}
     */
    @Override
    ProcessForkOptions workingDir(Object workDir) {
        this.workingDir = workDir
        return this
    }

    @Override
    Map<String, Object> getEnvironment() {
        this.env
    }

    @Override
    void setEnvironment(Map<String, ?> map) {
        this.env.clear()
        this.env.putAll(map)
    }

    @Override
    ProcessForkOptions environment(Map<String, ?> map) {
        this.env.putAll(map)
        return this
    }

    @Override
    ProcessForkOptions environment(String envVar, Object value) {
        this.env.put(envVar,value)
        return this
    }

    /** Copies options from this Spec to the given target.
     *
     * If the target is not an instance of {@link AbstractToolExecSpec} and
     * the executable is of type {@link ResolvedExecutable} then it will be processed before
     * copying.
     *
     * @param processForkOptions Copy to this target.
     * @return This object as a {@link org.gradle.process.ProcessForkOptions}
     */
    @Override
    ProcessForkOptions copyTo(ProcessForkOptions processForkOptions) {
        processForkOptions.setEnvironment(this.env)
        processForkOptions.setWorkingDir(this.workingDir)

        if( !(processForkOptions instanceof AbstractToolExecSpec) && this.executable instanceof ResolvedExecutable) {
            processForkOptions.setExecutable( ((ResolvedExecutable)(this.executable)).getExecutable() )
        } else {
            processForkOptions.setExecutable(this.executable)
        }
        return this
    }

    /** The executable used in this specification as a String.
     *
     * @return Executable name if set (else null).
     */
    @Override
    String getExecutable() {
        if(this.executable == null) {
            null
        }
        else if(this.executable instanceof ResolvedExecutable) {
            ((ResolvedExecutable)(this.executable)).getExecutable().toString()
        } else {
            StringUtils.stringize(this.executable)
        }
    }

    /** Set the executable to use.
     *
     * @param exe Anything that can be resolved via {@link StringUtils.stringize(Object)} or an implementaton of
     *   {@link ResolvedExecutable}
     */
    @Override
    void setExecutable(Object exe) {
        this.executable = exe
    }

    /** Set the executable to use.
     *
     * @param exe Anything that can be resolved via {@link StringUtils.stringize(Object)} or an implementaton of
     *   {@link ResolvedExecutable}
     * @return This object as an instance of {@link org,gradle.process.ProcessForkOptions}
     */
    @Override
    ProcessForkOptions executable(Object o) {
        this.executable = executable
        return this
    }

    /** Returns the full script line, including the executable, it's specific arguments, tool specific instruction and
     * the arguments spefic to the instruction.
     *
     * @return Command-line as a list of items
     */
    @Override
    List<String> getCommandLine() {
        buildCommandLine()
    }

    protected AbstractToolExecSpec() {
    }

    /** Builds up the script-line.
     *
     * @return
     * @throw {@code GradleException} is null.
     */
    protected List<String> buildCommandLine() {
        List<String> parts = []

        String exe = getExecutable()

        if(exe == null) {
            throw new GradleException( '''The 'executable' part cannot be null.''')
        }

        parts.add getExecutable()
        parts.addAll getExeArgs()

        String instruction = getToolInstruction()

        if(instruction != null && !instruction.empty) {
            parts.add instruction
            parts.addAll getInstructionsArgs()
        }

        return parts
    }

    /** Replace the tool-specific arguments with a new set.
     *
     * @param args New list of tool-specific arguments
     */
    protected void setExeArgs(Iterable<?> args) {
        exeArgs.clear()
        exeArgs.addAll(args)
    }

    /** Add more tool-specific arguments.
     *
     * @param args Additional list of arguments
     */
    protected void exeArgs(Iterable<?> args) {
        exeArgs.addAll(args)
    }

    /** Add more tool-specific arguments.
     *
     * @param args Additional list of arguments
     */
    protected void exeArgs(Object... args) {
        exeArgs.addAll(args)
    }

    /** Any arguments specific to the tool in use
     *
     * @return Arguments to the tool. Can be empty, but never null.
     */
    protected  List<String> getExeArgs() {
        StringUtils.stringize(this.exeArgs)
    }

    /** Replace the instruction-specific arguments with a new set.
     *
     * @param args New list of instruction-specific arguments
     */
    protected void setInstructionArgs(Iterable<?> args) {
        instructionArgs.clear()
        instructionArgs.addAll(args)
    }

    /** Add more instruction-specific arguments.
     *
     * @param args Additional list of arguments
     */
    protected void instructionArgs(Iterable<?> args) {
        instructionArgs.addAll(args)
    }

    /** Add more instruction-specific arguments.
     *
     * @param args Additional list of arguments
     */
    protected void instructionArgs(Object... args) {
        instructionArgs.addAll(args)
    }

    /** List of arguments sepcific to the tool instruction.
     *
     * @return List of arguments. Can be empty, but not null.
     */
    protected List<String> getInstructionsArgs() {
        StringUtils.stringize(this.instructionArgs)
    }

    /** A specific instruction passed to a tool.
     *
     * * Instruction can be empty or null, which means that by default implementation {@link #getToolInstructionArgs()} will be ignored.
     *
     * @return Instruction as string
     */
    protected abstract String getToolInstruction()

    private boolean ignoreExitValue = false
    private InputStream inputStream
    private OutputStream outputStream
    private OutputStream errorStream
    private Object workingDir = '.'
    private Object executable
    private Map<String,Object> env = [:]
    private List<Object> exeArgs = []
    private List<Object> instructionArgs = []
}
