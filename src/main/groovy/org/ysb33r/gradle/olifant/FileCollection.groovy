/*
 * Copyright 2016 Schalk W. Cronjé
 * (Strongly based on the original AbstractFileCollection Gradle code, released under same license).
 *
 * Copyright 2009 the original author or authors.
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

import groovy.transform.CompileStatic
import org.gradle.api.file.FileTree
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskDependency
import org.gradle.util.GUtil

//import org.gradle.api.internal.file.AntFileCollectionBuilder

/**
 *
 */
@CompileStatic
class FileCollection implements org.gradle.api.file.FileCollection {

    FileCollection(final String displayName) {
        this.displayName = displayName
    }

    /** COnvenience method to access as single File if the colelction contains exactly one file
     *
     * @return File instance
     * @throws IllegalStateException if collection size != 1s
     */
    @Override
    File getSingleFile()  {
        Set files = this.getFiles()
        if(files.empty) {
            throw new IllegalStateException("Expected ${displayName} to contain exactly one file, however, it contains no files.")
        } else if(files.size() != 1) {
            throw new IllegalStateException("Expected ${displayName} to contain exactly one file, however, it contains ${files.size()} files.")
        }

        files[0]
    }

    /**
     *
     * @param file
     * @return
     */
    @Override
    boolean contains(File file) {
        getFiles().contains(file)
    }

    /**
     *
     * @return
     */
    @Override
    String getAsPath() {
        GUtil.asPath(getFiles())
    }

    /** Indicates whether collection is empty.
     *
     * @return {@code true} is collection is empty
     */
    @Override
    boolean isEmpty() {
        getFiles().empty
    }


    /**
     *
     * @param aClass
     * @return
     * @throws UnsupportedOperationException
     */
    @Override
    def asType(Class<?> type)  {
        if(type.isAssignableFrom(Set)) {
            return this.getFiles()
        } else if(type.isAssignableFrom(List)) {
            return new ArrayList(this.getFiles())
        } else if(type.isAssignableFrom(File)) {
            return this.getSingleFile()
        } else if(type.isAssignableFrom(org.gradle.api.file.FileCollection.class)) {
            return this
        } else if(type.isAssignableFrom(FileTree.class)) {
            return this.getAsFileTree()
        } else {
            throw new UnsupportedOperationException("Cannot convert ${displayName} to type ${type.simpleName}, as this type is not supported.")
        }
    }

    @Override
    org.gradle.api.file.FileCollection plus(org.gradle.api.file.FileCollection fileCollection) {
        return null
    }

    @Override
    org.gradle.api.file.FileCollection minus(org.gradle.api.file.FileCollection fileCollection) {
        return null
    }

    @Override
    org.gradle.api.file.FileCollection filter(Closure closure) {
        return null
    }

    @Override
    org.gradle.api.file.FileCollection filter(Spec<? super File> spec) {
        return null
    }



    @Override
    org.gradle.api.file.FileCollection add(org.gradle.api.file.FileCollection fileCollection) throws UnsupportedOperationException {
        return null
    }

    @Override
    org.gradle.api.file.FileCollection stopExecutionIfEmpty() throws StopExecutionException {
        return null
    }

    @Override
    FileTree getAsFileTree() {
        return null
    }

    @Override
    void addToAntBuilder(Object builder, String nodeName, org.gradle.api.file.FileCollection.AntType antType) {
        if(antType == org.gradle.api.file.FileCollection.AntType.ResourceCollection) {
            (new AntFileCollectionBuilder(this)).addToAntBuilder(builder, nodeName)
        } else if(antType == org.gradle.api.file.FileCollection.AntType.FileSet) {
            this.addAsFileSet(builder, nodeName);
        } else {
            this.addAsMatchingTask(builder, nodeName);
        }

    }

    @Override
    Object addToAntBuilder(Object o, String s) {
        return null
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    Iterator<File> iterator() {
        return null
    }

    @Override
    TaskDependency getBuildDependencies() {
        return null
    }

    @Override
    Set<File> getFiles() {
        return null
    }


    private String displayName
}
