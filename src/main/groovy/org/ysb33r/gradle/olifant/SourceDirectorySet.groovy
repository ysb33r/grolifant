/*
 * Copyright 2016 Schalk W. Cronjé
 * (Modified from original Gradle code, released under some license).
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
import org.gradle.api.file.DirectoryTree
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileTreeElement
import org.gradle.api.file.FileVisitor
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskDependency
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.api.tasks.util.PatternSet
import org.gradle.util.GUtil;

/**
 * @author Schalk W. Cronjé
 */
@CompileStatic
class SourceDirectorySet /*extends CompositeFileTree*/ implements org.gradle.api.file.SourceDirectorySet {

    SourceDirectorySet(final String name) {
        this.name = name
    }

    @Override
    String getName() {
        this.name
    }

    @Override
    org.gradle.api.file.SourceDirectorySet srcDir(def dir) {
        this.source.add(dir)
        return this
    }

    @Override
    org.gradle.api.file.SourceDirectorySet srcDirs(def... dirs) {
        this.source.addAll(dirs as List)
        return this
    }

    @Override
    Set<File> getSrcDirs() {
        this.dirs.files
    }

    @Override
    org.gradle.api.file.SourceDirectorySet setSrcDirs(Iterable<?> srcPaths) {
        this.source.clear()
        GUtil.addToCollection(this.source, srcPaths)
        return this
    }

    @Override
    org.gradle.api.file.SourceDirectorySet source(org.gradle.api.file.SourceDirectorySet sourceDirectorySet) {
        this.source.add(source)
        return this
    }

    @Override
    Set<DirectoryTree> getSrcDirTrees() {
        // TODO: This is based upon an implementation that was broken. It needs to take care of
        // include, exclude paths
        Map<File, DirectoryTree> trees = new LinkedHashMap<File, DirectoryTree>();
        for (DirectoryTree tree : doGetSrcDirTrees()) {
            if (!trees.containsKey(tree.getDir())) {
                trees.put(tree.getDir(), tree);
            }
        }
        return new LinkedHashSet<DirectoryTree>(trees.values());
    }

    @Override
    PatternFilterable getFilter() {
        this.filter
    }

    @Override
    FileTree visit(FileVisitor fileVisitor) {
        return null
    }

    @Override
    FileTree visit(Closure closure) {
        return null
    }

    @Override
    FileTree plus(FileTree fileTree) {
        return null
    }

    @Override
    FileTree getAsFileTree() {
        return null
    }

    @Override
    void addToAntBuilder(Object o, String s, FileCollection.AntType antType) {

    }

    @Override
    Object addToAntBuilder(Object o, String s) {
        return null
    }

    @Override
    File getSingleFile() throws IllegalStateException {
        return null
    }

    @Override
    Set<File> getFiles() {
        return null
    }

    @Override
    boolean contains(File file) {
        return false
    }

    @Override
    String getAsPath() {
        return null
    }

    @Override
    FileCollection plus(FileCollection fileCollection) {
        return null
    }

    @Override
    FileCollection minus(FileCollection fileCollection) {
        return null
    }

    @Override
    FileCollection filter(Closure closure) {
        return null
    }

    @Override
    FileCollection filter(Spec<? super File> spec) {
        return null
    }

    @Override
    Object asType(Class<?> aClass) throws UnsupportedOperationException {
        return null
    }

    @Override
    FileCollection add(FileCollection fileCollection) throws UnsupportedOperationException {
        return null
    }

    @Override
    boolean isEmpty() {
        return false
    }

    @Override
    FileCollection stopExecutionIfEmpty() throws StopExecutionException {
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
    Set<String> getIncludes() {
        patterns.getIncludes()
    }

    @Override
    Set<String> getExcludes() {
        patterns.getExcludes()
    }

    @Override
    PatternFilterable setIncludes(Iterable<String> iterable) {
        return null
    }

    @Override
    PatternFilterable setExcludes(Iterable<String> iterable) {
        return null
    }

    @Override
    PatternFilterable include(String... strings) {
        return null
    }

    @Override
    PatternFilterable include(Iterable<String> iterable) {
        return null
    }

    @Override
    PatternFilterable include(Spec<FileTreeElement> spec) {
        return null
    }

    @Override
    PatternFilterable include(Closure closure) {
        return null
    }

    @Override
    PatternFilterable exclude(String... strings) {
        return null
    }

    @Override
    PatternFilterable exclude(Iterable<String> iterable) {
        return null
    }

    @Override
    PatternFilterable exclude(Spec<FileTreeElement> spec) {
        return null
    }

    @Override
    PatternFilterable exclude(Closure closure) {
        return null
    }

    @Override
    FileTree matching(Closure closure) {
        return null
    }

    @Override
    FileTree matching(PatternFilterable patternFilterable) {
        return null
    }


    private final List<Object> source = []
    private final String name
    private final String displayName
    private final PatternSet patterns
    private final PatternSet filter
    private final FileCollection dirs
//    private final FileResolver fileResolver;


}


/*

import groovy.lang.Closure;
import org.gradle.api.Buildable;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.file.DirectoryTree;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.CompositeFileTree
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.file.collections.DefaultDirectoryFileTreeFactory;
import org.gradle.api.internal.file.collections.DirectoryFileTree;
import org.gradle.api.internal.file.collections.DirectoryFileTreeFactory;
import org.gradle.api.internal.file.collections.FileCollectionAdapter;
import org.gradle.api.internal.file.collections.FileCollectionResolveContext;
import org.gradle.api.internal.file.collections.MinimalFileSet;
import org.gradle.api.internal.tasks.TaskDependencyResolveContext;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.TaskDependency;
import org.gradle.api.tasks.util.PatternFilterable;
import org.gradle.api.tasks.util.PatternSet;
import org.gradle.util.DeprecationLogger;
import org.gradle.util.GUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultSourceDirectorySet extends CompositeFileTree implements org.gradle.api.file.SourceDirectorySet {
    private final List<Object> source = new ArrayList<Object>();
    private final String name;
    private final String displayName;
    private final FileResolver fileResolver;
    private final DirectoryFileTreeFactory directoryFileTreeFactory;
    private final PatternSet patterns;
    private final PatternSet filter;
    private final FileCollection dirs;

    public DefaultSourceDirectorySet(String name, String displayName, FileResolver fileResolver, DirectoryFileTreeFactory directoryFileTreeFactory) {
        this.name = name;
        this.displayName = displayName;
        this.fileResolver = fileResolver;
        this.directoryFileTreeFactory = directoryFileTreeFactory;
        this.patterns = fileResolver.getPatternSetFactory().create();
        this.filter = fileResolver.getPatternSetFactory().create();
        dirs = new FileCollectionAdapter(new SourceDirectories());
    }

    @Deprecated
    public DefaultSourceDirectorySet(String name, FileResolver fileResolver) {
        this(name, name, fileResolver, new DefaultDirectoryFileTreeFactory());
        DeprecationLogger.nagUserOfDeprecated("Constructor DefaultSourceDirectorySet(String, FileResolver)");
    }

    public DefaultSourceDirectorySet(String name, FileResolver fileResolver, DirectoryFileTreeFactory directoryFileTreeFactory) {
        this(name, name, fileResolver, directoryFileTreeFactory);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public FileCollection getSourceDirectories() {
        return dirs;
    }

    public Set<File> getSrcDirs() {
        Set<File> dirs = new LinkedHashSet<File>();
        for (DirectoryTree tree : getSrcDirTrees()) {
            dirs.add(tree.getDir());
        }
        return dirs;
    }

    public Set<String> getIncludes() {
        return patterns.getIncludes();
    }

    public Set<String> getExcludes() {
        return patterns.getExcludes();
    }

    public PatternFilterable setIncludes(Iterable<String> includes) {
        patterns.setIncludes(includes);
        return this;
    }

    public PatternFilterable setExcludes(Iterable<String> excludes) {
        patterns.setExcludes(excludes);
        return this;
    }

    public PatternFilterable include(String... includes) {
        patterns.include(includes);
        return this;
    }

    public PatternFilterable include(Iterable<String> includes) {
        patterns.include(includes);
        return this;
    }

    public PatternFilterable include(Spec<FileTreeElement> includeSpec) {
        patterns.include(includeSpec);
        return this;
    }

    public PatternFilterable include(Closure includeSpec) {
        patterns.include(includeSpec);
        return this;
    }

    public PatternFilterable exclude(Iterable<String> excludes) {
        patterns.exclude(excludes);
        return this;
    }

    public PatternFilterable exclude(String... excludes) {
        patterns.exclude(excludes);
        return this;
    }

    public PatternFilterable exclude(Spec<FileTreeElement> excludeSpec) {
        patterns.exclude(excludeSpec);
        return this;
    }

    public PatternFilterable exclude(Closure excludeSpec) {
        patterns.exclude(excludeSpec);
        return this;
    }

    public PatternFilterable getFilter() {
        return filter;
    }

    public Set<DirectoryTree> getSrcDirTrees() {
        // This implementation is broken. It does not consider include and exclude patterns
        Map<File, DirectoryTree> trees = new LinkedHashMap<File, DirectoryTree>();
        for (DirectoryTree tree : doGetSrcDirTrees()) {
            if (!trees.containsKey(tree.getDir())) {
                trees.put(tree.getDir(), tree);
            }
        }
        return new LinkedHashSet<DirectoryTree>(trees.values());
    }

    private Set<DirectoryTree> doGetSrcDirTrees() {
        Set<DirectoryTree> result = new LinkedHashSet<DirectoryTree>();
        for (Object path : source) {
            if (path instanceof org.gradle.api.file.SourceDirectorySet) {
                org.gradle.api.file.SourceDirectorySet nested = (org.gradle.api.file.SourceDirectorySet) path;
                result.addAll(nested.getSrcDirTrees());
            } else {
                for (File srcDir : fileResolver.resolveFiles(path)) {
                    if (srcDir.exists() && !srcDir.isDirectory()) {
                        throw new InvalidUserDataException(String.format("Source directory '%s' is not a directory.", srcDir));
                    }
                    result.add(directoryFileTreeFactory.create(srcDir, patterns));
                }
            }
        }
        return result;
    }

    @Override
    public void visitDependencies(TaskDependencyResolveContext context) {
        for (Object path : source) {
            if (path instanceof org.gradle.api.file.SourceDirectorySet) {
                context.add(((org.gradle.api.file.SourceDirectorySet) path).getBuildDependencies());
            } else {
                context.add(fileResolver.resolveFiles(path));
            }
        }
    }

    @Override
    public void visitContents(FileCollectionResolveContext context) {
        for (DirectoryTree directoryTree : doGetSrcDirTrees()) {
            context.add(((DirectoryFileTree) directoryTree).filter(filter));
        }
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public org.gradle.api.file.SourceDirectorySet srcDir(Object srcDir) {
        source.add(srcDir);
        return this;
    }

    public org.gradle.api.file.SourceDirectorySet srcDirs(Object... srcDirs) {
        for (Object srcDir : srcDirs) {
            source.add(srcDir);
        }
        return this;
    }

    public org.gradle.api.file.SourceDirectorySet setSrcDirs(Iterable<?> srcPaths) {
        source.clear();
        GUtil.addToCollection(source, srcPaths);
        return this;
    }

    public org.gradle.api.file.SourceDirectorySet source(org.gradle.api.file.SourceDirectorySet source) {
        this.source.add(source);
        return this;
    }

    private class SourceDirectories implements MinimalFileSet, Buildable {
        @Override
        public TaskDependency getBuildDependencies() {
            return org.gradle.api.internal.file.DefaultSourceDirectorySet.this.getBuildDependencies();
        }

        @Override
        public Set<File> getFiles() {
            return getSrcDirs();
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }
    }
}

 */