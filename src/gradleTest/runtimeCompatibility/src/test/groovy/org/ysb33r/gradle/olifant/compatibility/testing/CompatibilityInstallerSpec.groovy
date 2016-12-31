package org.ysb33r.gradle.olifant.compatibility.testing

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.ysb33r.gradle.olifant.AbstractDistributionInstaller
import spock.lang.Specification

import java.nio.file.Files

class CompatibilityInstallerSpec extends Specification {

    static final File TESTDIST_DIR = new File(System.getProperty('COMPAT_TEST_RESOURCES_DIR') ?: 'src/gradleTest/runtimeCompatibility/src/test/resources')

    Project project = ProjectBuilder.builder().build()

    def "Download a distribution from a URL"() {

        given: 'A basic distribution'
        TestInstaller installer = new TestInstaller(project)

        when: 'Exec patterns are set'
        installer.addExecPattern '**/*.sh'

        and: 'Checksum is set'
        installer.checksum = new File(TESTDIST_DIR,'testdist-0.2.zip.sha256').text.trim()

        and: 'the distribution is downloaded'
        File downloaded = installer.distributionRoot
        boolean execCheck = AbstractDistributionInstaller.IS_WINDOWS && new File(downloaded, 'test.sh').exists()

        if (!AbstractDistributionInstaller.IS_WINDOWS) {
            execCheck = Files.isExecutable(new File(downloaded, 'test.sh').toPath())
        }

        then: 'The distribution should be unpacked'
        downloaded.exists()
        downloaded.absolutePath.contains(TestInstaller.DISTPATH)
        downloaded.absolutePath.endsWith("testdist-${TestInstaller.DISTVER}")

        new File(downloaded, 'test.bat').exists()

        and: 'Execution permissions should be set on appropriate operating systems'
        execCheck == true
    }

    static class TestInstaller extends AbstractDistributionInstaller {

        static final String DISTPATH = 'foo/bar'
        static final String DISTVER  = '0.2'

        TestInstaller(Project project) {
            super('Test Distribution',DISTVER,DISTPATH,project)
        }

        @Override
        URI uriFromVersion(String version) {
            TESTDIST_DIR.toURI().resolve("testdist-${DISTVER}.zip")
        }
    }
}