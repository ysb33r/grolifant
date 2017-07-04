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
     * @param cmd Anything that can be resolved via {@link org.ysb33r.gradle.olifant.StringUtils.stringize(Object)}
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
