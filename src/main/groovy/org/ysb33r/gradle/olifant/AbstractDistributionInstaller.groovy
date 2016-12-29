/*
 * Copyright 2010 the original Gradle author or authors.
 * Copyright 2016 Schalk W. Cronjé
 * This code originates from the Gradle Wrapper codebase and has been appropriately
 * modifeid for a more generic context within Gradle plugins.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ysb33r.gradle.olifant

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.FileTree
import org.gradle.api.logging.LogLevel
import org.gradle.wrapper.*

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
     * @param sdkCandidateName
     */
    void setSdkManCandidateName(final String sdkCandidateName) {
        this.sdkManCandidateName = sdkCandidateName
    }

    /** Add patterns for files to be marked executable
     *
     * @param relPaths
     */
    void addExecPattern(String... relPaths) {
        this.execPatterns.addAll(relPaths as List)
    }

    /** Set a checksum that needs to be verified against dowmloaded archive
     *
     * @param cs SHA-256 Hex-encoded checksum
     */
    void setChecksum(final String cs) {
        this.checksum = cs
    }

    /** Returns the location which is the top or home folder for a distribution.
     *
     * @return
     */
    File getDistributionRoot() {

        File location = locateDistributionInCustomLocation(distributionVersion)

        if(location == null && this.sdkManCandidateName)  {
            location = getDistFromSdkMan()
        }

        location ?: getDistFromCache()
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

    /** Sets a download root directory for the distribution. If not supplied the default is to use the
     * Gradle User Home. This method is provided for convenience and is mostly only used for testing
     * purposes.
     *
     * @param downloadRootDir
     */
    void setDownloadRoot(File downloadRootDir) {
        this.downloadRoot = downloadRootDir
    }

    /** Returns the logger currently in use.
     *
     * @return Wrapper logger instance
     */
    protected Logger getLogger() {
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
        this.logger = new Logger(project.logging.level >= LogLevel.INFO)

        downloader = new Download(logger,distributionName,INSTALLER_VERSION)
        configuration = new WrapperConfiguration()
        configuration.distribution = uriFromVersion(distributionVersion)
        configuration.distributionPath = configuration.zipPath = basePath
    }

    protected File getAndVerifyDistributionRoot(final File distDir, final String distributionDescription) {
        List<File> dirs = listDirs(distDir)
        if (dirs.isEmpty()) {
            throw new GradleException("${distributionName} '${distributionDescription}' does not contain any directories. Expected to find exactly 1 directory.")
        }
        if (dirs.size() != 1) {
            throw new GradleException("${distributionName} '${distributionDescription} contains too many directories. Expected to find exactly 1 directory.")
        }
        return dirs[0]
    }

    protected void verifyDownloadChecksum(final String sourceUrl, final File localCompressedFile, final String expectedSum) {
        if (this.checksum != null) {
            String actualSum = calculateSha256Sum(localCompressedFile)
            if (!this.checksum.equals(actualSum)) {
                localCompressedFile.delete()
                String message = """Verification of ${distributionName} failed!

This ${distributionName} may have been tampered with.
 Distribution Url: ${sourceUrl}
Download Location: ${localCompressedFile}
Expected checksum: ${expectedSum}
  Actual checksum: ${actualSum}
"""
                throw new RuntimeException(message)
            }
        }
    }

    protected String calculateSha256Sum(final File file) {
        file.withInputStream { InputStream content ->
            MessageDigest digest = MessageDigest.getInstance("SHA-256")
            content.eachByte(4096) { bytes, len -> digest.update(bytes, 0, len) }
            digest.digest().encodeHex().toString()
        }
    }

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
                filesMatching (patterns,setExecMode)
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

        final URI distributionUrl = configuration.getDistribution()
        final String distributionSha256Sum = configuration.getDistributionSha256Sum()

        final PathAssembler pathAssembler = new PathAssembler(downloadRoot ?: project.gradle.gradleUserHomeDir)
        final PathAssembler.LocalDistribution localDistribution = pathAssembler.getDistribution(configuration)
        final File distDir = localDistribution.distributionDir
        final File localCompressedFile = localDistribution.zipFile

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
                        throw new GradleException("Cannot download ${distributionName} '${distributionVersion}' as currently offline")
                    }

                    File tmpCompressedFile = new File(localCompressedFile.getParentFile(), localCompressedFile.getName() + ".part")
                    tmpCompressedFile.delete()
                    logger.log("Downloading " + safeDistributionUrl)
                    downloader.download(distributionUrl, tmpCompressedFile)
                    tmpCompressedFile.renameTo(localCompressedFile)
                }

                List<File> topLevelDirs = listDirs(distDir)
                for (File dir : topLevelDirs) {
                    logger.log("Deleting directory " + dir.getAbsolutePath())
                    dir.deleteDir()
                }

                verifyDownloadChecksum(configuration.getDistribution().toString(), localCompressedFile, distributionSha256Sum)

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

    /**
     * Create a safe URI from the given one by stripping out user info.
     *
     * @param uri Original URI
     * @return a new URI with no user info
     */
    @PackageScope URI safeUri(URI uri)  {
        new URI(uri.scheme, null, uri.host, uri.port, uri.path, uri.query, uri.fragment)
    }

    // TODO: Handle tar.gz, tgz, tar.bz2, tbz
    private FileTree compressedTree(final File srcArchive) {
        project.zipTree(srcArchive)
    }


    private Logger logger
    private String sdkManCandidateName
    private String checksum
    private String distributionName
    private String distributionVersion
    private Project project
    private File downloadRoot
    private final List<String> execPatterns = []
    private final IDownload downloader
    private final WrapperConfiguration configuration
    private final ExclusiveFileAccessManager exclusiveFileAccessManager = new ExclusiveFileAccessManager(120000, 200)

}
