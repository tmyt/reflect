package net.refy.android.reflect

import java.lang.reflect.Constructor
import java.lang.reflect.Method
import kotlin.reflect.KProperty

abstract class Reflect {
    abstract val type: Class<*>
    open val value: Any? = null

    class MethodDelegate<T>(private val isStatic: Boolean, private vararg val args: Class<*>) {
        private lateinit var method: MethodRef<T>

        operator fun getValue(thisRef: Reflect?, property: KProperty<*>): MethodRef<T> {
            if (!::method.isInitialized) {
                method = MethodRef(
                    thisRef!!,
                    thisRef.type.getMethod(property.name, *args)
                )
            }
            return method
        }
    }

    class MethodRef<T>(private val thisRef: Reflect?, private val methodRef: Method) {
        @Suppress("UNCHECKED_CAST")
        operator fun invoke(vararg args: Any?): T {
            return methodRef.invoke(thisRef?.value, *args) as T
        }
    }

    class CtorRef(private val ctorRef: Constructor<*>) {
        operator fun invoke(vararg args: Any?): Any {
            return ctorRef.newInstance(*args)
        }
    }

    protected fun ctor(vararg args: Class<*>): CtorRef {
        return CtorRef(type.getConstructor(*args))
    }

    protected companion object {
        fun <T> static(vararg args: Class<*>): MethodDelegate<T> {
            return MethodDelegate<T>(true, *args)
        }

        fun <T> virtual(vararg args: Class<*>): MethodDelegate<T> {
            return MethodDelegate<T>(false, *args)
        }
    }
}
