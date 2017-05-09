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
    This code was lifted from the Gradle org.gradle.internal.os.OperatingSystem class
    which is nder the Apache v2.0 license. Original copyright from 2010 remains. Modifications
    from 2017+ are under the copyright and licensed mentioned above
*/

package org.ysb33r.gradle.olifant

import groovy.transform.CompileStatic
import org.gradle.api.Nullable
import org.ysb33r.gradle.olifant.internal.os.FreeBSD
import org.ysb33r.gradle.olifant.internal.os.GenericUnix
import org.ysb33r.gradle.olifant.internal.os.Linux
import org.ysb33r.gradle.olifant.internal.os.MacOsX
import org.ysb33r.gradle.olifant.internal.os.NetBSD
import org.ysb33r.gradle.olifant.internal.os.Solaris
import org.ysb33r.gradle.olifant.internal.os.Windows
import java.util.regex.Pattern

/**
 *
 */
@CompileStatic
abstract class OperatingSystem {

    enum Arch {

        X86_64( 'amd64' ),
        X86 ( 'i386' ),
        POWERPC( 'ppc'),
        SPARC( 'sparc'),
        UNKNOWN( '(unknown)' )

        private Arch( final String id ) {
            this.id = id
        }

        private final String id
    }

    /** Check is this is Microsoft Windows
     *
     * @return {@code true} if Windows
     */
    boolean isWindows() { false }

    /** Check is this is Apple Mac OS X
     *
     * @return {@code true} if Mac OS X
     */
    boolean isMacOsX()  { false }

    /** Check is this is a Linux flavour
     *
     * @return {@code true} if any kind of Linux
     */
    boolean isLinux()   { false }

    /** Check is this is FreeBSD
     *
     * @return {@code true} if FreeBSD
     */
    boolean isFreeBSD() { false }

    /** Check is this is NetBSD
     *
     * @return {@code true} if NetBSD
     */
    boolean isNetBSD()  { false }

    /** Check is this is a Solaris flavour
     *
     * @return {@code true} if Solaris
     */
    boolean isSolaris() { false }

    /** Check is this is any kind of Unix-like O/S
     *
     * @return {@code true} if any kind of Unix-like O/S
     */
    boolean isUnix()    { false }

    /** The short name for the current operating system.
     *
     * @return Short name, possibly the same as {@code System.getProperty("os.name")}
     */
    String getName() {
        OS_NAME
    }

    /** The version for the current operating system.
     *
     * @return Short name, possibly the same as {@code System.getProperty("os.version")}
     */
    String getVersion() {
        OS_VERSION
    }

    /** Name of environmental variable that holds the system search path
     *
     * @return Name of variable.
     */
    String getPathVar() {
        'PATH'
    }

    /** The character used to separate elements in a system search path
     *
     * @return OS-specific separator.
     */
    String getPathSeparator() {
        File.pathSeparator
    }

    /** Stringize implementation
     *
     * @return Name, Version and Architecture
     */
    String toString() {
        "${name?:''} ${version?:''} ${arch?:''}"
    }

    /** Locates the given executable in the system path.
     * @param name Name of executable to search for.
     * @return Executable loction of {@code null} if not found.
     */
    @Nullable
    File findInPath(String name) {
        String exeName = getExecutableName(name)
        if (exeName.contains(pathSeparator)) {
            File candidate = new File(exeName)
            if (candidate.isFile()) {
                return candidate
            }
            return null
        }
        for (File dir : getPath()) {
            File candidate = new File(dir, exeName)
            if (candidate.isFile()) {
                return candidate
            }
        }
    }

    /** List of system search paths
     *
     * @return List of entries (can be empty).
     */
    List<File> getPath() {
        List<File> entries= []
        String path = System.getenv(pathVar)
        if (path != null) {
            for (String entry : path.split(Pattern.quote(pathSeparator))) {
                entries.add(new File(entry))
            }
        }
        return entries
    }

    /** Find all files in system search path of a certain name.
     *
      * @param name Name to look for
     * @return List of files
     */
    List<File> findAllInPath(String name) {
        List<File> all = []

        for (File dir : getPath()) {
            File candidate = new File(dir, name)
            if (candidate.isFile()) {
                all.add(candidate)
            }
        }

        return all
    }

    /** Returns OS-specific decorated executable name.
     *
     * @param executablePath Name of executable
     * @return Returns an aproprtely decoarated executable
     */
    abstract String getExecutableName(String executablePath)

    /** Architecture underlying the operating system
     *
     * @return Architecture type. Returns {@code OperatingSystem.Arch.UNKNOWN} is it cannot be identified. In that a
     *   caller might need to use {@link #getArchStr()} to help with identification.
     */
    abstract OperatingSystem.Arch getArch()

    /** Architecture underlying the operating system
     *
     * @return Architecture string
     */
    abstract String getArchStr()

    /** OS-dependent string that is used to suffix to shared libraries
     *
     * @return Shared library suffix
     */
    abstract String getSharedLibrarySuffix()

    /** OS-dependent string that is used to suffix to static libraries
     *
     * @return Static library suffix
     */
    abstract String getStaticLibrarySuffix()

    /** Returns OS-specific decorated script name.
     *
     * @param scriptPath Name of script
     * @return Returns an appropriately decorated script name
     */
    abstract String getScriptName(String scriptPath)

    /** Returns OS-specific shared library name
     *
     * @param libraryName This can be a base name or a full name.
     * @return Shared library name.
     */
    abstract String getSharedLibraryName(String libraryName)

    /** Returns OS-specific static library name
     *
     * @param libraryName This can be a base name or a full name.
     * @return Static library name.
     */
    abstract String getStaticLibraryName(String libraryName)


    protected OperatingSystem() {}

    // tag::check_os[]
    static OperatingSystem current() {
        if (OS_NAME.contains("windows")) {
            return Windows.INSTANCE
        } else if (OS_NAME.contains("mac os x") || OS_NAME.contains("darwin") || OS_NAME.contains("osx")) {
            return MacOsX.INSTANCE
        } else if (OS_NAME.contains("linux")) {
            return Linux.INSTANCE
        } else if (OS_NAME.contains("freebsd")) {
            return FreeBSD.INSTANCE
        } else if (OS_NAME.contains("sunos") || OS_NAME.contains("solaris")) {
            return Solaris.INSTANCE
        } else if (OS_NAME.contains("netbsd")) {
            return NetBSD.INSTANCE
        } else {
            // Not strictly true, but a good guess
            return GenericUnix.INSTANCE
        }
    }
    // end::check_os[]

    protected static final String OS_NAME = System.getProperty('os.name').toLowerCase()
    protected static final String OS_ARCH = System.getProperty('os.arch').toLowerCase()
    protected static final String OS_VERSION = System.getProperty('os.version').toLowerCase()

}
