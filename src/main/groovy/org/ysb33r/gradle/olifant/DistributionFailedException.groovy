package org.ysb33r.gradle.olifant

/** Thrown when a distribution failed to unpack correctly or does not meet specific criteria.
 *
 */
class DistributionFailedException extends Exception {

    /** Instantiates exception
     *
     * @param msg Exception message
     */
    DistributionFailedException(final String msg) {
        super(msg)
    }
}
