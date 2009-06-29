package org.seasar.doma.internal.apt.meta;

import static org.seasar.doma.internal.util.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;

import org.seasar.doma.Dao;
import org.seasar.doma.GenericDao;
import org.seasar.doma.internal.apt.AptException;
import org.seasar.doma.internal.apt.AptIllegalStateException;
import org.seasar.doma.internal.apt.Models;
import org.seasar.doma.internal.apt.Notifier;
import org.seasar.doma.internal.apt.Options;
import org.seasar.doma.jdbc.DomaAbstractDao;
import org.seasar.doma.message.MessageCode;


/**
 * @author taedium
 * 
 */
public class DaoMetaFactory {

    protected final ProcessingEnvironment env;

    protected final List<QueryMetaFactory> queryMetaFactories = new ArrayList<QueryMetaFactory>();

    public DaoMetaFactory(ProcessingEnvironment env,
            List<QueryMetaFactory> commandMetaFactories) {
        assertNotNull(env, commandMetaFactories);
        this.env = env;
        this.queryMetaFactories.addAll(commandMetaFactories);
    }

    public DaoMeta createDaoMeta(TypeElement daoElement) {
        assertNotNull(daoElement);
        DaoMeta daoMeta = new DaoMeta();
        doDaoElement(daoElement, daoMeta);
        List<ExecutableElement> concreteMethodElements = getConcreteMethodElements(daoMeta);
        doSuperInterfaceMethodElements(daoElement, concreteMethodElements, daoMeta);
        doMethodElements(daoElement, concreteMethodElements, daoMeta);
        return daoMeta;
    }

    protected void doDaoElement(TypeElement daoElement, DaoMeta daoMeta) {
        if (daoElement.getNestingKind().isNested()) {
            throw new AptException(MessageCode.DOMA4017, env, daoElement,
                    daoElement.getQualifiedName());
        }
        if (!daoElement.getKind().isInterface()) {
            throw new AptException(MessageCode.DOMA4014, env, daoElement,
                    daoElement.getQualifiedName());
        }
        String name = daoElement.getSimpleName().toString();
        String suffix = Options.getSuffix(env);
        if (name.endsWith(suffix)) {
            Notifier
                    .notify(env, Kind.WARNING, MessageCode.DOMA4026, daoElement, suffix);
        }
        daoMeta.setName(name);
        daoMeta.setDaoElement(daoElement);
        daoMeta.setDaoType(daoElement.asType());
        Dao daoAnnotation = daoElement.getAnnotation(Dao.class);
        GenericDao genericDaoAnnotation = daoElement
                .getAnnotation(GenericDao.class);
        if (daoAnnotation != null && genericDaoAnnotation != null) {
            throw new AptException(MessageCode.DOMA4047, env, daoElement);
        }
        if (daoAnnotation != null) {
            if (!daoElement.getTypeParameters().isEmpty()) {
                throw new AptException(MessageCode.DOMA4059, env, daoElement);
            }
            doConfig(daoAnnotation, daoMeta);
            doImplementedBy(daoAnnotation, daoMeta);
        } else if (genericDaoAnnotation != null) {
            if (daoElement.getTypeParameters().size() != 1) {
                throw new AptException(MessageCode.DOMA4046, env, daoElement);
            }
            daoMeta.setGenericDao(true);
        }
    }

    protected void doConfig(Dao daoAnnotation, DaoMeta daoMeta) {
        TypeMirror configType = getConfigType(daoAnnotation, daoMeta);
        daoMeta.setConfigType(configType);
    }

    protected TypeMirror getConfigType(Dao daoAnnotation, DaoMeta daoMeta) {
        try {
            daoAnnotation.config();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
        throw new AptIllegalStateException();
    }

    protected void doImplementedBy(Dao daoAnnotation, DaoMeta daoMeta) {
        TypeMirror implementedByType = getImplementedByType(daoAnnotation, daoMeta);
        TypeElement implementedByElement = Models
                .toTypeElement(implementedByType, env);
        if (implementedByElement == null) {
            throw new AptIllegalStateException();
        }
        daoMeta.setImplementedByType(implementedByType);
        daoMeta.setImplementedByElement(implementedByElement);
        if (implementedByElement.getQualifiedName()
                .contentEquals(DomaAbstractDao.class.getName())) {
            daoMeta.setMostSubtypeElement(daoMeta.getDaoElement());
        } else {
            daoMeta.setMostSubtypeElement(implementedByElement);
            if (!Models
                    .isAssignable(implementedByType, daoMeta.getDaoType(), env)) {
                throw new AptException(MessageCode.DOMA4020, env,
                        implementedByElement, implementedByElement
                                .getQualifiedName(), daoMeta.getDaoElement()
                                .getQualifiedName());
            }
        }
    }

    protected TypeMirror getImplementedByType(Dao daoAnnotation, DaoMeta daoMeta) {
        try {
            daoAnnotation.implementedBy();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
        throw new AptIllegalStateException();
    }

    protected List<ExecutableElement> getConcreteMethodElements(DaoMeta daoMeta) {
        if (daoMeta.isGenericDao()) {
            return Collections.emptyList();
        }
        TypeMirror implementedByType = daoMeta.getImplementedByType();
        TypeElement implementedByElement = Models
                .toTypeElement(implementedByType, env);
        List<ExecutableElement> concreteMethodElements = new ArrayList<ExecutableElement>();
        for (ExecutableElement methodElement : ElementFilter
                .methodsIn(implementedByElement.getEnclosedElements())) {
            Set<Modifier> modifiers = methodElement.getModifiers();
            if (modifiers.contains(Modifier.PUBLIC)
                    && Collections.disjoint(modifiers, EnumSet
                            .of(Modifier.ABSTRACT, Modifier.STATIC))) {
                concreteMethodElements.add(methodElement);
            }
        }
        return concreteMethodElements;
    }

    protected void doSuperInterfaceMethodElements(TypeElement typeElement,
            List<ExecutableElement> concreteMethodElements, DaoMeta daoMeta) {
        for (TypeMirror interfaceTypeMirror : env.getTypeUtils()
                .directSupertypes(typeElement.asType())) {
            TypeElement interfaceTypeElement = Models
                    .toTypeElement(interfaceTypeMirror, env);
            if (interfaceTypeElement == null
                    || !interfaceTypeElement.getKind().isInterface()) {
                continue;
            }
            if (interfaceTypeElement.getAnnotation(GenericDao.class) == null) {
                throw new AptException(MessageCode.DOMA4045, env, typeElement);
            }
            if (!interfaceTypeElement.getTypeParameters().isEmpty()) {
                Map<TypeMirror, TypeMirror> typeParameterMap = Models
                        .createTypeParameterMap(interfaceTypeElement, interfaceTypeMirror, env);
                daoMeta.addTypeParameterMap(typeParameterMap);
            }
            doSuperInterfaceMethodElements(interfaceTypeElement, concreteMethodElements, daoMeta);
            doMethodElements(interfaceTypeElement, concreteMethodElements, daoMeta);
            daoMeta.addSupertype(interfaceTypeMirror);
        }
    }

    protected void doMethodElements(TypeElement typeElement,
            List<ExecutableElement> concreteMethodElements, DaoMeta daoMeta) {
        outer: for (ExecutableElement methodElement : ElementFilter
                .methodsIn(typeElement.getEnclosedElements())) {
            for (ExecutableElement overrider : concreteMethodElements) {
                if (env.getElementUtils()
                        .overrides(overrider, methodElement, daoMeta
                                .getImplementedByElement())) {
                    continue outer;
                }
            }
            try {
                doMethodElement(methodElement, daoMeta);
            } catch (AptException e) {
                if (Models.isEnclosing(daoMeta.getDaoElement(), e.getElement())) {
                    Notifier.notify(env, e);
                } else {
                    Notifier
                            .notify(env, e.getKind(), MessageCode.DOMA4044, daoMeta
                                    .getDaoElement(), typeElement, e
                                    .getElement(), e.getMessage());
                }
            }
        }
    }

    protected void doMethodElement(ExecutableElement methodElement,
            DaoMeta daoMeta) {
        QueryMeta queryMeta = createQueryMeta(methodElement, daoMeta);
        if (daoMeta.getSupertypes().size() > 0) {
            for (Iterator<QueryMeta> it = daoMeta.getQueryMetas().iterator(); it
                    .hasNext();) {
                QueryMeta overridenMeta = it.next();
                ExecutableElement overriden = overridenMeta
                        .getExecutableElement();
                ExecutableElement overrider = queryMeta.getExecutableElement();
                if (env.getElementUtils()
                        .overrides(overrider, overriden, daoMeta
                                .getMostSubtypeElement())) {
                    it.remove();
                    break;
                }
            }
        }
        daoMeta.addQueryMeta(queryMeta);
    }

    protected QueryMeta createQueryMeta(ExecutableElement method,
            DaoMeta daoMeta) {
        for (QueryMetaFactory factory : queryMetaFactories) {
            QueryMeta queryMeta = factory.createQueryMeta(method, daoMeta);
            if (queryMeta != null) {
                return queryMeta;
            }
        }
        throw new AptException(MessageCode.DOMA4005, env, method);
    }
}