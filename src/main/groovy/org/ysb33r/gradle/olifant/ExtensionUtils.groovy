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
import org.ysb33r.gradle.olifant.exec.AbstractToolExecSpec
import org.ysb33r.gradle.olifant.exec.ExecSpecInstantiator
import org.ysb33r.gradle.olifant.internal.execspec.ExecProjectExtension

/** Utilities to deal with Gradle extensions and project extensions
 *
 * @since 0.3
 */
@CompileStatic
class ExtensionUtils {

    /** Adds a project extension so that specific tools can be execute in a similar manner to {@link }.
     *
     * @param name Name of extension.
     * @param project Project to attach to.
     * @param instantiator Instantiator to use to create new execution specifications.
     */
    static void addExecProjectExtension(final String name, Project project, ExecSpecInstantiator<? extends AbstractToolExecSpec> instantiator) {
//        project.extensions.add(name, new ExecProjectExtension(project,instantiator))
//        project.extensions.extraProperties.set(name, new ExecProjectExtension(project,instantiator))
        final ExecProjectExtension delegator = new ExecProjectExtension(project,instantiator)
        project.extensions.extraProperties.set(name, { def cfg ->
            switch(cfg) {
                case Closure:
                    delegator.call ((Closure)cfg)
                    break
                case Action:
                    delegator.call ((Action)cfg)
                    break
                default:
                    throw new GradleException('Invalid type passed. Use closure or actions.')
            }
        })
    }
}
