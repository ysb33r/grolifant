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

/** Generic Unix-like implementation of {@code OperatingSystem}.
 *
 * Also serves as a base class for specific Unix-like implementations.
 */
@CompileStatic
class GenericUnix extends OperatingSystem {
    static final OperatingSystem INSTANCE = new GenericUnix()

    @Override
    boolean isUnix() { true }

    @Override
    String getExecutableName(final String executablePath) {
        executablePath
    }

    /** Returns OS-specific decorated script name.
     *
     * @param scriptPath Name of script
     * @return Returns an appropriately decorated script name
     */
    @Override
    String getScriptName(String scriptPath) {
        scriptPath
    }

    /** Returns OS-specific shared library name
     *
     * @param libraryName This can be a base name or a full name.
     * @return Shared library name.
     */
    @Override
    String getSharedLibraryName(String libraryName) {
        getLibraryName(libraryName, getSharedLibrarySuffix())
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
            case 'x86_64':
                return Arch.X86_64
            case 'i386':
            case 'x86':
                return Arch.X86
            case 'ppc':
            case 'powerpc':
                return Arch.POWERPC
            case 'sparc':
                return Arch.SPARC
            default:
                return Arch.UNKNOWN
        }
    }

    @Override
    String getArchStr() {
        OS_ARCH
    }

    /** Return Unix-like string that is used to suffix to shared libraries
     *
     * @return {@code .so}
     */
    @Override
    String getSharedLibrarySuffix() {
        '.so'
    }

    /** Default Unix-like string that is used to suffix to static libraries
     *
     * @return {@code .a}
     */
    @Override
    String getStaticLibrarySuffix() {
        '.a'
    }

    /** Returns OS-specific static library name
     *
     * @param libraryName This can be a base name or a full name.
     * @return Static library name.
     */
    @Override
    String getStaticLibraryName(String libraryName) {
        getLibraryName(libraryName, staticLibrarySuffix)
    }

    protected GenericUnix() {
        super()
    }

    private String getLibraryName(String libraryName, String suffix) {
        if (libraryName.endsWith(suffix)) {
            return libraryName
        }

        int pos = libraryName.lastIndexOf('/')
        if (pos >= 0) {
            return "${libraryName.substring(0, pos + 1)}lib${libraryName.substring(pos + 1)}${suffix}"
        } else {
            return "lib${libraryName}${suffix}"
        }
    }

}
