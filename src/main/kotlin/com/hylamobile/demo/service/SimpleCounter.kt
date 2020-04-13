package com.hylamobile.demo.service

import org.springframework.stereotype.Service

@Service
class SimpleCounter : Counter {

    private var holder: Int = 0

    override fun value(): Int = holder

    override fun increment() {
        holder++
    }
}
