package com.hylamobile.demo.config

import net.matlux.NreplServerSpring
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ReplConfig {

    @Bean
    fun clojureRepl(@Value("\${repl.port}") port: Int): NreplServerSpring =
        NreplServerSpring(port)
}
