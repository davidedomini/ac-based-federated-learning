#!/usr/bin/env kotlin

import java.io.File

data class SimulationResult(val path: String, val content: List<String>)

fun clean(lines: List<String>): List<String> {
    return lines
        .filter { !it.startsWith("#") || it.contains("max") }
        .filter { !it.contains("NaN") }
        .map { if (it.startsWith("#")) it.substringAfter("# ") else it }
}

val dir = "../alchemist-data/"
val paths = listOf(
    dir + "simulation_mnist.csv",
    dir + "simulation_fashion.csv"
)

paths
    .map { File(it) }
    .map { SimulationResult(it.path, it.readLines()) }
    .map { SimulationResult(it.path, clean(it.content)) }
    .forEach {
        val prefix = it.path.substringBeforeLast("/")
        val postfix = it.path.substringAfterLast("/")
        File("${prefix}/cleaned-${postfix}")
            .writeText(it.content.joinToString(separator = "\n"))
    }