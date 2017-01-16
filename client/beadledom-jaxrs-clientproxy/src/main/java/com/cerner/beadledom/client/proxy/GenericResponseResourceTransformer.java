package com.cerner.beadledom.client.proxy;

import com.cerner.beadledom.jaxrs.GenericResponse;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.SignatureAttribute;

import javax.ws.rs.core.Response;

/**
 * A JAX-RS resource interface transformer for dynamically creating a standard
 * (no {@code GenericResponse}) JAX-RS resource interface from one with {@link GenericResponse}
 * return types.
 *
 * <p>This class generates an interface at runtime that is compatible with standard JAX-RS client
 * proxies by duplicating the proxied interface that uses {@link GenericResponse} and modifying the
 * duplicate to use the standard JAX-RS {@link Response} in place of {@code GenericResponse}. This
 * is necessary so that a standard proxy can be created and wrapped by {@link GenericClientProxy}.
 *
 * @author John Leacox
 * @since 2.0
 */
class GenericResponseResourceTransformer {
  private static final String PROXY_PREFIX = "JaxRsGenericResponseProxy_";

  private final ClassPool pool = ClassPool.getDefault();

  private CtClass genericResponseClass;
  private CtClass responseClass;

  GenericResponseResourceTransformer() {
  }

  private synchronized CtClass getResponseClass() throws NotFoundException {
    if (responseClass == null) {
      responseClass = pool.get(Response.class.getName());
    }

    return responseClass;
  }

  private synchronized CtClass getGenericResponseClass() throws NotFoundException {
    if (genericResponseClass == null) {
      genericResponseClass = pool.get(GenericResponse.class.getName());
    }

    return genericResponseClass;
  }

  /**
   * Transforms the given interface, creating a duplicate interface with return types
   * {@link GenericResponse} replaced with {@link Response}.
   */
  <T> Class<?> transform(Class<T> proxyInterface) {
    try {
      String packageName = proxyInterface.getPackage().getName();
      String genericProxyInterfaceName =
          packageName + "." + PROXY_PREFIX + proxyInterface.getSimpleName();
      try {
        return Class.forName(genericProxyInterfaceName);
      } catch (ClassNotFoundException e) {
        pool.insertClassPath(new ClassClassPath(proxyInterface));
        CtClass copy = pool.getAndRename(proxyInterface.getName(), genericProxyInterfaceName);
        replaceGenericResponseMethods(copy);

        return copy
            .toClass(this.getClass().getClassLoader(), this.getClass().getProtectionDomain());
      }
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    } catch (CannotCompileException e) {
      throw new RuntimeException(e);
    } catch (BadBytecode e) {
      throw new RuntimeException(e);
    }
  }

  private void replaceGenericResponseMethods(CtClass clazz)
      throws NotFoundException, CannotCompileException, BadBytecode {
    for (CtMethod method : clazz.getMethods()) {
      if (method.getReturnType().subtypeOf(getGenericResponseClass())) {
        MethodInfo methodInfo = method.getMethodInfo();
        CtMethod replacementMethod = CtNewMethod
            .abstractMethod(
                getResponseClass(), method.getName(), method.getParameterTypes(),
                method.getExceptionTypes(), clazz);

        for (Object attributeInfo : methodInfo.getAttributes()) {
          if (attributeInfo instanceof SignatureAttribute) {
            String sig = transformMethodSignature((SignatureAttribute) attributeInfo);
            replacementMethod.getMethodInfo().addAttribute(
                new SignatureAttribute(((SignatureAttribute) attributeInfo).getConstPool(), sig));
          } else {
            replacementMethod.getMethodInfo().addAttribute((AttributeInfo) attributeInfo);
          }
        }
        clazz.removeMethod(method);
        clazz.addMethod(replacementMethod);
      }
    }
  }

  private String transformMethodSignature(SignatureAttribute signatureAttribute)
      throws BadBytecode {
    String originalSig = signatureAttribute.getSignature();
    SignatureAttribute.MethodSignature methodSignature =
        SignatureAttribute.toMethodSignature(originalSig);

    SignatureAttribute.ClassType returnType =
        new SignatureAttribute.ClassType("javax.ws.rs.core.Response");
    SignatureAttribute.MethodSignature replacementSignature =
        new SignatureAttribute.MethodSignature(
            methodSignature.getTypeParameters(),
            methodSignature.getParameterTypes(),
            returnType,
            methodSignature.getExceptionTypes());
    return replacementSignature.encode();
  }
}
