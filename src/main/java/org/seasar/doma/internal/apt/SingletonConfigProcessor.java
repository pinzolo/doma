/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.seasar.doma.internal.apt;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import org.seasar.doma.SingletonConfig;
import org.seasar.doma.internal.apt.mirror.SingletonConfigMirror;
import org.seasar.doma.internal.apt.util.TypeMirrorUtil;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.message.Message;

/**
 * @author nakamura-to
 * 
 */
@SupportedAnnotationTypes({ "org.seasar.doma.SingletonConfig" })
@SupportedOptions({ Options.TEST, Options.DEBUG })
public class SingletonConfigProcessor extends AbstractProcessor {

    public SingletonConfigProcessor() {
        super(SingletonConfig.class);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }
        for (TypeElement a : annotations) {
            for (TypeElement typeElement : ElementFilter.typesIn(roundEnv
                    .getElementsAnnotatedWith(a))) {
                handleTypeElement(typeElement, this::validate);
            }
        }
        return true;
    }

    protected void validate(TypeElement typeElement) {
        SingletonConfigMirror mirror = SingletonConfigMirror.newInstance(
                typeElement, processingEnv);
        if (mirror == null) {
            throw new AptIllegalStateException("mirror must not be null");
        }
        validateClass(typeElement, mirror);
        validateConstructors(typeElement);
        validateMethod(typeElement, mirror.getMethodValue());
    }

    protected void validateClass(TypeElement typeElement,
            SingletonConfigMirror mirror) {
        if (!TypeMirrorUtil.isAssignable(typeElement.asType(), Config.class,
                processingEnv)) {
            throw new AptException(Message.DOMA4253, processingEnv,
                    typeElement, mirror.getAnnotationMirror(),
                    new Object[] { typeElement.getQualifiedName() });
        }
    }

    protected void validateConstructors(TypeElement typeElement) {
        ElementFilter
                .constructorsIn(typeElement.getEnclosedElements())
                .stream()
                .filter(c -> !c.getModifiers().contains(Modifier.PRIVATE))
                .findAny()
                .ifPresent(
                        c -> {
                            throw new AptException(Message.DOMA4256,
                                    processingEnv, c,
                                    new Object[] { typeElement
                                            .getQualifiedName() });
                        });
    }

    protected void validateMethod(TypeElement typeElement, String methodName) {
        Optional<ExecutableElement> method = ElementFilter
                .methodsIn(typeElement.getEnclosedElements())
                .stream()
                .filter(m -> m.getModifiers().containsAll(
                        EnumSet.of(Modifier.STATIC, Modifier.PUBLIC)))
                .filter(m -> TypeMirrorUtil.isAssignable(m.getReturnType(),
                        Config.class, processingEnv))
                .filter(m -> m.getParameters().isEmpty())
                .filter(m -> m.getSimpleName().toString().equals(methodName))
                .findAny();
        if (!method.isPresent()) {
            throw new AptException(Message.DOMA4254, processingEnv,
                    typeElement, new Object[] { methodName,
                            typeElement.getQualifiedName() });
        }
    }
}
