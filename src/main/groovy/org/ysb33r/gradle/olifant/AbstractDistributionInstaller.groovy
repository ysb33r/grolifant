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

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.FileTree
import org.gradle.api.logging.LogLevel
import org.gradle.process.ExecResult
import org.gradle.wrapper.Download
import org.gradle.wrapper.ExclusiveFileAccessManager
import org.gradle.wrapper.IDownload
import org.gradle.wrapper.PathAssembler
import org.gradle.wrapper.WrapperConfiguration
import org.tukaani.xz.XZInputStream
import org.ysb33r.gradle.olifant.errors.DistributionFailedException
import org.ysb33r.gradle.olifant.internal.LegacyLevel
import org.ysb33r.gradle.olifant.internal.msi.LessMSIUnpacker

import java.security.MessageDigest
import java.util.concurrent.Callable

/** Common functionality to be able to download a SDK and use it within Gradle
 *
 */
@CompileStatic
abstract class AbstractDistributionInstaller {

    static final String INSTALLER_VERSION = '1.0'
    static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase(Locale.US).contains('windows')

    /** Creates a download URI from a given distribution version
     *
     * @param version Version of the distribution to download
     * @return
     */
    abstract URI uriFromVersion(final String version)

    /** Set candidate name for SdkMan if the latter should be searched for installed versions
     *
     * @param sdkCandidateName SDK Candidate name. This is the same names that will be shown when
     *   running {@code sdk list candidates} on the script-line.
     */
    void setSdkManCandidateName(final String sdkCandidateName) {
        this.sdkManCandidateName = sdkCandidateName
    }

    /** Add patterns for files to be marked executable,
     *
     * Calling this method multiple times simply appends for patterns
     * @param relPaths One or more ANT-stype include patterns
     */
    void addExecPattern(String... relPaths) {
        this.execPatterns.addAll(relPaths as List)
    }

    /** Set a checksum that needs to be verified against downloaded archive
     *
     * @param cs SHA-256 Hex-encoded checksum
     */
    void setChecksum(final String cs) {
        if(cs.length() != 64 || !(cs ==~ /[\p{Digit}\p{Alpha}]{64}/) ) {
            throw new IllegalArgumentException("Not a valid SHA-256 checksum")
        }
        this.checksum = cs.toLowerCase()
    }

    /** Returns the location which is the top or home folder for a distribution.
     *
     *  This value is affected by {@link #setDownloadRoot(java.io.File)} and
     *  the parameters passed in during construction time.
     *
     * @return Location of the distribution.
     */
    File getDistributionRoot() {

        // tag::download_logic[]
        File location = locateDistributionInCustomLocation(distributionVersion) // <1>

        if(location == null && this.sdkManCandidateName)  { // <2>
            location = getDistFromSdkMan()
        }

        location ?: getDistFromCache() // <3>
        // end::download_logic[]
    }

    /** Override this method to provide alternative means to look for distributions.
     *
     * @version Version of distribution to locate
     *
     * @return Location of distribution or null if none were found.
     */
    File locateDistributionInCustomLocation(final String version) {
        null
    }

    /** Sets a download root directory for the distribution.
     *
     * If not supplied the default is to use the Gradle User Home.
     * This method is provided for convenience and is mostly only used for testing
     * purposes.
     *
     * The fodler will be created at download time if it does not exist.
     *
     * @param downloadRootDir Any writeable directory on the filesystem.
     */
    void setDownloadRoot(File downloadRootDir) {
        this.downloadRoot = downloadRootDir
    }

    /** Returns the logger currently in use.
     *
     * @return Wrapper logger instance
     */
    protected ProgressLogger getLogger() {
        this.logger
    }

    /** Creates setup for installing to a local cache.
     *
     * @param distributionName Descriptive name of the distribution
     * @param distributionVersion Version of the distribution to obtain
     * @param basePath Relative path below Gradle User Home to create cache for all version of this distribution type.
     * @param project Gradle project that this downloader is attached to.
     */
    protected AbstractDistributionInstaller(
        final String distributionName,
        final String distributionVersion,
        final String basePath,
        final Project project
    ) {
        this.distributionName = distributionName
        this.distributionVersion = distributionVersion
        this.project = project
        this.basePath = basePath

        List<?> bootstrap = AbstractDistributionInstaller.createDownloader(distributionName,project)
        this.logger = (ProgressLogger)(bootstrap[0])
        this.downloader = (IDownload)(bootstrap[1])
    }

    /** Validates that the unpacked distribution is good.
     *
     * The default implementation simply checks that only one directory should exist.
     *
     * @param distDir Directory where distribution was unpacked to.
     * @param distributionDescription A descriptive name of the distribution
     * @return The directory where the real distribution now exists. In the default implementation it will be
     *   the single directory that exists below {@code distDir}.
     *
     * @throw {@link org.ysb33r.gradle.olifant.errors.DistributionFailedException} if distribution failed to meet criteria.
     */
    protected File getAndVerifyDistributionRoot(final File distDir, final String distributionDescription) {
        List<File> dirs = listDirs(distDir)
        if (dirs.isEmpty()) {
            throw new DistributionFailedException("${distributionName} '${distributionDescription}' does not contain any directories. Expected to find exactly 1 directory.")
        }
        if (dirs.size() != 1) {
            throw new DistributionFailedException("${distributionName} '${distributionDescription} contains too many directories. Expected to find exactly 1 directory.")
        }
        return dirs[0]
    }

    /** Verifies the checksum (if provided) of a newly downloaded distribution archive.
     *
     * @param sourceUrl The URL/URI where it was downloaded from
     * @param localCompressedFile The location of the downloaded archive
     * @param expectedSum The expected checksum. Can be null in which case no checks will be performed.
     *
     * @throw {@link org.ysb33r.gradle.olifant.ChecksumFailedException} if the checksum did not match
     */
    protected void verifyDownloadChecksum(final String sourceUrl, final File localCompressedFile, final String expectedSum) {
        if (expectedSum != null) {
            String actualSum = calculateSha256Sum(localCompressedFile)
            if (!this.checksum.equals(actualSum)) {
                localCompressedFile.delete()
                throw new ChecksumFailedException(distributionName,sourceUrl,localCompressedFile,expectedSum,actualSum)
            }
        }
    }

    /** Provides a list of directories below an unpacked distribution
     *
     * @param distDir Unpacked distribution directory
     * @return List of directories. Can be empty is nothing was unpacked or only files exist within the
     *   supplied directory.
     */
    protected List<File> listDirs(File distDir) {
        if(distDir.exists()) {
            distDir.listFiles(new FileFilter() {
                @Override
                boolean accept(File pathname) {
                    pathname.isDirectory()
                }
            }) as List<File>
        } else {
            []
        }
    }

    /** Unpacks a downloaded archive.
     *
     * <p> The default implementation supports the following formats:
     *
     * <ul>
     *   <li>zip</li>
     *   <li>tar</li>
     *   <li>tar.gz & tgz</li>
     *   <li>tar.bz2 & tbz</li>
     *   <li>tar.xz</li>
     * </ul>
     *
     * <p> If you need MSI support you need to override this method and call out to the
     * provided {@link #unpackMSI} method yourself.
     *
     * @param srcArchive The location of the download archive
     * @param destDir The directory where the archive needs to be unpacked into
     */
    @CompileDynamic
    protected void unpack(final File srcArchive, final File destDir) {
        final FileTree archiveTree = compressedTree(srcArchive)
        final List<String> patterns = this.execPatterns

        final Action<FileCopyDetails> setExecMode = { FileCopyDetails fcd ->
            if (!fcd.isDirectory()) {
                fcd.mode = fcd.mode | 0111
            }
        }

        project.copy {
            from archiveTree
            into destDir

            if(!IS_WINDOWS && !patterns.empty) {
                if(LegacyLevel.PRE_3_1) {
                    for( String pat in patterns) {
                        filesMatching (pat,setExecMode)
                    }
                } else {
                    filesMatching (patterns,setExecMode)
                }
            }
        }
    }

    /** Attempts to locate distribution in the list of SdkMan candidates.
     *
     * @return Location of the distribution if found in the candidate area.
     */
    @PackageScope File getDistFromSdkMan() {
        File sdkCandidate = new File("${System.getProperty('user.home')}/.sdkman/${sdkManCandidateName}/${distributionVersion}")

        sdkCandidate.exists() && sdkCandidate.isDirectory() ? sdkCandidate : null
    }

    /** Creates a distribution it it does not exist already.
     *
     * @return Location of distribution
     */
    @PackageScope File getDistFromCache()  {
        final WrapperConfiguration configuration = getNewWrapperConfiguration()

        final URI distributionUrl = configuration.getDistribution()

        final PathAssembler pathAssembler = new PathAssembler(downloadRoot ?: project.gradle.gradleUserHomeDir)
        final PathAssembler.LocalDistribution localDistribution = pathAssembler.getDistribution(configuration)
        final File distDir = localDistribution.distributionDir
        final File localCompressedFile = localDistribution.zipFile
        final String expectedChecksum = this.checksum

        return exclusiveFileAccessManager.access(localCompressedFile, new Callable<File>() {
            File call() throws Exception {
                final File markerFile = new File(localCompressedFile.getParentFile(), localCompressedFile.getName() + ".ok")
                if (distDir.isDirectory() && markerFile.isFile()) {
                    return getAndVerifyDistributionRoot(distDir, distDir.getAbsolutePath())
                }

                boolean needsDownload = !localCompressedFile.isFile()
                URI safeDistributionUrl = safeUri(distributionUrl)

                if (needsDownload) {

                    if(project.gradle.startParameter.isOffline() && distributionUrl.scheme != 'file') {
                        throw new DistributionFailedException("Cannot download ${distributionName} '${distributionVersion}' as currently offline")
                    }

                    File tmpCompressedFile = new File(localCompressedFile.getParentFile(), localCompressedFile.getName() + ".part")
                    tmpCompressedFile.delete()
                    logger.log("Downloading ${safeDistributionUrl}")
                    downloader.download(distributionUrl, tmpCompressedFile)
                    tmpCompressedFile.renameTo(localCompressedFile)
                }

                List<File> topLevelDirs = listDirs(distDir)
                for (File dir : topLevelDirs) {
                    logger.log("Deleting directory " + dir.getAbsolutePath())
                    dir.deleteDir()
                }

                verifyDownloadChecksum(configuration.getDistribution().toString(), localCompressedFile, expectedChecksum)

                logger.log("Unpacking " + localCompressedFile.getAbsolutePath() + " to " + distDir.getAbsolutePath())
                unpack(localCompressedFile, distDir)

                File root = getAndVerifyDistributionRoot(distDir, safeDistributionUrl.toString())
                markerFile.createNewFile()

                return root
            }
        })
    }

    /** Returns the attached project
     *
     * @return Attached project instance
     */
    protected Project getProject() {
        this.project
    }

    /** Provides the capability of unpacking an MSI file under Windows by calling out to {@code msiexec}.
     *
     * <p> {@code msiexec} will be located via the system search path.
     *
     * @param srcArchive The location of the download MSI
     * @param destDir The directory where the MSI needs to be unpacked into
     * @param env Environment to use. Can be null or empty inwhich case a default environment will be used
     */
    protected void unpackMSI(File srcArchive, File destDir, final Map<String,String> env) {
        if(IS_WINDOWS) {
            new LessMSIUnpacker(project).unpackMSI(srcArchive,destDir,env)
        } else {
            throw new DistributionFailedException("MSI unpacking is only supported under Windows")
        }
    }

    /**
     * Create a safe URI from the given one by stripping out user info.
     *
     * @param uri Original URI
     * @return a new URI with no user info
     */
    @PackageScope URI safeUri(URI uri)  {
        new URI(uri.scheme, null, uri.host, uri.port, uri.path, uri.query, uri.fragment)
    }

    private String calculateSha256Sum(final File file) {
        file.withInputStream { InputStream content ->
            MessageDigest digest = MessageDigest.getInstance("SHA-256")
            content.eachByte(4096) { bytes, len -> digest.update(bytes, 0, len) }
            digest.digest().encodeHex().toString()
        }
    }


    private FileTree compressedTree(final File srcArchive) {
        final String name = srcArchive.name.toLowerCase()
        if(name.endsWith('.zip')) {
            return project.zipTree(srcArchive)
        } else if(name.endsWith('.tar')) {
            return project.tarTree(srcArchive)
        } else if(name.endsWith('.tar.gz') || name.endsWith('.tgz')) {
            return project.tarTree(project.resources.gzip(srcArchive))
        } else if(name.endsWith('.tar.bz2') || name.endsWith('.tbz')) {
            return project.tarTree(project.resources.bzip2(srcArchive))
        } else if(name.endsWith('.tar.xz')) {
            final File unpackedXZTar = File.createTempFile(srcArchive.name.replaceAll(~/.xz$/,''),'$$$')
            unpackedXZTar.withOutputStream { OutputStream xz ->
                srcArchive.withInputStream { tarXZ ->
                    new XZInputStream(tarXZ).withStream { strm ->
                        xz << strm
                    }
                }
            }
            return project.tarTree(unpackedXZTar)
        }

        throw new IllegalArgumentException("${name} is not a supported archive type")

    }

    private WrapperConfiguration getNewWrapperConfiguration() {
        final WrapperConfiguration configuration = new WrapperConfiguration()
        configuration.distribution = uriFromVersion(distributionVersion)
        configuration.distributionPath = configuration.zipPath = basePath

        return setConfigChecksum(configuration)
    }

    @CompileDynamic
    private WrapperConfiguration setConfigChecksum(WrapperConfiguration configuration) {
        if(!LegacyLevel.PRE_2_6) {
            configuration.distributionSha256Sum = this.checksum
        }

        return configuration
    }

    @CompileDynamic
    private static List<?> createDownloader(final String distributionName,Project project) {
        boolean quiet = project.logging.level < LogLevel.INFO
        IDownload downloader
        ProgressLogger progressLogger

        Class<?> wrapperLoggerClass = null;
        try {
            wrapperLoggerClass = Class.forName("org.gradle.wrapper.Logger")
        } catch( ClassNotFoundException ) {
        }

        Download.constructors.findResult { ctor ->
            if( ctor.parameterTypes == [wrapperLoggerClass, String, String] as Class[] ) {
                // Gradle 2.3+
                Object wrapperLogger = wrapperLoggerClass.constructors.findResult { loggerCtor ->
                    loggerCtor.newInstance(quiet)
                }

                progressLogger = [ log : { String msg -> wrapperLogger.log(msg)} ] as ProgressLogger
                downloader = ctor.newInstance(wrapperLogger, distributionName, INSTALLER_VERSION)

            } else if( ctor.parameterTypes == [org.gradle.api.logging.Logger, String, String] as Class[] ) {

                progressLogger = new Progress(quiet)
                downloader = ctor.newInstance(project.logger, distributionName, INSTALLER_VERSION)

            } else if( ctor.parameterTypes == [String, String] as Class[] ){

                progressLogger = new Progress(quiet)
                downloader = ctor.newInstance(distributionName, INSTALLER_VERSION)
            }
        }

        [progressLogger,downloader]
    }


    private static class Progress implements ProgressLogger {
        private final boolean quiet

        Progress(boolean quiet) {
            this.quiet = quiet
        }

        void log(String message) {
            if (!quiet) {
                println message
            }
        }
    }

    private ProgressLogger logger
    private String sdkManCandidateName
    private String checksum
    private String distributionName
    private String distributionVersion
    private Project project
    private File downloadRoot
    private final List<String> execPatterns = []
    private final IDownload downloader
    private final String basePath
    private final ExclusiveFileAccessManager exclusiveFileAccessManager = new ExclusiveFileAccessManager(120000, 200)

}
