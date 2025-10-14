package org.babyfish.jimmer.sql;

import org.babyfish.jimmer.Draft;
import org.babyfish.jimmer.impl.util.TypeCache;
import org.babyfish.jimmer.lang.Generics;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.meta.KeyMatcher;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.*;

class DraftPreProcessorManager {

    private final Map<ImmutableType, List<DraftPreProcessor<?>>> processorMap;

    private final TypeCache<DraftPreProcessor<?>> cache =
            new TypeCache<>(this::create, true);

    DraftPreProcessorManager(Collection<DraftPreProcessor<?>> processors) {
        Map<ImmutableType, List<DraftPreProcessor<?>>> processorMap = new HashMap<>();
        for (DraftPreProcessor<?> processor : processors) {
            if (processor != null) {
                Type[] argTypes = Generics
                        .getTypeArguments(
                                processor.getClass(),
                                DraftPreProcessor.class
                        );
                if (argTypes.length == 0) {
                    throw new IllegalArgumentException(
                            "Illegal draft processor type \"" +
                                    processor.getClass().getName() +
                                    "\", it extends \"" +
                                   DraftPreProcessor.class.getName() +
                                    "\" but the generic type arguments are not specified"
                    );
                }
                Type draftType = argTypes[0];
                if (!(draftType instanceof Class<?>) ||
                        !(((Class<?>)draftType).isInterface()) ||
                        !Draft.class.isAssignableFrom((Class<?>)draftType)) {
                    throw new IllegalArgumentException(
                            "Illegal draft processor type \"" +
                                    processor.getClass().getName() +
                                    "\", it extends \"" +
                                    DraftPreProcessor.class.getName() +
                                    "\" but its draft type \"" +
                                    draftType +
                                    "\" is not non-generic interface type extends " +
                                    "\"" +
                                    Draft.class.getName() +
                                    "\""
                    );
                }
                ImmutableType immutableType = ImmutableType.get((Class<?>) draftType);
                Class<?> entityType = immutableType.getJavaClass();
                if (!entityType.isInterface() || (
                                entityType.getAnnotation(Entity.class) == null &&
                                        entityType.getAnnotation(MappedSuperclass.class) == null
                        )
                ) {
                    throw new IllegalArgumentException(
                            "Illegal draft processor type \"" +
                                    processor.getClass().getName() +
                                    "\", it extends \"" +
                                    DraftPreProcessor.class.getName() +
                                    "\" but the processed entity type \"" +
                                    entityType +
                                    "\" is not interface type decorated by \"@" +
                                    Entity.class.getName() +
                                    "\" or \"@" +
                                    MappedSuperclass.class.getName() +
                                    "\""
                    );
                }
                processorMap
                        .computeIfAbsent(immutableType, it -> new ArrayList<>())
                        .add(processor);
            }
        }
        this.processorMap = processorMap;
    }

    public DraftPreProcessor<?> get(ImmutableType type) {
        return cache.get(type);
    }

    @SuppressWarnings("unchecked")
    private DraftPreProcessor<?> create(ImmutableType type) {
        List<DraftPreProcessor<?>> processors = new ArrayList<>();
        Set<ImmutableType> allTypes = type.getAllTypes();
        for (ImmutableType t : allTypes) {
            List<DraftPreProcessor<?>> list = processorMap.get(t);
            if (list != null) {
                processors.addAll(list);
            }
        }
        if (processors.isEmpty()) {
            return null;
        }
        boolean ignoreIdOnly = false;
        for (DraftPreProcessor<?> processor : processors) {
            ignoreIdOnly |= processor.ignoreIdOnly();
        }
        final boolean mergedIgnoreIdOnly = ignoreIdOnly;

        return new DraftPreProcessor<Draft>() {
            @Override
            public void beforeSave(@NotNull Draft draft) {
                for (DraftPreProcessor<?> processor : processors) {
                    ((DraftPreProcessor<Draft>)processor).beforeSave(draft);
                }
            }

            @Override
            public boolean ignoreIdOnly() {
                return mergedIgnoreIdOnly;
            }

            @Override
            public boolean ignoreKeyOnly(@NotNull KeyMatcher.Group group) {
                for (DraftPreProcessor<?> processor : processors) {
                    if (processor.ignoreKeyOnly(group)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
