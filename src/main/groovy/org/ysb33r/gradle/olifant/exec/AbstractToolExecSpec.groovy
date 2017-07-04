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
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.process.ExecSpec
import org.gradle.process.ProcessForkOptions
import org.ysb33r.gradle.olifant.ClosureUtils
import org.ysb33r.gradle.olifant.StringUtils
import org.ysb33r.gradle.olifant.internal.execspec.ResolveExecutableFromPath
import org.ysb33r.gradle.olifant.internal.execspec.ResolveExecutableInSearchPath

/** A base class to aid plugin developers create their own {@link org.gradle.process.ExecSpec} implementations.
 *
 * @since 0.3
 */
@CompileStatic
abstract class AbstractToolExecSpec extends AbstractExecSpec {

    /** The executable used in this specification as a String.
     *
     * @return Executable name if set (else null).
     */
    @Override
    String getExecutable() {
        if(this.executable == null) {
            null
        }
        else {
            this.executable.getExecutable().toString()
        }
    }

    /** Set the executable to use.
     *
     * If you need to search the system path, to find the executable use the {@code search : 'exeName'} form instead.
     *
     * @param exe Anything that can be resolved via {@link org.ysb33r.gradle.olifant.StringUtils.stringize(Object)} or an implementation of
     *   {@link ResolvedExecutable}
     */
    @Override
    void setExecutable(Object exe) {
        setExecutable ([path : exe])
    }

    /** Set the executable to use.
     *
     * @param exe An implementation of {@link ResolvedExecutable}
     */
    void setExecutable(ResolvedExecutable resolver) {
        this.executable = resolver
    }

    /** Set the executable to use.
     *
     * <p> This variant of the method has been introduced to cope with the API change in Gradle 4.0.
     *
     * @param exe Executable as String representation
     */
    void setExecutable(String exe) {
        setExecutable ([path : (Object)exe])
    }

    /** Set the executable to use.
     *
     * If you need to search the system path, to find the executable use the {@code search : 'exeName'} form instead.
     *
     * @param exe Anything that can be resolved via {@link org.ysb33r.gradle.olifant.StringUtils.stringize(Object)} or an implementation of
     *   {@link ResolvedExecutable}
     * @return This object as an instance of {@link org,gradle.process.ProcessForkOptions}
     */
    @Override
    ProcessForkOptions executable(Object exe) {
        setExecutable(exe)
        return this
    }

    /** Set the executable to use.
     *
     * @param exe An implementation of {@link ResolvedExecutable}
     * @return This object as an instance of {@link org,gradle.process.ProcessForkOptions}
     */
    ProcessForkOptions executable(ResolvedExecutable resolver) {
        this.executable = resolver
        return this
    }


    /** Use a key-value approach to setting the executable.
     *
     * In the default implementation only {@code path} and {@code search} are supported as a declarative keys. Implementations should use
     *   {@link #registerExecutableKeyAction} to add more keys.
     *
     * @param exe Key-value setting executable (with optional extra keys)
     */
    void setExecutable(Map<String,Object> exe) {
        String exeKey = findValidKey(exe)
        Map<String,Object> options = [:]
        options.putAll(exe)
        options.remove(exeKey)
        this.executable = executableKeyActions[exeKey].build(options,exe[exeKey])
    }

    /** Use a key-value approach to setting the executable.
     *
     * In the default implementation only {@code path} and {@code search} are supported as a declarative keys. Implementations should use
     *   {@link #registerExecutableKeyAction} to add more keys.
     *
     * @param exe Key-value setting executable (with optional extra keys)
     */
    ProcessForkOptions executable(Map<String,Object> exe) {
        setExecutable(exe)
        return (ProcessForkOptions)this
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

    /** Copies settings from this execution specification to a standard {@link org.gradle.process.ExecSpec}
     *
     * This method is intended to be called as late as possible by a project extension or a task which would want to delegate to
     * {@ project.exec} project extension. It will cause arguments to be evaluated.
     * The only items not immediately evaluated are {@code workingDir} and {@code executable}.
     *
     * @param execSpec Exec spec to configure.
     */
    void copyToExecSpec(ExecSpec execSpec ) {
        copyTo(execSpec)

        execSpec.errorOutput = getErrorOutput()
        execSpec.standardOutput = getStandardOutput()
        execSpec.standardInput = getStandardInput()
        execSpec.ignoreExitValue = isIgnoreExitValue()

        execSpec.setArgs(buildCommandLine().drop(1))
    }

    /** Construct class and attach it to specific project.
     *
     * @param project Project this exec spec is attached.
     */
    protected AbstractToolExecSpec(Project project) {
        super(project)

        executableKeyActions = [
            'path' : new ResolveExecutableFromPath(this.project),
            'search' : ResolveExecutableInSearchPath.INSTANCE
        ] as Map< String, ResolvedExecutableFactory >
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

        parts.add exe
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

    /** List of arguments specific to the tool instruction.
     *
     * @return List of arguments. Can be empty, but not null.
     */
    protected List<String> getInstructionsArgs() {
        StringUtils.stringize(this.instructionArgs)
    }

    /** A specific instruction passed to a tool.
     *
     * * Instruction can be empty or null. In the default implementation {@link #getToolInstructionArgs()} will be ignored.
     *
     * @return Instruction as string
     */
    protected String getToolInstruction() {
        null
    }

    /** Add additional keys for methods to resolve executables.
     *
     * If the key exists, it will be replaced.
     *
     * @param key Key Key to be used as an option looking for executables.
     * @param factory How to resolve an executable.
     */
    protected void registerExecutableKeyActions( final String key, final ResolvedExecutableFactory factory) {
        executableKeyActions.put(key,factory)
    }

    /** Look for excactly one valid key in the supplied map.
     *
     * @param exe List of keys to search.
     * @return The valid key
     * @throw GradleException if no keys are valid, or more than one key is valid.
     */
    private String findValidKey(Map<String,Object> exe) {
        Set<String> validKeys = executableKeyActions.keySet()
        Set<String> candidateKeys = exe.keySet()

        Set<String> found = candidateKeys.findAll { String candidate ->
            validKeys.find { String validKey ->
                candidate == validKey
            }
        } as Set<String>
        if(found.empty) {
            throw new GradleException("No valid keys found in ${candidateKeys}")
        }
        if(found.size() > 1) {
            throw new GradleException("More than one key found: ${found}")
        }
        found[0]
    }

    private ResolvedExecutable executable
    private final List<Object> exeArgs = []
    private final List<Object> instructionArgs = []
    private final Map< String, ResolvedExecutableFactory > executableKeyActions
}
