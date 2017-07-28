package org.ysb33r.gradle.olifant.internal.msi

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.ysb33r.gradle.olifant.AbstractDistributionInstaller
import org.ysb33r.gradle.olifant.errors.DistributionFailedException

/** Downloads an MSI to use at a later stage.
 *
 * @since 0.4
 */
@CompileStatic
class LessMSIUnpacker extends AbstractDistributionInstaller {

    static final String LESSMSI_VERSION =  '1.16.1'
    static final String LESSMSI_DOWNLOAD_URI = 'https://github.com/activescott/lessmsi/releases/download'

    /** Creates setup for installing to a local cache.
     *
     * @param project Gradle project that this downloader is attached to.
     */
    LessMSIUnpacker(Project project) {
        super(
            'lessmsi',
            System.getProperty('org.ysb33r.gradle.olifant.lessmsi.version') ?: LESSMSI_VERSION,
            'native-binaries/lessmsi',
            project
        )
    }

    /** Creates a download URI from a given distribution version
     *
     * @param version Version of the distribution to download
     * @return
     */
    @Override
    URI uriFromVersion(String version) {
        "${System.getProperty('org.ysb33r.gradle.olifant.lessmsi.uri') ?: LESSMSI_DOWNLOAD_URI}/v${version}/lessmsi-v${version}.zip".toURI()
    }

    /** Returns the path to the {@code lessmsi} executable.
     * Will force a download if not already downloaded.
     *
     * @return Location of {@code lessmsi} or null if not a supported operating system.
     */
    File getLessMSIExecutablePath() {
        File root = getDistributionRoot()
        if(root == null) {
            return null
        } else {
            new File(root,'lessmsi.exe')
        }
    }

    /** Unpacks an MSI given the {@code lessmsi} executable downloaded by this incantation.
     *
     * @param srcArchive Location of MSI
     * @param destDir Directory to unpack to
     * @param env Environment to use when unpacking. If null or empty will add {@code TEMP}, {@code TMP} from Gradle environment.
     */
    void unpackMSI(File srcArchive, File destDir, final Map<String,String> env) {
        if(env == null || env.isEmpty()) {
            doUnpackMSI(srcArchive,destDir,[
                TMP : System.getenv('TMP'),
                TEMP : System.getenv('TEMP')
            ])
        } else {
            doUnpackMSI(srcArchive,destDir,env)
        }
    }

    /** Validates that the unpacked distribution is good.
     *
     * @param distDir Directory where distribution was unpacked to.
     * @param distributionDescription A descriptive name of the distribution
     * @return {@code distDir} as {@code Packer} distributions contains only a single executable.
     *
     * @throw {@link org.ysb33r.gradle.olifant.DistributionFailedException} if distribution failed to meet criteria.
     */
    @Override
    protected File getAndVerifyDistributionRoot(File distDir, String distributionDescription) {

        File checkFor = new File(distDir,'lessmsi.exe')

        if(!checkFor.exists()) {
            throw new DistributionFailedException("${checkFor.name} not found in downloaded ${distributionDescription} distribution.")
        }

        distDir
    }

    @CompileDynamic
    private doUnpackMSI(File srcArchive, File destDir,Map<String,String> env) {
        project.exec {
            executable getLessMSIExecutablePath()
            cmdArgs 'x', srcArchive.absolutePath, destDir.absolutePath
            environment env
        }
    }
}
