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

import org.gradle.process.BaseExecSpec
import org.gradle.process.ProcessForkOptions

/**
 *
 * @since 0.1
 */
abstract class AbstractExecSpec implements BaseExecSpec {

    /** Determine whether the exit value should be ignored.
     *
     * @param flag Whether exit value should be ignored.
     * @return This object as an instance of {@link org.gradle.process.BaseExecSpec}
     */
    private boolean ignoreExitValue = false
    private InputStream inputStream = System.in
    private OutputStream outputStream = System.out
    private OutputStream errorStream = System.err
    protected Object workingDir = '.'
    protected final Map<String,Object> env = [:]

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
    /** Returns the environment to be used for the process.
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
     * @param value Value of environmental variable.
     * @return This object as an instance of {@link org.gradle.process.ProcessForkOptions}
     */
    @Override
    ProcessForkOptions environment(String envVar, Object value) {
        this.env.put(envVar,value)
        return this
    }
}
