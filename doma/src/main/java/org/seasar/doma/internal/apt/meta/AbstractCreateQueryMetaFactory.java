/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.internal.apt.meta;

import static org.seasar.doma.internal.util.Assertions.*;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.seasar.doma.domain.Domain;
import org.seasar.doma.internal.apt.AptException;
import org.seasar.doma.internal.apt.AptIllegalStateException;
import org.seasar.doma.internal.apt.Models;
import org.seasar.doma.message.MessageCode;

/**
 * @author taedium
 * 
 */
public abstract class AbstractCreateQueryMetaFactory<M extends AbstractCreateQueryMeta>
        extends AbstractQueryMetaFactory<M> {

    protected final Class<?> domainValueClass;

    public AbstractCreateQueryMetaFactory(ProcessingEnvironment env,
            Class<?> domainValueClass) {
        super(env);
        assertNotNull(domainValueClass);
        this.domainValueClass = domainValueClass;
    }

    @Override
    protected void doReturnType(M queryMeta, ExecutableElement method,
            DaoMeta daoMeta) {
        TypeMirror returnType = method.getReturnType();
        if (!isDomain(returnType)) {
            throw new AptException(MessageCode.DOMA4022, env, method);
        }
        TypeMirror domainValueType = getDomainValueType(returnType);
        if (domainValueType == null) {
            throw new AptIllegalStateException();
        }
        TypeElement domainValueElement = Models
                .toTypeElement(domainValueType, env);
        if (domainValueElement == null) {
            throw new AptIllegalStateException();
        }
        if (!domainValueElement.getQualifiedName()
                .contentEquals(domainValueClass.getName())) {
            throw new AptException(MessageCode.DOMA4075, env, method,
                    domainValueClass.getName());
        }
        queryMeta.setReturnTypeName(Models.getTypeName(returnType, daoMeta
                .getTypeParameterMap(), env));
    }

    protected TypeMirror getDomainValueType(TypeMirror domainType) {
        for (TypeMirror supertype : env.getTypeUtils()
                .directSupertypes(domainType)) {
            TypeElement typeElement = Models.toTypeElement(supertype, env);
            if (typeElement == null) {
                continue;
            }
            if (typeElement.getQualifiedName().contentEquals(Domain.class
                    .getName())) {
                DeclaredType declaredType = Models
                        .toDeclaredType(supertype, env);
                if (declaredType == null) {
                    continue;
                }
                List<? extends TypeMirror> args = declaredType
                        .getTypeArguments();
                return args.get(0);
            }
            TypeMirror domainValueType = getDomainValueType(supertype);
            if (domainValueType != null) {
                return domainValueType;
            }
        }
        return null;
    }

    @Override
    protected void doParameters(M queryMeta, ExecutableElement method,
            DaoMeta daoMeta) {
        List<? extends VariableElement> params = method.getParameters();
        int size = params.size();
        if (size != 0) {
            throw new AptException(MessageCode.DOMA4078, env, method);
        }
    }

}