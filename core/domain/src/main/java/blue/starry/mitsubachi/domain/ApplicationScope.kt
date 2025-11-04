package blue.starry.mitsubachi.domain

import javax.inject.Qualifier

// TODO: モジュール移動。common がいい？
@Qualifier
@Retention(AnnotationRetention.BINARY)
@Target(
  AnnotationTarget.FUNCTION,
  AnnotationTarget.VALUE_PARAMETER,
  AnnotationTarget.PROPERTY,
)
annotation class ApplicationScope
