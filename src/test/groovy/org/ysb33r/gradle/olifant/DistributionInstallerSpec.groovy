package org.ysb33r.gradle.olifant

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import java.nio.file.Files


/**
 * @author Schalk W. Cronjé
 */
class DistributionInstallerSpec extends Specification {

    static final File TESTDIST_DIR = new File( System.getProperty('TEST_RESOURCES_DIR') ?: '.', 'src/test/resources')

    def "Download a distribution from a URL"() {

        given:
        Project project = ProjectBuilder.builder().build()
        File baseDir = project.gradle.gradleUserHomeDir
        TestInstaller installer = new TestInstaller(project)
        installer.addExecPattern '**/*.sh'

        when:
        File downloaded = installer.distributionRoot
        boolean execCheck = AbstractDistributionInstaller.IS_WINDOWS && new File(downloaded,'test.sh').exists()

        if(!AbstractDistributionInstaller.IS_WINDOWS)  {
            execCheck = Files.isExecutable(new File(downloaded,'test.sh').toPath())
        }

        then:
        downloaded.exists()
        downloaded.absolutePath.contains(TestInstaller.DISTPATH)
        downloaded.absolutePath.endsWith("testdist-${TestInstaller.DISTVER}")

        new File(downloaded,'test.bat').exists()
        execCheck == true
    }

    static class TestInstaller extends AbstractDistributionInstaller {

        static final String DISTPATH = 'foo/bar'
        static final String DISTVER  = '0.1'

        TestInstaller(Project project) {
            super('Test Distribution',DISTVER,DISTPATH,project)
        }

        @Override
        URI uriFromVersion(String version) {
            TESTDIST_DIR.toURI().resolve("testdist-${DISTVER}.zip")
        }
    }
}