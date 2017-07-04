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
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/** A abstract task type for executing binaries that take a command as well as a set of command arguments.
 *
 * @since 0.3
 */
@CompileStatic
abstract class AbstractCommandExecTask<T extends AbstractCommandExecSpec> extends AbstractExecTask<AbstractCommandExecTask<T>,T> {

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
     * @param cmd Anything that can be resolved via {@link org.ysb33r.gradle.olifant.StringUtils.stringize(Object)}
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

    /** Execution specification customised for the specific tool
     *
     * @return Execution specification cast to the appropriate type.
     */
    protected T getToolExecSpec() {
        (T)(super.execSpec)
    }
}
