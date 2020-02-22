# Reflection utility for Kotlin

## Overview

The library aim to provide more smarter way of the reflection with the Kotlin.

Here is the example.

```kotlin
// Target.kt
package com.example

class Target {
    fun hello(name: String): String{
        return "Hello $name!"
    }
}
```

```kotlin
// Example.kt
package com.example

import net.refy.android.reflect.Reflect

class Example : Reflect("com.example.Target"){
    val hello by virtual<String>(String::class.java)
}
```

```kotlin
// Program.kt
package com.example

fun main(args: Array<String>){
    // print "Hello Alice"
    System.out.println(Example().hello("Alice"))
}
```

## Installation

Add gradle repositories and dependency in your build.grade.

```gradle
repositories {
    maven { url 'https://tmyt.github.io/reflect/' }
}

dependencies {
    ...
    implementation 'tech.onsen:reflect:1.1.0'
    ...
}
```

## Usage

You need to initialize the target type via `constructor(className: String)`, `constructor(clazz: Class<*>)` or `override val type: Class<*>`.
And optionally needed to override `val value: Any?`, if you need some constructor arguments.

### Constructors

#### constructor()

Note: You need to override `val type: Class<*>` and optinally `val value: Any?` properties.

For Exmple:
```kotlin
class Example : Reflect() {
    override type by lazy { Class.forName("com.example.Target") }
}
```

#### constructor(className: String)

Initialize `type` property with `Class.forName(className)`, and `value` property initialized with default constructor of `type`.
You can override `value` property optionally, if you need some constructor arguments.

For Example:
```kotlin
class Example : Reflect("com.example.Target") {
}
```

#### constructor(clazz: Class<*>)

Initialize `type` property with argument, and `value` property initialized with default constructor of `type`.
You can override `value` property optionally, if you need some constructor arguments.

For example:
```kotlin
class Example(target: Class<*>) : Reflect(target) {
}
```

### Property Delegates

#### <T> virtual(vararg args: Class<*>)

Initialize property as virtual method.

For example: You want to call `hello` method with `String` argument and `Boolean` return value,

```kotlin
val hello by virtual<Boolean>(String::class.java)
```

You can invoke the property same as function.

```kotlin
hello("alice")
```

#### <T> static(vararg args: Class<*>)

Initialize property as static method.

For example: You want to call `hello` method with `String` argument and `Boolean` return value,

```kotlin
val hello by static<Boolean>(String::class.java)
```

You can invoke the property same as function.

```kotlin
hello("bob")
```

### Utilities

#### ctor(vararg args: Class<*>)

Reflect constructor with `args` arguments. You can invoke the return value same as funtion, you can initialize the `value` property smiply.

For example: Call constructor with One String argument.

```kotlin
val value = ctor(String::class.java)("hello alice")
```

### Properties

#### val type: Class<*>

The property indicates the Class<*> of reflect target.

The proeprty automatically initialized when only use constructor with arguments. If you use primary constructor of `Reflect`, you need override the property in the derived class.

For example:

```kotlin
override val type: Class<*> by lazy { value.javaClass }
```


#### val value: Any?

The property indicates the instance of the reflect target.

The property automatically initialized with default constructor of the `type`. If you need some constructor arguments, you need override the property in the derived class.

For example:

```kotlin
override val value: Any? by lazy { ctor(String::class.java)("hello alice") }
```

## License

MIT