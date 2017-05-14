/*
 * ============================================================================
 * (C) Copyright Schalk W. Cronje 2016 - 2017
 *
 * This software is licensed under the Apache License 2.0
 * See http://www.apache.org/licenses/LICENSE-2.0 for license details
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 * ============================================================================
 */
package org.ysb33r.gradle.olifant

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project
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
     * @param flag Whether exit value should be ignored.
     * @return This object as an instance of {@link org.gradle.process.BaseExecSpec}
     */
    @Override
    BaseExecSpec setIgnoreExitValue(boolean flag) {
        this.ignoreExitValue = flag
        return this
    }

    /** Determine whether the exit value should be ignored.
     *
     * @param flag Whether exit value should be ignored.
     * @return This object as an instance of {@link org.gradle.process.BaseExecSpec}
     */
    BaseExecSpec ignoreExitValue(boolean flag) {
        setIgnoreExitValue(flag)
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

    /** Set the stream where standard input should be read from for this process when executing.
     *
     * @param inputStream Inout stream to use.
     * @return This object as an instance of {@link org.gradle.process.BaseExecSpec}
     */
    @Override
    BaseExecSpec setStandardInput(InputStream inputStream) {
        this.inputStream = inputStream
        return this
    }

    /** Set the stream where standard input should be read from for this process when executing.
     *
     * @param inputStream Inout stream to use.
     * @return This object as an instance of {@link org.gradle.process.BaseExecSpec}
     */
    BaseExecSpec standardInput(InputStream inputStream) {
        setStandardInput(inputStream)
    }


    /** Where input is read from during execution.
     *
     * @return Input stream.
     */
    @Override
    InputStream getStandardInput() {
        this.inputStream
    }

    /** Set the stream where standard output should be sent to for this process when executing.
     *
     * @param outputStream Output stream to use.
     * @return This object as an instance of {@link org.gradle.process.BaseExecSpec}
     */
    @Override
    BaseExecSpec setStandardOutput(OutputStream outputStream) {
        this.outputStream = outputStream
        return this
    }

    /** Set the stream where standard output should be sent to for this process when executing.
     *
     * @param outputStream Output stream to use.
     * @return This object as an instance of {@link org.gradle.process.BaseExecSpec}
     */
    BaseExecSpec standardOutput(OutputStream outputStream) {
        setStandardOutput(outputStream)
    }

    /** Where standard output is sent to during execution.
     *
     * @return Output stream.
     */
    @Override
    OutputStream getStandardOutput() {
        this.outputStream
    }

    /** Set the stream where error output should be sent to for this process when executing.
     *
     * @param outputStream Output stream to use.
     * @return This object as an instance of {@link org.gradle.process.BaseExecSpec}
     */
    @Override
    BaseExecSpec setErrorOutput(OutputStream outputStream) {
        this.errorStream = outputStream
        return this
    }

    /** Set the stream where error output should be sent to for this process when executing.
     *
     * @param outputStream Output stream to use.
     * @return This object as an instance of {@link org.gradle.process.BaseExecSpec}
     */
    BaseExecSpec errorOutput(OutputStream outputStream) {
        setErrorOutput(outputStream)
    }

    /** Where error output is sent to during execution.
     *
     * @return Output stream.
     */
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

    /** Returns the environment ro be used for the process.
     *
     * @return Key-value pairing of environmental variables.
     */
    @Override
    Map<String, Object> getEnvironment() {
        this.env
    }

    /** Set the environment variables to use for the process.
     *
     * @param map Environmental variables as key-value pairs.
     */
    @Override
    void setEnvironment(Map<String, ?> map) {
        this.env.clear()
        this.env.putAll(map)
    }

    /** Add additional environment variables for use with the process.
     *
     * @param map Environmental variables as key-value pairs.
     * @return This object as an instance of {@link org.gradle.process.ProcessForkOptions}
     */
    @Override
    ProcessForkOptions environment(Map<String, ?> map) {
        this.env.putAll(map)
        return this
    }

    /** Add additional environment variable for use with the process.
     *
     * @param envVar Name of environmental variable.
     * @param vluae Value of environmental variable.
     * @return This object as an instance of {@link org.gradle.process.ProcessForkOptions}
     */
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
    ProcessForkOptions executable(Object exe) {
        setExecutable(exe)
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

    /** Configure this spec from an {@link org.gradle.api.Action}
     *
     * @param action Configurating action.
     * @return {@code this}.
     */
    AbstractToolExecSpec configure( Action<? extends AbstractToolExecSpec>  action) {
        action.execute(this)
        return this
    }

    /** Configure this spec from a closure.
     *
     * @param cfg Closure to use.
     * @return
     */
    AbstractToolExecSpec configure(@DelegatesTo(AbstractToolExecSpec) Closure cfg) {
        ClosureUtils.configureItem(this,cfg)
        return this
    }

    /** Replace the tool-specific arguments with a new set.
     *
     * @param args New list of tool-specific arguments
     */
    void setExeArgs(Iterable<?> args) {
        exeArgs.clear()
        exeArgs.addAll(args)
    }

    /** Add more tool-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void exeArgs(Iterable<?> args) {
        exeArgs.addAll(args)
    }

    /** Add more tool-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void exeArgs(Object... args) {
        exeArgs.addAll(args)
    }

    /** Any arguments specific to the tool in use
     *
     * @return Arguments to the tool. Can be empty, but never null.
     */
    List<String> getExeArgs() {
        StringUtils.stringize(this.exeArgs)
    }

    /** Construct class and attach it to specific project.
     *
     * @param project Project this exec spec is attached.
     */
    protected AbstractToolExecSpec(Project project) {
        this.project=project
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
    protected String getToolInstruction() {
        null
    }

    private Project project
    private boolean ignoreExitValue = false
    private InputStream inputStream
    private OutputStream outputStream
    private OutputStream errorStream
    private Object workingDir = '.'
    private Object executable
    private final Map<String,Object> env = [:]
    private final List<Object> exeArgs = []
    private final List<Object> instructionArgs = []
}
