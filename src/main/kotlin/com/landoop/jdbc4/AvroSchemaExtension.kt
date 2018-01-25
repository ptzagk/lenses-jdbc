package com.landoop.jdbc4

import org.apache.avro.Schema

fun Schema.isNullable(): Boolean {
  return this.type == Schema.Type.UNION &&
      this.types.firstOrNull { it -> it.type == Schema.Type.NULL } != null
}

fun Schema.fromUnion(): Schema {
  val schemaTypes = this.types
  return when {
    schemaTypes.size == 1 -> types[0]
    schemaTypes.size == 2 -> schemaTypes.first { it -> it.type != Schema.Type.NULL }
    else -> throw IllegalArgumentException("Not a Union schema")
  }
}