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
/*
    This code is based upon code from the Gradle org.gradle.internal.os.OperatingSystem class
    which is nder the Apache v2.0 license. Original copyright from 2010 remains. Modifications
    from 2017+ are under the copyright and licensed mentioned above
*/
package org.ysb33r.gradle.olifant.internal.os

import groovy.transform.CompileStatic
import org.ysb33r.gradle.olifant.OperatingSystem

/** Windows implementation of {@code OperatingSystem}.
 *
 */
@CompileStatic
class Windows extends OperatingSystem {
    static final OperatingSystem INSTANCE = new Windows()

    /** Confirms this is a representation of the Microsoft Windows operating system.
     *
     * @return {@code true}
     */
    @Override
    boolean isWindows() {true}

    @Override
    String getExecutableName(String executablePath) {
        withSuffix(executablePath, '.exe')
    }

    /** Returns Windows-specific decorated script name.
     *
     * @param scriptPath Name of script.
     * @return Returns a {@code .bat} based name.
     */
    @Override
    String getScriptName(String scriptPath) {
        withSuffix(scriptPath, '.bat')
    }

    /** Returns Windows shared library name
     *
     * @param libraryName This can be a base name or a full name.
     * @return Shared library name with {@code .dll} extension
     */
    @Override
    String getSharedLibraryName(String libraryName) {
        withSuffix(libraryName, sharedLibrarySuffix)
    }

    /** Returns OS-specific static library name
     *
     * @param libraryName This can be a base name or a full name.
     * @return Static library name.
     */
    @Override
    String getStaticLibraryName(String libraryName) {
        withSuffix(libraryName, staticLibrarySuffix)
    }

    /** Returns Windows system seach path environmental variable name.
     *
     * @return {@code Path}.
     */
    @Override
    String getPathVar() {
        return "Path";
    }

    /** Return Windows string that is used to suffix to shared libraries
     *
     * @return {@code .dll}
     */
    @Override
    String getSharedLibrarySuffix() {
        '.dll'
    }

    /** Windows string that is used to suffix to static libraries
     *
     * @return {@code .lib}
     */
    @Override
    String getStaticLibrarySuffix() {
        '.lib'
    }


    /** Architecture underlying the operating system
     *
     * @return Architecture type. Returns {@code OperatingSystem.Arch.UNKNOWN} is it cannot be identified. In that a
     *   caller might need to use {@link #getArchStr()} to help with identification.
     */
    @Override
    Arch getArch() {
        switch(getArchStr()) {
            case 'amd64':
                return Arch.X86_64
            case 'i386':
                return Arch.X86
            default:
                return Arch.UNKNOWN
        }
    }


    /** Architecture underlying the operating system
     *
     * @return {@code amd64} or {@code i386}
     */
    @Override
    String getArchStr() {
        if(OS_ARCH == 'x86_64' || OS_ARCH == 'amd64') {
            return 'amd64'
        }

        if (OS_ARCH == 'x86') {
            return 'i386'
        }

        'unknown'
    }

    protected Windows() {
        super()
    }

    private String withSuffix(final String executablePath, final String extension) {
        executablePath.toLowerCase().endsWith(extension) ? executablePath :"${removeExtension(executablePath)}${extension}"
    }

    private String removeExtension(final String executablePath) {
        int fileNameStart = Math.max(executablePath.lastIndexOf('/'), executablePath.lastIndexOf('\\'))
        int extensionPos = executablePath.lastIndexOf('.')

        (extensionPos > fileNameStart) ? executablePath.substring(0, extensionPos) : executablePath
    }

}
