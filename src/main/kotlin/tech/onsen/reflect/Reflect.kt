package tech.onsen.reflect

import java.lang.IllegalStateException
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import kotlin.reflect.KProperty

abstract class Reflect() {
    private lateinit var _clazz: Class<*>

    constructor(className: String) : this() {
        _clazz = Class.forName(className)
    }

    constructor(clazz: Class<*>): this(){
        _clazz = clazz
    }

    protected open val type: Class<*> by lazy {
        if (!::_clazz.isInitialized) throw IllegalStateException("target class must initialized")
        else _clazz
    }

    protected open val value: Any? by lazy {
        try {
            type.getConstructor().newInstance()
        }catch(e: NoSuchMethodException){
            throw IllegalStateException("target have no default constructor")
        }
    }

    class MethodDelegate<T>(private val isStatic: Boolean, private vararg val args: Class<*>) {
        private lateinit var method: MethodRef<T>

        operator fun getValue(thisRef: Reflect, property: KProperty<*>): MethodRef<T> {
            if (!::method.isInitialized) {
                method = MethodRef(
                    if(isStatic) null else thisRef,
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
