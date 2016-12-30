package org.ysb33r.gradle.olifant

import groovy.transform.CompileStatic

/** Thrown when a checksum fails.
 *
 */
@CompileStatic
class ChecksumFailedException extends RuntimeException {

    /** Instantiates an exception
     *
     * @param distributionName A descriptive name of the distribution
     * @param sourceUrl The URL/URI where it was downloaded from
     * @param localCompressedFile The location of the downloaded archive
     * @param expectedSum The expected checksum
     * @param actualSum The actual checksum
     */
    ChecksumFailedException(final String distributionName,
                            final String sourceUrl,
                            final File localCompressedFile,
                            final String expectedSum,
                            final String actualSum) {
        super("""Verification of ${distributionName} failed!

This ${distributionName} may have been tampered with.
 Distribution Url: ${sourceUrl}
Download Location: ${localCompressedFile}
Expected checksum: ${expectedSum}
  Actual checksum: ${actualSum}
""")
    }
}
