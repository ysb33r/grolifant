package org.ysb33r.gradle.olifant

import groovy.transform.CompileStatic
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileVisitor
import org.gradle.api.tasks.util.PatternFilterable

/**
 *
 */
@CompileStatic
abstract class CompositeFileTree implements FileTree {

    @Override
    FileTree plus(FileTree fileTree) {
        new UnionFileTree(this,fileTree)
    }

    @Override
    FileTree matching(Closure filterConfigClosure) {
        new FilteredFileTree(this,filterConfigClosure)
    }

    @Override
    FileTree matching(PatternFilterable patterns) {
        new FilteredFileTree(this,patterns)
    }

    @Override
    FileTree getAsFileTree() {
        this
    }


//    private class FilteredFileTree extends org.gradle.api.internal.file.CompositeFileTree {
//        private final Closure closure;
//        private final PatternFilterable patterns;
//
//        public FilteredFileTree(Closure closure) {
//            this.closure = closure;
//            patterns = null;
//        }
//
//        public FilteredFileTree(PatternFilterable patterns) {
//            this.patterns = patterns;
//            closure = null;
//        }
//
//        @Override
//        public String getDisplayName() {
//            return org.gradle.api.internal.file.CompositeFileTree.this.getDisplayName();
//        }
//
//        @Override
//        public void visitContents(FileCollectionResolveContext context) {
//            ResolvableFileCollectionResolveContext nestedContext = context.newContext();
//            org.gradle.api.internal.file.CompositeFileTree.this.visitContents(nestedContext);
//            for (FileTree set : nestedContext.resolveAsFileTrees()) {
//                if (closure != null) {
//                    context.add(set.matching(closure));
//                } else {
//                    context.add(set.matching(patterns));
//                }
//            }
//        }
//
//        @Override
//        public void visitDependencies(TaskDependencyResolveContext context) {
//            org.gradle.api.internal.file.CompositeFileTree.this.visitDependencies(context);
//        }
//    }

    static class UnionFileTree extends CompositeFileTree {
        UnionFileTree(FileTree left,FileTree right) {
            sourceCollections.add(left)
            sourceCollections.add(right)
        }

        @Override
        FileTree visit(Closure visitor) {
            for (FileTree tree : sourceCollections) {
                tree.visit(visitor)
            }
            return this
        }

        @Override
        Set<File> getFiles() {
            Set<File> allFiles = [] as Set<File>
            sourceCollections.each { FileTree tree ->
                allFiles
            }
            allFiles
        }

        @Override
        public FileTree visit(FileVisitor visitor) {
            for (FileTree tree : sourceCollections) {
                tree.visit(visitor)
            }
            return this
        }

        private List<FileTree> sourceCollections = []

    }

    static class ReducedFileTree extends CompositeFileTree {

    }

    static class FilteredFileTree extends CompositeFileTree {
        FilteredFileTree( FileTree fileTree, Closure filter ) {

        }
        FilteredFileTree( FileTree fileTree, PatternFilterable patterns ) {

        }
    }
}
