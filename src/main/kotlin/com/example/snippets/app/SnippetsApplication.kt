package com.example.snippets.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.example.snippets.controller"])
class SnippetsApplication

fun main(args: Array<String>) {
	runApplication<SnippetsApplication>(*args)
}
