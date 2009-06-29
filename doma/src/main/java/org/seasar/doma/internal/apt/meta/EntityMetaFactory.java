package org.seasar.doma.internal.apt.meta;

import static org.seasar.doma.internal.util.Assertions.*;

import java.util.Iterator;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;

import org.seasar.doma.Entity;
import org.seasar.doma.MappedSuperclass;
import org.seasar.doma.Table;
import org.seasar.doma.internal.apt.AptException;
import org.seasar.doma.internal.apt.AptIllegalStateException;
import org.seasar.doma.internal.apt.Models;
import org.seasar.doma.internal.apt.Notifier;
import org.seasar.doma.internal.apt.Options;
import org.seasar.doma.jdbc.EntityListener;
import org.seasar.doma.jdbc.JdbcException;
import org.seasar.doma.message.MessageCode;


/**
 * 
 * @author taedium
 * 
 */
public class EntityMetaFactory {

    protected final ProcessingEnvironment env;

    protected final PropertyMetaFactory propertyMetaFactory;

    public EntityMetaFactory(ProcessingEnvironment env,
            PropertyMetaFactory propertyMetaFactory) {
        assertNotNull(env, propertyMetaFactory);
        this.env = env;
        this.propertyMetaFactory = propertyMetaFactory;
    }

    public EntityMeta createEntityMeta(TypeElement entityElement) {
        assertNotNull(entityElement);
        EntityMeta entityMeta = new EntityMeta();
        doEntityElement(entityElement, entityMeta);
        doSuperInterfaceMethodElements(entityElement, entityMeta, entityMeta
                .isMappedSuperclass());
        doMethodElements(entityElement, entityMeta);
        return entityMeta;
    }

    protected void doEntityElement(TypeElement entityElement,
            EntityMeta entityMeta) {
        if (entityElement.getNestingKind().isNested()) {
            throw new AptException(MessageCode.DOMA4018, env, entityElement,
                    entityElement.getQualifiedName());
        }
        if (!entityElement.getKind().isInterface()) {
            throw new AptException(MessageCode.DOMA4015, env, entityElement,
                    entityElement.getQualifiedName());
        }
        String name = entityElement.getSimpleName().toString();
        String suffix = Options.getSuffix(env);
        if (name.endsWith(suffix)) {
            Notifier
                    .notify(env, Kind.WARNING, MessageCode.DOMA4026, entityElement, suffix);
        }
        entityMeta.setName(name);
        entityMeta.setEntityElement(entityElement);
        entityMeta.setEntityType(entityElement.asType());
        Entity entityAnnotation = entityElement.getAnnotation(Entity.class);
        MappedSuperclass mappedSuperclassAnnotation = entityElement
                .getAnnotation(MappedSuperclass.class);
        if (entityAnnotation != null && mappedSuperclassAnnotation != null) {
            throw new JdbcException(MessageCode.DOMA4049, entityElement);
        }
        if (entityAnnotation != null) {
            if (!entityElement.getTypeParameters().isEmpty()) {
                throw new JdbcException(MessageCode.DOMA4051, entityElement);
            }
            doListener(entityAnnotation, entityElement, entityMeta);
            doTableMeta(entityElement, entityMeta);
        } else if (mappedSuperclassAnnotation != null) {
            if (!entityElement.getTypeParameters().isEmpty()) {
                throw new JdbcException(MessageCode.DOMA4050, entityElement);
            }
            entityMeta.setMappedSuperclass(true);
        }
    }

    protected void doListener(Entity entityAnnotation,
            TypeElement entityElement, EntityMeta entityMeta) {
        TypeMirror entityListenerType = getListenerType(entityAnnotation);
        TypeMirror argumentType = getListenerArgumentType(entityListenerType);
        assertNotNull(argumentType);
        if (!Models.isAssignable(entityMeta.getEntityType(), argumentType, env)) {
            throw new AptException(MessageCode.DOMA4038, env, entityElement,
                    entityListenerType, argumentType, entityElement
                            .getQualifiedName());
        }
        entityMeta.setListenerType(entityListenerType);
    }

    protected TypeMirror getListenerType(Entity entityAnnotation) {
        try {
            entityAnnotation.listener();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
        throw new AptIllegalStateException();
    }

    protected TypeMirror getListenerArgumentType(TypeMirror typeMirror) {
        for (TypeMirror supertype : env.getTypeUtils()
                .directSupertypes(typeMirror)) {
            TypeElement superElement = Models.toTypeElement(supertype, env);
            if (superElement == null || !superElement.getKind().isInterface()) {
                continue;
            }
            if (superElement.getQualifiedName()
                    .contentEquals(EntityListener.class.getName())) {
                DeclaredType declaredType = Models
                        .toDeclaredType(supertype, env);
                assertNotNull(declaredType);
                List<? extends TypeMirror> args = declaredType
                        .getTypeArguments();
                assertEquals(1, args.size());
                return args.get(0);
            }
            TypeMirror argumentType = getListenerArgumentType(supertype);
            if (argumentType != null) {
                return argumentType;
            }
        }
        return null;
    }

    protected void doTableMeta(TypeElement entityElement, EntityMeta entityMeta) {
        Table table = entityElement.getAnnotation(Table.class);
        if (table == null) {
            table = Table.Default.get();
        }
        TableMeta tableMeta = new TableMeta();
        if (!table.catalog().isEmpty()) {
            tableMeta.setCatalog(table.catalog());
        }
        if (!table.schema().isEmpty()) {
            tableMeta.setSchema(table.schema());
        }
        if (!table.name().isEmpty()) {
            tableMeta.setName(table.name());
        }
        entityMeta.setTableMeta(tableMeta);
    }

    protected void doSuperInterfaceMethodElements(TypeElement typeElement,
            EntityMeta entityMeta, boolean mappedSuperclassAnnotated) {
        boolean mappedSuperclass = mappedSuperclassAnnotated;
        for (TypeMirror interfaceTypeMirror : env.getTypeUtils()
                .directSupertypes(typeElement.asType())) {
            TypeElement interfaceTypeElement = Models
                    .toTypeElement(interfaceTypeMirror, env);
            if (interfaceTypeElement == null
                    || !interfaceTypeElement.getKind().isInterface()) {
                continue;
            }
            if (mappedSuperclass) {
                if (interfaceTypeElement.getAnnotation(MappedSuperclass.class) == null) {
                    throw new AptException(MessageCode.DOMA4048, env,
                            typeElement);
                }
            } else {
                if (interfaceTypeElement.getAnnotation(MappedSuperclass.class) != null) {
                    mappedSuperclass = true;
                } else if (interfaceTypeElement.getAnnotation(Entity.class) == null) {
                    throw new AptException(MessageCode.DOMA4052, env,
                            typeElement);
                }
            }
            doSuperInterfaceMethodElements(interfaceTypeElement, entityMeta, mappedSuperclass);
            doMethodElements(interfaceTypeElement, entityMeta);
            entityMeta.addSupertype(interfaceTypeMirror);
        }
    }

    protected void doMethodElements(TypeElement typeElement,
            EntityMeta entityMeta) {
        for (ExecutableElement methodElement : ElementFilter
                .methodsIn(typeElement.getEnclosedElements())) {
            try {
                doMethodElement(methodElement, entityMeta);
            } catch (AptException e) {
                Notifier.notify(env, e);
            }
        }
    }

    protected void doMethodElement(ExecutableElement methodElement,
            EntityMeta entityMeta) {
        PropertyMeta propertyMeta = propertyMetaFactory
                .createPropertyMeta(methodElement, entityMeta);
        if (entityMeta.getSupertypes().size() > 0) {
            for (Iterator<PropertyMeta> it = entityMeta.getAllPropertyMetas()
                    .iterator(); it.hasNext();) {
                PropertyMeta overridenMeta = it.next();
                ExecutableElement overriden = overridenMeta
                        .getExecutableElement();
                ExecutableElement overrider = propertyMeta
                        .getExecutableElement();
                if (env.getElementUtils()
                        .overrides(overrider, overriden, entityMeta
                                .getEntityElement())) {
                    it.remove();
                    break;
                }
            }
        }
        entityMeta.addPropertyMeta(propertyMeta);
    }

}