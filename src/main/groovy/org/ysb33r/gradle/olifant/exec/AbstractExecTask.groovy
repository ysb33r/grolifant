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
package org.ysb33r.gradle.olifant.exec

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec

/** A base class to use for developing execution classes for wrapping tools
 *
 * @since 0.3
 */
@CompileStatic
abstract class AbstractExecTask<B extends AbstractExecTask, T extends AbstractToolExecSpec> extends DefaultTask {

    /** Determine whether the exit value should be ignored.
     *
     * @param flag Whether exit value should be ignored.
     * @return {@code this}.
     */
    B setIgnoreExitValue(boolean flag) {
        execSpec.setIgnoreExitValue(flag)
        return (B)this
    }

    /** Determine whether the exit value should be ignored.
     *
     * @param flag Whether exit value should be ignored.
     * @return {@code this}.
     */
    B ignoreExitValue(boolean flag) {
        setIgnoreExitValue(flag)
        return (B)this
    }

    /** State of exit value monitoring.
     *
     * @return Whether return value shoul dbe ignored.
     */
    @Input
    boolean isIgnoreExitValue() {
        execSpec.isIgnoreExitValue()
    }

    /** Set the stream where standard input should be read from for this process when executing.
     *
     * @param inputStream Inout stream to use.
     * @return {@code this}.
     */
    B setStandardInput(InputStream inputStream) {
        execSpec.setStandardInput(inputStream)
        return (B)this
    }

    /** Set the stream where standard input should be read from for this process when executing.
     *
     * @param inputStream Inout stream to use.
     * @return {@code this}.
     */
    B standardInput(InputStream inputStream) {
        setStandardInput(inputStream)
    }


    /** Where input is read from during execution.
     *
     * @return Input stream.
     */
    @Internal
    InputStream getStandardInput() {
        execSpec.getStandardInput()
    }

    /** Set the stream where standard output should be sent to for this process when executing.
     *
     * @param outputStream Output stream to use.
     * @return {@code this}.
     */
    B setStandardOutput(OutputStream outputStream) {
        execSpec.setStandardOutput(outputStream)
        return (B)this
    }

    /** Set the stream where standard output should be sent to for this process when executing.
     *
     * @param outputStream Output stream to use.
     * @return {@code this}.
     */
    B standardOutput(OutputStream outputStream) {
        setStandardOutput(outputStream)
    }

    /** Where standard output is sent to during execution.
     *
     * @return Output stream.
     */
    @Internal
    OutputStream getStandardOutput() {
        execSpec.getStandardOutput()
    }

    /** Set the stream where error output should be sent to for this process when executing.
     *
     * @param outputStream Output stream to use.
     * @return {@code this}.
     */
    B setErrorOutput(OutputStream outputStream) {
        execSpec.setErrorOutput(outputStream)
        return (B)this
    }

    /** Set the stream where error output should be sent to for this process when executing.
     *
     * @param outputStream Output stream to use.
     * @return {@code this}.
     */
    B errorOutput(OutputStream outputStream) {
        setErrorOutput(outputStream)
    }

    /** Where error output is sent to during execution.
     *
     * @return Output stream.
     */
    @Internal
    OutputStream getErrorOutput() {
        execSpec.getErrorOutput()
    }

    /** Obtain the working directory for this process.
     *
     * This call will evaluate the lazily-set working directory for {@link #setWorkingDir}0
     * @return A {@code java.io.File} object.
     */
    @Internal
    File getWorkingDir() {
        execSpec.getWorkingDir()
    }

    /** Set the working directory for the execution.
     *
     * @param workDir Any object that is convertiable using Gradle's {@code project.file}.
     */
    void setWorkingDir(Object workDir) {
        execSpec.setWorkingDir(workDir)
    }

    /** Set the working directory for the execution.
     *
     * @param workDir Any object that is convertible using Gradle's {@code project.file}.
     * @return This object as {@link org.gradle.process.ProcessForkOptions}
     */
    B workingDir(Object workDir) {
        this.workingDir = workDir
        return (B)this
    }

    /** Returns the environment to be used for the process. Defaults to the environment of this process.
     *
     * @return Key-value pairing of environmental variables.
     */
    @Internal
    Map<String, Object> getEnvironment() {
        execSpec.getEnvironment()
    }

    /** Set the environment variables to use for the process.
     *
     * @param map Environmental variables as key-value pairs.
     */
    void setEnvironment(Map<String, ?> map) {
        execSpec.setEnvironment(map)
    }

    /** Add additional environment variables for use with the process.
     *
     * @param map Environmental variables as key-value pairs.
     * @return {@code this}.
     */
    B environment(Map<String, ?> map) {
        execSpec.environment(map)
        return (B)this
    }

    /** Add additional environment variable for use with the process.
     *
     * @param envVar Name of environmental variable.
     * @param value Value of environmental variable.
     * @return {@code this}.
     */
    B environment(String envVar, Object value) {
        execSpec.environment(envVar,value)
        return (B)this
    }

    /** The executable used in this specification as a String.
     *
     * @return Executable name if set (else null).
     */
    @Optional
    @Input
    String getExecutable() {
        execSpec.getExecutable()
    }

    /** Returns the full script line, including the executable, it's specific arguments, tool specific instruction and
     * the arguments spefic to the instruction.
     *
     * @return Command-line as a list of items
     */
    @Internal
    List<String> getCommandLine() {
        execSpec.getCommandLine()
    }

    /** Replace the tool-specific arguments with a new set.
     *
     * @param args New list of tool-specific arguments
     */
    void setExeArgs(Iterable<?> args) {
        execSpec.setExeArgs(args)
    }

    /** Add more tool-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void exeArgs(Iterable<?> args) {
        execSpec.exeArgs(args)
    }

    /** Add more tool-specific arguments.
     *
     * @param args Additional list of arguments
     */
    void exeArgs(Object... args) {
        execSpec.exeArgs(args)
    }

    /** Any arguments specific to the tool in use
     *
     * @return Arguments to the tool. Can be empty, but never null.
     */
    @Optional
    @Input
    List<String> getExeArgs() {
        execSpec.getExeArgs()
    }

    /** Returns the result for the execution, that was run by this task.
     *
     * @return The result of the execution. Returns null if this task has not been executed yet.
     */
    @Internal
    ExecResult getExecResult() {
        execResult
    }

    /** Runs this process against an internal execution specification. If a failure occurs and
     * {@link #isIgnoreExitValue} is not set an exception will be raised.
     */
    @TaskAction
    void exec() {
        Closure runner = { T fromSpec, ExecSpec toSpec ->
            fromSpec.copyToExecSpec(toSpec)
        }
        execResult = project.exec runner.curry(this.execSpec)
    }

    /** Creates class and sets default environment to be that of Gradle,
     *
     */
    protected AbstractExecTask() {
        super()
        this.execSpec = createExecSpec(project)
        setEnvironment( System.getenv() )
    }

    /** Sets the executable to use for this task
     *
     * @param exe Anything resolvable via {@link org.ysb33r.gradle.olifant.StringUtils.stringize(java.lang.Object}
     */
    protected void setToolExecutable(Object exe) {
        execSpec.executable(exe)
    }

    /** Provides access to the execution specification that is associated with this task
     *
     * @return Execution specification
     */
    protected T getExecSpec() {
        this.execSpec
    }

    /** Factory method for creating an execution specification
     *
     * @param project Project that the execution speciofication should be associated to.
     * @return Execution Specification
     */
    protected abstract T createExecSpec(Project project)

    private final T execSpec
    private ExecResult execResult
}
