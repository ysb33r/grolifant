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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.olifant.errors.DistributionFailedException
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Files

class DistributionInstallerSpec extends Specification {

    static final File TESTDIST_DIR = new File( System.getProperty('TEST_RESOURCES_DIR') ?: '.', 'src/test/resources')

    Project project = ProjectBuilder.builder().build()

    def "Download a distribution from a URL"() {

        given: 'A basic distribution'
        // tag::installer_init[]
        TestInstaller installer = new TestInstaller(project)
        // end::installer_init[]

        when: 'Exec patterns are set'
        // tag::installer_execpattern[]
        installer.addExecPattern '**/*.sh' // <1>
        // end::installer_execpattern[]

        and: 'the distribution is downloaded'
        File downloaded = installer.distributionRoot
        boolean execCheck = AbstractDistributionInstaller.IS_WINDOWS && new File(downloaded,'test.sh').exists()

        if(!AbstractDistributionInstaller.IS_WINDOWS)  {
            execCheck = Files.isExecutable(new File(downloaded,'test.sh').toPath())
        }

        then: 'The distribution should be unpacked'
        downloaded.exists()
        downloaded.absolutePath.contains(distPathString(TestInstaller.DISTPATH))
        downloaded.absolutePath.endsWith("testdist-${TestInstaller.DISTVER}")

        new File(downloaded,'test.bat').exists()

        and: 'Execution permissions should be set on appropriate operating systems'
        execCheck == true
    }

    @Unroll
    def "Download a #ext from a URL"() {

        given: 'A basic distribution'
        TestInstallerForVariousFormats installer = new TestInstallerForVariousFormats(ext,project)

        when: 'The #ext based distribution is downloaded'
        File downloaded = installer.distributionRoot

        then: 'The distribution should be unpacked'
        downloaded.exists()
        downloaded.absolutePath.contains(distPathString(TestInstallerForVariousFormats.DISTPATH))
        downloaded.absolutePath.endsWith("testdist-${TestInstallerForVariousFormats.DISTVER}")

        where:
        ext << [ 'tar','tar.gz','tgz','tar.bz2', 'tbz','tar.xz']
    }

    def 'Checksums should be checked if supplied'() {
        given: 'A basic distribution'
        TestInstaller installer = new TestInstaller(project)

        when: 'A checksum is set'
        // tag::installer_checksum[]
        installer.checksum = 'b1741e3d2a3f7047d041c79d018cf55286d1168fd6f0533e7fae897478abcdef'  // <1>
        // end::installer_checksum[]

        and: 'the distribution is downloaded'
        File downloaded = installer.distributionRoot

        then: 'it should fail by throwing an exception, because the checksum did not match'
        thrown(ChecksumFailedException)

        when: 'A correct checksum is set'
        installer = new TestInstaller(project)
        installer.checksum = new File(TESTDIST_DIR,'testdist-0.1.zip.sha256').text.trim()

        and: 'The distribution is downloaded'
        downloaded = installer.distributionRoot

        then: 'The distribution should be unpacked'
        downloaded.exists()
        downloaded.absolutePath.contains(distPathString(TestInstaller.DISTPATH))
        downloaded.absolutePath.endsWith("testdist-${TestInstaller.DISTVER}")

        when: 'An invalid checksum is provided (not correct length)'
        installer = new TestInstaller(project)
        installer.checksum = 'abcde'

        then: 'An exception will be raised'
        thrown(IllegalArgumentException)

        when: 'An invalid checksum is provided (bad characters)'
        installer.checksum = '_'.multiply(64)

        then: 'An exception will be raised'
        thrown(IllegalArgumentException)
    }

    void 'Unpacking an MSI'() {
        given: 'A MSO distribution'
        TestInstallerForMSI installer = new TestInstallerForMSI(project)

        when: 'The MSI based distribution is downloaded'
        File downloaded = installer.distributionRoot

        then: 'The MSI distribution should be unpacked on Windows (or exception or other OS)'
        if(installer.IS_WINDOWS) {
            downloaded.exists()
            downloaded.absolutePath.contains(distPathString(TestInstallerForVariousFormats.DISTPATH))
            downloaded.absolutePath.endsWith("testdist-${TestInstallerForVariousFormats.DISTVER}")
        } else {
            thrown(DistributionFailedException)
        }
    }

    static class TestInstaller extends AbstractDistributionInstaller {

        // tag::test_installer[]
        static final String DISTPATH = 'foo/bar'
        static final String DISTVER  = '0.1'

        TestInstaller(Project project) {
            super('Test Distribution',DISTVER,DISTPATH,project) // <1>
        }

        @Override
        URI uriFromVersion(String version) { // <2>
            TESTDIST_DIR.toURI().resolve("testdist-${DISTVER}.zip") // <3>
        }
        // end::test_installer[]
    }

    static class TestInstallerForVariousFormats extends AbstractDistributionInstaller {

        static final String DISTPATH = TestInstaller.DISTPATH
        static final String DISTVER  = TestInstaller.DISTVER
        final String ext

        TestInstallerForVariousFormats(final String extension,Project project) {
            super('Test Distribution',DISTVER,DISTPATH,project)
            ext = extension
        }

        @Override
        URI uriFromVersion(String version) {
            TESTDIST_DIR.toURI().resolve("testdist-${DISTVER}.${ext}")
        }
    }

    static class TestInstallerForMSI extends TestInstallerForVariousFormats {
        TestInstallerForMSI(Project project) {
            super ('msi',project )
        }

        @Override
        protected void unpack(File srcArchive, File destDir) {
            unpackMSI(srcArchive, destDir)
        }
    }

    String distPathString(final String s) {
        AbstractDistributionInstaller.IS_WINDOWS ? s.replaceAll(~/\//,'\\\\') : s
    }
}