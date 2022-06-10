@file:JvmName("ClassesKt")
package bas.lib.core.lang

import bas.lib.core.lang.annotation.Note
import java.lang.reflect.ParameterizedType

inline fun isClassExists(className: String): Boolean {
    return try {
        Class.forName(className)
        true
    } catch (e: ClassNotFoundException) {
        false
    }
}

/**
 * 推断泛型对应的Class
 * @param tPosition 类型定义的位置：即T类型是第几个参数 ,比如Person<T,V>,如果要推断V的类型，则[tPosition] = 1
 */
@Note(message = "注意：自动推断在有几种情况下无法推断出正确类型，比如范型的个数、位置等会影响范型的推断，对于只有一个类型的范型子类推断无问题。")
fun <T> deduceGenericsClass(instance: Any, tPosition: Int = 0): Class<T>? {
    val gSuperClass = findParameterizedType(instance.javaClass) ?: return null
    val target = gSuperClass.actualTypeArguments[tPosition]

    if (target is Class<*>) {
        return target as? Class<T>
    }
    return null
//        else if(target is TypeVariable<*>){
//            val field =  target.javaClass.getDeclaredField("boundASTs")
//            field.isAccessible = true
//            val typeSignature = field.get(target) as Array<*>
//            val tempFieldValue = typeSignature[0]!!
//            val pathField =tempFieldValue.javaClass.getDeclaredField("path")
//            pathField.isAccessible = true
//            val pathValue = pathField.get(tempFieldValue) as ArrayList<*>
//            val nameField = pathValue[0].javaClass.getDeclaredField("name")
//            nameField.isAccessible = true
//            val nameValue = nameField.get(pathValue[0]) as String
//
//            val temClass = Class.forName(nameValue)
//            println(temClass.name)
//            return temClass as? Class<T>
//        }
}

//查找泛型具体类型
private fun findParameterizedType(clazz: Class<*>): ParameterizedType? {
    val superClass = clazz.superclass ?: return null
//            if (superClass != AMActivity::class.java) {
//                return findParameterizedType(superClass)
//            }
    val gSuperClass = clazz.genericSuperclass
    if (gSuperClass !is ParameterizedType)
        return findParameterizedType(superClass)
    return gSuperClass
}
