// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.debugger.ui.tree.render;

import com.intellij.debugger.engine.DebuggerUtils;
import com.intellij.debugger.engine.SuspendContext;
import com.intellij.debugger.impl.DebuggerUtilsAsync;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.CommonClassNames;
import com.sun.jdi.Type;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class TypeRenderer implements Renderer {
  private static final Logger LOG = Logger.getInstance(ReferenceRenderer.class);
  protected BasicRendererProperties myProperties = new BasicRendererProperties(false);

  protected TypeRenderer() {
    this(CommonClassNames.JAVA_LANG_OBJECT);
  }

  protected TypeRenderer(@NotNull String className) {
    myProperties.setClassName(className);
  }

  public String getClassName() {
    return myProperties.getClassName();
  }

  public void setClassName(String className) {
    myProperties.setClassName(className);
  }

  @Override
  public Renderer clone() {
    try {
      final TypeRenderer cloned = (TypeRenderer)super.clone();
      cloned.myProperties = myProperties.clone();
      return cloned;
    }
    catch (CloneNotSupportedException e) {
      LOG.error(e);
    }
    return null;
  }

  @Override
  public boolean isApplicable(Type type) {
    return DebuggerUtils.instanceOf(type, getClassName());
  }

  @Override
  public CompletableFuture<Boolean> isApplicableAsync(Type type, SuspendContext context) {
    return DebuggerUtilsAsync.instanceOf(type, getClassName(), context);
  }

  @Override
  public void writeExternal(Element element) throws WriteExternalException {
    myProperties.writeExternal(element, CommonClassNames.JAVA_LANG_OBJECT);
  }

  @Override
  public void readExternal(Element element) throws InvalidDataException {
    myProperties.readExternal(element, CommonClassNames.JAVA_LANG_OBJECT);
  }

  protected CachedEvaluator createCachedEvaluator() {
    return new CachedEvaluator() {
      @Override
      protected String getClassName() {
        return TypeRenderer.this.getClassName();
      }
    };
  }
}
