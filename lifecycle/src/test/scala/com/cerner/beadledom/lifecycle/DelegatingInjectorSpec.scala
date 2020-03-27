package com.cerner.beadledom.lifecycle

import com.google.inject.{Injector, Key, Module, TypeLiteral}
import org.mockito.Mockito
import org.scalatest._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Unit tests for [[DelegatingInjector]].
  *
  * @author John Leacox
  */
class DelegatingInjectorSpec extends AnyFunSpec with Matchers with MockitoSugar {
  describe("DelegatingInjector") {
    describe("constructor") {
      it("throws a NullPointerException for a null injector parameter") {
        intercept[NullPointerException] {
          new DelegatingInjector(null) {}
        }
      }
    }

    describe("#injectMembers(Object instance)") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        val mockObject = mock[Object]
        delegatingInjector.injectMembers(mockObject)

        Mockito.verify(underlying).injectMembers(mockObject)
      }
    }

    describe("#getMembersInjector(TypeLiteral<T> typeLiteral)") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        val typeLiteral: TypeLiteral[Object] = mock[TypeLiteral[Object]]
        delegatingInjector.getMembersInjector(typeLiteral)
        
        Mockito.verify(underlying).getMembersInjector(typeLiteral)
      }
    }

    describe("#getMembersInjector(Class<T> type)") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        val clazz = classOf[Object]
        delegatingInjector.getMembersInjector(clazz)

        Mockito.verify(underlying).getMembersInjector(clazz)
      }
    }

    describe("#getBindings") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        delegatingInjector.getBindings

        Mockito.verify(underlying).getBindings
      }
    }

    describe("#getAllBindings") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        delegatingInjector.getAllBindings

        Mockito.verify(underlying).getAllBindings
      }
    }

    describe("#getBinding(Key<T> key)") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        val key = mock[Key[Object]]
        delegatingInjector.getBinding(key)

        Mockito.verify(underlying).getBinding(key)
      }
    }

    describe("#getBinding(Class<T> type)") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        val clazz = classOf[Object]
        delegatingInjector.getBinding(clazz)

        Mockito.verify(underlying).getBinding(clazz)
      }
    }

    describe("#getExistingBinding(Key<T> key)") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        val key = mock[Key[Object]]
        delegatingInjector.getExistingBinding(key)

        Mockito.verify(underlying).getExistingBinding(key)
      }
    }

    describe("#findBindingsByType(TypeLiteral<T> type)") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        val typeLiteral: TypeLiteral[Object] = mock[TypeLiteral[Object]]
        delegatingInjector.findBindingsByType(typeLiteral)

        Mockito.verify(underlying).findBindingsByType(typeLiteral)
      }
    }

    describe("#getProvider(Key<T> key)") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        val key = mock[Key[Object]]
        delegatingInjector.getProvider(key)

        Mockito.verify(underlying).getProvider(key)
      }
    }

    describe("#getProvider(Class<T> type)") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        val clazz = classOf[Object]
        delegatingInjector.getProvider(clazz)

        Mockito.verify(underlying).getProvider(clazz)
      }
    }

    describe("#getInstance(Key<T> key)") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        val key = mock[Key[Object]]
        delegatingInjector.getInstance(key)

        Mockito.verify(underlying).getInstance(key)
      }
    }

    describe("#getInstance(Class<T> type)") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        val clazz = classOf[Object]
        delegatingInjector.getInstance(clazz)

        Mockito.verify(underlying).getInstance(clazz)
      }
    }

    describe("#getParent()") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        delegatingInjector.getParent

        Mockito.verify(underlying).getParent
      }
    }

    describe("#createChildInjector(Iterable<? extends Module> modules)") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        val modules = mock[java.lang.Iterable[Module]]
        delegatingInjector.createChildInjector(modules)

        Mockito.verify(underlying).createChildInjector(modules)
      }
    }

    describe("#createChildInjector(Module... modules)") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        val module1 = mock[Module]
        val module2 = mock[Module]
        delegatingInjector.createChildInjector(module1, module2)

        Mockito.verify(underlying).createChildInjector(module1, module2)
      }
    }

    describe("#getScopeBindings()") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        delegatingInjector.getScopeBindings

        Mockito.verify(underlying).getScopeBindings
      }
    }

    describe("#getTypeConverterBindings()") {
      it("delegates to the underlying injector") {
        val underlying = mock[Injector]
        val delegatingInjector = new DelegatingInjector(underlying) {}

        delegatingInjector.getTypeConverterBindings

        Mockito.verify(underlying).getTypeConverterBindings
      }
    }
  }
}
